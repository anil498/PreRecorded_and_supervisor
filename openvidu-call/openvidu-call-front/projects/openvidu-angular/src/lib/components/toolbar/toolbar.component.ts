import {
	AfterViewInit,
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	ContentChild,
	EventEmitter,
	HostListener,
	OnDestroy,
	OnInit,
	Output,
	TemplateRef,
	ViewChild
} from '@angular/core';
import { interval, skip, Subscription } from 'rxjs';
import { ChatService } from '../../services/chat/chat.service';
import { DocumentService } from '../../services/document/document.service';
import { PanelEvent, PanelService } from '../../services/panel/panel.service';

import { MediaChange } from '@angular/flex-layout';
import { MatMenuTrigger } from '@angular/material/menu';
import { Session } from 'openvidu-browser';
import {
	ToolbarAdditionalButtonsDirective,
	ToolbarAdditionalPanelButtonsDirective
} from '../../directives/template/openvidu-angular.directive';
import { ChatMessage } from '../../models/chat.model';
import { ILogger } from '../../models/logger.model';
import { PanelType } from '../../models/panel.model';
import { OpenViduRole, ParticipantAbstractModel } from '../../models/participant.model';
import { RecordingInfo, RecordingStatus } from '../../models/recording.model';
import { ActionService } from '../../services/action/action.service';
import { OpenViduAngularConfigService } from '../../services/config/openvidu-angular.config.service';
import { DeviceService } from '../../services/device/device.service';
import { LayoutService } from '../../services/layout/layout.service';
import { LoggerService } from '../../services/logger/logger.service';
import { OpenViduService } from '../../services/openvidu/openvidu.service';
import { ParticipantService } from '../../services/participant/participant.service';
import { PlatformService } from '../../services/platform/platform.service';
import { RecordingService } from '../../services/recording/recording.service';
import { StorageService } from '../../services/storage/storage.service';
import { TranslateService } from '../../services/translate/translate.service';
import { Signal } from '../../models/signal.model';
import { CdkOverlayService } from '../../services/cdk-overlay/cdk-overlay.service';

/**
 *
 * The **ToolbarComponent** is hosted inside of the {@link VideoconferenceComponent}.
 * It is in charge of displaying the participants controlls for handling the media, panels and more videoconference features.
 *
 * <div class="custom-table-container">
 * <div>
 *  <h3>API Directives</h3>
 *
 * This component allows us to show or hide certain HTML elements with the following {@link https://angular.io/guide/attribute-directives Angular attribute directives}
 * with the aim of fully customizing the ToolbarComponent.
 *
 * | **Name**                  | **Type**  | **Reference**                                   |
 * | :----------------------------: | :-------: | :---------------------------------------------: |
 * | **screenshareButton**       | `boolean` | {@link ToolbarScreenshareButtonDirective}       |
 * | **fullscreenButton**        | `boolean` | {@link ToolbarFullscreenButtonDirective}        |
 * | **backgroundEffectsButton** | `boolean` | {@link ToolbarBackgroundEffectsButtonDirective} |
 * | **leaveButton**             | `boolean` | {@link ToolbarLeaveButtonDirective}             |
 * | **chatPanelButton**         | `boolean` | {@link ToolbarChatPanelButtonDirective}         |
 * | **participantsPanelButton** | `boolean` | {@link ToolbarParticipantsPanelButtonDirective} |
 * | **questionPanelButton**     | `boolean` | {@link ToolbarQuestionPanelButtonDirective}     |
 * | **displayLogo**             | `boolean` | {@link ToolbarDisplayLogoDirective}             |
 * | **displaySessionName**      | `boolean` | {@link ToolbarDisplaySessionNameDirective}      |
 *
 * <p class="component-link-text">
 * <span class="italic">See all {@link ApiDirectiveModule API Directives}</span>
 * </p>
 *
 * </div>
 * <div>
 *
 * <h3>OpenVidu Angular Directives</h3>
 *
 * The ToolbarComponent can be replaced with a custom component. It provides us the following {@link https://angular.io/guide/structural-directives Angular structural directives}
 * for doing this.
 *
 * |            **Directive**           |                 **Reference**                 |
 * |:----------------------------------:|:---------------------------------------------:|
 * |           ***ovToolbar**           |            {@link ToolbarDirective}           |
 *
 * </br>
 *
 * It is also providing us a way to **add additional buttons** to the default toolbar.
 * It will recognise the following directive in a child element.
 *
 * |            **Directive**           |                 **Reference**                 |
 * |:----------------------------------:|:---------------------------------------------:|
 * |   ***ovToolbarAdditionalButtons**   |   {@link ToolbarAdditionalButtonsDirective}   |
 * |***ovToolbarAdditionalPanelButtons**   |   {@link ToolbarAdditionalPanelButtonsDirective}   |
 *
 * <p class="component-link-text">
 * 	<span class="italic">See all {@link OpenViduAngularDirectiveModule OpenVidu Angular Directives}</span>
 * </p>
 * </div>
 * </div>
 */

