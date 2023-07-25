import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import {
  catchError,
  lastValueFrom,
  Subject,
  Subscribable,
  Subscription,
} from "rxjs";
import { authKey, baseHref } from "app/app.component";
import { ObserverService } from "./observer.service";
@Injectable({
  providedIn: "root",
})
export class RestService {
  private dialogClosedSource = new Subject<boolean>();
  public dialogClosed$ = this.dialogClosedSource.asObservable();
  username = "";
  private authKey: string;
  private _response: any;
  private _token: string;
  private _userId: string;
  private baseHref: string;
  private url: string;
  private url1: string;
  private token: string;
  private userId: string;

  private urlSub: Subscription;
  private authKeySub: Subscription;
  constructor(
    private http: HttpClient,
    private router: Router,
    private observerService: ObserverService
  ) {
    // this.baseHref = '/' + (!!window.location.pathname.split('/')[1] ? window.location.pathname.split('/')[1] + '/VPService/v1/' : '');
    //this.baseHref = "https://demo2.progate.mobi/VPService/v1/";
    //this.baseHref = "http://172.17.0.122:5000/VPService/v1/";
    //this.authKey = "AC001CBoh3eACy9";
    //this.authKey = authKey;
    //this.baseHref = baseHref;

    this.urlSub = this.observerService.urlObs.subscribe((url) => {
      this.baseHref = url;
      console.log(this.baseHref);
    });
    this.authKeySub = this.observerService.authKeyObs.subscribe((auth) => {
      this.authKey = auth;
      console.log(this.authKey);
    });
  }

  async onReload() {
    console.log(localStorage);
    console.log(localStorage.getItem("loginId"));
    console.log(localStorage.getItem("password"));
    const body = {
      loginId: localStorage.getItem("loginId"),
      password: localStorage.getItem("password"),
    };
    const loginResponse = await this.loginRequest("login", body);
    this.setData(loginResponse);
    this.setToken(this.token);
    this.setAuthKey(loginResponse.auth_key);
    this.setUserId(this.username);
  }
  async getHeaders(authKey, baseHref) {
    if (this._response == null) {
      this.authKey = authKey;
      this.baseHref = baseHref;
      console.log(this.baseHref);
      console.log(this.authKey);
      this.onReload();

      // this.router.navigate([""]);
    }
  }

  setData(response: any) {
    this._response = response;
  }

  setToken(value: string) {
    this._token = value;
  }

