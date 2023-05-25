import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

import { RestService } from 'src/app/Services/rest.service';

import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-connectpage',
  templateUrl: './connectpage.component.html',
  styleUrls: ['./connectpage.component.css']
})
export class ConnectpageComponent {
  username: string='';
  phone: string='';
  constructor(private router: Router,private aroute: ActivatedRoute,private restService :RestService,private http: HttpClient) {
     
  }

  ngOnInit(): void {
    
    this.aroute.queryParams.subscribe(params => {
      this.username = params['username'];
      this.phone=params['phone_no'];
    });
    console.log("username in table"+this.username);
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