@Component({
	selector: 'ov-toolbar',
	templateUrl: './toolbar.component.html',
	styleUrls: ['./toolbar.component.css'],
	changeDetection: ChangeDetectionStrategy.OnPush
})
export class ToolbarComponent implements OnInit, OnDestroy, AfterViewInit {
	/**
	 * @ignore
	 */
	@ContentChild('toolbarAdditionalButtons', { read: TemplateRef }) toolbarAdditionalButtonsTemplate: TemplateRef<any>;

	/**
	 * @ignore
	 */
	@ContentChild('toolbarAdditionalPanelButtons', { read: TemplateRef }) toolbarAdditionalPanelButtonsTemplate: TemplateRef<any>;

	/**
	 * @ignore
	 */
	@ContentChild(ToolbarAdditionalButtonsDirective)
	set externalAdditionalButtons(externalAdditionalButtons: ToolbarAdditionalButtonsDirective) {
		// This directive will has value only when ADDITIONAL BUTTONS component tagget with '*ovToolbarAdditionalButtons' directive
		// is inside of the TOOLBAR component tagged with '*ovToolbar' directive
		if (externalAdditionalButtons) {
			this.toolbarAdditionalButtonsTemplate = externalAdditionalButtons.template;
		}
	}

	/**
	 * @ignore
	 */
	@ContentChild(ToolbarAdditionalPanelButtonsDirective)
	set externalAdditionalPanelButtons(externalAdditionalPanelButtons: ToolbarAdditionalPanelButtonsDirective) {
		// This directive will has value only when ADDITIONAL PANEL BUTTONS component tagget with '*ovToolbarAdditionalPanelButtons' directive
		// is inside of the TOOLBAR component tagged with '*ovToolbar' directive
		if (externalAdditionalPanelButtons) {
			this.toolbarAdditionalPanelButtonsTemplate = externalAdditionalPanelButtons.template;
		}
	}

	/**
	 * Provides event notifications that fire when leave button has been clicked.
	 */
	@Output() onLeaveButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when add supervisor button has been clicked.
	 */
	@Output() onAddSupervisorButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when camera toolbar button has been clicked.
	 */
	@Output() onCameraButtonClicked: EventEmitter<void> = new EventEmitter<any>();
	/**
	 * Provides event notifications that fire when publish video toolbar button has been clicked.
	 */
	@Output() onPublishVideoButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when publish video toolbar button has been clicked.
	 */
	@Output() onVideoControlButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when microphone toolbar button has been clicked.
	 */
	@Output() onMicrophoneButtonClicked: EventEmitter<void> = new EventEmitter<any>();
	/**
	 * Provides event notifications that fire when screen microphone toolbar button has been clicked.
	 */
	@Output() onFullScreenClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when fullscreen toolbar button has been clicked.
	 */
	@Output() onFullscreenButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when screenshare toolbar button has been clicked.
	 */
	@Output() onScreenshareButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when participants panel button has been clicked.
	 */
	@Output() onParticipantsPanelButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when question panel button has been clicked.
	 */
	@Output() onQuestionPanelButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when chat panel button has been clicked.
	 */
	@Output() onChatPanelButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	/**
	 * Provides event notifications that fire when activities panel button has been clicked.
	 */
	@Output() onActivitiesPanelButtonClicked: EventEmitter<void> = new EventEmitter<any>();
	/**
	 * Provides event notifications that fire when start recording button has been clicked.
	 */
	@Output() onStartRecordingClicked: EventEmitter<void> = new EventEmitter<void>();

	/**
	 * Provides event notifications that fire when stop recording button has been clicked.
	 */
	@Output() onStopRecordingClicked: EventEmitter<void> = new EventEmitter<void>();
	/**
	 * @ignore
	 */
	@ViewChild(MatMenuTrigger) public menuTrigger: MatMenuTrigger;

