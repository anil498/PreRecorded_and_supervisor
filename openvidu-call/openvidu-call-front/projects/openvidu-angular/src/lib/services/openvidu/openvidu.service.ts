import { ChangeDetectorRef, Injectable } from '@angular/core';
import {
	Connection,
	OpenVidu,
	OpenViduError,
	OpenViduErrorName,
	Publisher,
	PublisherProperties,
	Session,
	SignalOptions,
	Stream
} from 'openvidu-browser';

import { LoggerService } from '../logger/logger.service';

import { BehaviorSubject, Observable, Subscription, single } from 'rxjs';
import { CameraType } from '../../models/device.model';
import { ILogger } from '../../models/logger.model';
import { OpenViduEdition } from '../../models/openvidu.model';
import { Signal } from '../../models/signal.model';
import { ScreenType, VideoType } from '../../models/video-type.model';
import { OpenViduAngularConfigService } from '../config/openvidu-angular.config.service';
import { DeviceService } from '../device/device.service';
import { ParticipantService } from '../participant/participant.service';
import { PlatformService } from '../platform/platform.service';

@Injectable({
	providedIn: 'root'
})
export class OpenViduService {
	isSttReadyObs: Observable<boolean>;
	private ovEdition: OpenViduEdition;
	private webcamToken = '';
	private screenToken = '';
	protected OV: OpenVidu;
	protected OVScreen: OpenVidu;
	protected webcamSession: Session;
	protected screenSession: Session;
	protected videoSource = undefined;
	protected audioSource = undefined;
	private STT_TIMEOUT_MS = 2 * 1000;
	private sttReconnectionTimeout: NodeJS.Timeout;
	private _isSttReady: BehaviorSubject<boolean> = new BehaviorSubject(true);
	protected log: ILogger;
	private videoElement: any;
	private isVideoPlaying: boolean = false;
	sessionTimerObs: Observable<{ time?: Date }>;
	private sessionTimerStatus = <BehaviorSubject<{ time?: Date }>>new BehaviorSubject(null);
	private sessionTimerSub: Subscription;
	private videoFilePathSubs: Subscription;
	showSessionTimer: boolean;
	videoFilePath: string;

	private sessionTime: Date;
	private sessionTimeInterval: NodeJS.Timer;

	/**
	 * @internal
	 */
	constructor(
		protected openviduAngularConfigSrv: OpenViduAngularConfigService,
		protected platformService: PlatformService,
		protected loggerSrv: LoggerService,
		private participantService: ParticipantService,
		protected deviceService: DeviceService,
		protected libService: OpenViduAngularConfigService
	) {
		this.log = this.loggerSrv.get('OpenViduService');
		this.isSttReadyObs = this._isSttReady.asObservable();
		this.sessionTimerObs = this.sessionTimerStatus.asObservable();
	}

	/**
	 * @internal
	 */
	initialize() {
		this.OV = new OpenVidu();
		this.OV.setAdvancedConfiguration({
			publisherSpeakingEventsOptions: {
				interval: 50
			}
		});
		if (this.openviduAngularConfigSrv.isProduction()) this.OV.enableProdMode();
		this.webcamSession = this.OV.initSession();

		// Initialize screen session only if it is not mobile platform
		if (!this.platformService.isMobile()) {
			this.OVScreen = new OpenVidu();
			if (this.openviduAngularConfigSrv.isProduction()) this.OVScreen.enableProdMode();
			this.screenSession = this.OVScreen.initSession();
		}
	}

	/**
	 * @internal
	 */
	setWebcamToken(token: string) {
		this.webcamToken = token;
	}

	/**
	 * @internal
	 */
	setScreenToken(token: string) {
		this.screenToken = token;
	}

	/**
	 * @internal
	 */
	getWebcamToken(): string {
		return this.webcamToken;
	}

	/**
	 * @internal
	 */
	getScreenToken(): string {
		return this.screenToken;
	}

	/**
	 * @internal
	 */
	isOpenViduCE(): boolean {
		return this.ovEdition === OpenViduEdition.CE;
	}

