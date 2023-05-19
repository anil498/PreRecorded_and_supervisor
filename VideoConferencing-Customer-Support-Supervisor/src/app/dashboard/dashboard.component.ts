import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RestService } from '../services/rest.service';

@Component({
	selector: 'app-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
	title = 'openvidu-angular';
	name: string ;
	constructor(private router: Router , private restService: RestService) {}

	ngOnInit(): void {}

// goTo Function Specifies the page to navigate to which path with the SessionID provided as roomName . The path is mentioned in html file .

	goTo(path: string) {
		


		this.router.navigate([`/${path}`, {roomName: 'daily-call2' , name: this.name}]);
		
		// const body = {
		// 	notifyTo: 'support',
		// 	sessionId: 'daily-call'
		// };

		// this.restService.postRequest('daily-call' , body);
		
	}

	EnterName(value: string){
		this.name = value;
	}
}