	/**
	 * @ignore
	 */
	session: Session;
	/**
	 * @ignore
	 */
	unreadMessages: number = 0;
	/**
	 * @ignore
	 */
	messageList: ChatMessage[] = [];
	/**
	 * @ignore
	 */
	isScreenShareActive: boolean;
	/**
	 * @ignore
	 */
	isWebcamVideoActive: boolean;
	/**
	 * @ignore
	 */
	isAudioActive: boolean;
	/**
	 * @ignore
	 */
	isConnectionLost: boolean;
	/**
	 * @ignore
	 */
	hasVideoDevices: boolean;
	/**
	 * @ignore
	 */
	hasAudioDevices: boolean;
	/**
	 * @ignore
	 */
	isFullscreenActive: boolean = false;
	/**
	 * @ignore
	 */
	isPublishVideoActive: boolean = false;
	/**
	 * @ignore
	 */
	isVideoControlActive: boolean = false;
	/**
	 * @ignore
	 */
	isSupervisorActive: boolean = false;

	/**
	 * @ignore
	 */
	isChatOpened: boolean = false;
	/**
	 * @ignore
	 */
	isParticipantsOpened: boolean = false;

	/**
	 * @ignore
	 */
	isQuestionOpened: boolean = false;

	/**
	 * @ignore
	 */
	isActivitiesOpened: boolean = false;

	/**
	 * @ignore
	 */
	isMinimal: boolean = false;
	/**
	 * @ignore
	 */
	sessionName: string;
	/**
	 * @ignore
	 */
	showScreenshareButton: boolean = true;
	/**
	 * @ignore
	 */
	showPublishVideoButton: boolean = true;
	/**
	 * @ignore
	 */
	showVideoControlButton: boolean = false;
	/**
	 * @ignore
	 */
	showFullscreenButton: boolean;
	/**
	 * @ignore
	 */
	showBackgroundEffectsButton: boolean = true;

	/**
	 * @ignore
	 */
	showLeaveButton: boolean = true;

	/**
	 * @ignore
	 */
	showRecordingButton: boolean = true;

	/**
	 * @ignore
	 */
	showMuteCameraButton: boolean = true;

	/**
	 * @ignore
	 */
	showSettingsButton: boolean = true;

	/**
	 * @ignore
	 */
	showMoreOptionsButton: boolean = true;

	/**
	 * @ignore
	 */
	showParticipantsPanelButton: boolean = true;

	/**
	 * @ignore
	 */
	showQuestionPanelButton: boolean = true;
	/**
	 * @ignore
	 */
	showActivitiesPanelButton: boolean = true;
	/**
	 * @ignore
	 */
	showSupervisorButton: boolean = false;
	/**
	 * @ignore
	 */
	showWhenSupervisorButton: boolean = false;
	/**
	 * @ignore
	 */
	showChatPanelButton: boolean = true;
	/**
	 * @ignore
	 */
	showLogo: boolean = true;
	/**
	 * @ignore
	 */
	showLogoValue: string;
	/**
	 * @ignore
	 */
	showSessionName: boolean = true;
	/**
	 * @ignore
	 */
	showSessionTimer: boolean = true;
	/**
	 * @ignore
	 */
	sessionDuration: number;
	/**
	 * @ignore
	 */
	sessionTime: Date;

	/**
	 * @ignore
	 */
	showCaptionsButton: boolean = true;

	/**
	 * @ignore
	 */
	captionsEnabled: boolean;

	/**
	 * @ignore
	 */
	videoMuteChanging: boolean = false;

	/**
	 * @ignore
	 */
	recordingStatus: RecordingStatus = RecordingStatus.STOPPED;
	/**
	 * @ignore
	 */
	_recordingStatus = RecordingStatus;

	/**
	 * @ignore
	 */
	recordingTime: Date;

	/**
	 * @ignore
	 */
	isSessionCreator: boolean = false;

	/**
	 * @ignore
	 */
	screenSize: string;
	/**
	 * @ignore
	 */
	displayicdc: boolean = true;

	/**
	 * @ignore
	 */
	isicdc: boolean = true;

