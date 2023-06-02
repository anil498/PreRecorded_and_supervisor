import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ParticipantService, RecordingInfo, TokenModel,ActionService,TranslateService} from 'openvidu-angular';

import { RestService } from '../../services/rest.service';
import { Subscription } from 'rxjs';

@Component({
	selector: 'app-call',
	templateUrl: './call.component.html',
	styleUrls: ['./call.component.scss']
})
export class CallComponent implements OnInit {
	sessionId = '';
	tokens: TokenModel;
	joinSessionClicked: boolean = false;
	closeClicked: boolean = false;
	isSessionAlive: boolean = false;
	recordingEnabled1: boolean = true;
	screenShareEnabled: boolean = true;
	chatEnabled: boolean = true;
	showSessionId:boolean=true;
	broadcastingEnabled: boolean=true;
	recordingList: RecordingInfo[] = [];
	recordingError: any;
	serverError: string = '';
	participantName1:any
	loading: boolean = true;
	redirectUrl:string;
	private isDebugSession: boolean = false;

	constructor(
		private restService: RestService,
		private participantService: ParticipantService,
		private router: Router,
		private route: ActivatedRoute,
		private actionService:ActionService,
		private translateService:TranslateService
	) {}

	async ngOnInit() {
		this.route.params.subscribe((params: Params) => {
			this.sessionId = params.roomName;
		});

		// Just for debugging purposes
		const regex = /^UNSAFE_DEBUG_USE_CUSTOM_IDS_/gm;
		const match = regex.exec(this.sessionId);
		this.isDebugSession = match && match.length > 0;

		try {
			await this.initializeTokens();
		} catch (error) {
			console.log(error)
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
		if (this.participantName1 === "Customer") {
			// Navigate to a specific URL for customers
			window.location.href = "https://www.axisbank.com/grab-deals/online-offers";
		  } else {
			// Close the window
			window.location.href = "https://www.axisbank.com/grab-deals/online-offers";
		  }
	}
	participantName(){
		return this.participantName1
	}
	async onStartRecordingClicked() {
		try {
			await this.restService.startRecording(this.sessionId);
		} catch (error) {
			this.recordingError = error;
		}
	}

	async onStopRecordingClicked() {
		try {
			this.recordingList = await this.restService.stopRecording(this.sessionId);
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
		const response = await this.restService.getTokens(this.sessionId, nickname);
		this.recordingEnabled1 = response.recordingEnabled
		this.broadcastingEnabled=response.isBroadCasting
		this.recordingList = response.recordings
		this.tokens = {
			webcam: response.cameraToken,
			screen: response.screenToken
		};
		this.screenShareEnabled=response.isScreenSharing;
		this.chatEnabled=response.isChatEnabled;
		this.showSessionId=response.showSessionId;
		this.participantName1=response.participantName;
		console.log(response);
		console.log(this.screenShareEnabled);
	}catch(error){
		// console.error(error);
		// console.log(this.loading)
		// this.loading = false;
		// this.redirectUrl="https://www.axisbank.com/grab-deals/online-offers"
		// this.actionService.openDialog(this.translateService.translate('ERRORS.SESSION'), error?.error || error?.message || error,true,this.redirectUrl)
	}
	}
	recordingEnabled(){
		return this.recordingEnabled1;
	}
}
