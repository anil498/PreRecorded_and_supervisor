import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, lastValueFrom, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RestService {
  private dialogClosedSource = new Subject<boolean>();
  public dialogClosed$ = this.dialogClosedSource.asObservable();

 // private _token!: string;
 // private _userId!: string;
  private baseHref!: string;
  private url: string;
  //private token!: string;
  //private userId!: string;
  private _token!: string;
  private _authkey!:string;

  constructor(private http: HttpClient) {
    // this.url =
    //   '/' +
    //   (!!window.location.pathname.split('/')[1]
    //     ? window.location.pathname.split('/')[1] + '/'
    //     : '');
       
    // this.url="https://demo2.progate.mobi/dynamicsupport/send/sendNotification";

    //app run by this url
   // this.url = 'https://demo2.progate.mobi/dynamicsupport/';

    
    //mychnage run sms and chat
     //this.url = 'https://demo2.progate.mobi';
    this.url='https://demos.progate.mobi';
    this.getKeys();
  } //constructur close

  getKeys() {
    let x = Math.floor((Math.random() * 100) + 1);
    this.http.get<any[]>('assets/key.json?'+x).subscribe(
      (response: any[]) => {
        
         
        console.log("token key ->"+response[0]['Token']);
        console.log("authorization key -->"+response[0]['Authorization']);
        this._token=response[0]['Token'];
        this._authkey=response[0]['Authorization'];
        },
      (error) => {
        console.error(error);
      }
    );
  }   



  private callRequest(
    sessionId: string,
    path: string,
    body: any
  ): Promise<any> {
    console.log("callrequest body"+body);
    try {
      //app run by this header
      // const headers = {
      //   'Content-Type': 'application/json',
      //   sessionid: sessionId,
      // };
      const headers = new HttpHeaders({
        // Token: `UR101S3yyOTKf5u`,
        Token:this._token,
        // Authorization: 'AC101rFSyWDmFBf',
        Authorization:this._authkey

      });

      return lastValueFrom(
        this.http.post<any>(this.url + path, body, { headers })
      );
    } catch (error:any) {
      console.log('error in call request catch' + error);
        if (error.status === 404) {
          throw {
            status: error.status,
            message: "Cannot connect with backend. " + error.url + " not found",
          };
        }
      throw error;
    }
  } //call request promise close
  //////////---------------------------------------------
  async sendSMS(sessionId: string, msisdn: string, callUrl: string) {
    console.log('sms Sent');

    try {
      return this.callRequest(sessionId, '/VPService/v1/video/api/sendSms', {
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

    templateId: string,
    userInfo:string
  ) {
    console.warn('Whatsapp Message Sent');

    try {
      return this.callRequest(sessionId, '/VPService/v1/video/api/sendWhatsapp', {
        msisdn,

        callUrl,

        from,

        type,

        templateId,
        userInfo
      });
    } catch (error) {
      console.log(error);
    }
  }

  /////////-------------------------------------------------

  async sendNotify(
    title: string,
    body: string,
    sessionId: string,
    msisdn: string
  ) {
    console.log('sendNotify run Notification Sent by data'+title+body+sessionId+msisdn);
    try {
      //===="/video/api/notification"
      ///VPService/v1/video/api/notification
      return this.callRequest(sessionId, '/VPService/v1/video/api/notification', {
        msisdn,
        title,
        body,
      });
    } catch (error) {
      console.log("error in cath this.callRequest of restsevice sendnotify");
      console.log(error);
    }
  } //send notify clsoe
} //class  clsoe



