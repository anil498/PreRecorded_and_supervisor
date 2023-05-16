import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, lastValueFrom, Subject } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class RestService {
  private dialogClosedSource = new Subject<boolean>();
  public dialogClosed$ = this.dialogClosedSource.asObservable();

  private _response: any;
  private _token: string;
  private _userId: string;
  private baseHref: string;
  private url: string;
  private token: string;
  private userId: string;

  constructor(private http: HttpClient) {
    this.baseHref =
      "/" +
      (!!window.location.pathname.split("/")[1]
        ? window.location.pathname.split("/")[1] + "/"
        : "");
    this.url = "http://172.17.0.122:5000/VPService/v1";
  }

  setData(response: any) {
    this._response = response;
  }

  setToken(value: string) {
    this._token = value;
  }

  setUserId(value: string) {
    this._userId = value;
  }

  getData() {
    return this._response;
  }

  getToken() {
    return this._token;
  }

  getUserId() {
    return this._userId;
  }

  closeDialog() {
    this.dialogClosedSource.next(true);
  }

  private loginRequest(path: string, body: any): Promise<any> {
    console.warn(this.url + "/" + path);
    console.warn(body);
    try {
      const headers = {
        Authorization: "AC1XD9cyYcNxO",
        "Content-Type": "application/json",
      };
      return lastValueFrom(
        this.http.post<any>(this.url + "/user/" + path, body, { headers })
      );
    } catch (error) {
      console.warn(error);
      if (error.status === 404) {
        throw {
          status: error.status,
          message: "Cannot connect with backend. " + error.url + " not found",
        };
      }
      throw error;
    }
  }

  private postRequest1(path: string, body: any): Promise<any> {
    console.warn(this.url + "/" + path);
    console.warn(body);
    try {
      const headers = new HttpHeaders({
        Authorization: "AC1XD9cyYcNxO",
        Token: `${this._token}`,
      });
      return lastValueFrom(
        this.http.post<any>(this.url + "/user/" + path, body, { headers })
      );
    } catch (error) {
      console.warn(error);
      if (error.status === 404) {
        throw {
          status: error.status,
          message: "Cannot connect with backend. " + error.url + " not found",
        };
      }
      throw error;
    }
  }

  private postRequest2(path: string, body: any): Promise<any> {
    console.warn(this.url + "/" + path);
    console.warn(body);
    try {
      const headers = new HttpHeaders({
        Authorization: "AC1XD9cyYcNxO",
        Token: `${this._token}`,
      });
      return lastValueFrom(
        this.http.post<any>(this.url + "/account/" + path, body, { headers })
      );
    } catch (error) {
      console.warn(error);
      if (error.status === 404) {
        throw {
          status: error.status,
          message: "Cannot connect with backend. " + error.url + " not found",
        };
      }
      throw error;
    }
  }

  private callRequest(
    sessionId: string,
    path: string,
    body: any
  ): Promise<any> {
    console.warn(body);
    try {
      const headers = {
        "Content-Type": "application/json",
        sessionid: sessionId,
      };
      return lastValueFrom(
        this.http.post<any>(this.url + path, body, { headers })
      );
    } catch (error) {
      if (error.status === 404) {
        throw {
          status: error.status,
          message: "Cannot connect with backend. " + error.url + " not found",
        };
      }
      throw error;
    }
  }

  async login(type: string, loginId: string, password: string) {
    return this.loginRequest(type, { loginId, password });
  }

  async createUser(
    type: string,

    userFname: string,
    userLname: string,
    contact: number,
    email: string,
    loginId: string,
    exp_date: string,

    password: string,
    accessId: number[],

    max_duration: number,
    max_participants: number,
    max_active_sessions: number,

    features: number[],
    featuresMeta: any
  ) {
    return this.postRequest1(type, {
      userFname,
      userLname,
      contact,
      email,
      loginId,
      exp_date,
      password,
      accessId,

      session: {
        max_duration,
        max_participants,
        max_active_sessions,
      },

      features,
      featuresMeta,
    });
  }

  async createAccountUser(
    type: string,

    name: string,
    address: string,
    logo: Blob,
    maxUser: number,
    expDate: string,

    max_active_sessions: number,
    max_duration: number,
    max_participants: number,
    accessId: number[],

    features: number[],
    featuresMeta: any,

    fname: string,
    lname: string,
    contact: number,
    email: string,
    loginId: string,
    password: string
  ) {
    return this.postRequest2(type, {
      name,
      address,
      maxUser,
      expDate,

      accessId,
      session: {
        max_active_sessions,
        max_participants,
        max_duration,
      },

      features,
      featuresMeta,

      fname,
      lname,
      contact,
      email,
      loginId,
      password,
    });
  }

  async update(
    type: string,
    fname: string,
    lname: string,
    mobile: number,
    email: string,
    address: string,
    userId: string,
    parentId: string,
    userType: string,
    serviceType: string,
    userPassword: string,
    accStatus: string,
    accExpDate: string,
    maxUsers: number,
    maxDuration: number,
    maxParticipants: number,
    activeSessions: number,
    screen_share: boolean,
    video_share: boolean,
    live_chat: boolean,
    recording: boolean
  ) {
    return this.postRequest1(type, {
      fname,
      lname,
      mobile,
      email,
      address,
      userId,
      parentId,
      userType,
      serviceType,
      userPassword,
      accStatus,
      accExpDate,
      maxUsers,
      maxDuration,
      maxParticipants,
      activeSessions,
      extraAttributes: {
        screen_share,
        video_share,
        live_chat,
        recording,
      },
    });
  }

  async getUserById(token: string, id: string) {
    console.warn(token + "\n" + id);
    const headers = new HttpHeaders({
      token: `${token}`,
      id: `${id}`,
    });
    try {
      return lastValueFrom(
        this.http.get<any>(this.url + "/user/getById/" + id, { headers })
      );
    } catch (error) {
      if (error.status === 404) {
        throw {
          status: error.status,
          message: "Cannot connect with backend. " + error.url + " not found",
        };
      }
    }
  }

  async getUserList(token: string, id: string) {
    console.warn(token + "\n" + id);
    const accId = 102;
    const authKey =
      "AccounteyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDIiLCJpYXQiOjE2ODQwNjE4MjEsImV4cCI6MTY4NDE0ODIyMX0.oDI2pKx1orZ0QlgqTGfRSkvRkocJy65tR6VTGKZTyZU102";
    const headers = new HttpHeaders({
      Token: `${token}`,
      Authorization: "AC1XD9cyYcNxO",
    });
    try {
      return lastValueFrom(
        this.http.get<any>(this.url + "/user/getAll/", { headers })
      );
    } catch (error) {
      if (error.status === 404) {
        throw {
          status: error.status,
          message: "Cannot connect with backend. " + error.url + " not found",
        };
      }
      throw error;
    }
  }
  async getAccountList(token: string, id: string) {
    console.warn(token + "\n" + id);
    const accId = 102;
    const authKey =
      "AccounteyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDIiLCJpYXQiOjE2ODQwNjE4MjEsImV4cCI6MTY4NDE0ODIyMX0.oDI2pKx1orZ0QlgqTGfRSkvRkocJy65tR6VTGKZTyZU102";
    const headers = new HttpHeaders({
      Token: `${token}`,
      Authorization: "AC1XD9cyYcNxO",
    });
    try {
      return lastValueFrom(
        this.http.get<any>(this.url + "/account/getAll", { headers })
      );
    } catch (error) {
      if (error.status === 404) {
        throw {
          status: error.status,
          message: "Cannot connect with backend. " + error.url + " not found",
        };
      }
      throw error;
    }
  }

  async sendSMS(sessionId: string, msisdn: string, callUrl: string) {
    console.log("sms Sent");
    try {
      return this.callRequest(sessionId, "/video/api/sendSms", {
        msisdn,
        callUrl,
      });
    } catch (error) {
      console.log(error);
    }
  }
  async sendWhatsapp(
    sessionId: string,
    msisdn: string,
    callUrl: string,
    from: string,
    type: string,
    templateId: string
  ) {
    console.warn("Whatsapp Message Sent");
    try {
      return this.callRequest(sessionId, "/video/api/sendWhatsapp", {
        msisdn,
        callUrl,
        from,
        type,
        templateId,
      });
    } catch (error) {
      console.log(error);
    }
  }

  async sendNotify(
    title: string,
    body: string,
    sessionId: string,
    msisdn: string
  ) {
    console.warn("Notification Sent");
    try {
      return this.callRequest(sessionId, "/video/api/notification", {
        msisdn,
        sessionId,
        title,
        body,
      });
    } catch (error) {
      console.log(error);
    }
  }
}
