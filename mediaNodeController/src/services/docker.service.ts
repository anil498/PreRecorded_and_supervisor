import { HttpException, HttpStatus, Injectable, Logger } from '@nestjs/common';
import Docker from 'dockerode';
import net from 'net';
import { SystemService } from './system.service.js';
import { PromiseWithTimeout } from '../utils/PromiseWithTimeout.js';
import child from 'child_process';
import { DockerRegistryAuth } from 'src/model/config.js';

@Injectable()
export class DockerService {

    private readonly logger = new Logger(DockerService.name);
    private docker: Docker;

    constructor(private systemService: SystemService) {
        this.docker = new Docker();
    }
    addNewBind(options: any, newBind: string) {
        if (!options.HostConfig) {
          options.HostConfig = {};
        }
        if (!options.HostConfig.Binds) {
          options.HostConfig.Binds = [];
        }
        options.HostConfig.Binds.push(newBind);
      }
    public async startContainer(options: Docker.ContainerCreateOptions): Promise<string> {
        this.logger.log('Launching container ' + options.Image);
        const newBind = "/data/prerecoded:/data/prerecoded";
        this.addNewBind(options, newBind);
        this.logger.log(options);
        
        if (!(await this.imageExists(options.Image))) {
            await this.pullImage(options.Image);
        }
        const container: Docker.Container = await this.docker.createContainer(options);
        await container.start();
        this.logger.log('Container running: ' + options.Image);
        return container.id;
    }

    public async stopContainer(containerNameOrId: string) {
        const container = await this.getContainerByNameOrId(containerNameOrId);
        if (container != null) {
            await container.stop();
            this.logger.log('Container ' + containerNameOrId + ' stopped');
        } else {
            throw 'Container ' + containerNameOrId + ' does not exist'
        }
    }

    public async removeContainer(containerNameOrId: string, throwErrorIfNotExists: boolean) {
        const container = await this.getContainerByNameOrId(containerNameOrId);
        const notFoundErrMessage = `Container ${containerNameOrId} does not exist`;

        const handleNotFoundError = () => {
            if (throwErrorIfNotExists) {
                throw notFoundErrMessage;
            } else {
                this.logger.warn(notFoundErrMessage);
            }
        };

        if (container != null) {
            try {
                await container.remove({ force: true });
                this.logger.log(`Container ${containerNameOrId} removed`);
            } catch (error) {
                if (error.statusCode === 404) {
                    handleNotFoundError();
                } else {
                    this.logger.error(`Unexpected error removing container ${containerNameOrId}: ${error}`);
                    throw error;
                }
            }
        } else {
            handleNotFoundError();
        }
    }

    public async getContainerIp(containerNameOrId: string): Promise<string> {
        const container: Docker.Container = await this.getContainerByNameOrId(containerNameOrId);
        const containerInfo: Docker.ContainerInspectInfo = await container?.inspect();
        return containerInfo?.NetworkSettings?.IPAddress;
    }

    public async getContainerIdByName(containerName: string): Promise<string> {
        const container: Docker.Container = await this.getContainerByNameOrId(containerName);
        if (container == null) {
            return null;
        }
        return (await container.inspect()).Id;
    }

    public async imageExists(image: string): Promise<boolean> {
        const imageNamesArr: string[] = [image];
        const images = await this.docker.listImages({
            filters: JSON.stringify({ reference: imageNamesArr })
        });
        return (images.length > 0);
    }

    public async getAllContainers(onlyRunning: boolean): Promise<Docker.ContainerInfo[]> {
        const allContainers = await this.docker.listContainers({ all: true });
        if (onlyRunning) {
            return allContainers.filter((container) => container.State === 'running');
        }
        return allContainers;
    }

    public async isContainerRunning(containerNameOrId: string): Promise<boolean> {
        this.logger.log(`Checking if ${containerNameOrId} container is running`);
        const runningContainers: Docker.ContainerInfo[] = await this.getAllContainers(true);
        for (const container of runningContainers) {
            if (container.Names.indexOf("/" + containerNameOrId) >= 0) {
                return true;
            }
            if (container.Id === containerNameOrId) {
                return true;
            }
        }
        return false;
    }

    public async waitForContainerStopped(containerNameOrId: string) {
        const container = await this.getContainerByNameOrId(containerNameOrId);
        if (container != null) {
            const maxWaitTime = 30;
            const errorMsg = 'Container ' + containerNameOrId + ' did not stop in ' + maxWaitTime + ' seconds';
            await new PromiseWithTimeout(() => container.wait(), maxWaitTime * 1000, errorMsg).run();
        } else {
            this.logger.warn('Container ' + containerNameOrId + ' does not exist');
        }
    }

    public async getContainerByNameOrId(containerNameOrId: string): Promise<Docker.Container> {
        const runningContainers: Docker.ContainerInfo[] = await this.getAllContainers(false);
        let container: Docker.Container = null;
        for (const containerInfo of runningContainers) {
            if (containerInfo.Names.indexOf("/" + containerNameOrId) >= 0) {
                container = this.docker.getContainer(containerInfo.Id);
                break;
            }
            if (containerInfo.Id === containerNameOrId) {
                container = this.docker.getContainer(containerInfo.Id);
            }
        }
        return container;
    }