	/**
	 * @internal
	 */
	isOpenViduPro(): boolean {
		return this.ovEdition === OpenViduEdition.PRO;
	}

	/**
	 * @internal
	 */
	setOpenViduEdition(edition: OpenViduEdition) {
		this.ovEdition = edition;
	}

	isSessionConnected(): boolean {
		return !!this.webcamSession.connection;
	}

	/**
	 * @internal
	 */
	async clear() {
		this.videoSource = undefined;
		this.audioSource = undefined;
		await this.participantService.getMyCameraPublisher()?.stream?.disposeMediaStream();
		await this.participantService.getMyScreenPublisher()?.stream?.disposeMediaStream();
	}

	/**
	 *
	 * Returns the local Session. See {@link https://docs.openvidu.io/en/stable/api/openvidu-browser/classes/Session.html  Session} object.
	 */
	getSession(): Session {
		return this.getWebcamSession();
	}

	/**
	 * @internal
	 */
	getWebcamSession(): Session {
		return this.webcamSession;
	}

	/**
	 * @internal
	 */
	isWebcamSessionConnected(): boolean {
		return !!this.webcamSession.capabilities;
	}

	/**
	 * @internal
	 */
	getScreenSession(): Session {
		return this.screenSession;
	}

	/**
	 * @internal
	 */
	isScreenSessionConnected(): boolean {
		return !!this.screenSession.capabilities;
	}

	/**
	 * @internal
	 * Whether the STT service is ready or not
	 * This will be `false` when the app receives a SPEECH_TO_TEXT_DISCONNECTED exception
	 * and it cannot subscribe to STT
	 */
	isSttReady(): boolean {
		return this._isSttReady.getValue();
	}

	/**
	 * @internal
	 */
	setSTTReady(value: boolean): void {
		if (this._isSttReady.getValue() !== value) {
			this._isSttReady.next(value);
		}
	}

	/**
	 * @internal
	 */
	async connectSession(session: Session, token: string): Promise<void> {
		if (!!token && session) {
			const nickname = this.participantService.getMyNickname();
			const participantId = this.participantService.getLocalParticipant().id;
			if (session === this.webcamSession) {
				this.log.d('Connecting webcam session');
				await this.webcamSession.connect(token, {
					clientData: nickname,
					participantId,
					type: VideoType.CAMERA
				});
				this.participantService.setMyCameraConnectionId(this.webcamSession.connection.connectionId);
				this.sessionTimerSub = this.libService.displayTimer.subscribe((value: boolean) => {
					this.showSessionTimer = value;
				});
				this.videoFilePathSubs = this.libService.videoFilePath.subscribe((value: string) => {
					this.videoFilePath = value;
				});
				if (this.showSessionTimer) {
					this.startSessionTime()
				}
			} else if (session === this.screenSession) {
				this.log.d('Connecting screen session');
				await this.screenSession.connect(token, {
					clientData: `${nickname}_${VideoType.SCREEN}`,
					participantId,
					type: VideoType.SCREEN
				});

				this.participantService.setMyScreenConnectionId(this.screenSession.connection.connectionId);
			}
		}
	}

	/**
	 * Leaves the session, destroying all local streams and clean all participant data.
	 */
	disconnect() {
		this.disconnectSession(this.webcamSession);
		this.disconnectSession(this.screenSession);
	}

