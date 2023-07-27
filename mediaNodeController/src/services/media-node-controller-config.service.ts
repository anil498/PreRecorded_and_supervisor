import {Injectable, Logger} from '@nestjs/common';
import {ConfigService} from "@nestjs/config";

@Injectable()
export class MediaNodeControllerConfigService {

    private _openViduIps: string[] = [];
    private _basicAuth: string = null;

    // Recordings directory
    private readonly _recordingsSystemPath: string = null;

    // Log rotation configuration
    private readonly _logsDirectories: string[] = [];
    private readonly _maxLogFiles: number = 0;

    constructor(private readonly _configService: ConfigService) {
        this._recordingsSystemPath = this._configService.get<string>('recordingsSystemPath');
        this._logsDirectories = this._configService.get<string[]>('logsDirectories');
        this._maxLogFiles = Number(this._configService.get<number>('maxLogFiles'));
        this.logConfig();
    }

    get openViduIps(): string[] {
        return this._openViduIps;
    }

    addOpenViduIp(openViduIp: string) {
        this._openViduIps.push(openViduIp);
    }

    get basicAuth(): string {
        return this._basicAuth;
    }

    set basicAuth(basicAuth: string) {
        this._basicAuth = basicAuth;
    }

    get recordingsSystemPath(): string {
        return this._recordingsSystemPath;
    }

    get logsDirectories(): string[] {
        return this._logsDirectories
    }

    get maxLogFiles(): number {
        return this._maxLogFiles;
    }

    private logConfig(): void {
        Logger.log('======== MEDIA NODE CONTROLLER CONFIGURATION ==========');
        Logger.log(`RECORDINGS_SYSTEM_PATH: ${this._recordingsSystemPath}`);
        Logger.log(`LOGS_DIRECTORIES: ${this._logsDirectories}`);
        Logger.log(`MAX_LOG_FILES: ${this._maxLogFiles}`);
        Logger.log('=======================================================');
    }
}