	private log: ILogger;
	private minimalSub: Subscription;
	private panelTogglingSubscription: Subscription;
	private chatMessagesSubscription: Subscription;
	private localParticipantSubscription: Subscription;
	private screenshareButtonSub: Subscription;
	private publishVideoButtonSub: Subscription;
	private videoControlButtonSub: Subscription;
	private fullscreenButtonSub: Subscription;
	private backgroundEffectsButtonSub: Subscription;
	private leaveButtonSub: Subscription;
	private recordingButtonSub: Subscription;
	private recordingSubscription: Subscription;
	private sessionTimerSubscription: Subscription;
	private activitiesPanelButtonSub: Subscription;
	private participantsPanelButtonSub: Subscription;
	private questionPanelButtonSub: Subscription;
	private icdcSub: Subscription;
	private displayicdcSub: Subscription;
	private chatPanelButtonSub: Subscription;
	private displayLogoSub: Subscription;
	private displayLogoValueSub: Subscription;
	private displaySessionNameSub: Subscription;
	private screenSizeSub: Subscription;
	private settingsButtonSub: Subscription;
	private captionsSubs: Subscription;
	private currentWindowHeight = window.innerHeight;
	private sessionNameSub: Subscription;
	private displayTimerSub: Subscription;
	private sessionDurationSub: Subscription;
	private supervisorButtonSub: Subscription;
	private supervisorWhenButtonSub: Subscription;
	private MuteCameraButtonSub: Subscription;
	private SupervisorSub: Subscription;
	/**
	 * @ignore
	 */
	constructor(
		protected documentService: DocumentService,
		protected chatService: ChatService,
		protected panelService: PanelService,
		protected participantService: ParticipantService,
		protected openviduService: OpenViduService,
		protected oVDevicesService: DeviceService,
		protected actionService: ActionService,
		protected loggerSrv: LoggerService,
		private layoutService: LayoutService,
		private cd: ChangeDetectorRef,
		private libService: OpenViduAngularConfigService,
		private platformService: PlatformService,
		private recordingService: RecordingService,
		private translateService: TranslateService,
		private storageSrv: StorageService,
		private cdkOverlayService: CdkOverlayService
	) {
		this.log = this.loggerSrv.get('ToolbarComponent');
	}
	/**
	 * @ignore
	 */
	@HostListener('window:resize', ['$event'])
	sizeChange(event) {
		if (this.currentWindowHeight >= window.innerHeight) {
			// The user has exit the fullscreen mode
			this.isFullscreenActive = false;
			this.currentWindowHeight = window.innerHeight;
		}
	}

	/**
	 * @ignore
	 */
	@HostListener('document:keydown', ['$event'])
	keyDown(event: KeyboardEvent) {
		if (event.key === 'F11') {
			event.preventDefault();
			this.toggleFullscreen();
			this.toggleScreenShare();
			this.currentWindowHeight = window.innerHeight;
			return false;
		}
	}

	async ngOnInit() {
		this.subscribeToToolbarDirectives();

		this.hasVideoDevices = this.oVDevicesService.hasVideoDeviceAvailable();
		this.hasAudioDevices = this.oVDevicesService.hasAudioDeviceAvailable();
		this.session = this.openviduService.getWebcamSession();

		this.subscribeToUserMediaProperties();
		this.subscribeToReconnection();
		this.subscribeToMenuToggling();
		this.subscribeToChatMessages();
		this.subscribeToRecordingStatus();
		this.subscribeToScreenSize();
		this.subscribeToCaptionsToggling();
		this.subscribeToSessionTimergStatus();
	}

	ngAfterViewInit() {
		// Sometimes the connection is undefined so we have to check the role when the mat menu is opened
		this.menuTrigger?.menuOpened.subscribe(() => {
			this.isSessionCreator = this.participantService.getMyRole() === OpenViduRole.MODERATOR;
		});
		this.subscribeToFullscreenChanged();
	}

