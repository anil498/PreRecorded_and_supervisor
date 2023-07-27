import { Injectable, Logger } from '@nestjs/common';
import {Cron, CronExpression} from '@nestjs/schedule';
import fs from 'fs';
import path from 'path';
import {MediaNodeControllerConfigService} from "./media-node-controller-config.service.js";

@Injectable()
export class LogCleanerService {

    private readonly logger = new Logger(LogCleanerService.name);

    constructor(private readonly _mncConfig: MediaNodeControllerConfigService) {
    }

    @Cron(CronExpression.EVERY_HOUR)
    handleCron() {
        this.logger.log(`Executing log rotation. Max num of logs is ${this._mncConfig.maxLogFiles}`)
        this.removeOldLogs().then(() => {
            this.logger.log('Old files deleted')
        }).catch((error) => {
            this.logger.error('Error deleting old log files');
            this.logger.error(error);
        });
    }

    private async removeOldLogs() {
        const maxNumLogFiles: number = this._mncConfig.maxLogFiles;
        for (const directory of this._mncConfig.logsDirectories) {
            const logFilesChronoSorted = await this.readdirChronoSorted(directory, -1);
            const actualNumLogFiles = logFilesChronoSorted.length;
            if (actualNumLogFiles > maxNumLogFiles) {
                const filesToDelete = actualNumLogFiles - maxNumLogFiles;
                for(let i = 0; i < filesToDelete; i++) {
                    const fileNameToDelete = logFilesChronoSorted[0];
                    const fileToDelete = directory + '/' + fileNameToDelete;
                    logFilesChronoSorted.shift();
                    this.logger.log(`Deleting old log file: ${fileToDelete}`)
                    fs.unlinkSync(fileToDelete);
                }
            }
        }
    }

    private async readdirChronoSorted(dirpath: string, order?: number): Promise<string[]> {
        order = order || 1;
        const files = await fs.readdirSync(dirpath);
        const stats = await Promise.all(
            files.map((filename) => {
                const stat = fs.statSync(path.join(dirpath, filename));
                return { filename, stat }
            })
        );
        return stats.sort((a, b) =>
            order * (b.stat.mtime.getTime() - a.stat.mtime.getTime())
        ).map((stat) => stat.filename);
    }


}
