import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent {

  constructor(private router: Router){

  }

  onsubmit()
  {
    console.log("click on login btn");
    this.router.navigate(['/table']);
  }
  
}