	/**
	 * @internal
	 * Initialize a publisher checking devices saved on storage or if participant have devices available.
	 */
	async initDefaultPublisher(): Promise<Publisher | undefined> {
		const hasVideoDevices = this.deviceService.hasVideoDeviceAvailable();
		const hasAudioDevices = this.deviceService.hasAudioDeviceAvailable();
		const isVideoActive = !this.deviceService.isVideoMuted();
		const isAudioActive = !this.deviceService.isAudioMuted();

		let videoSource: string | boolean = false;
		let audioSource: string | boolean = false;

		if (hasVideoDevices) {
			// Video is active, assign the device selected
			videoSource = this.deviceService.getCameraSelected().device;
		} else if (!isVideoActive && hasVideoDevices) {
			// Video is muted, assign the default device
			// videoSource = undefined;
		}

		if (hasAudioDevices) {
			// Audio is active, assign the device selected
			audioSource = this.deviceService.getMicrophoneSelected().device;
		} else if (!isAudioActive && hasAudioDevices) {
			// Audio is muted, assign the default device
			// audioSource = undefined;
		}

		const mirror = this.deviceService.getCameraSelected() && this.deviceService.getCameraSelected().type === CameraType.FRONT;
		const properties: PublisherProperties = {
			videoSource,
			audioSource,
			publishVideo: isVideoActive,
			publishAudio: isAudioActive,
			mirror: false
		};
		if (hasVideoDevices || hasAudioDevices) {
			this.log.d('Initializing publisher with properties1: ', properties);
			const publisher = await this.initPublisher(undefined, properties);
			this.participantService.setMyCameraPublisher(publisher);
			this.participantService.updateLocalParticipant();
			return publisher;
		} else {
			this.participantService.setMyCameraPublisher(null);
		}
	}

	/**
	 * @internal
	 */
	async initPublisher(targetElement: string | HTMLElement, properties: PublisherProperties): Promise<Publisher> {
		this.log.d('Initializing publisher with properties: ', properties);
		return await this.OV.initPublisherAsync(targetElement, properties);
	}

	/**
	 * @internal
	 */
	async publish(publisher: Publisher): Promise<void> {
		if (!!publisher) {
			if (publisher === this.participantService.getMyCameraPublisher()) {
				if (this.webcamSession?.capabilities?.publish) {
					return await this.webcamSession.publish(publisher);
				}
				this.log.e('Webcam publisher cannot be published');
			} else if (publisher === this.participantService.getMyScreenPublisher()) {
				if (this.screenSession?.capabilities?.publish) {
					return await this.screenSession.publish(publisher);
				}
				this.log.e('Screen publisher cannot be published');
			}
		}
	}

	/**
	 * @internal
	 */
	private unpublish(publisher: Publisher): void {
		if (!!publisher) {
			this.log.d("Publisher", publisher)
			if (publisher === this.participantService.getMyCameraPublisher()) {
				this.publishAudioAux(this.participantService.getMyScreenPublisher(), this.participantService.isMyAudioActive());
				this.webcamSession.unpublish(publisher);
			} else if (publisher === this.participantService.getMyScreenPublisher()) {
				this.screenSession.unpublish(publisher);
			}
		}
	}

	/**
	 * Publish or unpublish the video stream (if available).
	 * It hides the camera muted stream if screen is sharing.
	 * See openvidu-browser {@link https://docs.openvidu.io/en/stable/api/openvidu-browser/classes/Publisher.html#publishVideo publishVideo}
	 */
	async publishVideo(publish: boolean): Promise<void> {
		const publishAudio = this.participantService.isMyAudioActive();
		this.log.d("Publishing video")

		// Disabling webcam
		if (this.participantService.haveICameraAndScreenActive()) {
			this.log.d("Publishing video1")
			await this.publishVideoAux(this.participantService.getMyCameraPublisher(), publish);
			// this.participantService.disableWebcamStream();
			// this.unpublish(this.participantService.getMyCameraPublisher());
			// this.publishAudioAux(this.participantService.getMyScreenPublisher(), publishAudio);
		} else if (this.participantService.isOnlyMyScreenActive()) {
			// Enabling webcam
			this.log.d("Publishing video2")
			const hasAudio = this.participantService.hasScreenAudioActive();
			if (!this.isWebcamSessionConnected()) {
				this.log.d("Publishing video3")
				await this.connectSession(this.getWebcamSession(), this.getWebcamToken());
			}
			// await this.publish(this.participantService.getMyCameraPublisher());
			this.participantService.enableWebcamStream();
			await this.publishVideoAux(this.participantService.getMyCameraPublisher(), true);
			this.publishAudioAux(this.participantService.getMyScreenPublisher(), true);
			this.publishAudioAux(this.participantService.getMyCameraPublisher(), hasAudio);
		} else {
			this.log.d("Publishing video4")
			// Muting/unmuting webcam
			await this.publishVideoAux(this.participantService.getMyCameraPublisher(), publish);
		}
	}

