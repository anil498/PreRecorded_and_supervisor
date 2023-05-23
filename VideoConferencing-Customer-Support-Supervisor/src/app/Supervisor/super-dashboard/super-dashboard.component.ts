
//All the imports

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../../services/websocket.service';
import { ConfirmationDialogService } from '../../confirmation-dialog/confirmation-dialog.service';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog.component';

@Component({
	selector: 'super-dashboard',
	templateUrl: './super-dashboard.component.html',
	styleUrls: ['./super-dashboard.component.css']
})
export class SuperDashboardComponent implements OnInit {

	//Declaring variables

	title = 'openvidu-angular';
	message: any;
	name: string;
	sessionId: string;
	sessionId1: string;
	busy: boolean;
	open: boolean;
	count: boolean;
	checkid: string = null;
	session_message: string;
	count2: any = 0;

	//Constructor to declare objects

	constructor(
		private router: Router,
		private webSocketService: WebSocketService,
		private confirmationDialogService: ConfirmationDialogService
	) {}

	//Main Function

	ngOnInit(): void {
		if (this.webSocketService.status != true) {
			
			let stompClient = this.webSocketService.connect();
			stompClient.connect({}, (frame) => {

				// Subscribe to topic/supervisor

				stompClient.subscribe('/topic/supervisor', (notifications) => {

					console.log(this.router.url);
					this.sessionId1 = JSON.parse(notifications.body).sessionId;
					this.count = JSON.parse(notifications.body).count;
					console.log(this.count);

					if (this.router.url === '/super' && this.open != true) {

						this.sessionId = JSON.parse(notifications.body).sessionId;
						this.openConfirmationDialog();
						console.warn('Randomn : ' + this.sessionId);
						this.open = true;
						this.onMessageReceived(notifications.body);

					} else if (this.count != true) {

						console.log(this.count);
						this.webSocketService.available(this.sessionId1);
					}
				});
			});
		}
	}

	//Open COnfirmation Dialog Function

	public openConfirmationDialog() {

		console.log('Confirmation Dialog');

		this.confirmationDialogService
			.confirm('Support Video Call Request..', 'Do you really want to Join ... ?')
			.then((confirmed) => {
				console.log('User confirmed:', confirmed);

				if (confirmed) {

					console.warn("SessionID : "+this.sessionId);
					this.goTo('call-super');
					this.session_message = 'confirmed';
					this.webSocketService.confirmation(this.session_message);
					this.busy = true;
					this.open = true;

				} else {

					this.session_message = 'notconfirmed';
					this.webSocketService.confirmation(this.session_message);
				}

				if (this.open != true) {
					this.busy = false;
				}
				this.open = false;
			})
			.catch(() => (this.open = false));
	}

	//Goto Function to Navigate to different page

	goTo(path: string) {

		console.log('Route SessionId' + this.sessionId);
		this.confirmationDialogService.close(false);
		this.router.navigate([`/${path}`, this.sessionId]);

	}

	handleMessage(message) {
		this.message = JSON.parse(message).sessionId;
		console.log(this.message);
	}

	onMessageReceived(message) {
		console.log('Message Recieved from Server :: ' + message);
		this.handleMessage(message);
	}

	hidebyOther() {}
}
