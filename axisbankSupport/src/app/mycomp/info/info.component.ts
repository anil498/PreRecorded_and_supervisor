import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { DataService } from 'src/app/Services/Data.service';


@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent {

  username: string='';
  constructor(private router: Router,private aroute: ActivatedRoute,private dataService:DataService){

  }

  onsubmit()
  {
    console.log("click on login btn by user "+this.username);
    this.dataService.shareusername=this.username;
    this.router.navigate(['/table']);
    //this.router.navigate(['/table'], { queryParams: { username: this.username } });

  }
  
}
