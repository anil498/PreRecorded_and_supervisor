import requestIp from "request-ip";

export class Utils {

    public static getRemoteIpFromRequest(request: any): string {
        let remoteIp = requestIp.getClientIp(request);
        if(remoteIp.substr(0, 7) == '::ffff:') {
            remoteIp = remoteIp.substr(7);
        } else if (remoteIp == '::1') {
            remoteIp = '127.0.0.1';
        }
        return remoteIp;
    }

}