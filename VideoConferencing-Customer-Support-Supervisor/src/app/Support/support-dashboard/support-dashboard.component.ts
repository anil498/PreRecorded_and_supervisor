
//All the imports

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../../services/websocket.service';
import { ConfirmationDialogService } from '../../confirmation-dialog/confirmation-dialog.service';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog.component';

@Component({
	selector: 'support-dashboard',
	templateUrl: './support-dashboard.component.html',
	styleUrls: ['./support-dashboard.component.css']
})
export class SupportDashboardComponent implements OnInit {
	
	// Variables and objects Declaration

	title = 'openvidu-angular';
	message: any;
	session_message: any;
	name: string;
	sessionId: string;
	sessionId1: string;
	busy: boolean;
	open: boolean;
	count: boolean;
	id: string;
	count2: any = 0;

	// Constructor for declaring objects

	constructor(
		private router: Router,
		private webSocketService: WebSocketService,
		private confirmationDialogService: ConfirmationDialogService
	) {}

	//Main function

	ngOnInit(): void {
		if (this.webSocketService.status != true) {

			let stompClient = this.webSocketService.connect();
			stompClient.connect({}, (frame) => {

				// Subscribe to topic/support

				stompClient.subscribe('/topic/support', (notifications) => {
					
					console.log("Current route : "+this.router.url);
					this.sessionId1 = JSON.parse(notifications.body).sessionId;
					this.count = JSON.parse(notifications.body).count;
					console.log("Current Count : "+this.count);
					
					if (this.router.url === '/support' && this.open != true) {

						this.sessionId = JSON.parse(notifications.body).sessionId;
						this.openConfirmationDialog();
						this.open = true;
						this.onMessageReceived(notifications.body);

					} else if (this.count != true) {

						console.log("Current Count : "+this.count);
						this.webSocketService.available(this.sessionId1);
					}
				});

				// Subscribe to topic/sendname

				stompClient.subscribe('/topic/sendname', (notifications) => {

					this.name = JSON.parse(notifications.body).sessionId;
					console.warn('Name Recieved : ' + this.name);

					if (this.name == undefined) {

						this.name = 'Customer';
						console.log("Default Name : "+this.name);
					}
				});
			});
		}
	}

	//Function to open ConfirmationDialog

	public openConfirmationDialog() {

		console.log('COnfirmation Dialog');

		this.confirmationDialogService
			.confirm(this.name + ' Video Call Request..', 'Do you really want to Join ... ?')
			.then((confirmed) => {
				console.log('User confirmed:', confirmed);
				if (confirmed) {
					console.warn("SessionId is : "+this.sessionId);
					this.goTo('call-support');

					this.session_message = 'confirmed';
					this.webSocketService.send(this.session_message);
					this.busy = true;
					this.open = true;
				} else {
					this.session_message = 'notconfirmed';
					this.webSocketService.send(this.session_message);
				}

				if (this.open != true) {
					this.busy = false;
				}
				this.open = false;
			})
			.catch(() => ((this.open = false), this));
	}

	//Goto Function to navugate to other component

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
		console.log('Message Recieved from Server : ' + message);
		this.handleMessage(message);
	}
	
	hidebyOther() {}
}