	/**
	 * @internal
	 */
	private async publishVideoAux(publisher: Publisher, publish: boolean): Promise<void> {
		if (!!publisher) {
			let resource: boolean | MediaStreamTrack = true;
			if (publish) {
				// Forcing restoration with a custom media stream (the older one instead the default)
				const currentDeviceId = this.deviceService.getCameraSelected()?.device;
				const mediaStream = await this.createMediaStream({ videoSource: currentDeviceId, audioSource: false });
				resource = mediaStream.getVideoTracks()[0];
			}

			await publisher.publishVideo(publish, resource);
			this.participantService.updateLocalParticipant();
		}
	}

	/**
	 * Publish or unpublish the audio stream (if available).
	 * See openvidu-browser {@link https://docs.openvidu.io/en/stable/api/openvidu-browser/classes/Publisher.html#publishAudio publishAudio}.
	 */
	async publishAudio(publish: boolean): Promise<void> {
		if (this.participantService.isMyCameraActive()) {
			// if (this.participantService.isMyScreenActive() && this.participantService.hasScreenAudioActive()) {
			// 	this.publishAudioAux(this.participantService.getMyScreenPublisher(), false);
			// }

			this.publishAudioAux(this.participantService.getMyCameraPublisher(), publish);
		} else {
			this.publishAudioAux(this.participantService.getMyScreenPublisher(), publish);
		}
	}
	async publishScreenAudio(publish: boolean): Promise<void> {
		if (this.participantService.isMyScreenActive() && !this.participantService.hasScreenAudioActive()) {
			this.publishAudioAux(this.participantService.getMyScreenPublisher(), publish);
		} else {
			this.publishAudioAux(this.participantService.getMyScreenPublisher(), publish);
		}
	}

	/**
	 * Share or unshare the screen.
	 * Hide the camera muted stream when screen is sharing.
	 */
	async toggleScreenshare() {
		if (this.participantService.haveICameraAndScreenActive()) {
			// Disabling screenShare
			this.participantService.disableScreenStream();
			this.unpublish(this.participantService.getMyScreenPublisher());
		} else if (this.participantService.isOnlyMyCameraActive()) {
			// I only have the camera published
			const hasAudioDevicesAvailable = this.deviceService.hasAudioDeviceAvailable();
			const userMedia = await navigator.mediaDevices.getUserMedia({ audio: false, video: true });
			const displayMediaStream = await navigator.mediaDevices.getDisplayMedia({ audio: true, video: true });
			const hasAudio = displayMediaStream.getAudioTracks().length == 0 ? false : true;
			const properties: PublisherProperties = {
				videoSource: displayMediaStream.getVideoTracks()[0],
				audioSource: displayMediaStream.getAudioTracks()[0],
				publishVideo: true,
				publishAudio: hasAudio,
				mirror: false,
				frameRate: 30,
				resolution: "1920x1080"
			};
			this.log.d('Initializing publisher with properties2: ', displayMediaStream);

			const screenPublisher = await this.initPublisher(undefined, properties);

			screenPublisher.once('accessAllowed', async () => {
				// Listen to event fired when native stop button is clicked
				screenPublisher.stream
					.getMediaStream()
					.getVideoTracks()[0]
					.addEventListener('ended', async () => {
						this.log.d('Clicked native stop button. Stopping screen sharing');
						await this.toggleScreenshare();
					});

				// Enabling screenShare
				this.participantService.activeMyScreenShare(screenPublisher);

				if (!this.isScreenSessionConnected()) {
					await this.connectSession(this.getScreenSession(), this.getScreenToken());
				}
				await this.publish(this.participantService.getMyScreenPublisher());

				// This is commented for Publishing screen share and local partitcipant at same time
				if (!this.participantService.isMyVideoActive()) {
					// 	// Disabling webcam
					// 	this.participantService.disableWebcamStream();
					// 	this.unpublish(this.participantService.getMyCameraPublisher());
					this.log.d('Disabling video', this.participantService.isMyVideoActive());
				}
			});

			screenPublisher.once('accessDenied', (error: any) => {
				return Promise.reject(error);
			});

		} else {
			// I only have my screenshare active and I have no camera or it is muted
			const hasAudio = this.participantService.hasScreenAudioActive();
			this.log.d('has audio', hasAudio);

			// Enable webcam
			if (!this.isWebcamSessionConnected()) {
				this.log.d('has webcam', this.isWebcamSessionConnected());
				await this.connectSession(this.getWebcamSession(), this.getWebcamToken());
			}
			await this.publish(this.participantService.getMyCameraPublisher());
			this.publishAudioAux(this.participantService.getMyCameraPublisher(), hasAudio);
			this.participantService.enableWebcamStream();

			// Disabling screenshare
			// this.participantService.disableScreenStream();
			// this.unpublish(this.participantService.getMyScreenPublisher());
		}
	}

