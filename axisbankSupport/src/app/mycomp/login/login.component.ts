import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';


import * as XLSX from 'xlsx';
import { RestService } from 'src/app/Services/rest.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  username: string='';
  headers: any[] = [];
  rows: any[] = [];
  // keyy:string[]=[];
  // showTable: boolean = false;
  // address: string = '';
  // upname:string='anil';
  userinfo:string='';
  dateTimeString :string='';
  data!:any;
  // data = [
  //   // Mr. Sanjay Sinha - 9910234718
  //   {
  //     Name: 'Mr. Sanjay Sinha',
  //     'Phone No': '9910234718',
  //     Address: 'SMS Noida',
      
  //     'Last Interaction Mode':'WhatsApp',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'WhatsApp',
  //   },
  //   // Mr. Rajendra Bhandhare - 9833607063
  //   {
  //     Name: 'Mr. Rajendra Bhandhare',
  //     'Phone No': '9833607063',
  //     Address: 'WhatsApp Mumbai',
      
  //     'Last Interaction Mode':'SMS',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'SMS',

  //   },
    
   
  //   //Mr. Narayanan - 9769029125
  //   {
  //     Name: 'Mr. Narayanan',
  //     'Phone No': '9769029125',
  //     Address: 'App Mumbai',
      
  //     'Last Interaction Mode':'CallToApp',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'CallToApp',
  //   },
  //   {
  //     Name: 'Mr. Narayanan',
  //     'Phone No': '9769029125',
  //     Address: 'SMS Mumbai',
  //     'Last Interaction Mode':'SMS',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'SMS',
  //   },
  //   {
  //     Name: 'Mr. Narayanan',
  //     'Phone No': '9769029125',
  //     Address: 'WhatsApp Mumbai',
  //     'Last Interaction Mode':'WhatsApp',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'WhatsApp',
  //   },
  //   //Pallavi - 9773603893
  //   {
  //     Name: 'Pallavi',
  //     'Phone No': '9773603893',
  //     Address: 'App Mumbai',
  //     'Last Interaction Mode':'CallToApp',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'CallToApp',
  //   },
  //   {
  //     Name: 'Pallavi',
  //     'Phone No': '9773603893',
  //     Address: 'SMS Mumbai',
  //     'Last Interaction Mode':'SMS',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M ',
  //     'Send Video Call Link': 'SMS',
  //   },
  //   {
  //     Name: 'Pallavi',
  //     'Phone No': '9773603893',
  //     Address: 'WhatsApp Mumbai',
  //     //  
  //     'Last Interaction Mode':'WhatsApp',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'WhatsApp',
  //   },
  //   {
  //     Name: 'Anil',
  //     'Phone No': '9636189023',
  //     Address: 'APP Rajasthan',
     
  //     'Last Interaction Mode':'CallToApp',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'CallToApp',
  //   },
  //   {
  //     Name: 'Saheemuddin',
  //     'Phone No': '8285955472',
  //     Address: 'SMS Noida',
      
  //     'Last Interaction Mode':'SMS',
  //     'Last Interaction Time':'5/5/2023, 10:44 A.M',
  //     'Send Video Call Link': 'WhatsApp',
  //   },
    
   
  // ];
  constructor(private router: Router,private aroute: ActivatedRoute, private restservice: RestService,private http: HttpClient) {
   
    //  this.data=this.readJSONData();
    //  console.log("data in constru---"+this.data);
    // this.headers = Object.keys( this.data[0]);
    // console.log("headers in constru--"+this.headers);
    // this.rows = this.data;
    // console.log("ro are in constr-->"+this.rows);
    // this.showData();
    this.getData();
    
    
  }
  ngOnInit(): void {
    
    this.aroute.queryParams.subscribe(params => {
      this.username = params['username'];
    });
    console.log("username in table"+this.username);
  }
////------------------------------------


