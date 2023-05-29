
// All the Imports

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../services/websocket.service';

@Component({
	selector: 'app-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.scss']
})

export class DashboardComponent implements OnInit {
	//Variable Declarations

	title = 'openvidu-angular';
	name: string;
	session_message: string;

	//Constructor declaring objects

	constructor(
		private router: Router, 
		private webSocketService: WebSocketService
		) {}

	//Main function

	ngOnInit(): void {

		//Connecting to websocket service

		let stompClient = this.webSocketService.connect();
		stompClient.connect({}, (frame) => {});
	}

	// goTo Function Specifies the page to navigate to which path with the SessionID provided as roomName . The path is mentioned in html file .

	goTo(path: string) {

		//Using endpoint Api to send data 

		this.session_message = this.name;
		this.webSocketService.sendname(this.session_message);

		//Navigating to page

		this.router.navigate([`/${path}`, { roomName: 'm-call', name: this.name }]);
		localStorage.removeItem('key');
	}
}
