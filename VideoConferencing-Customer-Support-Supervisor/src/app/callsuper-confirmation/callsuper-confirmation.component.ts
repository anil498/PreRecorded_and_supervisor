import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WebSocketService } from '../services/websocket.service';
import { RestService } from '../services/rest.service';

@Component({
	selector: 'superconfirmation-call',
	templateUrl: './callsuper-confirmation.component.html',
	styleUrls: ['./callsuper-confirmation.component.scss']
})
export class CallSuperConfirmationComponent implements OnInit {
	sessionId: string;
	getName: string;
	path: string;
	id: string;
	showLoading: boolean = true;
	response: string;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private webSocketService: WebSocketService,
		private restService: RestService
	) {}

	async ngOnInit() {
		// Recieve Parameters from previous page

		// this.sessionId = this.route.snapshot.paramMap.get('roomName');
		
		this.route.queryParams.subscribe(params => {
			this.sessionId = params['roomName'];
		  });

		this.path = 'call-support';

		const id = this.sessionId + '_2';
		const body = {
			notifyTo: 'supervisor',
			sessionId: id
		};

		// Post Request body

		this.restService.postRequest(id, body);

		//connect to Websocket Connection

		let stompClient = this.webSocketService.connect();

		stompClient.connect({}, (frame) => {

			// Subscribe to callconfirmation topic
			
			stompClient.subscribe('/topic/supercallconfirmation', (notifications) => {
				this.response = JSON.parse(notifications.body).sessionId;
				console.warn('Successfully Recieved Notification : ' + this.response);
				if (this.response == 'confirmed') {
					console.warn('Call Confirmed');

					const session_message = 'hidebutton';
					this.webSocketService.hide(session_message);

					const session_message2 = 'callhold';
					this.webSocketService.alert(session_message2);
					
					this.router.navigate([`/call-support/${id}`]);
				} else if (this.response == 'notconfirmed') {
					console.warn('Call Not Confirmed');
					this.showLoading = false;
				}
			});

		});

		setTimeout(() => {
			this.showLoading = false;
		}, 60000); // 1 minute in milliseconds
	}

	exit(path: string) {
		window.close();
	}
}