	/**
	 * @internal
	 */
	private publishAudioAux(publisher: Publisher, value: boolean): void {
		if (!!publisher) {
			publisher.publishAudio(value);
			this.participantService.updateLocalParticipant();
		}
	}

	/**
	 * @internal
	 */
	sendSignal(type: Signal, connections?: Connection[], data?: any): void {
		const signalOptions: SignalOptions = {
			data: JSON.stringify(data),
			type,
			to: connections && connections.length > 0 ? connections : undefined
		};
		this.webcamSession.signal(signalOptions);
	}

	/**
	 * @internal
	 */
	async replaceTrack(videoType: VideoType, props: PublisherProperties) {
		try {
			this.log.d(`Replacing ${videoType} track`, props);

			if (videoType === VideoType.CAMERA) {
				let mediaStream: MediaStream;
				const isReplacingAudio = !!props.audioSource;
				const isReplacingVideo = !!props.videoSource;

				if (isReplacingVideo) {
					mediaStream = await this.createMediaStream(props);
					// Replace video track
					const videoTrack: MediaStreamTrack = mediaStream.getVideoTracks()[0];
					await this.participantService.getMyCameraPublisher().replaceTrack(videoTrack);
				} else if (isReplacingAudio) {
					mediaStream = await this.createMediaStream(props);
					// Replace audio track
					const audioTrack: MediaStreamTrack = mediaStream.getAudioTracks()[0];
					await this.participantService.getMyCameraPublisher().replaceTrack(audioTrack);
				}
			} else if (videoType === VideoType.SCREEN) {
				try {
					let newScreenMediaStream = await this.OVScreen.getUserMedia(props);
					this.participantService.getMyScreenPublisher().stream.getMediaStream().getVideoTracks()[0].stop();
					await this.participantService.getMyScreenPublisher().replaceTrack(newScreenMediaStream.getVideoTracks()[0]);
				} catch (error) {
					this.log.w('Cannot create the new MediaStream', error);
				}
			}
		} catch (error) {
			this.log.e('Error replacing track ', error);
		}
	}

	/**
	 * @internal
	 * Subscribe all `CAMERA` stream types to speech-to-text
	 * It will retry the subscription each `STT_TIMEOUT_MS`
	 *
	 * @param lang The language of the Stream's audio track.
	 */
	async subscribeRemotesToSTT(lang: string): Promise<void> {
		const remoteParticipants = this.participantService.getRemoteParticipants();
		let successNumber = 0;

		for (const p of remoteParticipants) {
			const stream = p.getCameraConnection()?.streamManager?.stream;
			if (stream) {
				try {
					await this.subscribeStreamToStt(stream, lang);
					successNumber++;
				} catch (error) {
					this.log.e(`Error subscribing ${stream.streamId} to STT:`, error);
					break;
				}
			}
		}

		this.setSTTReady(successNumber === remoteParticipants.length);
		if (!this.isSttReady()) {
			this.log.w('STT is not ready. Retrying subscription...');
			this.sttReconnectionTimeout = setTimeout(this.subscribeRemotesToSTT.bind(this, lang), this.STT_TIMEOUT_MS);
		}
	}