///---------------------------------------
getData() {
  let x=this.getCurrentTime();
  this.http.get<any[]>('assets/data.json?'+x).subscribe(
    (response: any[]) => {
      const firstObject = response[0];
      console.log(firstObject);
      console.log(firstObject['Phone No']);
      
      this.headers = Object.keys( response[0]);
      console.log("headers are in get data -->"+this.headers);
    this.rows = response;
    this.data=response;
    console.log("ro are in get data-->"+this.rows);
      // Perform additional operations with the data
    },
    (error) => {
      console.error(error);
    }
  );
}  
///-------------------------------------------
  readJSONData() {
    
    this.http.get('assets/data.json').subscribe((dataa) => {
      // Process the JSON data here
      console.log(dataa);
      this.headers = Object.keys( dataa);
    this.rows = this.data;
      return dataa;
    });
  }

  // showData() {
  //   // Retrieve the names from the JSON objects
  //   console.log('ros are -->' + this.rows);
  //   const names = this.data.map((obj) => obj.Name);
  //   console.log(names); // Output: ["John", "Jane", "Bob"]

  //   // Retrieve the ages from the JSON objects
  //   const ages = this.data.map((obj) => obj.Address);
  //   console.log(ages); // Output: [25, 30, 35]

  //   // Retrieve a specific property from a JSON object
  //   const firstPersonName = this.data[0].Name;
  //   console.log(firstPersonName); // Output: "John"
  // }

  // onFileChange(evt: any) {
  //   const target: DataTransfer = <DataTransfer>evt.target;
  //   if (target.files.length > 1) {
  //     alert('Multiple files are not allowed');
  //     return;
  //   } else {
  //     const reader: FileReader = new FileReader();
  //     reader.onload = (e: any) => {
  //       const bstr: string = e.target.result;
  //       const wb: XLSX.WorkBook = XLSX.read(bstr, { type: 'binary' });
  //       const wsname = wb.SheetNames[0];
  //       const ws: XLSX.WorkSheet = wb.Sheets[wsname];
  //       let data: any[] = XLSX.utils.sheet_to_json(ws); //return object
  //       // let datah = (XLSX.utils.sheet_to_json(ws,{header:1})); //return 2d array

  //       // Print the Excel Data
  //       // console.log(data);
  //       // console.log(datah[0]);
  //       // this.headers=datah[0];
  //       //for(let i=0;i<datah[0].length;i++)

  //       this.headers = Object.keys(data[0]);
  //       this.rows = data;
  //     };
  //     reader.readAsBinaryString(target.files[0]);
  //   }
  // }

  // toggleShowTable(): void {
  //   this.showTable = !this.showTable;
  // }

  // constructor(private router: Router){
  //   // //../../../assets/userdata.xlsx
  //    const filePath = "../../../assets/userdata.xlsx";
  //    const data = this.readExcelFile(filePath);
  //    this.headers = Object.keys(data[0]);
  //    this.rows = data;
  // }

  // readExcelFile(filePath: string): any[] {
  //   try {
  //     const workbook = XLSX.readFile(filePath);
  //     const sheet_name_list = workbook.SheetNames;
  //     const data = XLSX.utils.sheet_to_json(
  //       workbook.Sheets[sheet_name_list[0]]
  //     );
  //     return data;
  //   } catch (e) {
  //     console.error('error is ' + e);
  //   }
  //   return ['error'];
  // }

  // goToPage(pageName: string): void {
  //   this.router.navigate([`${pageName}`]);
  // }
  ConnectToUserClickByUpdate(phone_no: string, connecttype: string, index: number,header:string):void
  {

    //console.log("ro->"+row+" header"+header);
    
    this.data[index]['Last Interaction Mode']="Video Call";
    
    this.data[index]['Last Interaction Time']=this.getCurrentTime();;
     
  }
  getCurrentTime():string {
    const currentTime = new Date();
    const options: Intl.DateTimeFormatOptions = {  day: '2-digit',
    month: '2-digit',
    year: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true };
     this.dateTimeString = currentTime.toLocaleDateString('en-GB', options);
     //this.dateTimeString = currentTime.toLocaleString();
     console.log('Current Time:', this.dateTimeString);

    return  this.dateTimeString;
    
    
  }



  ConnectToUserClick(phone_no: string, connecttype: string,index: number): void {

    console.log("ConnectToUserClick run");
    this.userinfo="Name: "+this.data[index].Name+","+" PhoneNo: "+this.data[index]['Phone No']
    +","+" Address: "+this.data[index].Address;

    console.log("userinfo  "+this.userinfo);
    console.log('Ph---' + phone_no + ' type--' + connecttype);
    
    this.data[index]['Last Interaction Mode']="Video Call";
    
    this.data[index]['Last Interaction Time']=this.getCurrentTime();;
     ///
      //  let choice=-1;
      //  choice=index%3;
      //  if(choice==0)
      //  connecttype ='SMS'
      //  else if(choice==1)
      //  connecttype == 'WhatsApp'
      //  else 
      //  connecttype == 'CallToApp'

     ///


    if (connecttype == 'SMS') {
      console.log('function call for sms');
      this.SendBySMS(phone_no);
    } else if (connecttype == 'WhatsApp') {
      console.log('function call for WhatsApp');
      this.SendByWhatsApp(phone_no,this.userinfo);
    } else if (connecttype == 'CallToApp') {
      console.log('function call for App');
      this.SendByApp(phone_no);
    }
  }

  async SendBySMS(phone_no: string) {
    console.log(' call api for sms by no' + phone_no);

    const sessionId = 'anilsession';
    const callUrl = '/customers/#/' + sessionId;
    const msisdn = phone_no;
    let messageResponse;

    ///-------------------------
    try {
    
        messageResponse = await this.restservice.sendSMS(
          sessionId,

          phone_no,

          callUrl
        );
      

      console.warn(messageResponse);
      //this.goTo(messageResponse.callUrl);
      // this.goTo(sessionId);
    } catch (error) {
      console.log(error);

      // this.state = error.statusText;

      // this.failedMessage = true;

      // this.failedMessageShow = '';

      // this.timeOut(3000);

      return;
    }

    //this.failedMessage = false;

    if (messageResponse.state == 'SUBMIT_FAILED') {
      // this.state =
      //   'Message Submit failed due to ' + messageResponse.description;

      // this.failedMessage = true;
      console.log('sms messageResponse.status failed');

    } else {
      // this.state = messageResponse.description;

      // this.showDescription = true;

      // this.timeOut(3000);

      console.log('sms messageResponse.status not failed');
      console.log('messageresponse is --->'+messageResponse);

      
      console.log('messageresponse callurl is --->'+messageResponse.callurl);
      this.goTo(messageResponse.callurl);
      //this.goTo(sessionId); //this.goTo("/call", sessionId);
    }

    ////----------------------------
  } //###########sendbysms close

  /////---------------------------------------------------------
  async SendByWhatsApp(phone_no: string,userdetils:string) {
    console.log(' call api for  WhatsApp by no' + phone_no);

    //------------------
    const type = 'template';
    const from = '919811026184';
    const templateId = '53571';
    const sessionId = 'anilsession';
    const callUrl = '/customers/#/' + sessionId;
    const msisdn = phone_no;
    let messageResponse;

    try {
      messageResponse = await this.restservice.sendWhatsapp(
        sessionId,
        msisdn,
        callUrl,
        from,
        type,
        templateId,
        userdetils
      );
    } catch (error) {
      console.log(error);

      // this.state = error.statusText;

      // this.failedMessage = true;

      // this.timeOut(3000);

      return;
    }

    // this.failedMessage = false;

    if (messageResponse.status == 'FAILED') {
      console.log('WhatsApp messageResponse.status failed');
      // this.state = 'Message Submit failed due to ' + messageResponse.message;

      // this.failedMessage = true;
    } else {
      // this.state = messageResponse.message;

      // this.showDescription = true;

      // this.failedMessageShow = '';

      // this.timeOut(3000);

      console.log('WhatsApp messageResponse.status not failed');
      console.log('messageresponse is --->'+messageResponse);

      
      console.log('messageresponse callurl is --->'+messageResponse.callurl);

      console.warn(messageResponse);
      this.goTo(messageResponse.callurl);

      //this.goTo(sessionId); //this.goTo("/call", sessionId);
    }

    //---------------
  } //SendByWhatsApp close

  async SendByApp(phone_no: string) {
    console.log(' call api for app by no' + phone_no);
    let messageResponse;
    //for app
    try {
      messageResponse = await this.restservice.sendNotify(
        'Please join the video call',
        '',
        'axis_session',
        phone_no
      );
    } catch (error) {
      //this.goTo("/call", sessionId);
      console.log("error in catch of this.restservice.sendNotify( ");
      console.log(error);
      //  this.state = error.statusText;
      //  this.failedMessage = true;
      //  this.timeOut(3000); return;
    } //catch close

    if (messageResponse.state == 'SUBMIT_FAILED') {
      // this.state =
      //   'Message Submit failed due to ' + messageResponse.description;

      // this.failedMessage = true;
      console.log('app messageResponse.status failed');

    } else {
      // this.state = messageResponse.description;

      // this.showDescription = true;

      // this.timeOut(3000);

      console.log('app messageResponse.status not failed');
      console.log('messageresponse is --->'+messageResponse);
      console.log('messageresponse callurl is --->'+messageResponse.callurl);
      this.goTo(messageResponse.callurl);
      //this.goTo(sessionId); //this.goTo("/call", sessionId);
    }
    //this.goTo('axis_session');
  }
  //for app close

  //this.goToLink(this.address);
  goTo(windowUrl: string) {
    // url:String="http://172.17.0.122:5000";
    window.open(windowUrl
      ,
      '_blank'
    ); // this.router.navigate([`/${path}`, sessionId]);
  }

  goToLink(url: string) {
    window.open(url, '_blank');
  }
}
