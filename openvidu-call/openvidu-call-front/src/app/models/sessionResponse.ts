import { RecordingInfo } from 'openvidu-angular';

export interface sessionResponse {
  sessionId: string;
  sessionName: string;
  participantName:string;
  redirectUrl:string;
  settings: {
    duration: number;
    showLogo: boolean;
    logo: string;
    description:string;
    moderators: boolean;
    displayTicker: boolean;
    displayTimer: boolean;
    recording: boolean;
    recordingDetails: {
      max_time: number;
      max_dur: number;
    };
    screenShare: boolean;
    waitForModerator: boolean;
    chat: boolean;
    activitiesButton: boolean;
    participantsButton: boolean;
    floatingLayout: boolean;
    supervisor: boolean;
    preRecorded: boolean;
    broadcast: boolean;
  };
  cameraToken: string;
  screenToken: string;
  recordings: RecordingInfo[];
  type: string;
  recordingMode: string;
}
