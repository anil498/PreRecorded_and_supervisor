import { Component } from '@angular/core';
import { Router } from '@angular/router';
import * as XLSX from 'xlsx';
import { RestService } from 'src/app/Services/rest.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  headers: any[] = [];
  rows: any[] = [];
  showTable: boolean = false;
  address:string="";
  constructor(private router: Router,private restservice:RestService){

  }

  onFileChange(evt: any) {
    const target: DataTransfer = <DataTransfer>(evt.target);
    if (target.files.length > 1) {
      alert('Multiple files are not allowed');
      return;
    }
    else {
      const reader: FileReader = new FileReader();
      reader.onload = (e: any) => {
        const bstr: string = e.target.result;
        const wb: XLSX.WorkBook = XLSX.read(bstr, { type: 'binary' });
        const wsname = wb.SheetNames[0];
        const ws: XLSX.WorkSheet = wb.Sheets[wsname];
        let data :any[]= (XLSX.utils.sheet_to_json(ws)); //return object
        // let datah = (XLSX.utils.sheet_to_json(ws,{header:1})); //return 2d array


        
        // Print the Excel Data
        // console.log(data);
        // console.log(datah[0]);
        // this.headers=datah[0];
        //for(let i=0;i<datah[0].length;i++)
      
      this.headers = Object.keys(data[0]);
      this.rows = data;
      }
      reader.readAsBinaryString(target.files[0]);
    }
  }

  toggleShowTable(): void {
    this.showTable = !this.showTable;
}

  // constructor(private router: Router){
  //   // //../../../assets/userdata.xlsx
  //    const filePath = "../../../assets/userdata.xlsx";
  //    const data = this.readExcelFile(filePath);
  //    this.headers = Object.keys(data[0]);
  //    this.rows = data;
  // }
  
  readExcelFile(filePath: string): any[] {
    try{
    const workbook = XLSX.readFile(filePath);
    const sheet_name_list = workbook.SheetNames;
    const data = XLSX.utils.sheet_to_json(workbook.Sheets[sheet_name_list[0]]);
    return data;
    }catch(e)
    {
      console.error("error is "+e);
    }
    return ["error"];
  }

  goToPage(pageName:string):void {
    this.router.navigate([`${pageName}`]);
  }

  

  async callbtnpress()
  {
console.log("click on call btn");
let messageResponse;
//for app
try { 
     messageResponse = await this.restservice.sendNotify( "Please join the video call", "", "axis_session", "9876543210" ); 
     
  } 
   //this.goTo("/call", sessionId); 
   catch (error) { 
    console.log(error);
    //  this.state = error.statusText; 
    //  this.failedMessage = true; 
    //  this.timeOut(3000); return; 
    } //catch close
     
     //this.failedMessage = false; 
     console.warn(messageResponse); 
     //this.goTo("hisessionid"); 
     this.goTo("axis_session");
    }
//for app close


//this.goToLink(this.address);
goTo(sessionId: string) {
   // url:String="http://172.17.0.122:5000";
   window.open("https://demo2.progate.mobi/dynamicsupport/#/call/" + sessionId, '_blank'); // this.router.navigate([`/${path}`, sessionId]); 
  }


  goToLink(url: string){
    window.open(url, "_blank");
}
  
}
