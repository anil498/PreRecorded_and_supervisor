import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, lastValueFrom, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RestService {
  private dialogClosedSource = new Subject<boolean>();
  public dialogClosed$ = this.dialogClosedSource.asObservable();

 
  private baseHref!: string;
  private url!: string;

  private smspath!:string;
  private whatsapppath!:string;
  private apppath!:string;
  
  private _token!: string;
  private _authkey!:string;

  constructor(private http: HttpClient) {
    // this.url =
    //   '/' +
    //   (!!window.location.pathname.split('/')[1]
    //     ? window.location.pathname.split('/')[1] + '/'
    //     : '');
       
    
    //app run by this url
   // this.url = 'https://demo2.progate.mobi/dynamicsupport/';

    
    //mychnage run sms and chat
    // this.url = 'https://demo2.progate.mobi';
    //this.url='https://demos.progate.mobi';
    this.getKeys();
  } //constructur close

  getKeys() {
    let x = Math.floor((Math.random() * 100) + 1);
    this.http.get<any[]>('assets/key.json?'+x).subscribe(
      (response: any) => {
        
         
        console.log("token key ->"+response['Token']);
        console.log("authorization key -->"+response['Authorization']);
        this._token=response['Token'];
        this._authkey=response['Authorization'];

        this.url=response['URL'];
        console.log("url by json file--> "+this.url);
        this.smspath=response['SMSpath'];
        console.log("url by json file--> "+this.smspath);
        this.whatsapppath=response['WHATSAPPpath'];
        console.log("url by json file--> "+this.whatsapppath);
        this.apppath=response['APPpath'];
        console.log("url by json file--> "+this.apppath);
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
  async sendSMS(sessionId: string, msisdn: string, callUrl: string,getLink:string,participantName:string,description:string) {
    console.log('sms Sent');

    try {
      return this.callRequest(sessionId, this.smspath, {
        msisdn,getLink,participantName,description

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
    description:string,
    getLink:string,
    participantName:string
  ) {
    console.warn('Whatsapp Message Sent');

    try {
      return this.callRequest(sessionId, this.whatsapppath, {
        msisdn,

        callUrl,

        from,

        type,

        templateId,
        participantName,
        description,
        getLink
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
    msisdn: string,
    getLink:string,
    description:string,
    participantName:string
  ) {
    console.log('sendNotify run Notification Sent by data'+title+body+sessionId+msisdn);
    try {
      //===="/video/api/notification"
      ///VPService/v1/video/api/notification
      return this.callRequest(sessionId, this.apppath, {
        msisdn,
        title,
        body,
        getLink,
        participantName,
        description,

      });
    } catch (error) {
      console.log("error in cath this.callRequest of restsevice sendnotify");
      console.log(error);
    }
  } //send notify clsoe
} //class  clsoe



