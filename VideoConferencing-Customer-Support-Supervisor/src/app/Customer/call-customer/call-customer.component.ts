import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import {
	ParticipantService,
	OpenViduService,
	BroadcastingStatus,
	BroadcastingService,
	RecordingStatus,
	RecordingInfo,
	RecordingService,
	TokenModel
} from 'openvidu-angular';
import { RestService } from '../../services/rest.service';
import { Subscription } from 'rxjs';
import { animals, colors, Config, countries, names, uniqueNamesGenerator } from 'unique-names-generator';
import { OpenVidu, Publisher, Session } from 'openvidu-browser';
import { WebSocketService } from '../../services/websocket.service';


@Component({
	selector: 'call-customer',
	templateUrl: './call-customer.component.html',
	styleUrls: ['./call-customer.component.css']
})
export class CallCustomerComponent implements OnInit {
	// Variables Declaration

	sessionId = 'daily-call';
	sessionId1 = 'daily-call';
	tokens: TokenModel;
	joinSessionClicked: boolean = false;
	closeClicked: boolean = false;
	isSessionAlive: boolean = false;
	recordingList: RecordingInfo[] = [];
	recordingError: any;
	serverError: string = '';
	loading: boolean = true;
	isLoading: boolean = true;
	private isDebugSession: boolean = false;
	message: string;
	subscription: Subscription;
	getName: string;
	session: Session;
    publisher: Publisher;
	openVidu: OpenVidu;

	// Constructor for declaring objects/properties

	constructor(
		private restService: RestService,
		private recordingService: RecordingService,
		private router: Router,
		private route: ActivatedRoute,
		private broadcastingService: BroadcastingService,
		private participants: ParticipantService,
		private webSocketService: WebSocketService , 

	) {
		// this.openVidu = new OpenVidu();
		// this.session = this.openVidu.initSession();
		// const publisherOptions = {
		// 	insertMode: 'append', // Choose how the video element is inserted in the target element
		// 	mirror: false // Set to true if you want to mirror the published video
		//   };
  		// this.publisher = this.openVidu.initPublisher(undefined,publisherOptions);
	}

	async ngOnInit() {

		// const body = {
		// 	notifyTo: 'support',
		// 	sessionId: 'daily-call2'
		// };

		// this.restService.postRequest('daily-call2' , body);


		// The below code checks if the roomName parameter is provided . If it exists it sets the sessionID to roomName
		// , else the code will genrate a randomn name

		if (this.route.snapshot.paramMap.get('roomName') != null) {
			console.warn('RoomName exists !!!');
			this.sessionId = this.route.snapshot.paramMap.get('roomName');
			this.getName = this.route.snapshot.paramMap.get('name');
			if(this.getName == 'undefined' ){
				this.getName = 'Customer';
				console.log(this.getName);
			}
			console.log(this.getName);
			console.log((this.sessionId = this.route.snapshot.paramMap.get('roomName')));
		} else {
			console.warn('RoomName Doesnt exists !!!');
			this.sessionId = this.getRandomName();
			console.log(this.sessionId);
		}


		// below code determines whether the sessionId string matches the regular expression and sets a
		// boolean flag to indicate whether the session is a debug session or not.

		const regex = /^UNSAFE_DEBUG_USE_CUSTOM_IDS_/gm;
		const match = regex.exec(this.sessionId);
		this.isDebugSession = match && match.length > 0;

		// Request for tokens

		try {
			await this.requestForTokens();
		} catch (error) {
			console.error(error);
			this.serverError = error?.error?.message || error?.statusText;
			this.router.navigate(['']);
		} finally {
			this.loading = false;
		}


	}

	// Request the tokens again for reconnect to the session

	async onNodeCrashed() {
		await this.requestForTokens();
	}

	// Join Button Clicked

	onJoinClicked() {
		console.warn('VC JOIN BUTTON CLICKED');
	}

	// Toolbar Camera Button Clicked

	onToolbarCameraButtonClicked() {
		console.warn('VC camera CLICKED');
	}

