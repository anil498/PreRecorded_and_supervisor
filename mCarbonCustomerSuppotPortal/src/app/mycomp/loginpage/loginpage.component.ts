import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-loginpage',
  templateUrl: './loginpage.component.html',
  styleUrls: ['./loginpage.component.css']
})
export class LoginpageComponent {

  username: string="";
  phone: string="";
  constructor(private router: Router,private aroute: ActivatedRoute){

  }

  onsubmit()
  {
    console.log("click on login btn by user "+this.username);
    //this.router.navigate(['/table']);
    this.router.navigate(['/connectpage'], { queryParams: { username: this.username,phone_no:this.phone } });

  }

}//class close