	ngOnDestroy(): void {
		if (this.panelTogglingSubscription) this.panelTogglingSubscription.unsubscribe();
		if (this.chatMessagesSubscription) this.chatMessagesSubscription.unsubscribe();
		if (this.localParticipantSubscription) this.localParticipantSubscription.unsubscribe();
		if (this.screenshareButtonSub) this.screenshareButtonSub.unsubscribe();
		if (this.fullscreenButtonSub) this.fullscreenButtonSub.unsubscribe();
		if (this.backgroundEffectsButtonSub) this.backgroundEffectsButtonSub.unsubscribe();
		if (this.leaveButtonSub) this.leaveButtonSub.unsubscribe();
		if (this.recordingButtonSub) this.recordingButtonSub.unsubscribe();
		if (this.participantsPanelButtonSub) this.participantsPanelButtonSub.unsubscribe();
		if (this.questionPanelButtonSub) this.questionPanelButtonSub.unsubscribe();
		if (this.chatPanelButtonSub) this.chatPanelButtonSub.unsubscribe();
		if (this.displayLogoSub) this.displayLogoSub.unsubscribe();
		if (this.displayLogoValueSub) this.displayLogoValueSub.unsubscribe();
		if (this.displaySessionNameSub) this.displaySessionNameSub.unsubscribe();
		if (this.minimalSub) this.minimalSub.unsubscribe();
		if (this.activitiesPanelButtonSub) this.activitiesPanelButtonSub.unsubscribe();
		if (this.recordingSubscription) this.recordingSubscription.unsubscribe();
		if (this.sessionTimerSubscription) this.sessionTimerSubscription.unsubscribe();
		if (this.screenSizeSub) this.screenSizeSub.unsubscribe();
		if (this.settingsButtonSub) this.settingsButtonSub.unsubscribe();
		if (this.captionsSubs) this.captionsSubs.unsubscribe();
		document.removeEventListener('fullscreenchange', () => {
			this.isFullscreenActive = false;
			this.cdkOverlayService.setSelector('body');
		});
	}

	/**
	 * @ignore
	 */
	async toggleMicrophone() {
		this.onMicrophoneButtonClicked.emit();
		try {
			await this.openviduService.publishAudio(!this.isAudioActive);
		} catch (error) {
			this.log.e('There was an error toggling microphone:', error.code, error.message);
			this.actionService.openDialog(
				this.translateService.translate('ERRORS.TOGGLE_MICROPHONE'),
				error?.error || error?.message || error,
				true
			);
		}
	}

	/**
	 * @ignore
	 */
	async toggleCamera() {
		this.videoMuteChanging = true;
		this.onCameraButtonClicked.emit();
		try {
			const publishVideo = !this.participantService.isMyVideoActive();
			if (this.panelService.isExternalPanelOpened() && !publishVideo) {
				this.panelService.togglePanel(PanelType.BACKGROUND_EFFECTS);
			}
			await this.openviduService.publishVideo(publishVideo);
		} catch (error) {
			this.log.e('There was an error toggling camera:', error.code, error.message);
			this.actionService.openDialog(
				this.translateService.translate('ERRORS.TOGGLE_CAMERA'),
				error?.error || error?.message || error,
				true
			);
		}
		this.videoMuteChanging = false;
	}

	/**
	 * @ignore
	 */
	async toggleScreenShare() {
		this.onScreenshareButtonClicked.emit();

		try {
			await this.openviduService.toggleScreenshare(this.isScreenShareActive);
		} catch (error) {
			this.log.e('There was an error toggling screen share', error.code, error.message);
			if (error && error.name === 'SCREEN_SHARING_NOT_SUPPORTED') {
				this.actionService.openDialog(
					this.translateService.translate('ERRORS.SCREEN_SHARING'),
					this.translateService.translate('ERRORS.SCREEN_SUPPORT'),
					true
				);
			}
		}
	}

	/**
	 * @ignore
	 */
	leaveSession() {
		this.log.d('Leaving session...');
		this.openviduService.closeQuestionpanel();
		this.openviduService.disconnect();
		this.openviduService.stopTune();
		this.onLeaveButtonClicked.emit();
	}

	/**
	 * @ignore
	 */
	toggleRecording() {
		if (this.recordingStatus === RecordingStatus.STARTED) {
			this.log.d('Stopping recording');
			this.onStopRecordingClicked.emit();
			this.recordingService.updateStatus(RecordingStatus.STOPPING);
		} else if (this.recordingStatus === RecordingStatus.STOPPED) {
			this.onStartRecordingClicked.emit();
			this.recordingService.updateStatus(RecordingStatus.STARTING);

			if (this.showActivitiesPanelButton && !this.isActivitiesOpened) {
				this.toggleActivitiesPanel('recording');
			}
		}
	}

	/**
	 * @ignore
	 */
	toggleBackgroundEffects() {
		if (this.openviduService.isOpenViduPro()) {
			this.panelService.togglePanel(PanelType.BACKGROUND_EFFECTS);
		} else {
			this.actionService.openProFeatureDialog(
				this.translateService.translate('PANEL.BACKGROUND.TITLE'),
				this.translateService.translate('PANEL.PRO_FEATURE')
			);
		}
	}

