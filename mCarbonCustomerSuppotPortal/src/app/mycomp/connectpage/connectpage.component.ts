import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

import { RestService } from 'src/app/Services/rest.service';

import { HttpClient } from '@angular/common/http';
import { DataService } from 'src/app/Services/Data.service';

@Component({
  selector: 'app-connectpage',
  templateUrl: './connectpage.component.html',
  styleUrls: ['./connectpage.component.css']
})
export class ConnectpageComponent {
  username: string='';
  phone: string='';
  constructor(private router: Router,private aroute: ActivatedRoute,private dataService:DataService,private restService :RestService,private http: HttpClient) {
      
      this.username=this.dataService.shareusername;
      this.phone=this.dataService.sharephone;
  }

  ngOnInit(): void {
    
    
  }

  joinVCCall():void{
    console.log("join video call run" );
    this.ConnectToUserClick(this.username,this.phone);
  }
  ConnectToUserClick(username: string,phone_no: string): void {
    console.log("ConnectToUserClick run with username-->"+username+ " phone no-->"+phone_no );
  }
  joinPhCall():void{
    console.log("join phone call run" );
    this.ConnectToUserClick(this.username,this.phone);
  }


}//class close
