import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { DataService } from 'src/app/Services/Data.service';

@Component({
  selector: 'app-loginpage',
  templateUrl: './loginpage.component.html',
  styleUrls: ['./loginpage.component.css'],
})
export class LoginpageComponent {
  username: string = '';
  phone: string = '';
  constructor(
    private router: Router,
    private aroute: ActivatedRoute,
    private dataService: DataService
  ) {}

  onsubmit() {
    if (this.username.length < 3) {
      alert('Enter Name must have atleast 3 character');
    } else {
      if (this.isValid()) {
        console.log(
          'click on login btn by user ' +
            this.username +
            ' with phone no ' +
            this.phone
        );
        this.dataService.shareusername = this.username;
        this.dataService.sharephone = this.phone;
        this.router.navigate(['/connectpage']);
        //this.router.navigate(['/connectpage'], { queryParams: { username: this.username,phone_no:this.phone } });
      } else {
        alert('Enter Correct Phone No');
      }
    }
  }
  isValid(): any {
    var cleanedNumber = this.phone.replace(/\D/g, '');
    const phoneRegex = /^\d{10}$/; // Matches exactly 10 digits
    return phoneRegex.test(cleanedNumber);
    const phreg = /^(?:(?:\+|0{0,2})91(\s*[\-]\s*)?|[0]?)?[789]\d{9}$/;

    //return phreg.test(cleanedNumber);
  }
} //class close