    public async getContainerInfoByNameOrId(containerNameOrId: string): Promise<Docker.ContainerInspectInfo> {
        return (await this.getContainerByNameOrId(containerNameOrId))?.inspect();
    }

    public pullImage(image: string): Promise<void> {
        return new Promise((resolve, reject) => {
            this.logger.log('Pulling image ' + image);
            var stdoutChunks = [], stderrChunks = [];
            let stdoutContent = "", stderrContent = "";
            this.logger.log(`Executing command to pull to docker image ${image}`);
            const cmdExecute = child.spawn("sh", [
                "-c",
                `docker pull ${image}`
            ]);

            cmdExecute.stdout.on('data', (data) => {
                this.logger.log(data);
                stdoutChunks = stdoutChunks.concat(data);
            });
            cmdExecute.stdout.on('end', () => {
                stdoutContent = Buffer.concat(stdoutChunks).toString();
            });

            cmdExecute.stderr.on('data', (data) => {
                stderrChunks = stderrChunks.concat(data);
            });
            cmdExecute.stderr.on('end', () => {
                stderrContent = Buffer.concat(stderrChunks).toString();
            });

            cmdExecute.on('exit', (code) => { // handler invoked when cmd completes
                this.logger.log('Child process exited with code ' + code);
                if (code === 0) {
                    if (stdoutContent.length > 0) {
                        this.logger.log(`Image pulled succesfully`);
                        resolve();
                    } else {
                        reject(`Error while pulling image ${image}: ${stderrContent}`);
                    }
                } else {
                    reject(`Error while pulling image ${image}: ${stderrContent}`);
                }
            });
        });
    }

    public async getDockerGatewayIp(): Promise<string> {
        let list: Docker.NetworkInspectInfo[] = await this.docker.listNetworks();
        let bridgeNetwork: Docker.NetworkInspectInfo = null;
        for (let network of list) {
            if (network.Name === "bridge") {
                bridgeNetwork = network;
            }
        }
        if (bridgeNetwork == null) {
            throw new Error("Bridge network not found");
        }
        let gateway = bridgeNetwork.IPAM.Config[0].Gateway;
        if (gateway == null) {
            let bridgeSubnet = bridgeNetwork.IPAM.Config[0].Subnet;
            let prefix = bridgeSubnet.substring(0, bridgeSubnet.lastIndexOf('.'));
            gateway = prefix + '.1';
        }
        if (net.isIP(gateway) != 4) {
            throw new Error("Default docker gateway not found");
        }
        return gateway;
    }

    public async runCommandInContainer(containerNameOrId: string, command: string, secondsOfWait?: number) {
        const container = await this.getContainerByNameOrId(containerNameOrId);
        if (container != null) {

            if (secondsOfWait != null) {
                const errorMsg = 'Container ' + containerNameOrId + ' did not return from command "' + command + '" in ' + secondsOfWait + ' seconds';
                const exec = await container.exec({
                    AttachStdout: true,
                    AttachStderr: true,
                    Cmd: ['/bin/bash', '-c', command],
                    Privileged: true
                });
                await new PromiseWithTimeout(() => exec.start({}), secondsOfWait * 1000, errorMsg).run();
                this.logger.log('Container ' + containerNameOrId + ' successfully executed command ' + command);
            } else {
                const exec = await container.exec({
                    AttachStdout: true,
                    AttachStderr: true,
                    Cmd: ['/bin/bash', '-c', command],
                    Privileged: true
                });
                exec.start({}).then(() => this.logger.log('Container ' + containerNameOrId + ' successfully executed command ' + command));
            }
        } else {
            throw 'Container ' + containerNameOrId + ' does not exist';
        }
    }

