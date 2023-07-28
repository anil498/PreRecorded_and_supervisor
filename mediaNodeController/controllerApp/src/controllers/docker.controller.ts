import { Controller, Post, Delete, Param, Body, HttpException, HttpStatus, Get, Logger, Query } from '@nestjs/common';
import { DockerService } from '../services/docker.service.js';
import Docker from 'dockerode';
import AsyncLock from 'async-lock';
import { DockerRegistryAuth } from 'src/model/config.js';

@Controller('media-node')
export class DockerController {

  private readonly logger = new Logger(DockerController.name);

  private lock = new AsyncLock({ timeout: 30000 });

  constructor(private readonly _dockerService: DockerService) { }

  @Get('/dockerGatewayIp')
  async getDockerGatewayIp() {
    try {
      this.logger.log("Getting docker gateway...");
      return await this._dockerService.getDockerGatewayIp();
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Get('/getContainerIp/:containerIdOrName')
  async getContainerIp(@Param('containerIdOrName') containerIdOrName: string) {
    let containerIp = null;
    try {
      containerIp = await this._dockerService.getContainerIp(containerIdOrName);
    }
    catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
    if (containerIp == null) {
      throw new HttpException(`Container '${containerIdOrName}' not found`, HttpStatus.NOT_FOUND)
    }
    return containerIp;
  }

  @Get('/getRunningContainers')
  async getRunningContainers() {
    try {
      return await this._dockerService.getAllContainers(true);
    } catch (error) {
      new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Get('/isContainerRunning/:containerNameOrId')
  async checkContainerRunning(@Param('containerNameOrId') containerNameOrId): Promise<boolean> {
    return await this._dockerService.isContainerRunning(containerNameOrId);
  }

  @Get('/getContainerIdByName/:containerName')
  async getContainerIdByName(@Param('containerName') containerName): Promise<string> {
    return await this._dockerService.getContainerIdByName(containerName);
  }

  @Get('getContainerByNameOrId/:containerNameOrId')
  async getContainerByNameOrId(@Param('containerNameOrId') containerNameOrId: string): Promise<Docker.ContainerInspectInfo> {
    return await this._dockerService.getContainerInfoByNameOrId(containerNameOrId);
  }

  @Get('getLabelsFromContainerNameOrId/:containerNameOrId')
  async getContainerLabelsByNameOrId(@Param('containerNameOrId') containerNameOrId: string): Promise<any> {
    return this.lock.acquire(containerNameOrId, async () => {
      const container: Docker.ContainerInspectInfo = await this._dockerService.getContainerInfoByNameOrId(containerNameOrId);
      return container.Config?.Labels;
    }).catch(error => {
      // Error handling
      this.logger.error('Error on getContainerLabelsByNameOrId for container ' + containerNameOrId + ': ' + error);
      throw new HttpException(`Container '${containerNameOrId}' not found: ${error}`, HttpStatus.NOT_FOUND);
    });
  }

  @Post('/checkImageExists')
  async checkImageExists(@Body() options: Docker.ContainerCreateOptions): Promise<boolean> {
    const image: string = options.Image;
    if (await this._dockerService.imageExists(image)) {
      return true;
    }
    return false;
  }

  @Post('/pullDockerImage')
  async pullImage(@Body() options: Docker.ContainerCreateOptions): Promise<void> {
    const image: string = options.Image;
    try {
      if (await this._dockerService.imageExists(image)) {
        return;
      }
      return await this._dockerService.pullImage(image)
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Starts a new container. This method is concurrency-safe for containers with the same name.
   * If the container already exists, then a 409 response will be sent with the existing container id.
   * Any other kind of error will trigger a 500 response.
   */
  @Post()
  async initContainer(@Body() options: Docker.ContainerCreateOptions): Promise<string> {
    // Options being https://docs.docker.com/engine/api/v1.40/#operation/ContainerCreate
    return this.lock.acquire(options.name, async () => {
      // Lock acquired
      let isContainerRunning = await this._dockerService.isContainerRunning(options.name);
      if (!isContainerRunning) {
        try {
          return await this._dockerService.startContainer(options);
        } catch (error) {
          this.logger.error(error);
          throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } else {
        this.logger.warn('Container with the same name ' + options.name + ' is already running');
        const existingContainer: Docker.Container = await this._dockerService.getContainerByNameOrId(options.name);
        throw new HttpException(existingContainer.id, HttpStatus.CONFLICT);
      }
    }).catch(error => {
      // Error handling
      this.logger.error('Error on initContainer with options ' + options + ': ' + error);
      throw error;
    });
  }

  @Post('/waitContainer/:containerNameOrId')
  async waitContainer(@Param('containerNameOrId') containerNameOrId): Promise<void> {
    try {
      this.logger.log('Waiting for container ' + containerNameOrId + ' to be stopped');
      await this._dockerService.waitForContainerStopped(containerNameOrId);
      this.logger.log('Container ' + containerNameOrId + ' is now successfully stopped');
      return;
    } catch (error) {
      this.logger.error(error);
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Delete('/stopContainer/:containerNameOrId')
  async stopContainer(@Param('containerNameOrId') containerNameOrId): Promise<void> {
    try {
      this.logger.log('Stopping container ' + containerNameOrId);
      await this._dockerService.stopContainer(containerNameOrId);
      return;
    } catch (error) {
      this.logger.error(error);
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Delete('/removeContainer/:containerNameOrId')
  async deleteContainer(@Param('containerNameOrId') containerNameOrId, @Query('throwError') throwError) {
    return this.lock.acquire(containerNameOrId, async () => {
      try {
        this.logger.log('Deleting container ' + containerNameOrId);
        await this._dockerService.removeContainer(containerNameOrId, throwError === "true");
        return;
      } catch (error) {
        this.logger.error('Error on deleteContainer for container ' + containerNameOrId + ': ' + error);
        // Error handling
        if (error.statusCode === 409) {
          this.logger.warn(error.message);
        } else {
          this.logger.error(error);
          throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    }).catch(error => {
      this.logger.error('Async Lock error when trying to delete container ' + containerNameOrId + ': ' + error);
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
    });
  }

  @Post('/runCommandInContainer/:containerNameOrId')
  async runCommandInContainer(@Param('containerNameOrId') containerNameOrId, @Body() options): Promise<void> {
    try {
      this.logger.log('Running command in container ' + containerNameOrId + ' ' + JSON.stringify(options));
      await this._dockerService.runCommandInContainer(containerNameOrId, options.command, options.secondsTimeout);
      return;
    } catch (error) {
      this.logger.error(error);
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Post('/waitForContainerLog/:containerNameOrId')
  async waitForContainerLog(@Param('containerNameOrId') containerNameOrId, @Body() options): Promise<string> {
    this.logger.log('Waiting for container ' + containerNameOrId + ' to have log: ' + JSON.stringify(options));
    try {
      const matchingLineOutput = await this._dockerService.waitForContainerLog(containerNameOrId, options.expectedLog, options.expectedLogIsRegex, options.forbiddenLog, options.forbiddenLogIsRegex, options.secondsTimeout);
      this.logger.log('Container ' + containerNameOrId + ' has log "' + options.log + '"');
      return matchingLineOutput;
    } catch (error) {
      this.logger.error("Error waiting for container log: " + error.toString());
      if (error instanceof HttpException) {
        throw error;
      } else {
        // Passing an object as first argument allows for a simple string response entity
        throw new HttpException(new String(error.message), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  @Post('/authDockerRegistry')
  async authDockerRegistry(@Body() loginData: DockerRegistryAuth): Promise<void> {
    // Check data
    if (!loginData || Object.keys(loginData).length === 0) {
      throw new HttpException('loginData is not defined', HttpStatus.BAD_REQUEST);
    }
    const possibleAttributes = ['serveraddress', 'username', 'password'];
    // Check if not valid attributes are defined
    for (const attribute of Object.keys(loginData)) {
      if (!possibleAttributes.includes(attribute)) {
        throw new HttpException('loginData.' + attribute + ' is not a valid attribute', HttpStatus.BAD_REQUEST);
      }
    }

    for (const attribute of possibleAttributes) {
      // Check if the attribute is empty
      if (!loginData[attribute]) {
        throw new HttpException('loginData.' + attribute + ' is not defined', HttpStatus.BAD_REQUEST);
      }
    }

    try {
      await this._dockerService.authToRegistry(loginData);
      return;
    } catch (error) {
      this.logger.error(error);
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
