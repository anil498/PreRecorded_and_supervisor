import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';


import * as XLSX from 'xlsx';
import { RestService } from 'src/app/Services/rest.service';

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
  // data!:any;
  data = [
    {
      Name: 'Prashant',
      'Phone No': '9636189023',
      Address: 'Noida',
      
      'Last Interaction Mode':'SMS',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'SMS',

    },
    {
      Name: 'Vats',
      'Phone No': '9636189023',
      Address: 'Mumbai',
      
      'Last Interaction Mode':'WhatsApp',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'WhatsApp',
    },
    {
      Name: 'Anil',
      'Phone No': '9636189023',
      Address: 'Pune',
      
      'Last Interaction Mode':'CallToApp',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'CallToApp',
    },
    {
      Name: 'Krishna',
      'Phone No': '9639500599',
      Address: 'Chennai',
      'Last Interaction Mode':'SMS',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'SMS',
    },
    {
      Name: 'Aditi',
      'Phone No': '9984392555',
      Address: 'Kanpur',
      'Last Interaction Mode':'WhatsApp',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'WhatsApp',
    },
    {
      Name: 'Divya',
      'Phone No': '9636189023',
      Address: 'Goa',
      'Last Interaction Mode':'CallToApp',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'CallToApp',
    },
    {
      Name: 'Manoj',
      'Phone No': '9639500599',
      Address: 'Kota',
      'Last Interaction Mode':'SMS',
      'Last Interaction Time':'5/5/2023, 10:44 A.M ',
      'Send Video Call Link': 'SMS',
    },
    {
      Name: 'Shiv',
      'Phone No': '9984392555',
      Address: 'Delhi',
      //  
      'Last Interaction Mode':'WhatsApp',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'WhatsApp',
    },
    {
      Name: 'Vishnu',
      'Phone No': '9636189023',
      Address: 'Rajasthan',
     
      'Last Interaction Mode':'CallToApp',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'CallToApp',
    },
    {
      Name: 'Gunjan',
      'Phone No': '9639500599',
      Address: 'Noida',
      
      'Last Interaction Mode':'SMS',
      'Last Interaction Time':'5/5/2023, 10:44 A.M',
      'Send Video Call Link': 'SMS',
    },
    
   
  ];
  constructor(private router: Router,private aroute: ActivatedRoute, private restservice: RestService) {
    this.headers = Object.keys(this.data[0]);
    this.rows = this.data;
    // this.showData();
  }
  ngOnInit(): void {
    
    this.aroute.queryParams.subscribe(params => {
      this.username = params['username'];
    });
    console.log("username in table"+this.username);
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

    this.userinfo="Name: "+this.data[index].Name+","+" PhoneNo: "+this.data[index]['Phone No']
    +","+" Address: "+this.data[index].Address;

    console.log("userinfo  "+this.userinfo);
    console.log('Ph---' + phone_no + ' type--' + connecttype);
    
    this.data[index]['Last Interaction Mode']="Video Call";
    
    this.data[index]['Last Interaction Time']=this.getCurrentTime();;
    
    if (connecttype == 'SMS') {
      console.log('function call for sms');
      this.SendBySMS(phone_no);
    } else if (connecttype == 'WhatsApp') {
      console.log('function call for WhatsApp');
      this.SendByWhatsApp(phone_no);
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
  async SendByWhatsApp(phone_no: string) {
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
        templateId
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
        '9876543210'
      );
    } catch (error) {
      //this.goTo("/call", sessionId);
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