	/**
	 * @internal
	 * Subscribe a stream to speech-to-text
	 * @param stream
	 * @param lang
	 */
	async subscribeStreamToStt(stream: Stream, lang: string): Promise<void> {
		// await this.getWebcamSession().subscribeToSpeechToText(stream, lang);
		this.log.d(`Subscribed stream ${stream.streamId} to STT with ${lang} language.`);
	}

	/**
	 * @internal
	 * Unsubscribe to all `CAMERA` stream types to speech-to-text if STT is up(ready)
	 */
	async unsubscribeRemotesFromSTT(): Promise<void> {
		clearTimeout(this.sttReconnectionTimeout);
		if (this.isSttReady()) {
			for (const p of this.participantService.getRemoteParticipants()) {
				const stream = p.getCameraConnection().streamManager.stream;
				if (stream) {
					try {
						// await this.getWebcamSession().unsubscribeFromSpeechToText(stream);
					} catch (error) {
						this.log.e(`Error unsubscribing ${stream.streamId} from STT:`, error);
					}
				}
			}
		}
	}

	private async createMediaStream(pp: PublisherProperties): Promise<MediaStream> {
		let mediaStream: MediaStream;
		const isFirefoxPlatform = this.platformService.isFirefox();
		const isReplacingAudio = !!pp.audioSource;
		const isReplacingVideo = !!pp.videoSource;

		try {
			mediaStream = await this.OV.getUserMedia(pp);
		} catch (error) {
			if ((<OpenViduError>error).name === OpenViduErrorName.DEVICE_ACCESS_DENIED) {
				if (isFirefoxPlatform) {
					this.log.w('The device requested is not available. Restoring the older one');
					// The track requested is not available so we are getting the old tracks ids for recovering the track
					if (isReplacingVideo) {
						pp.videoSource = this.deviceService.getCameraSelected().device;
					} else if (isReplacingAudio) {
						pp.audioSource = this.deviceService.getMicrophoneSelected().device;
					}
					mediaStream = await this.OV.getUserMedia(pp);
					// TODO show error alert informing that the new device is not available
				}
			}
		} finally {
			return mediaStream;
		}
	}

	/**
	 * @internal
	 */
	needSendNicknameSignal(): boolean {
		let oldNickname: string;
		try {
			const connData = JSON.parse(this.webcamSession.connection.data.split('%/%')[0]);
			oldNickname = connData.clientData;
		} catch (error) {
			this.log.e(error);
		}
		return oldNickname !== this.participantService.getMyNickname();
	}

	/**
	 * @internal
	 */
	isMyOwnConnection(connectionId: string): boolean {
		return (
			this.webcamSession?.connection?.connectionId === connectionId || this.screenSession?.connection?.connectionId === connectionId
		);
	}

	/**
	 *
	 * Returns the remote connections of the Session.
	 * See {@link https://docs.openvidu.io/en/stable/api/openvidu-browser/classes/Connection.html  Connection} object.
	 */
	getRemoteConnections(): Connection[] {
		// Avoid screen connections
		const remoteCameraConnections: Connection[] = Array.from(this.webcamSession.remoteConnections.values()).filter((conn) => {
			let type: VideoType;
			const db = conn.data.split('%/%')[0]
			type = JSON.parse(db).type;
			return type !== VideoType.SCREEN;
		});
		return remoteCameraConnections;
	}
	getScreenRemoteConnections(): Connection[] {
		// Avoid screen connections
		const remoteScreenConnections: Connection[] = Array.from(this.webcamSession.remoteConnections.values()).filter((conn) => {
			let type: VideoType;
			const db = conn.data.split('%/%')[0]
			type = JSON.parse(db).type;
			return type == VideoType.SCREEN;
		});
		return remoteScreenConnections;
	}

