import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	ContentChild,
	ElementRef,
	EventEmitter,
	HostListener,
	Input,
	OnDestroy,
	OnInit,
	Output,
	TemplateRef,
	ViewChild
} from '@angular/core';
import {
	ConnectionEvent,
	ExceptionEvent,
	ExceptionEventName,
	RecordingEvent,
	Session,
	SessionDisconnectedEvent,
	StreamEvent,
	StreamPropertyChangedEvent,
	Subscriber
} from 'openvidu-browser';

import { ILogger } from '../../models/logger.model';
import { VideoType } from '../../models/video-type.model';

import { animate, style, transition, trigger } from '@angular/animations';
import { MatDrawerContainer, MatSidenav } from '@angular/material/sidenav';
import { skip, Subscription } from 'rxjs';
import { SidenavMode } from '../../models/layout.model';
import { PanelType } from '../../models/panel.model';
import { Signal } from '../../models/signal.model';
import { ActionService } from '../../services/action/action.service';
import { CaptionService } from '../../services/caption/caption.service';
import { ChatService } from '../../services/chat/chat.service';
import { OpenViduAngularConfigService } from '../../services/config/openvidu-angular.config.service';
import { LayoutService } from '../../services/layout/layout.service';
import { LoggerService } from '../../services/logger/logger.service';
import { OpenViduService } from '../../services/openvidu/openvidu.service';
import { PanelEvent, PanelService } from '../../services/panel/panel.service';
import { ParticipantService } from '../../services/participant/participant.service';
import { PlatformService } from '../../services/platform/platform.service';
import { RecordingService } from '../../services/recording/recording.service';
import { TranslateService } from '../../services/translate/translate.service';
import { VirtualBackgroundService } from '../../services/virtual-background/virtual-background.service';

/**
 * @internal
 */

