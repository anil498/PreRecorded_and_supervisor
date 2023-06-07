import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, lastValueFrom, Subject } from "rxjs";
import { environment } from "environments/environment.prod";
@Injectable({
  providedIn: "root",
})
export class RestService {
  private dialogClosedSource = new Subject<boolean>();
  public dialogClosed$ = this.dialogClosedSource.asObservable();

  private authKey:string;
  private _response: any;
  private _token: string;
  private _userId: string;
  private baseHref: string;
  private url: string;
  private url1: string;
  private token: string;
  private userId: string;

  constructor(private http: HttpClient, private router: Router) {
    // this.baseHref = '/' + (!!window.location.pathname.split('/')[1] ? window.location.pathname.split('/')[1] + '/VPService/v1/' : '');
    //this.baseHref = "https://demo2.progate.mobi/VPService/v1/";
    this.baseHref = environment.url;
    //this.baseHref = "http://172.17.0.122:5000/VPService/v1/";
    this.getHeaders();
    this.http.get<any>("assets/json/headers.json").subscribe((response) =>{
      this.authKey = response.authKey;
    })
  }

  getHeaders() {
    if (this._response == null) {
      this.router.navigate([""]);
    }
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

  private postRequest1(path: string, body: any): Promise<any> {
    console.warn(this.baseHref + "User/" + path);
    console.warn(body);
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
    try {
      const headers = new HttpHeaders({
        Authorization: `${this.authKey}`,
        Token: `${this._token}`,
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

  private putRequest1(path: string, body: any): Promise<any> {
    console.warn(this.baseHref + "User/" + path);
    console.warn(body);
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

  private callRequest(
    path: string,
    body: any
  ): Promise<any> {
    console.warn(body);
    try {
      const headers = new HttpHeaders({
        Token: `${this._token}`,
        Authorization: `${this.authKey}`,
      });

      console.warn(headers);
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
    this.setData(null);
    this.setToken(null);
    this.setUserId(null);
    localStorage.clear();
    sessionStorage.clear();
    location.reload();
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

    password: string,
    accessId: number[],

    max_duration: number,
    max_participants: number,
    max_active_sessions: number,

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
    userId: number,
    fname: string,
    lname: string,
    contact: number,
    email: string,
    loginId: string,
    expDate: string,

    password: string,
    accessId: number[],

    max_duration: number,
    max_participants: number,
    max_active_sessions: number,

    features: number[],
    featuresMeta: any
  ) {
    let path = type + "/" + userId;
    return this.putRequest1(type, {
      fname,
      lname,
      contact,
      email,
      loginId,
      expDate,
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

  async updateAccount(
    type: string,
    accountId: string,
    name: string,
    address: string,
    logo: Blob,
    maxUser: number,
    expDate: string,
    creationDate: string,
    max_duration: number,
    max_participants: number,
    max_active_sessions: number,
    accessId: number[],

    features: number[],
    featureMeta: any
  ) {
    let path = type + "/" + accountId;
    return this.putRequest2(type, {
      accountId,
      name,
      address,
      maxUser,
      expDate,
      creationDate,

      accessId,
      session: {
        max_duration,
        max_participants,
        max_active_sessions,
      },

      features,
      featureMeta,
    });
  }

  // async getUserById(token: string) {
  //   console.warn(token + "\n");
  //   const headers = new HttpHeaders({
  //     Token: `${token}`,
  //     Authorization: `${this.authKey}`,
  //   });
  //   try {
  //     return lastValueFrom(
  //       this.http.get<any>(
  //         this.baseHref +
  //           "User/getById/" +
  //           `${this._response.user_data.userId}`,
  //         { headers }
  //       )
  //     );
  //   } catch (error) {
  //     if (error.status === 404) {
  //       throw {
  //         status: error.status,
  //         message: "Cannot connect with backend. " + error.url + " not found",
  //       };
  //     }
  //   }
  // }

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

  async deleteAccount(id: number) {
    this.deleteRequest(`Account/Delete/${id}`);
  }

  async deleteUser(id: number) {
    this.deleteRequest(`User/Delete/${id}`);
  }

  async sendSMS(msisdn: string,getLink: any) {
    console.log("sms Sent");
    try {
      return this.callRequest("Session/CreateAndSendLink/SMS", {
        msisdn,
        getLink
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
        getLink
      });
    } catch (error) {
      console.log(error);
    }
  }

  async sendNotify(
    title: string,
    body: string,
    msisdn: string,
    getLink: any
  ) {
    console.warn("Notification Sent");
    try {
      return this.callRequest("Session/CreateAndSendLink/AppNotification", {
        msisdn,
        title,
        body,
        getLink
      });
    } catch (error) {
      console.log(error);
    }
  }
}
