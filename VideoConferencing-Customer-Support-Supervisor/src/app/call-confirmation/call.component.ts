//All the Imports

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WebSocketService } from '../services/websocket.service';
import { RestService } from '../services/rest.service';

@Component({
	selector: 'app-call',
	templateUrl: './call.component.html',
	styleUrls: ['./call.component.scss']
})
export class CallComponent implements OnInit {
	
	//Variable declaration

	sessionId: string;
	getName: string;
	path: string;
	id: string;
	showLoading: boolean = true;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private webSocketService: WebSocketService,
		private restService: RestService
	) {}

	async ngOnInit() {

		// Recieve Parameters from previous page

		this.sessionId = this.route.snapshot.paramMap.get('roomName');
		this.getName = this.route.snapshot.paramMap.get('name');
		if (this.getName == 'undefined') {
			this.getName = 'Customer';
		}
		this.path = 'call-customer';

		const body = {
			notifyTo: 'support',
			sessionId: this.sessionId
		};

		// Post Request body

		this.restService.postRequest(this.sessionId, body);

		//connect to Websocket Connection

		let stompClient = this.webSocketService.connect();
		stompClient.connect({}, (frame) => {

			// Subscribe to callconfirmation topic

			stompClient.subscribe('/topic/callconfirmation', (notifications) => {

				this.id = JSON.parse(notifications.body).sessionId;
				console.warn('Successfully Recieved Notification : ' + this.id);
				
				if (this.id == 'confirmed') {
					console.warn('Call Confirmed');
					this.router.navigate([`/${this.path}`, { roomName: this.sessionId, name: this.getName }]);
				} else if (this.id == 'notconfirmed') {
					console.warn('Call Not Confirmed');
					this.showLoading = false;
				}
			});
		});

		setTimeout(() => {
			this.showLoading = false;
		}, 60000); // 1 minute in milliseconds
	}

	//Exit function to go to the index page

	exit(path: string) {
		this.router.navigate([`/${path}`]);
	}
}
