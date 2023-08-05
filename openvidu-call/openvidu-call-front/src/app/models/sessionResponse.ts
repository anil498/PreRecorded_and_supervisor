import { RecordingInfo } from 'openvidu-angular';

export class SessionResponse {
	private _sessionId: string | undefined;
	private _sessionName: string | undefined;
	private _participantName: string | undefined;
	private _redirectUrl: string | undefined;
	private _settings: Settings;
	private _cameraToken: string | undefined;
	private _screenToken: string | undefined;
	private _recordings: RecordingInfo[] | undefined;
	private _type: string | undefined;
	private _recordingMode: string | undefined;
	private _isSessionExpired: boolean | undefined;

	get sessionId(): string | undefined {
		return this._sessionId;
	}

	set sessionId(value: string | undefined) {
		this._sessionId = value;
	}

	get sessionName(): string | undefined {
		return this._sessionName;
	}

	set sessionName(value: string | undefined) {
		this._sessionName = value;
	}

	get participantName(): string | undefined {
		return this._participantName;
	}

	set participantName(value: string | undefined) {
		this._participantName = value;
	}

	get redirectUrl(): string | undefined {
		return this._redirectUrl;
	}

	set redirectUrl(value: string | undefined) {
		this._redirectUrl = value;
	}

	get settings(): Settings | undefined {
		return this._settings;
	}

	set settings(value: Settings | undefined) {
		this._settings = value;
	}

	get cameraToken(): string | undefined {
		return this._cameraToken;
	}

	set cameraToken(value: string | undefined) {
		this._cameraToken = value;
	}

	get screenToken(): string | undefined {
		return this._screenToken;
	}

	set screenToken(value: string | undefined) {
		this._screenToken = value;
	}

	get recordings(): RecordingInfo[] | undefined {
		return this._recordings;
	}

	set recordings(value: RecordingInfo[] | undefined) {
		this._recordings = value;
	}

	get type(): string | undefined {
		return this._type;
	}

	set type(value: string | undefined) {
		this._type = value;
	}

	get recordingMode(): string | undefined {
		return this._recordingMode;
	}

	set recordingMode(value: string | undefined) {
		this._recordingMode = value;
	}

	get isSessionExpired(): boolean | undefined {
		return this._isSessionExpired;
	}

	set isSessionExpired(value: boolean | undefined) {
		this._isSessionExpired = value;
	}
}
export class Settings {
	private _duration?: number;
	private _showLogo?: boolean;
	private _logo?:string;
	private _description?: string;
	private _landingPage?: string;
	private _moderators?: boolean;
	private _displayTicker?: boolean;
	private _displayTimer?: boolean;
	private _recording?: boolean;
	private _recordingDetails?: {
		max_time?: number;
		max_dur?: number;
	};
	private _screenShare?: boolean;
	private _ScreenShareWithAudio?: boolean;
	private _isAutoFullScreen?: boolean;
	private _waitForModerator?: boolean;
	private _chat?: boolean;
	private _activitiesButton?: boolean;
	private _participantsButton?: boolean;
	private _floatingLayout?: boolean;
	private _layoutNumber?: number;
	private _supervisor?: boolean;
	private _preRecorded?: boolean;
	private _preRecordedDetails?: {
		pre_recorded_video_file?: string;
		share_pre_recorded_video?: boolean;
	};
	private _broadcast?: boolean;
	private _fileUrl?: string;

	private _icdcDetails?: {
		icdc?: boolean;
		display_icdc?: boolean;
		edit_icdc?: boolean;
		title_icdc?: string;
	};

	private _icdcQuestions: IcdcQuestions;

	get icdcQuestions(): IcdcQuestions | undefined {
		return this._icdcQuestions;
	}
	set icdcQuestions(value: IcdcQuestions | undefined) {
		this._icdcQuestions = value;
	}

	get duration(): number | undefined {
		return this._duration;
	}

	set duration(value: number | undefined) {
		this._duration = value;
	}

	get showLogo(): boolean | undefined {
		return this._showLogo;
	}

	set showLogo(value: boolean | undefined) {
		this._showLogo = value;
	}

	get logo(): string| undefined {
		return this._logo;
	}

	set logo(value: string | undefined) {
		this._logo = value;
	}

	get description(): string | undefined {
		return this._description;
	}

	set description(value: string | undefined) {
		this._description = value;
	}

	get landingPage(): string | undefined {
		return this._landingPage;
	}

	set landingPage(value: string | undefined) {
		this._landingPage = value;
	}

	get moderators(): boolean | undefined {
		return this._moderators;
	}

	set moderators(value: boolean | undefined) {
		this._moderators = value;
	}

	get displayTicker(): boolean | undefined {
		return this._displayTicker;
	}

