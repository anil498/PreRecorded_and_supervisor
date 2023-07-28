import { Injectable, CanActivate, ExecutionContext, Logger } from '@nestjs/common';
import { Observable } from 'rxjs';
import { MediaNodeControllerConfigService } from './services/media-node-controller-config.service.js';
import {Utils} from "./utils/Utils.js";

@Injectable()
export class AuthGuard implements CanActivate {

    private readonly logger = new Logger(AuthGuard.name);
    constructor(private _openViduConfigService: MediaNodeControllerConfigService) {}

    canActivate(context: ExecutionContext): boolean | Promise<boolean> | Observable<boolean> {
        const request = context.switchToHttp().getRequest();
        return this.validateRequest(request);
    }

    /**
     * First request with basic authorization header will securize next
     * requests to the media-node-controller
     *
     * After that all the IPs that sends a basic auth will be saved in a list of
     * ips. All these ips will be 'whitelisted'.
     *
     * In this way, the api will be protected externally and always available for
     * known OpenVidu Server Pro nodes.
     * @param request
     */
    validateRequest(request): boolean {

        const validBasicAuth: string = this._openViduConfigService.basicAuth;

        // Get Remote Ip
        const remoteIp = Utils.getRemoteIpFromRequest(request);

        // If no password is in settings, let all petitions access.
        if (validBasicAuth == null) {
            if (request.headers.authorization == null) {
                this.logger.warn('Security not enabled. You need to do one request with Basic Auth to enable security');
                return true;
            }
            // enable security on first request
            this._openViduConfigService.basicAuth = request.headers.authorization.split(' ')[1];
            this._openViduConfigService.addOpenViduIp(remoteIp);
            this.logger.log('First Request from: ' + remoteIp + '. Security Enabled');
            return true;
        }

        // Check if the remote IPs is a previously known OpenVidu IP
        // This is to ensure Media Nodes doesn't stop receiving
        // requests if the media node controller dies.
        for(const ovIp of this._openViduConfigService.openViduIps) {
            if (ovIp === remoteIp) {

                // Update basic auth if exists
                if (request.headers.authorization != null) {
                    this._openViduConfigService.basicAuth = request.headers.authorization.split(' ')[1];
                }

                this.logger.log('Valid Request from known OpenVidu IP: ' + remoteIp);
                return true;
            }
        }

        // If it is sending a secret, the IP is a known OpenVidu
        const authHeaderBasicAuth: string = request?.headers?.authorization?.split(' ')[1];
        if (!authHeaderBasicAuth) {
            this.logger.warn('Invalid request without authentication headers from: ' + remoteIp);
            return false;
        }

        if (validBasicAuth !== authHeaderBasicAuth) {
            this.logger.warn('Request with wrong credentials from: ' + remoteIp);
            return false;
        }
        this.logger.log('Valid request from: ' + remoteIp);
        this._openViduConfigService.addOpenViduIp(remoteIp);
        return true;
    }
}