	// Toolbar Microphone Button Clicked

	onToolbarMicrophoneButtonClicked() {
		console.warn('VC microphone CLICKED');
	}

	// Toolbar Screenshare Button Clicked

	onToolbarScreenshareButtonClicked() {
		console.warn('VC screenshare CLICKED');
	}

	// Toolbar FullScreen Button Clicked

	onToolbarFullscreenButtonClicked() {
		console.warn('VC fullscreen CLICKED');
	}

	// Toolbar Participants Panel Clicked

	onToolbarParticipantsPanelButtonClicked() {
		console.warn('VC participants CLICKED');
	}

	// Toolbar Chat Panel Clicked

	onToolbarChatPanelButtonClicked() {
		console.warn('VC chat CLICKED');
	}

	// Toolbar Leave Button Clicked

	onToolbarLeaveButtonClicked() {
		this.isSessionAlive = false;
		this.closeClicked = true;
		this.router.navigate(['customer']);
	}

	// Camera Button Clicked

	onCameraButtonClicked() {
		console.warn('TOOLBAR camera CLICKED');
	}

	// Microphone Button Clicked

	onMicrophoneButtonClicked() {
		console.warn('TOOLBAR microphone CLICKED');
	}

	// Screenshare Button Clicked

	onScreenshareButtonClicked() {
		console.warn('TOOLBAR screenshare CLICKED');
	}

	// Fullscreen Button Clicked

	onFullscreenButtonClicked() {
		console.warn('TOOLBAR fullscreen CLICKED');
	}

	// Participants Panel Button Clicked

	onParticipantsPanelButtonClicked() {
		console.warn('TOOLBAR participants CLICKED');
	}

	// Chat Panel Button Clicked

	onChatPanelButtonClicked() {
		console.warn('TOOLBAR chat CLICKED');
	}

	// Leave Button Clicked

	onLeaveButtonClicked() {
		this.isSessionAlive = false;
		console.log('TOOLBAR LEAVE CLICKED');
	}

	// Recording Button Clicked

	async onStartRecordingClicked() {
		console.warn('START RECORDING CLICKED');
		try {
			await this.restService.startRecording(this.sessionId);
		} catch (error) {
			this.recordingError = error;
		}
	}

	// Stop Recording Button Clicked

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

	// Delete Recording Button Clicked

	async onDeleteRecordingClicked(recordingId: string) {
		console.warn('DELETE RECORDING CLICKED');

		try {
			this.recordingList = await this.restService.deleteRecording(recordingId);
		} catch (error) {
			this.recordingError = error;
		}
	}

	// Request for tokens from backend

	private async requestForTokens() {
		console.log("Building A Session");
		const response = await this.restService.getTokensFromBackend1(this.sessionId , 'customer');
		if (response.cameraToken != null && response.screenToken != null) {
			console.log("Got Tokens");
			this.recordingList = response.recordings;
			this.tokens = {
				webcam: response.cameraToken,
				screen: response.screenToken
			};
		} else {
			this.isSessionAlive = false;
			this.closeClicked = true;
			this.router.navigate([``]);
		}
		console.log('Token requested: ', this.tokens);
	}

	// Function for getting Randomname

	private getRandomName(): string {
		const configName: Config = {
			dictionaries: [names, countries, colors, animals],
			separator: '-',
			style: 'lowerCase'
		};
		return uniqueNamesGenerator(configName).replace(/[^\w-]/g, '');
	}

	// Connect to Support Button Clicked

	// async connectToSupport() {
	// 	console.warn('TOOLBAR SUPPORT BUTTON CLICKED');

	// 	localStorage.removeItem('key');

	// 	const roomname = this.sessionId;
	// 	const url = `customer1/#/call?roomname=${roomname}`;

	// 	const body = {
	// 				notifyTo: 'support',
	// 				sessionId: this.sessionId
	// 			};

	// 	this.restService.postRequest(roomname , body);
	// }
}