	set displayTicker(value: boolean | undefined) {
		this._displayTicker = value;
	}

	get displayTimer(): boolean | undefined {
		return this._displayTimer;
	}

	set displayTimer(value: boolean | undefined) {
		this._displayTimer = value;
	}

	get recording(): boolean | undefined {
		return this._recording;
	}

	set recording(value: boolean | undefined) {
		this._recording = value;
	}

	get recordingDetails(): { max_time?: number; max_dur?: number } | undefined {
		return this._recordingDetails;
	}

	set recordingDetails(value: { max_time?: number; max_dur?: number } | undefined) {
		this._recordingDetails = value;
	}

	get screenShare(): boolean | undefined {
		return this._screenShare;
	}

	set screenShare(value: boolean | undefined) {
		this._screenShare = value;
	}

	get ScreenShareWithAudio(): boolean | undefined {
		return this._ScreenShareWithAudio;
	}

	set ScreenShareWithAudio(value: boolean | undefined) {
		this._ScreenShareWithAudio = value;
	}

	get isAutoFullScreen(): boolean | undefined {
		return this._isAutoFullScreen;
	}

	set isAutoFullScreen(value: boolean | undefined) {
		this._isAutoFullScreen = value;
	}

	get waitForModerator(): boolean | undefined {
		return this._waitForModerator;
	}

	set waitForModerator(value: boolean | undefined) {
		this._waitForModerator = value;
	}

	get chat(): boolean | undefined {
		return this._chat;
	}

	set chat(value: boolean | undefined) {
		this._chat = value;
	}

	get activitiesButton(): boolean | undefined {
		return this._activitiesButton;
	}

	set activitiesButton(value: boolean | undefined) {
		this._activitiesButton = value;
	}

	get participantsButton(): boolean | undefined {
		return this._participantsButton;
	}

	set participantsButton(value: boolean | undefined) {
		this._participantsButton = value;
	}

	get floatingLayout(): boolean | undefined {
		return this._floatingLayout;
	}

	set floatingLayout(value: boolean | undefined) {
		this._floatingLayout = value;
	}

	get layoutNumber(): number | undefined {
		return this._layoutNumber;
	}

	set layoutNumber(value: number | undefined) {
		this._layoutNumber = value;
	}

	get supervisor(): boolean | undefined {
		return this._supervisor;
	}

	set supervisor(value: boolean | undefined) {
		this._supervisor = value;
	}

	get preRecorded(): boolean | undefined {
		return this._preRecorded;
	}

	set preRecorded(value: boolean | undefined) {
		this._preRecorded = value;
	}

	get preRecordedDetails():
		| {
				pre_recorded_video_file?: string;
				share_pre_recorded_video?: boolean;
		  }
		| undefined {
		return this._preRecordedDetails;
	}

	set preRecordedDetails(value: { pre_recorded_video_file?: string; share_pre_recorded_video?: boolean } | undefined) {
		this._preRecordedDetails = value;
	}

	get broadcast(): boolean | undefined {
		return this._broadcast;
	}

	set broadcast(value: boolean | undefined) {
		this._broadcast = value;
	}

	get fileUrl(): string | undefined {
		return this._fileUrl;
	}

	set fileUrl(value: string | undefined) {
		this._fileUrl = value;
	}
	get icdcDetails():
		| {
				icdc?: boolean;
				display_icdc?: boolean;
				edit_icdc?: boolean;
				title_icdc?: string;
		  }
		| undefined {
		return this._icdcDetails;
	}

	set icdcDetails(
		value:
			| {
					icdc?: boolean;
					display_icdc?: boolean;
					edit_icdc?: boolean;
					title_icdc?: string;
			  }
			| undefined
	) {
		this._icdcDetails = value;
	}
}

export class IcdcQuestions {
	private _question_data: QuestionData[];
	private _icdc_id: number;

	public get icdc_id(): number {
		return this._icdc_id;
	}

	public set icdc_id(value: number) {
		this._icdc_id = value;
	}

	public get question_data(): QuestionData[] {
		return this._question_data;
	}
	public set question_data(value: QuestionData[]) {
		this._question_data = value;
	}
}
export class QuestionData {
	private _q_id: number;
	private _type: string;
	private _question: string;
	private _meta: any;

	public get q_id(): number {
		return this._q_id;
	}
	public set q_id(value: number) {
		this._q_id = value;
	}

	public get type(): string {
		return this._type;
	}
	public set type(value: string) {
		this._type = value;
	}

	public get question(): string {
		return this._question;
	}
	public set question(value: string) {
		this._question = value;
	}

	public get meta(): any {
		return this._meta;
	}
	public set meta(value: any) {
		this._meta = value;
	}
}
///--------------