@Component({
	selector: 'ov-session',
	templateUrl: './session.component.html',
	styleUrls: ['./session.component.css'],
	animations: [trigger('sessionAnimation', [transition(':enter', [style({ opacity: 0 }), animate('50ms', style({ opacity: 1 }))])])],
	changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionComponent implements OnInit, OnDestroy {
	@ContentChild('toolbar', { read: TemplateRef }) toolbarTemplate: TemplateRef<any>;
	@ContentChild('header', { read: TemplateRef }) headerTemplate: TemplateRef<any>;
	@ContentChild('panel', { read: TemplateRef }) panelTemplate: TemplateRef<any>;
	@ContentChild('layout', { read: TemplateRef }) layoutTemplate: TemplateRef<any>;

	@Input() usedInPrejoinPage = false;
	@Output() onSessionCreated = new EventEmitter<any>();

	@Output() onNodeCrashed = new EventEmitter<any>();
	/**
	 * Provides event notifications that fire when hold button has been clicked.
	 */
	@Output() onHoldButtonClicked: EventEmitter<void> = new EventEmitter<void>();
	/**
	 * Provides event notifications that fire when hold button has been clicked.
	 */
	@Output() onUnHoldButtonClicked: EventEmitter<void> = new EventEmitter<void>();

	/**
	 * Provides event notifications that fire when leave button has been clicked.
	 */
	@Output() onLeaveButtonClicked: EventEmitter<void> = new EventEmitter<any>();

	session: Session;
	sessionScreen: Session;

	sideMenu: MatSidenav;

	sidenavMode: SidenavMode = SidenavMode.SIDE;
	settingsPanelOpened: boolean;
	drawer: MatDrawerContainer;
	preparing: boolean = true;
	displayTickerValue: string;
	displayTicker: boolean = true;

	protected readonly SIDENAV_WIDTH_LIMIT_MODE = 790;

	protected menuSubscription: Subscription;
	protected layoutWidthSubscription: Subscription;

	protected updateLayoutInterval: NodeJS.Timer;
	// private sttReconnectionInterval: NodeJS.Timer;
	private captionLanguageSubscription: Subscription;
	private displayTickerValueSub: Subscription;
	private displayTickerSub: Subscription;

	protected log: ILogger;

	constructor(
		protected actionService: ActionService,
		protected openviduService: OpenViduService,
		protected participantService: ParticipantService,
		protected loggerSrv: LoggerService,
		protected chatService: ChatService,
		private libService: OpenViduAngularConfigService,
		protected layoutService: LayoutService,
		protected panelService: PanelService,
		private recordingService: RecordingService,
		private translateService: TranslateService,
		private captionService: CaptionService,
		private platformService: PlatformService,
		private backgroundService: VirtualBackgroundService,
		private cd: ChangeDetectorRef
	) {
		this.log = this.loggerSrv.get('SessionComponent');
	}

	@HostListener('window:beforeunload')
	beforeunloadHandler() {
		this.leaveSession();
	}

	@HostListener('window:resize')
	sizeChange() {
		this.layoutService.update();
	}

	@ViewChild('sidenav')
	set sidenavMenu(menu: MatSidenav) {
		setTimeout(() => {
			if (menu) {
				this.sideMenu = menu;
				this.subscribeToTogglingMenu();
			}
		}, 0);
	}

	@ViewChild('videoContainer', { static: false, read: ElementRef })
	set videoContainer(container: ElementRef) {
		setTimeout(() => {
			if (container && !this.toolbarTemplate) {
				container.nativeElement.style.height = '100%';
				container.nativeElement.style.minHeight = '100%';
				this.layoutService.update();
			}
		}, 0);
	}

	@ViewChild('container')
	set container(container: MatDrawerContainer) {
		setTimeout(() => {
			if (container) {
				this.drawer = container;
				this.drawer._contentMarginChanges.subscribe(() => {
					setTimeout(() => {
						this.stopUpdateLayoutInterval();
						this.layoutService.update();
						this.drawer.autosize = false;
					}, 250);
				});
			}
		}, 0);
	}

	@ViewChild('layoutContainer')
	set layoutContainer(container: ElementRef) {
		setTimeout(async () => {
			if (container) {
				// Apply background from storage when layout container is in DOM
				await this.backgroundService.applyBackgroundFromStorage();
			}
		}, 0);
	}

	async ngOnInit() {
		if (!this.usedInPrejoinPage) {
			if (!this.openviduService.getScreenToken()) {
				// Hide screenshare button if screen token does not exist
				this.libService.screenshareButton.next(false);
			}
			this.session = this.openviduService.getWebcamSession();
			this.sessionScreen = this.openviduService.getScreenSession();
			this.subscribeToOpenViduException();
			this.subscribeToCaptionLanguage();
			this.subscribeToConnectionCreatedAndDestroyed();
			this.subscribeToStreamCreated();
			this.subscribeToStreamDestroyed();
			this.subscribeToStreamPropertyChange();
			this.subscribeToToggleQuestionPanel();
			this.subscribeToNicknameChanged();
			this.chatService.subscribeToChat();
			this.subscribeToReconnection();
			this.subscribeToDisplayTrickerDirectives();
			this.subscribeToHold();
			this.subscribeToLeave();
			this.subscribeToUnhold();
			const recordingEnabled = this.libService.recordingButton.getValue() && this.libService.recordingActivity.getValue();
			if (recordingEnabled) {
				this.subscribeToRecordingEvents();
			}
			if (this.participantService.isMyCameraActive()) {
				console.log('turn on camera');
				this.libService.videoMuted.next(true);
			}
			this.onSessionCreated.emit(this.session);

			await this.connectToSession();
			// ios devices appear with blank video. Muting and unmuting it fix this problem
			if (this.platformService.isIos() && this.participantService.isMyCameraActive()) {
				await this.openviduService.publishVideo(false);
				await this.openviduService.publishVideo(true);
			}
		}
		this.preparing = false;
		this.cd.markForCheck();
	}

	ngOnDestroy() {
		// Reconnecting session is received in Firefox
		// To avoid 'Connection lost' message uses session.off()
		this.session?.off('reconnecting');
		this.participantService.clear();
		this.session = null;
		this.sessionScreen = null;
		if (this.menuSubscription) this.menuSubscription.unsubscribe();
		if (this.layoutWidthSubscription) this.layoutWidthSubscription.unsubscribe();
	}

	leaveSession() {
		this.log.d('Leaving session...');
		this.openviduService.disconnect();
		this.openviduService.stopTune();
	}

	protected subscribeToTogglingMenu() {
		this.sideMenu.openedChange.subscribe(() => {
			this.stopUpdateLayoutInterval();
			this.layoutService.update();
		});

		this.sideMenu.openedStart.subscribe(() => {
			this.startUpdateLayoutInterval();
		});

		this.sideMenu.closedStart.subscribe(() => {
			this.startUpdateLayoutInterval();
		});

		this.menuSubscription = this.panelService.panelOpenedObs.pipe(skip(1)).subscribe((ev: PanelEvent) => {
			if (this.sideMenu) {
				this.settingsPanelOpened = ev.opened && ev.type === PanelType.SETTINGS;

				if (this.sideMenu.opened && ev.opened) {
					if (ev.type === PanelType.SETTINGS || ev.oldType === PanelType.SETTINGS) {
						// Switch from SETTINGS to another panel and vice versa.
						// As the SETTINGS panel will be bigger than others, the sidenav container must be updated.
						// Setting autosize to 'true' allows update it.
						this.drawer.autosize = true;
						this.startUpdateLayoutInterval();
					}
				}
				ev.opened ? this.sideMenu.open() : this.sideMenu.close();
			}
		});
	}

	protected subscribeToLayoutWidth() {
		this.layoutWidthSubscription = this.layoutService.layoutWidthObs.subscribe((width) => {
			this.sidenavMode = width <= this.SIDENAV_WIDTH_LIMIT_MODE ? SidenavMode.OVER : SidenavMode.SIDE;
		});
	}

	private async connectToSession(): Promise<void> {
		try {
			const webcamToken = this.openviduService.getWebcamToken();
			const screenToken = this.openviduService.getScreenToken();

			if (this.participantService.haveICameraAndScreenActive()) {
				await this.openviduService.connectSession(this.openviduService.getWebcamSession(), webcamToken);
				await this.openviduService.connectSession(this.openviduService.getScreenSession(), screenToken);
				await this.openviduService.publish(this.participantService.getMyCameraPublisher());
				await this.openviduService.publish(this.participantService.getMyScreenPublisher());
			} else if (this.participantService.isOnlyMyScreenActive()) {
				await this.openviduService.connectSession(this.openviduService.getScreenSession(), screenToken);
				await this.openviduService.publish(this.participantService.getMyScreenPublisher());
			} else {
				await this.openviduService.connectSession(this.openviduService.getWebcamSession(), webcamToken);
				await this.openviduService.publish(this.participantService.getMyCameraPublisher());
			}
		} catch (error) {
			// this._error.emit({ error: error.error, messgae: error.message, code: error.code, status: error.status });
			this.log.e('There was an error connecting to the session:', error.code, error.message);
			this.actionService.openDialog(this.translateService.translate('ERRORS.SESSION'), error?.error || error?.message || error, true);
		}
	}

	private subscribeToOpenViduException() {
		this.session.on('exception', async (event: ExceptionEvent) => {
			// if (event.name === ExceptionEventName.SPEECH_TO_TEXT_DISCONNECTED) {
			// 	this.log.w(event.name, event.message);
			// 	this.openviduService.setSTTReady(false);
			// 	// Try to re-subscribe to STT
			// 	await this.openviduService.subscribeRemotesToSTT(this.captionService.getLangSelected().ISO);
			// } else {
			// 	this.log.e(event.name, event.message);
			// }
		});
	}

	private subscribeToConnectionCreatedAndDestroyed() {
		this.session.on('connectionCreated', (event: ConnectionEvent) => {
			const connectionId = event.connection?.connectionId;
			const nickname: string = this.participantService.getNicknameFromConnectionData(event.connection.data);
			const isRemoteConnection: boolean = !this.openviduService.isMyOwnConnection(connectionId);
			const isCameraConnection: boolean = !nickname?.includes(`_${VideoType.SCREEN}`);
			const data = event.connection?.data;
			if (isRemoteConnection && isCameraConnection) {
				// Adding participant when connection is created and it's not screen
				if (this.participantService.getMyNickname() == 'Support') {
					if (this.libService.isSupervisorActive.getValue()) {
						this.libService.supervisorWhenButton.next(false);
					} else {
						this.libService.supervisorWhenButton.next(true);
					}
				}
				try {
					if (JSON.parse(data.split('%/%')[0]).clientData == 'Supervisor') {
						this.libService.isSupervisorActive.next(true);
					}
				} catch (error) {
					console.log('This user is not supervisor');
				}
				if (this.participantService.getMyNickname() == 'Supervisor') {
					const isOnHoldConnection = this.openviduService
						.getRemoteConnections()
						.find((connection) => JSON.parse(connection.data.split('%/%')[0]).clientData == 'Customer');
					this.libService.isOnHold.next(isOnHoldConnection.stream.audioActive && isOnHoldConnection.stream.videoActive);
				}
				if (!this.libService.isOnHold.getValue()) {
					this.participantService.addRemoteConnection(connectionId, data, null);
				}

				//Sending nicnkanme signal to new participants
				if (this.openviduService.needSendNicknameSignal()) {
					const data = { clientData: this.participantService.getMyNickname() };
					this.openviduService.sendSignal(Signal.NICKNAME_CHANGED, [event.connection], data);
				}
			}
			if (this.participantService.getMyNickname() == 'Supervisor') {
				const stream = this.openviduService
					.getRemoteConnections()
					.find((con) => JSON.parse(con.data.split('%/%')[0]).clientData == 'Customer')?.stream;
				if (!stream?.videoActive || !stream?.audioActive) {
					const remoteParticipants = this.participantService.getRemoteParticipants();
					const participantToUpdate = remoteParticipants.find((participant) => participant.nickname === 'Customer');
					if (participantToUpdate) {
						participantToUpdate.isOnHold = true;
						this.participantService.updateRemoteParticipantsByModel(participantToUpdate);
					}
				}
			}
			if (this.participantService.getMyNickname() == 'Support' && JSON.parse(data.split('%/%')[0]).clientData == 'Customer') {
				if (JSON.parse(data.split('%/%')[1]).isOnHold) {
					this.openviduService
						.getRemoteConnections()
						.filter((connection) => this.openviduService.holdPartiticipantSiganl(connection.connectionId));
					const remoteParticipants = this.participantService.getRemoteParticipants();
					const participantToUpdate = remoteParticipants.find((participant) => participant.nickname === 'Customer');
					if (participantToUpdate) {
						participantToUpdate.isOnHold = true;
						this.participantService.updateRemoteParticipantsByModel(participantToUpdate);
					}
					this.libService.isOnHold.next(true);
				}
			}
		});

		this.session.on('connectionDestroyed', (event: ConnectionEvent) => {
			const nickname: string = this.participantService.getNicknameFromConnectionData(event.connection.data);
			const isRemoteConnection: boolean = !this.openviduService.isMyOwnConnection(event.connection.connectionId);
			const isCameraConnection: boolean = !nickname?.includes(`_${VideoType.SCREEN}`);
			// Deleting participant when connection is destroyed
			this.participantService.removeConnectionByConnectionId(event.connection.connectionId);
			if (JSON.parse(event.connection.data.split('%/%')[0]).clientData == 'Supervisor') {
				this.libService.isSupervisorActive.next(false);
			}
			if (JSON.parse(event.connection.data.split('%/%')[0]).clientData != 'Supervisor') {
				this.libService.isOnHold.next(false);
			}
			if (
				this.participantService.getMyNickname() == 'Customer' &&
				JSON.parse(event.connection.data.split('%/%')[0]).clientData == 'Support'
			) {
				this.openviduService.stopTune();
			}
		});
	}

	private subscribeToStreamCreated() {
		this.session.on('streamCreated', async (event: StreamEvent) => {
			const connectionId = event.stream?.connection?.connectionId;
			const nickname: string = this.participantService.getNicknameFromConnectionData(event.stream.connection.data);
			const data = event.stream?.connection?.data;
			if (
				this.participantService.getTypeConnectionData(data) === VideoType.SCREEN &&
				!this.openviduService.isMyOwnConnection(connectionId)
			) {
				this.openviduService.toggleShareFullscreen();
			}
			const isCameraType: boolean = this.participantService.getTypeConnectionData(data) === VideoType.CAMERA;
			const isRemoteConnection: boolean = !this.openviduService.isMyOwnConnection(connectionId);
			const lang = this.captionService.getLangSelected().ISO;
			const localNickname = this.participantService.getMyNickname();
			/*  
			//this comment for solve double time click on hold button before merge the call
			if (nickname == 'Supervisor' && localNickname != 'Customer' ) {
			 	this.libService.isOnHold.next(false);
			 }
			 */

			if (isRemoteConnection && (!this.libService.isOnHold.getValue() || localNickname == 'Support')) {
				const subscriber: Subscriber = this.session.subscribe(event.stream, undefined);
				this.participantService.addRemoteConnection(connectionId, data, subscriber);
				// this.oVSessionService.sendNicknameSignal(event.stream.connection);

				if (this.openviduService.isSttReady() && this.captionService.areCaptionsEnabled() && isCameraType) {
					// Only subscribe to STT when is ready and stream is CAMERA type and it is a remote stream
					try {
						await this.openviduService.subscribeStreamToStt(event.stream, lang);
					} catch (error) {
						this.log.e('Error subscribing from STT: ', error);
						// I assume the only reason of an STT error is a STT crash.
						// It must be subscribed to all remotes again
						// await this.openviduService.unsubscribeRemotesFromSTT();
						await this.openviduService.subscribeRemotesToSTT(lang);
					}
				}
			}
		});
	}

	private subscribeToStreamDestroyed() {
		this.session.on('streamDestroyed', async (event: StreamEvent) => {
			const connectionId = event.stream.connection.connectionId;
			const data = event.stream?.connection?.data;
			if (this.participantService.getTypeConnectionData(data) === VideoType.SCREEN) {
				this.openviduService.sendSignal(Signal.SCREEN, undefined, 'disable');
			}
			const isRemoteConnection: boolean = !this.openviduService.isMyOwnConnection(connectionId);
			const isCameraType: boolean = this.participantService.getTypeConnectionData(data) === VideoType.CAMERA;

			this.participantService.removeConnectionByConnectionId(connectionId);
			if (this.openviduService.isSttReady() && this.captionService.areCaptionsEnabled() && isRemoteConnection && isCameraType) {
				try {
					// await this.session.unsubscribeFromSpeechToText(event.stream);
				} catch (error) {
					this.log.e('Error unsubscribing from STT: ', error);
				}
			}
			if (this.participantService.getMyNickname() == 'Support' && JSON.parse(data.split('%/%')[0]).clientData == 'Customer') {
				this.libService.supervisorWhenButton.next(false);
			}
			if (this.participantService.getMyNickname() == 'Support' && JSON.parse(data.split('%/%')[0]).clientData == 'Supervisor') {
				this.libService.supervisorWhenButton.next(true);
			}
		});
	}

	private subscribeToCaptionLanguage() {
		this.captionLanguageSubscription = this.captionService.captionLangObs.subscribe(async (lang) => {
			if (this.captionService.areCaptionsEnabled()) {
				// Unsubscribe all streams from speech to text and re-subscribe with new language
				this.log.d('Re-subscribe from STT because of language changed to ', lang.ISO);
				await this.openviduService.unsubscribeRemotesFromSTT();
				await this.openviduService.subscribeRemotesToSTT(lang.ISO);
			}
		});
	}

	private subscribeToStreamPropertyChange() {
		this.session.on('streamPropertyChanged', (event: StreamPropertyChangedEvent) => {
			const connectionId = event.stream.connection.connectionId;
			const isRemoteConnection: boolean = !this.openviduService.isMyOwnConnection(connectionId);
			const remoteParticipants = this.participantService.getRemoteParticipants();
			const participantToUpdate = remoteParticipants.find((participant) => participant.nickname === 'Support');

			if (isRemoteConnection && !participantToUpdate.isOnHold) {
				this.participantService.updateRemoteParticipants();
			}
		});
	}

	private subscribeToToggleQuestionPanel() {
		//signal for Open Question Panel
		this.session.on(`signal:${Signal.QUESTION}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection: boolean = this.openviduService.isMyOwnConnection(connectionId);
			if (!isMyOwnConnection) {
				this.panelService.togglePanel(PanelType.QUESTIONS);
				this.openviduService.sendSignal(Signal.FORMINDEX, undefined, data);
			}
		});

		//signal for: Close Question Panel if anyone leave meeting
		this.session.on(`signal:${Signal.CLOSEQUESTIONPANEL}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection: boolean = this.openviduService.isMyOwnConnection(connectionId);
			if (!isMyOwnConnection) {
				if (this.panelService.isQuestionPanelOpened()) this.panelService.togglePanel(PanelType.QUESTIONS);
			}
		});
	}

	private subscribeToNicknameChanged() {
		this.session.on(`signal:${Signal.NICKNAME_CHANGED}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const isRemoteConnection: boolean = !this.openviduService.isMyOwnConnection(connectionId);

			if (isRemoteConnection) {
				const nickname = this.participantService.getNicknameFromConnectionData(event.data);
				this.participantService.setRemoteNickname(connectionId, nickname);
				if (JSON.parse(event.data).clientData == 'Supervisor') {
					this.libService.isSupervisorActive.next(true);
				}
			}
		});
	}
	private subscribeToLeave() {
		this.session.on(`signal:${Signal.LEAVEMEETING}`, async (event: any) => {
			this.log.d('Supervisor recieved Leave Meeting signal');
			if (this.participantService.getMyNickname() == 'Supervisor') {
				this.leaveSession();
				this.onLeaveButtonClicked.emit();
			}
		});
	}

	private subscribeToHold() {
		this.session.on(`signal:${Signal.HOLD}`, async (event: any) => {
			const data = JSON.parse(event.data);
			const eventConnectionId = event.from.connectionId;
			const connectionId = this.participantService.getHoldConnectionIdFromConnectionData(event.data);
			const isLocalConnection: boolean = this.openviduService.isMyOwnConnection(connectionId);

			if (isLocalConnection) {
				console.log('message', data.message);
				const participantAdded = this.openviduService
					.getRemoteConnections()
					.find((connection) => connection.connectionId === eventConnectionId);
				const subscriber: Subscriber = this.session.subscribe(participantAdded?.stream, undefined);

				if (data.message.includes('hold')) {
					this.onHoldButtonClicked.emit();
					this.log.d('Going to hold the partiticpant: ', connectionId);
					this.openviduService.holdPartiticipant(subscriber);
					this.libService.isOnHold.next(true);
					if (this.participantService.getMyNickname() == 'Customer') {
						const remoteParticipants = this.participantService.getRemoteParticipants();
						const participantToUpdate = remoteParticipants.find((participant) => participant.nickname === 'Support');
						if (participantToUpdate) {
							participantToUpdate.isOnHold = true;
							this.participantService.updateRemoteParticipantsByModel(participantToUpdate);
						}
					}
				}
			}
		});
	}
	private subscribeToUnhold() {
		this.session.on(`signal:${Signal.UNHOLD}`, async (event: any) => {
			const data = JSON.parse(event.data);
			const connectionId = this.participantService.getHoldConnectionIdFromConnectionData(event.data);
			const isLocalConnection: boolean = this.openviduService.isMyOwnConnection(connectionId);
			const eventConnectionId = event.from.connectionId;

			if (isLocalConnection) {
				if (this.participantService.getMyNickname() == 'Customer') {
					let sconnectionId;
					if (this.libService.isSupervisorActive.getValue()) {
						sconnectionId = this.openviduService
							.getRemoteConnections()
							.find((connection) => JSON.parse(connection.data.split('%/%')[0]).clientData == 'Supervisor');
					} else {
						sconnectionId = this.openviduService
							.getRemoteConnections()
							.find((connection) => JSON.parse(connection.data.split('%/%')[0]).clientData == 'Support');
					}
					const subscriber: Subscriber = this.session.subscribe(sconnectionId?.stream, undefined);
					this.participantService.addRemoteConnection(sconnectionId.connectionId, sconnectionId.data, subscriber);
				}
				const participantAdded = this.openviduService
					.getRemoteConnections()
					.find((connection) => connection.connectionId === eventConnectionId);
				const subscriber: Subscriber = this.session.subscribe(participantAdded?.stream, undefined);
				if (data.message.includes('unhold')) {
					this.onUnHoldButtonClicked.emit();
					this.log.d('Going to unhold the partiticpant: ', connectionId);
					this.openviduService.unholdPartiticipant(subscriber);
					this.libService.isOnHold.next(false);
					const remoteParticipants = this.participantService.getRemoteParticipants();
					if (this.participantService.getMyNickname() == 'Customer') {
						const participantToUpdate = remoteParticipants.find((participant) => participant.nickname === 'Support');
						if (participantToUpdate) {
							participantToUpdate.isOnHold = false;
							this.participantService.updateRemoteParticipantsByModel(participantToUpdate);
						}
					} else if (this.participantService.getMyNickname() == 'Supervisor') {
						const participantToUpdate = remoteParticipants.find((participant) => participant.nickname === 'Customer');
						if (participantToUpdate) {
							participantToUpdate.isOnHold = false;
							this.participantService.updateRemoteParticipantsByModel(participantToUpdate);
						}
					}
				}
			}
		});
	}

	private subscribeToReconnection() {
		this.session.on('reconnecting', () => {
			this.log.w('Connection lost: Reconnecting');
			this.actionService.openDialog(
				this.translateService.translate('ERRORS.CONNECTION'),
				this.translateService.translate('ERRORS.RECONNECT'),
				false
			);
		});
		this.session.on('reconnected', () => {
			this.log.w('Connection lost: Reconnected');
			this.actionService.closeDialog();
		});
		this.session.on('sessionDisconnected', (event: SessionDisconnectedEvent) => {
			if (event.reason === 'nodeCrashed') {
				this.actionService.openDialog(
					this.translateService.translate('ERRORS.CONNECTION'),
					this.translateService.translate('ERRORS.RECONNECT'),
					false
				);
				this.onNodeCrashed.emit();
			} else if (event.reason === 'networkDisconnect') {
				this.actionService.closeDialog();
				this.leaveSession();
			}
		});
	}

	private subscribeToRecordingEvents() {
		this.session.on('recordingStarted', (event: RecordingEvent) => {
			this.recordingService.startRecording(event);
		});

		this.session.on('recordingStopped', (event: RecordingEvent) => {
			this.recordingService.stopRecording(event);
		});
	}

	private startUpdateLayoutInterval() {
		this.updateLayoutInterval = setInterval(() => {
			this.layoutService.update();
		}, 50);
	}

	private stopUpdateLayoutInterval() {
		if (this.updateLayoutInterval) {
			clearInterval(this.updateLayoutInterval);
		}
	}
	private subscribeToDisplayTrickerDirectives() {
		this.displayTickerValueSub = this.libService.displayTickerValue.subscribe((displayTickerValue: string) => {
			this.displayTickerValue = displayTickerValue;
		});
		this.displayTickerSub = this.libService.displayTicker.subscribe((displayTicker: boolean) => {
			this.displayTicker = displayTicker;
		});
	}
}
