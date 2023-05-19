import { RecordingInfo } from 'openvidu-angular';
export interface sessionResponse{
    isRecording:boolean;
    isBroadCasting:boolean;
    cameraToken:String;
    screenToken:String;
    recordings:RecordingInfo[];
    isScreenSharing:boolean;
    isChatEnabled:boolean;
    showSessionId:boolean;
    participantName:string;
}