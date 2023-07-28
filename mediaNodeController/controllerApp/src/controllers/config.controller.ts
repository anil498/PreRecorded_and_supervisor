import { Controller, Post, Delete, Param, Body, HttpException, HttpStatus, Get, Logger, Req } from '@nestjs/common';
import { MediaNodeControllerConfigService } from '../services/media-node-controller-config.service.js';

import fs from "fs";
import { Utils } from "../utils/Utils.js";
import { SystemService } from "../services/system.service.js";
import { CpuService } from "../services/cpu.service.js";
import { publicIpv4, publicIpv6 } from 'public-ip';
import { satisfies } from 'compare-versions';

@Controller('media-node')
export class ConfigController {

  private readonly logger = new Logger(ConfigController.name);

  constructor(private _mncConfigService: MediaNodeControllerConfigService, private _systemService: SystemService,
    private _cpuService: CpuService) { }

  @Get('/status')
  async getStatus() {
    return "Media Node Controller is available";
  }

  @Get('/cpu')
  async getCpu() {
    return this._cpuService.getCpuLoad();
  }

  @Get('/getRequestIp')
  async getOpenViduPrivateIp(@Req() req: any) {
    return Utils.getRemoteIpFromRequest(req);
  }

  @Get('/getEnvVariable/:envVariable')
  async getEnvVariable(@Param('envVariable') envVariable: string): Promise<string> {
    return await this._systemService.getEnvVariable(envVariable);
  }

  @Get('/getPublicIpv4')
  async getPublicIpv4(): Promise<string> {
    try {
      return await publicIpv4({timeout: 1000});
    } catch (error) {
      throw new HttpException("Public IPv4 not found", HttpStatus.NOT_FOUND);
    }
  }

  @Get('/getPublicIpv6')
  async getPublicIpv6(): Promise<string> {
    try {
      return await publicIpv6({ timeout: 1000 });
    } catch (error) {
      throw new HttpException("Public IPv6 not found", HttpStatus.NOT_FOUND);
    }
  }

  @Get('/getRecordingsPath')
  async getRecordingsPath(): Promise<string> {
    let recordingsPath = this._mncConfigService.recordingsSystemPath;
    return recordingsPath.endsWith('/') ? recordingsPath : recordingsPath + '/';
  }

  @Post('/waitForFile')
  async waitForFile(@Body() options): Promise<boolean> {
    this.logger.log("Waiting for file to exist and not be empty: " + JSON.stringify(options));
    try {
      await this._systemService.waitForFileToExistAndNotEmpty(options.absolutePath, options.secondsTimeout);
      this.logger.log("File " + options.absolutePath + " exists and is not empty");
      return true;
    } catch (error) {
      this.logger.error("Error waiting for file: " + error.message);
      throw new HttpException(error, HttpStatus.NOT_FOUND);
    }
  }

  @Delete('recordings/:recordingId/:recordingFile')
  async deleteRecording(@Param('recordingId') recordingId, @Param('recordingFile') recordingFile): Promise<string> {

    let recordingsPath = this._mncConfigService.recordingsSystemPath;
    const recordingFolderPath = recordingsPath + '/' + recordingId;
    const recordingFilePath = recordingFolderPath + '/' + recordingFile;

    if (fs.existsSync(recordingFilePath)) {
      try {
        fs.unlinkSync(recordingFilePath);
        if (fs.readdirSync(recordingFolderPath).length === 0) {
          // Last recording file deleted from folder. Clean up folder
          fs.rmdirSync(recordingFolderPath);
        }
        return 'File ' + recordingFilePath + ' deleted';
      } catch (error) {
        this.logger.error(error);
        throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      throw new HttpException('File ' + recordingFilePath + ' does not exist', HttpStatus.NOT_FOUND);
    }
  }


  // The purpose of this method is to avoid people using non compatible
  // media-node-controller versions, so people always remember to update
  // their media-nodes when updating OpenVidu Server
  @Get('/checkVersion/:ver')
  async getVersion(@Param('ver') version: string): Promise<boolean> {
    const currentVersion = this._systemService.getCurrentVersion();
    console.log("Media node controller version: ", currentVersion);
    console.log("OpenVidu Server version", version);

    // Disable dev, beta, and master/nightly builds
    if (this.isAlwaysValid(version) || this.isAlwaysValid(currentVersion)) {
      return true;
    }

    // Compatible always between patch versions
    if (satisfies(version, `~${currentVersion}`)) {
      return true;
    } else if (satisfies(currentVersion, `~${version}`)) {
      return true;
    }

    return false;
  }

  private isAlwaysValid(version: string) {
    const alwaysValidList = [
      'master',
      'nightly',
      'beta',
      'dev'
    ];

    for (const validVersion of alwaysValidList) {
      if (version.indexOf(validVersion) !== -1) {
        return true;
      }
    }

    return false;
  }

}