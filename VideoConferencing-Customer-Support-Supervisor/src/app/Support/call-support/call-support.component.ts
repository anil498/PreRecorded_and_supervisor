
//All the imports

import { Component,  OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { OpenViduService, ParticipantService, RecordingInfo, RecordingService, TokenModel } from 'openvidu-angular';
import { RestService } from '../../services/rest.service';
import { Subscription } from 'rxjs';
import { OpenVidu, Publisher, Session } from 'openvidu-browser';
import { HttpClient } from '@angular/common/http';
import { WebSocketService } from 'src/app/services/websocket.service';

@Component({
	selector: 'call-support',
	templateUrl: './call-support.component.html',
	styleUrls: ['./call-support.component.css']
})
export class CallSupportComponent implements OnInit {

	// Objects and Variables declaration

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
	mute: string;
	isvisible: boolean = true;
	merge: boolean = false;
	openVidu: any;
	session: Session;

	private publisherlist: Publisher[];
	private openvidu: OpenVidu;

	// Construction to declare objects

	constructor(
		private restService: RestService,
		private recordingService: RecordingService,
		private router: Router,
		private route: ActivatedRoute,
		private http: HttpClient,
		private openviduService: OpenViduService,
		private participants: ParticipantService,
		private webSocketService: WebSocketService,
		private openViduService: OpenViduService
	) {

		//Get Key item from local storage to hide merge button and connect to supervisor button

		const value2 = localStorage.getItem('key');
		if (value2 == 'true') {
			this.merge = true;
			this.isvisible = false;
		}

		//Connecting to websocket service

		let stompClient = this.webSocketService.connect();
		stompClient.connect({}, (frame) => {

			//Subscribe to topic/hide

			stompClient.subscribe('/topic/hide', (notifications) => {
				this.isvisible = false;
			});

			//Subscribe to topic/unhold

			stompClient.subscribe('/topic/unhold', (notifications) => {
				this.publisherlist[0].publishVideo(false);
				this.publisherlist[0].publishAudio(true);
				this.merge = false;
			});
		});
	}

	//Main Function

	async ngOnInit() {
		
		//Used for Debug Session

		const regex = /^UNSAFE_DEBUG_USE_CUSTOM_IDS_/gm;
		const match = regex.exec(this.sessionId);
		this.isDebugSession = match && match.length > 0;
		this.newMessage();
		console.log(this.message);

		//Get Parameters from previous page

		this.sessionId = this.route.snapshot.paramMap.get('id');
		console.log("SessionId Recieved : "+this.sessionId);

		try {
			await this.requestForTokens();
		} catch (error) {
			console.error(error);
			this.serverError = error?.error?.message || error?.statusText;
		} finally {
			this.loading = false;
		}
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
		const response = await this.restService.getTokensFromBackend1(this.sessionId, 'support');
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

	newMessage() {}

	//Connect to Supervisor Function

	async connectToSupervisor() {

		console.warn('TOOLBAR SUPERVISOR BUTTON CLICKED');

		localStorage.setItem('key3', 'true');

		const id = this.sessionId + '_2';
		const body = {
			notifyTo: 'supervisor',
			sessionId: id
		};

		//Get Publisher object and Session Object to unpublish audio and video (Call Hold Functionality)

		this.session = this.openViduService.getSession();
		this.openvidu = this.session.openvidu;
		this.publisherlist = this.openvidu.publishers;
		console.log('Publishers are : ' + this.publisherlist.length);

		this.publisherlist[0].publishVideo(false);
		this.publisherlist[0].publishAudio(false);

		//Navigate to next page
		
		this.router.navigate([]).then(() => {
			window.open(`/#/confirm?roomName=${this.sessionId}`, '_blank');
		});

		localStorage.setItem('key', 'true');
	}

	//Merge Call Function

	async mergecall() {
		console.warn('Merge Button Clicked');

		const session_message = 'mergecall';
		this.webSocketService.mergecall(session_message);
		this.webSocketService.unhold(session_message);

		this.session = this.openViduService.getSession();
		this.openvidu = this.session.openvidu;

		this.openViduService.disconnect();
		window.close();
	}
}
