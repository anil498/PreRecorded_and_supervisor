import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ParticipantService, RecordingInfo, TokenModel } from 'openvidu-angular';
import { saveAs } from 'file-saver';
import { RestService } from '../../services/rest.service';

@Component({
	selector: 'app-call',
	templateUrl: './call.component.html',
	styleUrls: ['./call.component.scss']
})
export class CallComponent implements OnInit {
	sessionKey = '';
	tokens: TokenModel;
	joinSessionClicked: boolean = false;
	closeClicked: boolean = false;
	isSessionAlive: boolean = false;
	isRecording: boolean = true;
	screenShareEnabled: boolean = true;
	chatEnabled: boolean = true;
	showSessionId:boolean=true;
	broadcastingEnabled: boolean=true;
	recordingList: RecordingInfo[] = [];
	recordingError: any;
	serverError: string = '';
	participantNameValue:string
	loading: boolean = true;
	redirectUrl:string;
	sessionName:string;
	showLogo:Boolean;
	logo:string;
	activitiesButton:Boolean;
	displayTicker:Boolean;
	displayTimer:Boolean;
	description:string;
	participantsButton:Boolean;
	sessionDuration:number;
	private isDebugSession: boolean = false;

	constructor(
		private restService: RestService,
		private participantService: ParticipantService,
		private router: Router,
		private route: ActivatedRoute
	) {}

	async ngOnInit() {
		this.route.params.subscribe((params: Params) => {
			this.sessionKey = params.roomName;
		});

		// Just for debugging purposes
		const regex = /^UNSAFE_DEBUG_USE_CUSTOM_IDS_/gm;
		const match = regex.exec(this.sessionKey);
		this.isDebugSession = match && match.length > 0;

		try {
			await this.initializeTokens();
		} catch (error) {
			console.error(error);
			this.serverError = error?.error?.message || error?.statusText;
		} finally {
			this.loading = false;
		}
	}

	async onNodeCrashed() {
		// Request the tokens again for reconnect to the session
		await this.initializeTokens();
	}
	onLeaveButtonClicked() {
		this.isSessionAlive = false;
		this.closeClicked = true;
		// this.router.navigate([`/`]);
		if (this.participantNameValue === "Customer") {
			// Navigate to a specific URL for customers
			window.location.replace("https://www.axisbank.com/grab-deals/online-offers");
		  } else {
			// Close the window
			window.location.replace("https://www.axisbank.com/grab-deals/online-offers");
			this.restService.removeSession(this.sessionKey);
		  }
	}
	participantName(){
		return this.participantNameValue
	}
	async onStartRecordingClicked() {
		try {
			await this.restService.startRecording(this.sessionKey);
		} catch (error) {
			this.recordingError = error;
		}
	}

	async onStopRecordingClicked() {
		try {
			this.recordingList = await this.restService.stopRecording(this.sessionKey);
		} catch (error) {
			this.recordingError = error;
		}
	}

	async onDeleteRecordingClicked(recordingId: string) {
		try {
			this.recordingList = await this.restService.deleteRecording(recordingId);
		} catch (error) {
			this.recordingError = error;
		}
	}

	private async initializeTokens(): Promise<void> {
		let nickname: string = '';
		if (this.isDebugSession) {
			console.warn('DEBUGGING SESSION');
			nickname = this.participantService.getLocalParticipant().getNickname();
		}
		try{
			const response = await this.restService.getTokens(this.sessionKey, nickname);
		this.isRecording = response.settings.recording;
		this.broadcastingEnabled=response.settings.broadcast;
		this.recordingList = response.recordings
		this.tokens = {
			webcam: response.cameraToken,
			screen: response.screenToken
		};
		this.screenShareEnabled=response.settings.screenShare;
		this.chatEnabled=response.settings.chat;
		this.showSessionId=true;
		this.participantNameValue=response.participantName;
		this.redirectUrl=response.redirectUrl;
		this.sessionName=response.sessionName;
		this.showLogo=response.settings.showLogo;
		this.logo=response.settings.logo;
		this.activitiesButton=response.settings.activitiesButton;
		this.displayTicker=response.settings.displayTicker;
		this.displayTimer=response.settings.displayTimer;
		this.description=response.settings.description;
		this.sessionDuration=response.settings.duration;
		this.participantsButton=response.settings.participantsButton;
	}catch(error){
		console.log(error)
	}

	}
	recordingEnabled(){
		return this.isRecording;
	}

}
