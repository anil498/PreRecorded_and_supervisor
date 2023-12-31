import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ActionService, ParticipantService, RecordingInfo, TokenModel } from 'openvidu-angular';
import { RestService } from '../../services/rest.service';
import { TranslateService, PlatformService } from 'openvidu-angular';
import { QuestionData } from 'src/app/models/sessionResponse';

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
	sessionId: string;
	isRecording: boolean = true;
	screenShareEnabled: boolean = true;
	chatEnabled: boolean = true;
	showSessionId: boolean = true;
	broadcastingEnabled: boolean = true;
	recordingList: RecordingInfo[] = [];
	recordingError: any;
	serverError: string = '';
	participantNameValue: string;
	loading: boolean = true;
	redirectUrl: string;
	sessionName: string;
	showLogo: Boolean;
	logo: string;
	activitiesButton: Boolean;
	displayTicker: Boolean;
	displayTimer: Boolean;
	description: string;
	participantsButton: Boolean;
	sessionDuration: number;
	type: string;
	floatingLayout: boolean;
	layoutNumber: number;
	preRecorded: boolean;
	preRecordedFilePath: string;
	isVideoMuted: boolean;
	isAudioMuted: boolean;
	addSupervisor: boolean;
	isScreenShareWithAudio: boolean;
	isAutoFullScreen: boolean;
	private isDebugSession: boolean = false;

	isquestionpanel: boolean;
	isdisplayicdc: boolean;
	isediticdc: boolean;
	istitleicdc: string;
	questionsicdc: string;
	icdcId: string;

	constructor(
		private restService: RestService,
		private participantService: ParticipantService,
		private router: Router,
		private route: ActivatedRoute,
		private actionService: ActionService,
		private translateService: TranslateService,
		private platformService: PlatformService
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
		if (this.type === 'Support') {
			// Navigate to a specific URL for customers
			this.restService.removeSession(this.sessionKey);
			window.location.replace(this.redirectUrl);
		} else {
			window.location.replace(this.redirectUrl);
		}
	}

	onHoldButtonClicked() {
		try {
			console.log('Hold button clicked');
			this.restService.updateSession(this.sessionKey, true);
		} catch (error) {
			console.error('Getting error while calling update session API');
		}
	}

	onUnHoldButtonClicked() {
		try {
			console.log('UnHold button clicked');
			this.restService.updateSession(this.sessionKey, false);
		} catch (error) {
			console.error('Getting error while calling update session API');
		}
	}
	async onSubmitButtonClicked(value: any) {
		const formjson = JSON.parse(value.formresponsevalue);

		const icdcQuestionsarray = JSON.parse(this.questionsicdc); //total array

		const specificformquestionsarray = JSON.parse(icdcQuestionsarray[value.selectformindex].icdc_data);

		this.icdcId = String(icdcQuestionsarray[value.selectformindex].icdc_id);

		var ansarray: any[] = [];
		for (let i = 0; i < specificformquestionsarray.length; i++) {
			const item = {};
			const formkey = String(i);
			item['q_id'] = specificformquestionsarray[i]['q_id'];
			item['type'] = specificformquestionsarray[i]['type'];
			item['question'] = specificformquestionsarray[i]['question'];
			item['meta'] = specificformquestionsarray[i]['meta'];
			if (specificformquestionsarray[i]['type'] === 'checkbox') {
				const checkboxjson = formjson[formkey];
				var checkanswers: string[] = [];
				for (let key in checkboxjson) {
					if (checkboxjson[key]) {
						checkanswers.push(key);
					}
				}
				item['ans'] = checkanswers;
			} else {
				item['ans'] = formjson[formkey];
			}
			ansarray.push(item);
		}
		try {
			await this.restService.saveIcdcResponse(this.sessionId, this.icdcId, ansarray);
		} catch (error) {
			console.error('Getting error while calling Submit ICDC form response API');
		}
	}

	onAddSupervisorButtonClicked() {
		try {
			console.log('Add supervisor button clicked');
			this.restService.sendLink(this.sessionId);
		} catch (error) {
			console.error('Getting error while calling send link API');
		}
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
		try {
			const response = await this.restService.getTokens(this.sessionKey, nickname);
			if (response.isSessionExpired) {
				throw new Error('Session expired');
			}
			this.sessionId = response.sessionId;
			this.isRecording = response.settings.recording;
			this.broadcastingEnabled = response.settings.broadcast;
			this.recordingList = response.recordings;
			this.tokens = {
				webcam: response.cameraToken,
				screen: response.screenToken
			};
			this.screenShareEnabled = response.settings.screenShare;
			this.chatEnabled = response.settings.chat;
			this.showSessionId = true;
			if (response.settings.landingPage) {
				this.redirectUrl = response.settings.landingPage;
			}
			this.sessionName = response.sessionName;
			this.showLogo = response.settings.showLogo;
			const logoResponse=await this.restService.getLogo(response.settings.logo);
			if(logoResponse.logo){
				this.logo=logoResponse.logo;
			}
			this.activitiesButton = response.settings.activitiesButton;
			this.displayTicker = response.settings.displayTicker;
			this.displayTimer = response.settings.displayTimer;
			if (response.settings.description) {
				this.description = response.settings.description;
			} else {
				this.description = '';
			}
			this.sessionDuration = response.settings.duration;
			this.participantsButton = response.settings.participantsButton;
			this.type = response.type;
			this.floatingLayout = response.settings.floatingLayout;
			this.layoutNumber = response.settings.layoutNumber;
			if (response.settings.preRecorded) {
				this.preRecorded = response.settings.preRecordedDetails.share_pre_recorded_video;
			}
			this.preRecordedFilePath = response.settings.fileUrl;
			this.isScreenShareWithAudio = response.settings.ScreenShareWithAudio;
			this.isAutoFullScreen = response.settings.isAutoFullScreen;

			if (response.settings.icdcQuestions) {
				this.questionsicdc = JSON.stringify(response.settings.icdcQuestions);
			}

			if (response.settings.icdcDetails) {
				this.isquestionpanel = response.settings.icdcDetails.icdc;
				this.isdisplayicdc = response.settings.icdcDetails.display_icdc;
				this.isediticdc = response.settings.icdcDetails.edit_icdc;
				this.istitleicdc = response.settings.icdcDetails.title_icdc;
	           
				
			}

			if (response.participantName) {
				this.participantNameValue = response.participantName;
			} else {
				this.participantNameValue = response.type;
			}
			if (this.type === 'Support') {
				this.isAudioMuted = false;
				if (this.platformService.isIos()) {
					this.isVideoMuted = false;
				} else {
					this.isVideoMuted = true;
				}
				if (response.settings.supervisor) {
					this.participantNameValue = 'Support';
					this.addSupervisor = true;
					this.isVideoMuted = false;
				}
			} else {
				this.isAudioMuted = false;
				this.isVideoMuted = false;
			}
		} catch (error) {
			console.log(error);
			if (error.error.settings.landingPage) {
				this.redirectUrl = error.error.settings.landingPage;
			}
			this.actionService.openUrlDialog(this.translateService.translate('ERRORS.EXPIRED'), '', true, this.redirectUrl);
		}
	}
	recordingEnabled() {
		return this.isRecording;
	}
}
