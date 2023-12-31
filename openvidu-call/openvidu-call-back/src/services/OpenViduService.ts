import { Connection, ConnectionProperties, OpenVidu, OpenViduRole, Recording, Session, SessionProperties } from 'openvidu-node-client';
import { OPENVIDU_SECRET, OPENVIDU_URL } from '../config';

export class OpenViduService {
	MODERATOR_TOKEN_NAME = 'ovCallModeratorToken';
	PARTICIPANT_TOKEN_NAME = 'ovCallParticipantToken';
	moderatorsCookieMap: Map<string, { token: string; recordingId: string }> = new Map<string, { token: string; recordingId: string }>();
	participantsCookieMap: Map<string, string[]> = new Map<string, string[]>();

	protected static instance: OpenViduService;
	private openvidu: OpenVidu;
	private edition: string;

	private constructor() {
		this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
		if (process.env.NODE_ENV === 'production') this.openvidu.enableProdMode();
	}

	static getInstance() {
		if (!OpenViduService.instance) {
			OpenViduService.instance = new OpenViduService();
		}
		return OpenViduService.instance;
	}

	getBasicAuth(): string {
		return this.openvidu.basicAuth;
	}

	getDateFromCookie(cookies: any): number {
		try {
			const cookieToken = cookies[this.MODERATOR_TOKEN_NAME] || cookies[this.PARTICIPANT_TOKEN_NAME];
			if (!!cookieToken) {
				const cookieTokenUrl = new URL(cookieToken);
				const date = cookieTokenUrl?.searchParams.get('createdAt');
				return Number(date);
			} else {
				return Date.now();
			}
		} catch (error) {
			return Date.now();
		}
	}

	getSessionIdFromCookie(cookies: any): string {
		try {
			const cookieToken = cookies[this.MODERATOR_TOKEN_NAME] || cookies[this.PARTICIPANT_TOKEN_NAME];
			const cookieTokenUrl = new URL(cookieToken);
			return cookieTokenUrl?.searchParams.get('sessionId');
		} catch (error) {
			console.log('Session cookie not found', cookies);
			console.error(error);
			return '';
		}
	}

	getSessionIdFromRecordingId(recordingId: string): string {
		return recordingId.split('~')[0];
	}

	isModeratorSessionValid(sessionId: string, cookies: any): boolean {
		try {
			if (!this.moderatorsCookieMap.has(sessionId)) return false;
			if (!cookies[this.MODERATOR_TOKEN_NAME]) return false;
			const storedTokenUrl = new URL(this.moderatorsCookieMap.get(sessionId).token);
			const cookieTokenUrl = new URL(cookies[this.MODERATOR_TOKEN_NAME]);
			const cookieSessionId = cookieTokenUrl.searchParams.get('sessionId');
			const cookieToken = cookieTokenUrl.searchParams.get(this.MODERATOR_TOKEN_NAME);
			const cookieDate = cookieTokenUrl.searchParams.get('createdAt');

			const storedToken = storedTokenUrl.searchParams.get(this.MODERATOR_TOKEN_NAME);
			const storedDate = storedTokenUrl.searchParams.get('createdAt');

			return sessionId === cookieSessionId && cookieToken === storedToken && cookieDate === storedDate;
		} catch (error) {
			return false;
		}
	}

	isParticipantSessionValid(sessionId: string, cookies: any): boolean {
		try {
			if (!this.participantsCookieMap.has(sessionId)) return false;
			if (!cookies[this.PARTICIPANT_TOKEN_NAME]) return false;
			const storedTokens: string[] | undefined = this.participantsCookieMap.get(sessionId);
			const cookieTokenUrl = new URL(cookies[this.PARTICIPANT_TOKEN_NAME]);
			const cookieSessionId = cookieTokenUrl.searchParams.get('sessionId');
			const cookieToken = cookieTokenUrl.searchParams.get(this.PARTICIPANT_TOKEN_NAME);
			const cookieDate = cookieTokenUrl.searchParams.get('createdAt');

			return (
				storedTokens?.some((token) => {
					const storedTokenUrl = new URL(token);
					const storedToken = storedTokenUrl.searchParams.get(this.PARTICIPANT_TOKEN_NAME);
					const storedDate = storedTokenUrl.searchParams.get('createdAt');
					return sessionId === cookieSessionId && cookieToken === storedToken && cookieDate === storedDate;
				}) ?? false
			);
		} catch (error) {
			return false;
		}
	}

	public async createSession(sessionId: string): Promise<Session> {
		console.log('Creating session: ', sessionId);
		let sessionProperties: SessionProperties = { customSessionId: sessionId };
		const session = await this.openvidu.createSession(sessionProperties);
		await session.fetch();
		return session;
	}

	public async createConnection(session: Session, nickname: string, role: OpenViduRole): Promise<Connection> {
		console.log(`Requesting token for session ${session.sessionId}`);
		let connectionProperties: ConnectionProperties = { role };
		if (!!nickname) {
			connectionProperties.data = JSON.stringify({
				openviduCustomConnectionId: nickname
			});
		}
		console.log('Connection Properties:', connectionProperties);
		const connection = await session.createConnection(connectionProperties);
		this.edition = new URL(connection.token).searchParams.get('edition');

		return connection;
	}

	public async startRecording(sessionId: string): Promise<Recording> {
		return this.openvidu.startRecording(sessionId);
	}

	public stopRecording(recordingId: string): Promise<Recording> {
		return this.openvidu.stopRecording(recordingId);
	}

	public deleteRecording(recordingId: string): Promise<Error> {
		return this.openvidu.deleteRecording(recordingId);
	}
	public getRecording(recordingId: string): Promise<Recording> {
		return this.openvidu.getRecording(recordingId);
	}

	public async listAllRecordings(): Promise<Recording[]> {
		return await this.openvidu.listRecordings();
	}

	public async listRecordingsBySessionIdAndDate(sessionId: string, date: number) {
		const recordingList: Recording[] = await this.listAllRecordings();
		return recordingList.filter((recording) => {
			const recordingDateEnd = recording.createdAt + recording.duration * 1000;
			return recording.sessionId === sessionId && recordingDateEnd >= date;
		});
	}

	public async startBroadcasting(sessionId: string, url: string): Promise<void> {
		return this.openvidu.startBroadcast(sessionId, url);
	}

	public async stopBroadcasting(sessionId: string): Promise<void> {
		return this.openvidu.stopBroadcast(sessionId);
	}

	public isPRO(): boolean {
		return this.edition.toUpperCase() === 'PRO';
	}

	public isCE(): boolean {
		return this.edition.toUpperCase() === 'CE';
	}
}
