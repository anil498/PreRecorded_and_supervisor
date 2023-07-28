import { Injectable, Logger } from '@nestjs/common';
import child from 'child_process';
import fs from 'fs';
import { readFileSync } from "fs";

const packageJSON = JSON.parse(readFileSync("./package.json", "utf-8"));
const VERSION = packageJSON.version;

@Injectable()
export class SystemService {

    private readonly logger = new Logger(SystemService.name);

    /**
     * Get env variable from system.
     * @param envVariableName Env Variable to get
     */
    public getEnvVariable(envVariableName: string) {
        return process.env[envVariableName];
    }

    public waitForFileToExistAndNotEmpty(absolutePath: string, secondsTimeout: number): Promise<void> {
        return new Promise((resolve, reject) => {

            const condition = () => {
                return (fs.existsSync(absolutePath)) && (fs.statSync(absolutePath).size > 0);
            }

            const sleep = (ms: number) => {
                return new Promise(resolve => setTimeout(resolve, ms));
            }

            const timeout = async () => {
                let exists = false;
                const sleepInterval = 50;
                const maxLoops = (secondsTimeout * 1000) / sleepInterval;
                let loop = 0;
                while (!exists && loop < maxLoops) {
                    await sleep(sleepInterval);
                    exists = condition();
                    loop++;
                }
                if (exists) {
                    resolve();
                } else {
                    reject(new Error('File ' + absolutePath + ' did not exist and was not created during the ' + secondsTimeout + ' seconds timeout'));
                }
            }

            try {
                if (condition()) {
                    resolve();
                } else {
                    timeout();
                }
            } catch (err) {
                timeout();
            }
        });
    }

    public getDefaultGateway(): Promise<string> {
        return new Promise((resolve, reject) => {
            let result: string = "";
            this.logger.log(`Executing command to get default gateway`);
            const cmdExecute = child.spawn("sh", ["-c", "route -n | awk '$4 == \"UG\" {print $2}'"]);
            cmdExecute.stdout.on('data', (data) => { // handler for output on STDOUT
                result += data;
            });

            cmdExecute.stderr.on('data', (data) => { // handler for output on STDERR
                this.logger.log('stderr: ' + data);
            });

            cmdExecute.on('exit', (code) => { // handler invoked when cmd completes
                this.logger.log('Child process exited with code ' + code);
                if (code === 0) {
                    if (result.length > 0) {
                        resolve(result.replace(/'/g, '').trim());
                    } else {
                        reject("Error getting default gateway");
                    }
                } else {
                    reject("Error getting default gateway");
                }
            });
            cmdExecute.on('error', (error) => {
                reject(error);
            })
        });
    }

    public getCurrentVersion() {
        return VERSION;
    }

}