	private disconnectSession(session: Session) {
		if (session) {
			if (session.sessionId === this.webcamSession?.sessionId) {
				this.log.d('Disconnecting webcam session');
				this.webcamSession?.disconnect();
				this.webcamSession = null;
			} else if (session.sessionId === this.screenSession?.sessionId) {
				this.log.d('Disconnecting screen session');
				this.screenSession?.disconnect();
				this.screenSession = null;
			}
		}
	}
	async publishRecordedVideo(): Promise<void> {
		try {
			if (this.participantService.isMyVideoPublishActive()) {
				// Unpublishing video 
				console.log("unpublish")
				this.participantService.disablePublishVideoStream();
				this.isVideoPlaying = !this.isVideoPlaying
				this.screenSession.unpublish(this.participantService.getMyVideoPublisher());
			} else {
				console.log("publish")
				// Create a custom video element with the video and audio streams
				this.videoElement = document.createElement('video');
				const response = await fetch(this.videoFilePath);
				if (!response.ok) {
					throw new Error('Failed to fetch video file');
				}
				const videoBlob = await response.blob();

				// Create a local URL for the video file
				const videoURL = URL.createObjectURL(videoBlob);

				this.videoElement.src = videoURL;
				this.videoElement.controls = true;
				this.videoElement.setAttribute('controls', true.toString()); // or use setAttribute

				// Wait for video to be loaded and start playing
				this.videoElement.onloadedmetadata = async () => {
					this.videoElement.play();
					// Create an AudioContext
					const audioContext = new AudioContext();

					// Create a MediaElementSourceNode to connect the videoElement to the AudioContext
					const sourceNode = audioContext.createMediaElementSource(this.videoElement);

					// Create a MediaStreamDestination to capture the audio from the AudioContext
					const audioDestination = audioContext.createMediaStreamDestination();
					sourceNode.connect(audioDestination);

					// Create a video stream from the canvas
					let canvas = document.createElement('canvas');
					const videoWidth = this.videoElement.videoWidth;
					const videoHeight = this.videoElement.videoHeight;
					canvas.width = videoWidth;
					canvas.height = videoHeight;
					const canvasContext = canvas.getContext('2d');
					const videoStream = canvas.captureStream();
					const audioTrack = audioDestination.stream.getAudioTracks()[0];
					// this.duration = videoElement.duration;

					console.log("Audio is : " + audioTrack);
					const properties: PublisherProperties = {
						videoSource: videoStream.getVideoTracks()[0],
						audioSource: audioTrack,
						publishVideo: true,
						publishAudio: true,
						mirror: false,
						frameRate: 60
					}

					const videoPublisher = await this.initPublisher(undefined, properties);
					// Draw each frame of the video to the canvas and capture it as an image
					const drawFrame = () => {
						if (this.isVideoPlaying) {
							canvasContext.drawImage(this.videoElement, 0, 0, videoWidth, videoHeight);
						}
						setTimeout(drawFrame, 1000 / 30);
					};
					drawFrame();
					this.participantService.activeMyScreenShare(videoPublisher);
					if (!this.isScreenSessionConnected()) {
						await this.connectSession(this.screenSession, this.getScreenToken());
					}
					await this.screenSession.publish(videoPublisher);
				}
			}
		} catch (error) {
			this.log.d("Getting error while publishing the video", error);
		}
	}
	playVideo() {
		try {
			this.log.d("Playing")
			this.isVideoPlaying = !this.isVideoPlaying
		} catch (error) {
			this.log.d("Getting error while playing video", error)
		}
	}
	pauseVideo() {
		try {
			this.log.d("Pausing")
			this.isVideoPlaying = !this.isVideoPlaying
		} catch (error) {
			this.log.d("Getting error while pausing video", error)
		}
	}

	private startSessionTime() {
		this.sessionTime = new Date();
		this.sessionTime.setHours(0, 0, 0, 0);
		this.sessionTimeInterval = setInterval(() => {
			this.sessionTime.setSeconds(this.sessionTime.getSeconds() + 1);
			this.sessionTime = new Date(this.sessionTime.getTime());
			this.sessionTimerStatus.next({ time: this.sessionTime });
		}, 1000);
	}
}
