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
	
	sessionId: string;
	getName: string;
	path: string;
	id: string;
	showLoading: boolean = true;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private webSocketService: WebSocketService,
		private restService: RestService,
	) {}

	async ngOnInit() {

			this.sessionId = this.route.snapshot.paramMap.get('roomName');
			this.getName = this.route.snapshot.paramMap.get('name');
			if(this.getName == 'undefined'){
				this.getName = 'Customer';
			}
			this.path = 'call-customer';

			const body = {
				notifyTo: 'support',
				sessionId: 'daily-call2'
			};
	
			this.restService.postRequest('daily-call2' , body);

			let stompClient = this.webSocketService.connect();

			stompClient.connect({}, (frame) => {
				// Subscribe to notification topic
				stompClient.subscribe('/topic/support', (notifications) => {
					this.id = JSON.parse(notifications.body).sessionId;
					console.warn("Successfully Recieved Notification : "+ this.id);
					if(this.id == 'confirmed'){
					this.router.navigate([`/${this.path}`, {roomName: this.sessionId , name: this.getName}]);
				}

				});

				stompClient.subscribe('/topic/available', (notifications) => {
					this.id = JSON.parse(notifications.body).sessionId;
					console.warn("Successfully Recieved Notification : "+ this.id);
					if(this.id == 'notconfirmed'){
					console.warn("It Works");
					this.showLoading = false;
				}

			});

			});

			setTimeout(() => {
				this.showLoading = false;
			  }, 60000); // 1 minute in milliseconds
			
	}


	exit(path: string){
		this.router.navigate([`/${path}`]);
	}

	
}