  setAuthKey(value: string) {
    this.authKey = value;
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

  uploadVideo(name: string, login_id: string, formData: FormData) {
    console.log(name + login_id);
    console.log("Uploading Video");
    try {
      const headers = new HttpHeaders({
        name: `${name}`,
        loginId: `${login_id}`,
      });
      console.log(headers);
      this.http
        .post(this.baseHref + "User/getVideo", formData, {
          headers,
        })
        .subscribe(
          (response) => {
            console.log("video Uploaded");
          },
          (error) => {
            console.log("video not uploaded");
          }
        );
    } catch (error) {
      console.log("VIDEO ERROR");
      console.log(error);
    }
  }

  private loginRequest(path: string, body: any): Promise<any> {
    console.warn(this.baseHref + "/" + path);
    console.warn(this.authKey);
    console.warn(body);
    try {
      const headers = {
        Authorization: `${this.authKey}`,
        "Content-Type": "application/json",
      };
      return lastValueFrom(
        this.http.post<any>(this.baseHref + "User/" + path, body, { headers })
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

  private postRequest(path: string, body: any): Promise<any> {
    console.warn(this.baseHref + path);
    console.warn(body);
    console.warn(this.authKey);
    try {
      const headers = new HttpHeaders({
        Authorization: `${this.authKey}`,
        Token: `${this._token}`,
      });
      return lastValueFrom(
        this.http.post<any>(this.baseHref + path, body, { headers })
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
    console.warn(this.baseHref + "User/" + path);
    console.warn(body);
    console.warn(this.authKey);
    try {
      const headers = new HttpHeaders({
        Authorization: `${this.authKey}`,
        Token: `${this._token}`,
      });
      return lastValueFrom(
        this.http.post<any>(this.baseHref + "User/" + path, body, { headers })
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
    console.warn(this.baseHref + "Account/" + path);
    console.warn(body);
    console.warn(this.authKey);
    try {
      const headers = new HttpHeaders({
        Authorization: `${this.authKey}`,
        Token: `${this._token}`,
        "Content-Type": "application/json",
      });
      return lastValueFrom(
        this.http.post<any>(this.baseHref + "Account/" + path, body, {
          headers,
        })
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

  private putRequest(path: string, body: any): Promise<any> {
    console.warn(this.baseHref + path);
    console.warn(body);
    console.warn(this.authKey);
    try {
      const headers = new HttpHeaders({
        Authorization: `${this.authKey}`,
        Token: `${this._token}`,
      });
      return lastValueFrom(
        this.http.put<any>(this.baseHref + path, body, { headers })
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
  private putRequest1(path: string, body: any): Promise<any> {
    console.warn(this.baseHref + "User/" + path);
    console.warn(body);
    console.warn(this.authKey);
    try {
      const headers = new HttpHeaders({
        Authorization: `${this.authKey}`,
        Token: `${this._token}`,
      });
      return lastValueFrom(
        this.http.put<any>(this.baseHref + "User/" + path, body, { headers })
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

  private putRequest2(path: string, body: any): Promise<any> {
    console.warn(this.baseHref + "Account/" + path);
    console.warn(body);
    console.warn(this.authKey);
    try {
      const headers = new HttpHeaders({
        Authorization: `${this.authKey}`,
        Token: `${this._token}`,
      });
      return lastValueFrom(
        this.http.put<any>(this.baseHref + "Account/" + path, body, {
          headers,
        })
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

  private deleteRequest(path: string): Promise<any> {
    console.warn(this.baseHref + path);
    try {
      const headers = new HttpHeaders({
        Authorization: `${this.authKey}`,
        Token: `${this._token}`,
      });
      return lastValueFrom(
        this.http.delete<any>(this.baseHref + path, { headers })
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

  private callRequest(path: string, body: any): Promise<any> {
    console.warn(body);
    console.warn(this.authKey);
    try {
      const headers = new HttpHeaders({
        Token: `${this._token}`,
        Authorization: `${this.authKey}`,
      });
      return lastValueFrom(
        this.http.post<any>(this.baseHref + path, body, { headers })
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

  logout() {
    console.log("Clearing DATA>>>>>>>>>>>>");
    this.setData(null);
    this.setToken(null);
    this.setUserId(null);
    localStorage.clear();
    sessionStorage.clear();
    //location.reload();
    this.router.navigate([""]);
  }

  async createUser(
    type: string,

    fname: string,
    lname: string,
    contact: number,
    email: string,
    loginId: string,
    expDate: string,
    logo: any,

    password: string,
    accessId: number[],

    max_active_sessions: number,
    max_duration: number,
    max_participants: number,

    features: number[],
    featuresMeta: any
  ) {
    return this.postRequest1(type, {
      fname,
      lname,
      contact,
      email,
      loginId,
      expDate,
      logo,

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

  async updateUser(
    type: string,
    accountId: number,
    userId: number,
    fname: string,
    lname: string,
    contact: number,
    email: string,
    loginId: string,
    logo: any,
    expDate: string,

    accessId: number[],

    max_active_sessions: number,
    max_duration: number,
    max_participants: number,

    features: number[],
    featuresMeta: any,
    icdcId: number
  ) {
    return this.putRequest1(type, {
      accountId,
      userId,
      fname,
      lname,
      contact,
      email,
      loginId,
      logo,
      expDate,
      accessId,

      session: {
        max_duration,
        max_participants,
        max_active_sessions,
      },

      features,
      featuresMeta,
      icdcId,
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
      logo,
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

  async updateAccount(
    type: string,
    accountId: string,
    name: string,
    address: string,
    logo: Blob,
    maxUser: number,
    expDate: string,
    creationDate: string,
    max_active_sessions: number,
    max_duration: number,
    max_participants: number,

    accessId: number[],

    features: number[],
    featuresMeta: any,
    icdcId: number
  ) {
    return this.putRequest2(type, {
      accountId,
      name,
      address,
      maxUser,
      expDate,
      creationDate,
      logo,

      accessId,
      session: {
        max_duration,
        max_participants,
        max_active_sessions,
      },

      features,
      featuresMeta,
      icdcId,
    });
  }

  async getUserList(token: string, id: string) {
    console.warn(token + "\n" + id);
    const headers = new HttpHeaders({
      Token: `${token}`,
      Authorization: `${this.authKey}`,
    });
    try {
      return lastValueFrom(
        this.http.get<any>(this.baseHref + "User/Child/", { headers })
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
    const headers = new HttpHeaders({
      Token: `${token}`,
      Authorization: `${this.authKey}`,
    });
    try {
      return lastValueFrom(
        this.http.get<any>(this.baseHref + "Account/GetAll", { headers })
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

  async getSessionList(token: string, id: string) {
    console.warn(token + "\n" + id);
    const headers = new HttpHeaders({
      Token: `${token}`,
      Authorization: `${this.authKey}`,
    });
    try {
      return lastValueFrom(
        this.http.get<any>(this.baseHref + "Session/GetAll/", { headers })
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

  async getFeatureList(token: string, id: string) {
    console.warn(token + "\n" + id);
    const headers = new HttpHeaders({
      Token: `${token}`,
      Authorization: `${this.authKey}`,
    });
    try {
      return lastValueFrom(
        this.http.get<any>(this.baseHref + "Feature/GetAll/", { headers })
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
  async getAccessList(token: string, id: string) {
    console.warn(token + "\n" + id);
    const headers = new HttpHeaders({
      Token: `${token}`,
      Authorization: `${this.authKey}`,
    });
    try {
      return lastValueFrom(
        this.http.get<any>(this.baseHref + "Access/GetAll/", { headers })
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
  async createAccess(
    type: string,
    pId: number,
    accessId: number,
    seq: number,
    name: string,
    systemName: string
  ) {
    return this.postRequest(type, {
      pId,
      accessId,
      seq,
      name,
      systemName,
    });
  }

  async updateAccess(
    type: string,
    pId: number,
    accessId: number,
    seq: number,
    name: string,
    systemName: string,
    status: number
  ) {
    return this.putRequest(type, {
      pId,
      accessId,
      seq,
      name,
      systemName,
      status,
    });
  }
  async createFeature(
    type: string,
    featureId: number,
    name: string,
    metaList: any[]
  ) {
    return this.postRequest(type, {
      featureId,
      name,
      metaList,
    });
  }

  async updateFeature(
    type: string,
    featureId: number,
    name: string,
    metaList: any[],
    status: number
  ) {
    return this.putRequest(type, {
      featureId,
      name,
      metaList,
      status,
    });
  }
  async deleteAccess(accessId: number) {
    return await this.deleteRequest(`Access/Delete/${accessId}`);
  }
  async deleteFeature(featureId: number) {
    return await this.deleteRequest(`Feature/Delete/${featureId}`);
  }

  async deleteAccount(id: number) {
    return await this.deleteRequest(`Account/Delete/${id}`);
  }

  async deleteUser(id: number) {
    return await this.deleteRequest(`User/Delete/${id}`);
  }

  async sendSMS(msisdn: string, getLink: any) {
    console.log("sms Sent");
    try {
      return this.callRequest("Session/CreateAndSendLink/SMS", {
        msisdn,
        getLink,
      });
    } catch (error) {
      console.log(error);
    }
  }
  async sendWhatsapp(
    msisdn: string,
    from: string,
    type: string,
    templateId: string,
    getLink: any
  ) {
    console.warn("Whatsapp Message Sent");
    try {
      return this.callRequest("Session/CreateAndSendLink/WhatsApp", {
        msisdn,
        from,
        type,
        templateId,
        getLink,
      });
    } catch (error) {
      console.log(error);
    }
  }

  async sendNotify(title: string, body: string, msisdn: string, getLink: any) {
    console.warn("Notification Sent");
    try {
      return this.callRequest("Session/CreateAndSendLink/AppNotification", {
        msisdn,
        title,
        body,
        getLink,
      });
    } catch (error) {
      console.log(error);
    }
  }

  async joinSession(
    path: string,
    contactArray: string,
    sendTo: string,
    sessionId: string
  ) {
    try {
      return this.callRequest(path, {
        contactArray,
        sendTo,
        sessionId,
      });
    } catch (error) {
      console.log(error);
    }
  }

  async createForm(path: string, formName: string, icdcData: any) {
    try {
      return this.postRequest(path, {
        formName,
        icdcData,
      });
    } catch (error) {
      console.log(error);
    }
  }

  async getIcdcData(path: string, userId: number, accountId: number) {
    const headers = new HttpHeaders({
      Token: `${this._token}`,
      Authorization: `${this.authKey}`,
      "Content-Type": "application/json",
    });
    var body;
    if (userId == null) {
      body = {
        accountId,
      };
    } else {
      body = {
        userId,
        accountId,
      };
    }
    console.log(body);
    try {
      return lastValueFrom(
        this.http.post<any>(this.baseHref + path, body, { headers })
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
}
