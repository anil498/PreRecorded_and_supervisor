import { Inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { OpenViduAngularConfig, ParticipantFactoryFunction } from '../../config/openvidu-angular.config';
import { RecordingInfo } from '../../models/recording.model';

// import { version } from '../../../../package.json';

/**
 * @internal
 */
@Injectable()
export class OpenViduAngularConfigService {
	private configuration: OpenViduAngularConfig;

	minimal = <BehaviorSubject<boolean>>new BehaviorSubject(false);
	minimalObs: Observable<boolean>;

	participantName = <BehaviorSubject<string>>new BehaviorSubject('');
	participantNameObs: Observable<string>;
	
	floatingLayoutType = <BehaviorSubject<number>>new BehaviorSubject(0);
	floatingLayoutTypeObs: Observable<Number>;

	displayTickerValue = <BehaviorSubject<string>>new BehaviorSubject('');
	displayTickerValueObs: Observable<string>;

	prejoin = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	prejoinObs: Observable<boolean>;

	prefullscreen = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	prefullscreenObs: Observable<boolean>;

	videoMuted = <BehaviorSubject<boolean>>new BehaviorSubject(false);
	videoMutedObs: Observable<boolean>;

	audioMuted = <BehaviorSubject<boolean>>new BehaviorSubject(false);
	audioMutedObs: Observable<boolean>;

	screenshareButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	screenshareButtonObs: Observable<boolean>;

	publishVideoButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	publishVideoeButtonObs: Observable<boolean>;

	videoControlButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	videoControlButtonObs: Observable<boolean>;

	fullScreenshareButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	fullScreenshareButtonObs: Observable<boolean>;

	fullscreenButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	fullscreenButtonObs: Observable<boolean>;

	captionsButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	captionsButtonObs: Observable<boolean>;

	sessionName = <BehaviorSubject<string>>new BehaviorSubject('');
	sessionNameObs: Observable<string>;

	toolbarSettingsButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	toolbarSettingsButtonObs: Observable<boolean>;

	leaveButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	leaveButtonObs: Observable<boolean>;

	participantsPanelButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	participantsPanelButtonObs: Observable<boolean>;

	chatPanelButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	chatPanelButtonObs: Observable<boolean>;
	playvideoButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	playvideoButtonObs: Observable<boolean>;

	activitiesPanelButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	activitiesPanelButtonObs: Observable<boolean>;

	displaySessionName = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	displaySessionNameObs: Observable<boolean>;

	sessionDuration = <BehaviorSubject<number>>new BehaviorSubject(0);
	sessionDurationObs: Observable<number>;

	displayTimer = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	displayTimerObs: Observable<boolean>;

	displayTicker = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	displayTickerObs: Observable<boolean>;

	displayLogo = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	displayLogoObs: Observable<boolean>;

	displayLogoValue = <BehaviorSubject<string>>new BehaviorSubject('');
	displayLogoValueObs: Observable<string>;

	videoFilePath = <BehaviorSubject<Blob>><unknown>new BehaviorSubject('');
	videoFilePathObs: Observable<Blob>;

	displayParticipantName = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	displayParticipantNameObs: Observable<boolean>;
	displayAudioDetection = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	displayAudioDetectionObs: Observable<boolean>;
	streamSettingsButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	streamSettingsButtonObs: Observable<boolean>;
	floatingLayout = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	floatingLayoutObs: Observable<boolean>;
	participantItemMuteButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	participantItemMuteButtonObs: Observable<boolean>;
	backgroundEffectsButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	backgroundEffectsButtonObs: Observable<boolean>;
	recordingsList = <BehaviorSubject<RecordingInfo[]>>new BehaviorSubject([]);
	recordingsListObs: Observable<RecordingInfo[]>;
	recordingButton = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	recordingButtonObs: Observable<boolean>;
	recordingActivity = <BehaviorSubject<boolean>>new BehaviorSubject(true);
	recordingActivityObs: Observable<boolean>;
	recordingError = <BehaviorSubject<any>>new BehaviorSubject(null);
	recordingErrorObs: Observable<any>;
	adminRecordingsList = <BehaviorSubject<RecordingInfo[]>>new BehaviorSubject([]);
	adminRecordingsListObs: Observable<RecordingInfo[]>;
	adminLoginError = <BehaviorSubject<any>>new BehaviorSubject(null);
	adminLoginErrorObs: Observable<any>;

	constructor(@Inject('OPENVIDU_ANGULAR_CONFIG') config: OpenViduAngularConfig) {
		this.configuration = config;
		console.log(this.configuration);
		if (this.isProduction()) console.log('OpenVidu Angular Production Mode');
		this.minimalObs = this.minimal.asObservable();
		this.participantNameObs = this.participantName.asObservable();
		this.floatingLayoutTypeObs=this.floatingLayoutType.asObservable();
		this.displayTickerValueObs = this.displayTickerValue.asObservable();
		this.prejoinObs = this.prejoin.asObservable();
		this.prefullscreenObs = this.prefullscreen.asObservable();
		this.videoMutedObs = this.videoMuted.asObservable();
		this.audioMutedObs = this.audioMuted.asObservable();
		//Toolbar observables
		this.screenshareButtonObs = this.screenshareButton.asObservable();
		this.publishVideoeButtonObs = this.publishVideoButton.asObservable();
		this.videoControlButtonObs = this.videoControlButton.asObservable();
		this.fullScreenshareButtonObs=this.fullScreenshareButton.asObservable();
		this.fullscreenButtonObs = this.fullscreenButton.asObservable();
		this.backgroundEffectsButtonObs = this.backgroundEffectsButton.asObservable();
		this.leaveButtonObs = this.leaveButton.asObservable();
		this.participantsPanelButtonObs = this.participantsPanelButton.asObservable();
		this.chatPanelButtonObs = this.chatPanelButton.asObservable();
		this.playvideoButtonObs = this.playvideoButton.asObservable();
		this.activitiesPanelButtonObs = this.activitiesPanelButton.asObservable();
		this.displaySessionNameObs = this.displaySessionName.asObservable();
		this.sessionDurationObs = this.sessionDuration.asObservable();
		this.displayTimerObs = this.displayTimer.asObservable();
		this.displayTickerObs = this.displayTicker.asObservable();
		this.displayLogoObs = this.displayLogo.asObservable();
		this.displayLogoValueObs = this.displayLogoValue.asObservable();
		this.videoFilePathObs = this.videoFilePath.asObservable();
		this.recordingButtonObs = this.recordingButton.asObservable();
		this.toolbarSettingsButtonObs = this.toolbarSettingsButton.asObservable();
		this.captionsButtonObs = this.captionsButton.asObservable();
		this.sessionNameObs = this.sessionName.asObservable();
		//Stream observables
		this.displayParticipantNameObs = this.displayParticipantName.asObservable();
		this.displayAudioDetectionObs = this.displayAudioDetection.asObservable();
		this.streamSettingsButtonObs = this.streamSettingsButton.asObservable();
		this.floatingLayoutObs = this.floatingLayout.asObservable();
		// Participant item observables
		this.participantItemMuteButtonObs = this.participantItemMuteButton.asObservable();
		// Recording activity observables
		this.recordingActivityObs = this.recordingActivity.asObservable();
		this.recordingsListObs = this.recordingsList.asObservable();
		this.recordingErrorObs = this.recordingError.asObservable();
		// Admin dashboard
		this.adminRecordingsListObs = this.adminRecordingsList.asObservable();
		this.adminLoginErrorObs = this.adminLoginError.asObservable();
	}

	getConfig(): OpenViduAngularConfig {
		return this.configuration;
	}
	isProduction(): boolean {
		return this.configuration?.production || false;
	}

	hasParticipantFactory(): boolean {
		return typeof this.getConfig().participantFactory === 'function';
	}

	getParticipantFactory(): ParticipantFactoryFunction {
		return this.getConfig().participantFactory;
	}
}
