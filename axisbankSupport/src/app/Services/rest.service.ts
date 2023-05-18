import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, lastValueFrom, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RestService {
  private dialogClosedSource = new Subject<boolean>();
  public dialogClosed$ = this.dialogClosedSource.asObservable();

  private _token!: string;
  private _userId!: string;
  private baseHref!: string;
  private url: string;
  private token!: string;
  private userId!: string;

  constructor(private http: HttpClient) {
    this.url =
      '/' +
      (!!window.location.pathname.split('/')[1]
        ? window.location.pathname.split('/')[1] + '/'
        : '');
       
    // this.url="https://demo2.progate.mobi/dynamicsupport/send/sendNotification";

    //app run by this url
   // this.url = 'https://demo2.progate.mobi/dynamicsupport/';

    
    //mychnage run sms and chat
    //this.url = 'http://172.17.0.122:5000';
  } //constructur close

  private callRequest(
    sessionId: string,
    path: string,
    body: any
  ): Promise<any> {
    console.warn(body);
    try {
      //app run by this header
      // const headers = {
      //   'Content-Type': 'application/json',
      //   sessionid: sessionId,
      // };
      const headers = new HttpHeaders({
        Token: `UR003sQnEP5XGCC`,

        Authorization: 'AC003WgfiPGR6cG',
      });

      return lastValueFrom(
        this.http.post<any>(this.url + path, body, { headers })
      );
    } catch (error) {
      console.log('error' + error);
      //   if (error.status === 404) {
      //     throw {
      //       status: error.status,
      //       message: "Cannot connect with backend. " + error.url + " not found",
      //     };
      //   }
      throw error;
    }
  } //call request promise close
  //////////---------------------------------------------
  async sendSMS(sessionId: string, msisdn: string, callUrl: string) {
    console.log('sms Sent');

    try {
      return this.callRequest(sessionId, '/video/api/sendSms', {
        msisdn,

        // callUrl,
      });
    } catch (error) {
      console.log(error);
    }
  }

  ///////////-----------------------------------------------
  async sendWhatsapp(
    sessionId: string,

    msisdn: string,

    callUrl: string,

    from: string,

    type: string,

    templateId: string
  ) {
    console.warn('Whatsapp Message Sent');

    try {
      return this.callRequest(sessionId, '/video/api/sendWhatsapp', {
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

  /////////-------------------------------------------------

  async sendNotify(
    title: string,
    body: string,
    sessId: string,
    phoneNumber: string
  ) {
    console.warn('Notification Sent');
    try {
      //===="/video/api/notification"
      return this.callRequest(sessId, 'send/sendNotification', {
        phoneNumber,
        sessId,
        title,
        body,
      });
    } catch (error) {
      console.log(error);
    }
  } //send notify clsoe
} //class  clsoe