    public async waitForContainerLog(containerNameOrId: string, expectedLog: string, expectedLogIsRegex: boolean, forbiddenLog: string, forbiddenLogIsRegex: boolean, secondsTimeout: number): Promise<string> {
        const container = await this.getContainerByNameOrId(containerNameOrId);
        if (container != null) {
            const containerState = (await container.inspect()).State.Status;
            if (containerState === "running") {

                const timeoutException = new HttpException(new String('Container ' + containerNameOrId + ' did not log "' + expectedLog + '" in ' + secondsTimeout + ' seconds'), HttpStatus.REQUEST_TIMEOUT);
                let monitorContainerRunning: NodeJS.Timer;
                let logStream: NodeJS.ReadableStream;
                let dataHandler;
                const clearAll = () => {
                    clearInterval(monitorContainerRunning);
                    logStream.off('data', dataHandler);
                };

                return new PromiseWithTimeout(async () => {

                    let matchesForbiddenLog;
                    let forbiddenLogRegex;
                    if (!!forbiddenLog) {
                        this.logger.log("Forbidden log provided");
                        if (forbiddenLogIsRegex) {
                            forbiddenLogRegex = new RegExp(forbiddenLog);
                            this.logger.log("Using regex for forbidden log: " + forbiddenLogRegex.toString());
                            matchesForbiddenLog = (str) => forbiddenLogRegex.test(str);
                        } else {
                            this.logger.log("Using literal for forbidden log: " + forbiddenLog);
                            matchesForbiddenLog = (str) => str.indexOf(forbiddenLog) >= 0;
                        }
                    }

                    let matchesExpectedLog;
                    let expectedLogRegex;
                    if (expectedLogIsRegex) {
                        expectedLogRegex = new RegExp(expectedLog);
                        this.logger.log("Using regex " + expectedLogRegex.toString());
                        matchesExpectedLog = (str) => expectedLogRegex.test(str);
                    } else {
                        this.logger.log("Using literal " + expectedLog);
                        matchesExpectedLog = (str) => str.indexOf(expectedLog) >= 0;
                    }

                    logStream = await container.logs({
                        follow: true,
                        stdout: true,
                        stderr: true
                    });
                    logStream.setEncoding('utf8');

                    return await new Promise((resolve, reject) => {

                        monitorContainerRunning = setInterval(async () => {
                            let cont = await this.getContainerByNameOrId(containerNameOrId);
                            if (cont == null) {
                                clearAll();
                                reject(new HttpException(new String('Container ' + containerNameOrId + ' does not exist'), HttpStatus.NOT_FOUND));
                            } else if ((await cont.inspect()).State.Status !== "running") {
                                clearAll();
                                try {
                                    const stdout = (await cont.logs({
                                        stderr: false,
                                        stdout: true,
                                        tail: 20
                                    })).toString();
                                    const stderr = (await cont.logs({
                                        stderr: true,
                                        stdout: false,
                                        tail: 20
                                    })).toString();
                                    const logOutput = JSON.stringify({
                                        stdout,
                                        stderr
                                    });
                                    reject(new HttpException('Container ' + containerNameOrId + ' was running but unexpectedly exited. Output log: ' + logOutput, HttpStatus.NOT_FOUND));
                                } catch (e) {
                                    this.logger.error('Error trying to capture logs from failed container ' + containerNameOrId + ': ' + e);
                                    reject(new HttpException('Container ' + containerNameOrId + ' was running but unexpectedly exited. There is not output log to retrieve', HttpStatus.NOT_FOUND));
                                }
                            }
                        }, 100);

                        dataHandler = (data) => {
                            if ((matchesForbiddenLog != undefined) && matchesForbiddenLog(data.toString())) {
                                clearAll();
                                const str = Buffer.from(data).toString('utf8');
                                this.logger.error('Forbidden log found in line: [' + str + ']');
                                reject(new HttpException(new String('Forbidden log [' + forbiddenLog + '] found in line: [' + str + ']'), HttpStatus.NOT_ACCEPTABLE));
                            } else if (matchesExpectedLog(data.toString())) {
                                clearAll();
                                const str = Buffer.from(data).toString('utf8');
                                this.logger.log('First matching log line with expected output is: [' + str + ']');
                                resolve(str);
                            }
                        };

                        logStream.on('data', dataHandler);

                    });

                }, secondsTimeout * 1000, timeoutException, clearAll).run();

            } else {
                throw new HttpException(new String('Container ' + containerNameOrId + ' is not running'), HttpStatus.CONFLICT);
            }
        } else {
            throw new HttpException(new String('Container ' + containerNameOrId + ' does not exist'), HttpStatus.NOT_FOUND);
        }
    }

    public async authToRegistry(loginData: DockerRegistryAuth): Promise<void> {
        return new Promise((resolve, reject) => {
            var stdoutChunks = [], stderrChunks = [];
            let stdoutContent = "", stderrContent = "";
            this.logger.log(`Executing command to login to docker registry ${loginData.serveraddress}`);
            const cmdExecute = child.spawn("sh", [
                "-c",
                `echo ${loginData.password} | docker login -u ${loginData.username} --password-stdin ${loginData.serveraddress}`
            ]);

            cmdExecute.stdout.on('data', (data) => {
                stdoutChunks = stdoutChunks.concat(data);
            });
            cmdExecute.stdout.on('end', () => {
                stdoutContent = Buffer.concat(stdoutChunks).toString();
            });

            cmdExecute.stderr.on('data', (data) => {
                stderrChunks = stderrChunks.concat(data);
            });
            cmdExecute.stderr.on('end', () => {
                stderrContent = Buffer.concat(stderrChunks).toString();
            });

            cmdExecute.on('exit', (code) => { // handler invoked when cmd completes
                this.logger.log('Child process exited with code ' + code);
                if (code === 0) {
                    if (stdoutContent.length > 0) {
                        this.logger.log(`Successfully authenticated to ${loginData.serveraddress}`);
                        resolve();
                    } else {
                        reject(`Error while authenticating to ${loginData.serveraddress}`);
                    }
                } else {
                    reject(`Error while authenticating to ${loginData.serveraddress}: ${stderrContent}`);
                }
            });
        });
    }

}