	/**
	 * @ignore
	 */
	toggleCaptions() {
		if (this.openviduService.isOpenViduPro()) {
			this.layoutService.toggleCaptions();
		} else {
			this.actionService.openProFeatureDialog(
				this.translateService.translate('PANEL.SETTINGS.CAPTIONS'),
				this.translateService.translate('PANEL.PRO_FEATURE')
			);
		}
	}

	/**
	 * @ignore
	 */
	toggleSettings() {
		this.panelService.togglePanel(PanelType.SETTINGS);
	}

	/**
	 * @ignore
	 */
	toggleParticipantsPanel() {
		this.onParticipantsPanelButtonClicked.emit();
		this.panelService.togglePanel(PanelType.PARTICIPANTS);
	}

	/**
	 * @ignore
	 */
	toggleQuestionPanel() {
		this.onQuestionPanelButtonClicked.emit();
		if (this.displayicdc) {
			this.panelService.togglePanel(PanelType.QUESTIONS);
		}

		
	}

	/**
	 * @ignore
	 */
	toggleChatPanel() {
		this.onChatPanelButtonClicked.emit();
		this.panelService.togglePanel(PanelType.CHAT);
	}
	/**
	 * @ignore
	 */
	async togglePublishVideo() {
		this.onPublishVideoButtonClicked.emit();
		try {
			this.showVideoControlButton = !this.showVideoControlButton;
			console.log('Publishing video');
			await this.openviduService.publishRecordedVideo();
			this.libService.screenshareButton.next(this.isPublishVideoActive);
			this.isPublishVideoActive = !this.isPublishVideoActive;
		} catch (error) {
			this.showVideoControlButton = !this.showVideoControlButton;
			this.log.e('There was an error toggling Publish video:', error.code, error.message);
			this.actionService.openDialog(
				this.translateService.translate('ERRORS.TOGGLE_PLAYVIDEO'),
				error?.error || error?.message || error,
				true
			);
		}
	}

	/**
	 * @ignore
	 */
	toggleFullscreen() {
		this.log.d('Someone has shared screen', this.participantService.someoneIsSharingScreen());
		if (this.participantService.someoneIsSharingScreen()) {
			this.openviduService.toggleShareFullscreen();
		} else {
			this.documentService.toggleFullscreen('session-container');
		}
		this.onFullscreenButtonClicked.emit();
	}
	/**
	 * @ignore
	 */
	toggleVideoPlay() {
		this.onVideoControlButtonClicked.emit();
		try {
			if (!this.isVideoControlActive) {
				console.log('Playig video');
				this.openviduService.playVideo();
				this.isVideoControlActive = !this.isVideoControlActive;
			} else {
				console.log('Pausing video');
				this.openviduService.pauseVideo();
				this.isVideoControlActive = !this.isVideoControlActive;
			}
		} catch (error) {
			this.log.e('There was an error toggling Publish video:', error.code, error.message);
			this.actionService.openDialog(
				this.translateService.translate('ERRORS.TOGGLE_PLAYVIDEO'),
				error?.error || error?.message || error,
				true
			);
		}
	}
	/**
	 * @ignore
	 */
	toggleSupervisor() {
		try {
			this.isSupervisorActive = this.libService.isSupervisorActive.getValue();
			this.log.d('Sending signal for supervisor', this.isSupervisorActive);
			if (!this.isSupervisorActive) {
				this.onAddSupervisorButtonClicked.emit();
				this.openviduService
					.getRemoteConnections()
					.filter((connection) => this.openviduService.holdPartiticipantSiganl(connection.connectionId));
				const remoteParticipants = this.participantService.getRemoteParticipants();
				const participantToUpdate = remoteParticipants.find((participant) => participant.nickname === 'Customer');
				if (participantToUpdate) {
					participantToUpdate.isOnHold = true;
					this.participantService.updateRemoteParticipantsByModel(participantToUpdate);
				}
				console.log('update partiticpant', participantToUpdate);
				this.libService.isOnHold.next(true);
			} else if (this.isSupervisorActive) {
				this.openviduService
					.getRemoteConnections()
					.filter((connection) => this.openviduService.unholdPartiticipantSignal(connection.connectionId));
				this.libService.isOnHold.next(false);
				this.libService.supervisorWhenButton.next(false);
				const remoteParticipants = this.participantService.getRemoteParticipants();
				const participantToUpdate = remoteParticipants.find((participant) => participant.nickname === 'Customer');
				if (participantToUpdate) {
					participantToUpdate.isOnHold = false;
					this.participantService.updateRemoteParticipantsByModel(participantToUpdate);
				}
				console.log('update partiticpant', participantToUpdate);
			}
		} catch (error) {
			this.log.d('Getting error while adding supervisor', error.message);
		}
	}

