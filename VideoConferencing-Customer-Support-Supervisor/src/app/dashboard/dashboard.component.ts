import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RestService } from '../services/rest.service';
import { WebSocketService } from '../services/websocket.service';

@Component({
	selector: 'app-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
	title = 'openvidu-angular';
	name: string;
	session_message: string;

	constructor(
		private router: Router, 
		private restService: RestService, 
		private webSocketService: WebSocketService) {}

	ngOnInit(): void {
		let stompClient = this.webSocketService.connect();
		stompClient.connect({}, (frame) => {
	});
	}

	// goTo Function Specifies the page to navigate to which path with the SessionID provided as roomName . The path is mentioned in html file .

	goTo(path: string) {

		this.session_message = this.name;
		this.webSocketService.sendname(this.session_message);

		this.router.navigate([`/${path}`, { roomName: 'm-call', name: this.name }]);
		localStorage.removeItem('key');

		
	}
}
