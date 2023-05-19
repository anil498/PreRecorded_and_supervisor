import { DatePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { WebSocketService } from '../services/websocket.service';

@Component({
	selector: 'app-confirmation-dialog',
	templateUrl: './confirmation-dialog.component.html'
})
export class ConfirmationDialogComponent implements OnInit {
	@Input() title: string;
	@Input() message: string;
	@Input() btnOkText: string;
	@Input() btnCancelText: string;

	remainingTime: number;
  	intervalId: any;
	session_message: string;

	constructor(
		private activeModal: NgbActiveModal,
		private webSocketService: WebSocketService,
		) {}

	ngOnInit() {
		this.remainingTime = 60; // Set the initial time in seconds
   		 this.startTimer();
	}

	public decline() {
		this.activeModal.close(false);
	}

	public accept() {
		this.activeModal.close(true);
	}

	public dismiss() {
		this.activeModal.dismiss();
	}

	startTimer() {
		this.intervalId = setInterval(() => {
		  if (this.remainingTime > 0) {
			this.remainingTime--;
		  } else {
			this.stopTimer();
		  }
		}, 1000);
	  }
	
	  stopTimer() {
		clearInterval(this.intervalId);
		this.dismiss();

		this.session_message = 'notconfirmed';
		this.webSocketService.available(this.session_message);
	  }
	
}