	private toggleActivitiesPanel(expandPanel: string) {
		this.onActivitiesPanelButtonClicked.emit();
		this.panelService.togglePanel(PanelType.ACTIVITIES, expandPanel);
	}

	protected subscribeToReconnection() {
		this.session.on('reconnecting', () => {
			if (this.panelService.isPanelOpened()) {
				this.panelService.closePanel();
			}
			this.isConnectionLost = true;
		});
		this.session.on('reconnected', () => {
			this.isConnectionLost = false;
		});
	}

	private subscribeToFullscreenChanged() {
		document.addEventListener('fullscreenchange', (event) => {
			this.isFullscreenActive = Boolean(document.fullscreenElement);
			this.cdkOverlayService.setSelector(this.isFullscreenActive ? '#session-container' : 'body');
		});
	}

	protected subscribeToMenuToggling() {
		this.panelTogglingSubscription = this.panelService.panelOpenedObs.subscribe((ev: PanelEvent) => {
			this.isChatOpened = ev.opened && ev.type === PanelType.CHAT;
			this.isParticipantsOpened = ev.opened && ev.type === PanelType.PARTICIPANTS;
			this.isActivitiesOpened = ev.opened && ev.type === PanelType.ACTIVITIES;
			if (this.isChatOpened) {
				this.unreadMessages = 0;
			}
			this.cd.markForCheck();
		});
	}

	protected subscribeToChatMessages() {
		this.chatMessagesSubscription = this.chatService.messagesObs.pipe(skip(1)).subscribe((messages) => {
			if (!this.panelService.isChatPanelOpened()) {
				this.unreadMessages++;
			}
			this.messageList = messages;
			this.cd.markForCheck();
		});
	}
	protected subscribeToUserMediaProperties() {
		this.localParticipantSubscription = this.participantService.localParticipantObs.subscribe((p: ParticipantAbstractModel) => {
			if (p) {
				this.isWebcamVideoActive = p.isCameraVideoActive();
				this.isAudioActive = p.hasAudioActive();
				this.isScreenShareActive = p.isScreenActive();
				this.isSessionCreator = p.getRole() === OpenViduRole.MODERATOR;
				this.storageSrv.setAudioMuted(!this.isAudioActive);
				this.storageSrv.setVideoMuted(!this.isWebcamVideoActive);
				this.cd.markForCheck();
			}
		});
	}

	private subscribeToRecordingStatus() {
		this.recordingSubscription = this.recordingService.recordingStatusObs
			.pipe(skip(1))
			.subscribe((ev: { info: RecordingInfo; time?: Date }) => {
				this.recordingStatus = ev.info.status;
				if (ev.time) {
					this.recordingTime = ev.time;
				}
				this.cd.markForCheck();
			});
	}

	private subscribeToSessionTimergStatus() {
		if (this.showSessionTimer) {
			this.sessionTimerSubscription = this.openviduService.sessionTimerObs.pipe(skip(1)).subscribe((ev: { time?: Date }) => {
				if (ev.time) {
					this.sessionTime = ev.time;
				}
				this.cd.markForCheck();
			});
		}
	}

