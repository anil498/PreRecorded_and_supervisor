import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { RecordingInfo, RecordingService, TokenModel } from 'openvidu-angular';
import { RestService } from '../../services/rest.service';
import { Subscription } from 'rxjs';
import { WebSocketService } from 'src/app/services/websocket.service';
import { OpenVidu, Session } from 'openvidu-browser';

@Component({
	selector: 'call-super',
	templateUrl: './call-super.component.html',
	styleUrls: ['./call-super.component.css']
})
export class CallSuperComponent implements OnInit {
	// sessionId = 'daily-call';
	previousSessionId: string = '';
	sessionId: string;
	tokens: TokenModel;

	joinSessionClicked: boolean = false;
	closeClicked: boolean = false;
	isSessionAlive: boolean = false;
	recordingList: RecordingInfo[] = [];
	recordingError: any;
	serverError: string = '';
	loading: boolean = true;
	private isDebugSession: boolean = false;
	message: string;
	subscription: Subscription;
	openVidu: any;
	session: Session;
	prejoin: boolean=true;

	open: boolean;
	count: boolean;

	constructor(
		private restService: RestService,
		private recordingService: RecordingService,
		private router: Router,
		private route: ActivatedRoute,
		private webSocketService: WebSocketService,

	) {
	}
	async ngOnInit() {

		const regex = /^UNSAFE_DEBUG_USE_CUSTOM_IDS_/gm;
		const match = regex.exec(this.sessionId);
		this.isDebugSession = match && match.length > 0;
		this.newMessage();
		console.log(this.message);
		this.sessionId = this.route.snapshot.paramMap.get('id');
		console.log(this.sessionId);

		try {
			await this.requestForTokens();
		} catch (error) {
			console.error(error);
			this.serverError = error?.error?.message || error?.statusText;
		} finally {
			this.loading = false;
		}

		let stompClient = this.webSocketService.connect();
	
				stompClient.connect({}, (frame) => {
	
					
					// Subscribe to notification topic
					stompClient.subscribe('/topic/support', (notifications) => {
						console.log(this.router.url);
						
					this.mergebutton();
						
					});
				});
	}

	async onNodeCrashed() {
		// Request the tokens again for reconnect to the session
		await this.requestForTokens();
	}

	onJoinClicked() {
		console.warn('VC JOIN BUTTON CLICKED');
	}

	onToolbarCameraButtonClicked() {
		console.warn('VC camera CLICKED');
	}
	onToolbarMicrophoneButtonClicked() {
		console.warn('VC microphone CLICKED');
	}
	onToolbarScreenshareButtonClicked() {
		console.warn('VC screenshare CLICKED');
	}
	onToolbarFullscreenButtonClicked() {
		console.warn('VC fullscreen CLICKED');
	}
	onToolbarParticipantsPanelButtonClicked() {
		console.warn('VC participants CLICKED');
	}
	onToolbarChatPanelButtonClicked() {
		console.warn('VC chat CLICKED');
	}

	onToolbarLeaveButtonClicked() {
		this.isSessionAlive = false;
		this.closeClicked = true;
		this.router.navigate(['/support']);
	}

	onCameraButtonClicked() {
		console.warn('TOOLBAR camera CLICKED');
	}
	onMicrophoneButtonClicked() {
		console.warn('TOOLBAR microphone CLICKED');
	}
	onScreenshareButtonClicked() {
		console.warn('TOOLBAR screenshare CLICKED');
	}
	onFullscreenButtonClicked() {
		console.warn('TOOLBAR fullscreen CLICKED');
	}
	onParticipantsPanelButtonClicked() {
		console.warn('TOOLBAR participants CLICKED');
	}
	onChatPanelButtonClicked() {
		console.warn('TOOLBAR chat CLICKED');
	}

	onLeaveButtonClicked() {
		console.log('TOOLBAR LEAVE CLICKED');
		this.isSessionAlive = false;
		this.closeClicked = true;
		this.router.navigate(['/support']);
	}

	async onStartRecordingClicked() {
		console.warn('START RECORDING CLICKED');
		try {
			await this.restService.startRecording(this.sessionId);
		} catch (error) {
			this.recordingError = error;
		}
	}
	async onStopRecordingClicked() {
		console.warn('STOP RECORDING CLICKED');
		try {
			// await this.restService.startRecording(this.sessionId);

			this.recordingList = await this.restService.stopRecording(this.sessionId);
			console.log('RECORDING LIST ', this.recordingList);
		} catch (error) {
			this.recordingError = error;
		}
	}

	async onDeleteRecordingClicked(recordingId: string) {
		console.warn('DELETE RECORDING CLICKED');

		try {
			this.recordingList = await this.restService.deleteRecording(recordingId);
		} catch (error) {
			this.recordingError = error;
		}
	}

	private async requestForTokens() {
		const response = await this.restService.getTokensFromBackend1(this.sessionId,'supervisor');
		this.recordingList = response.recordings;
		this.tokens = {
			webcam: response.cameraToken,
			screen: response.screenToken
		};

		console.log('Token requested: ', this.tokens);
	}
	
	private async enableWaitingPage() {
		console.log('waiting for token');
		await this.sleep(10000);
	}
	private sleep(ms) {
		return new Promise((resolve) => setTimeout(resolve, ms));
	}
	newMessage() {
	}


	async mergebutton() {

		const value = this.sessionId;
		const sessionID = value.substring(0,value.length-2);
		console.log(sessionID);
		console.warn('TOOLBAR SUPERVISOR BUTTON CLICKED');
		this.router.navigate(['/']).then(() => {
		window.open(`/#/call-super/${sessionID}`, '_self');	
		});
		
	}
	
	

}