	private subscribeToToolbarDirectives() {
		this.minimalSub = this.libService.minimalObs.subscribe((value: boolean) => {
			this.isMinimal = value;
			this.cd.markForCheck();
		});
		this.screenshareButtonSub = this.libService.screenshareButtonObs.subscribe((value: boolean) => {
			this.showScreenshareButton = value && !this.platformService.isMobile();
			this.cd.markForCheck();
		});
		this.publishVideoButtonSub = this.libService.publishVideoeButtonObs.subscribe((value: boolean) => {
			this.showPublishVideoButton = value;
			this.cd.markForCheck();
		});
		this.videoControlButtonSub = this.libService.videoControlButtonObs.subscribe((value: boolean) => {
			this.showVideoControlButton = value;
			this.cd.markForCheck();
		});
		this.fullscreenButtonSub = this.libService.fullscreenButtonObs.subscribe((value: boolean) => {
			this.showFullscreenButton = value;
			this.checkDisplayMoreOptions();
			this.cd.markForCheck();
		});
		this.leaveButtonSub = this.libService.leaveButtonObs.subscribe((value: boolean) => {
			this.showLeaveButton = value;
			this.cd.markForCheck();
		});

		this.recordingButtonSub = this.libService.recordingButtonObs.subscribe((value: boolean) => {
			this.showRecordingButton = value;
			this.checkDisplayMoreOptions();
			this.cd.markForCheck();
		});

		this.settingsButtonSub = this.libService.toolbarSettingsButtonObs.subscribe((value: boolean) => {
			this.showSettingsButton = value;
			this.checkDisplayMoreOptions();
			this.cd.markForCheck();
		});
		this.chatPanelButtonSub = this.libService.chatPanelButtonObs.subscribe((value: boolean) => {
			this.showChatPanelButton = value;
			this.cd.markForCheck();
		});
		this.questionPanelButtonSub = this.libService.questionPanelButtonObs.subscribe((value: boolean) => {
			this.showQuestionPanelButton = value;
			this.cd.markForCheck();
		});
		this.participantsPanelButtonSub = this.libService.participantsPanelButtonObs.subscribe((value: boolean) => {
			this.showParticipantsPanelButton = value;
			this.cd.markForCheck();
		});
		this.activitiesPanelButtonSub = this.libService.activitiesPanelButtonObs.subscribe((value: boolean) => {
			this.showActivitiesPanelButton = value;
			this.cd.markForCheck();
		});
		this.backgroundEffectsButtonSub = this.libService.backgroundEffectsButton.subscribe((value: boolean) => {
			this.showBackgroundEffectsButton = value;
			this.checkDisplayMoreOptions();
			this.cd.markForCheck();
		});
		this.displayLogoSub = this.libService.displayLogoObs.subscribe((value: boolean) => {
			this.showLogo = value;
			this.cd.markForCheck();
		});
		this.displayLogoValueSub = this.libService.displayLogoValueObs.subscribe((value: string) => {
			this.showLogoValue = value;
			this.cd.markForCheck();
		});
		this.displaySessionNameSub = this.libService.displaySessionNameObs.subscribe((value: boolean) => {
			this.showSessionName = value;
			this.cd.markForCheck();
		});
		this.captionsSubs = this.libService.captionsButtonObs.subscribe((value: boolean) => {
			this.showCaptionsButton = value;
			this.cd.markForCheck();
		});
		this.sessionNameSub = this.libService.sessionNameObs.subscribe((value: string) => {
			this.sessionName = value;
			this.cd.markForCheck();
		});
		this.displayTimerSub = this.libService.displayTimerObs.subscribe((value: boolean) => {
			this.showSessionTimer = value;
			this.cd.markForCheck();
		});
		this.displayicdcSub = this.libService.displayicdc.subscribe((displayicdc: boolean) => {
			this.displayicdc = displayicdc;
		});
		this.sessionDurationSub = this.libService.sessionDurationObs.subscribe((value: number) => {
			this.sessionDuration = value;
			this.cd.markForCheck();
		});
		this.supervisorButtonSub = this.libService.supervisorButtonObs.subscribe((value: boolean) => {
			this.showSupervisorButton = value;
			this.cd.markForCheck();
		});
		this.supervisorWhenButtonSub = this.libService.supervisorWhenButtonObs.subscribe((value: boolean) => {
			this.showWhenSupervisorButton = value;
			this.cd.markForCheck();
		});
		this.MuteCameraButtonSub = this.libService.MuteCameraButtonObs.subscribe((value: boolean) => {
			this.showMuteCameraButton = value;
			this.cd.markForCheck();
		});
		this.SupervisorSub = this.libService.isSupervisorActiveObs.subscribe((value: boolean) => {
			this.isSupervisorActive = value;
			this.cd.markForCheck();
		});
	}

	private subscribeToScreenSize() {
		this.screenSizeSub = this.documentService.screenSizeObs.subscribe((change: MediaChange[]) => {
			this.screenSize = change[0].mqAlias;
			this.cd.markForCheck();
		});
	}

	private subscribeToCaptionsToggling() {
		this.captionsSubs = this.layoutService.captionsTogglingObs.subscribe((value: boolean) => {
			this.captionsEnabled = value;
			this.cd.markForCheck();
		});
	}

	private checkDisplayMoreOptions() {
		this.showMoreOptionsButton =
			this.showFullscreenButton || this.showBackgroundEffectsButton || this.showRecordingButton || this.showSettingsButton;
	}
}
