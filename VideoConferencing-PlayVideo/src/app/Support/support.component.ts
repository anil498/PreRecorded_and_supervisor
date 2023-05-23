import { Component, OnInit,ElementRef,ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../services/websocket.service';
import {ConfirmationDialogService} from '../confirmation-dialog/confirmation-dialog.service'
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';

@Component({
	selector: 'support-dashboard',
	templateUrl: './support.component.html',
	styleUrls: ['./support.component.scss']
})
export class SupportComponent implements OnInit {
	title = 'openvidu-angular';
  message: any;
  name: string;
  sessionId: string;
  sessionId1: string;
  busy:boolean;
  open:boolean;
  count:boolean;
 

  constructor(private router:Router,private webSocketService:WebSocketService,private confirmationDialogService:ConfirmationDialogService ){
  }

  ngOnInit(): void {
        if(this.webSocketService.status!=true){
        let stompClient = this.webSocketService.connect();
        
        stompClient.connect({}, frame => {

                        // Subscribe to notification topic
            stompClient.subscribe('/topic/notification', notifications => {
                
                console.log(this.router.url)
                this.sessionId1=JSON.parse(notifications.body).sessionId;
                this.count=JSON.parse(notifications.body).count;
                console.log(this.count)
                if(this.router.url==='/support'&&this.open!=true){
                this.sessionId=JSON.parse(notifications.body).sessionId;  
                this.openConfirmationDialog();
                this.open=true;
                this.onMessageReceived(notifications.body)
                }else if(this.count!=true){
                  console.log(this.count)
                  this.webSocketService.available(this.sessionId1)
                }

            })
            stompClient.subscribe('/topic/alert', notifications => {
              this.sessionId=JSON.parse(notifications.body).sessionId;
              console.log("alert"+this.router.url)
              if(this.router.url==='/'){ 
                this.confirmationDialogService.close(false) 
                
              }
    
          })
        });
        
      }
  }
  public openConfirmationDialog() {
    console.log("Notification")
    this.confirmationDialogService.confirm('Customer Video Call Request..', 'Do you really want to Join ... ?')
    .then((confirmed) => {console.log('User confirmed:', confirmed)
    if(confirmed){  
    this.goTo('call')
    this.webSocketService.send(this.sessionId)
    this.webSocketService.alert(this.sessionId)
    this.busy=true;
    this.open=true; 
  }else
    this.webSocketService.available(this.sessionId)
    //  this.goTo('/')
    if(this.open!=true){
      this.busy=false;
    }
    this.open=false;
     
  })
    .catch(() => this.open=false );
  }
  
  goTo(path: string) {
    console.log("Route SessionId"+this.sessionId);
    this.confirmationDialogService.close(false)
		this.router.navigate([`/${path}`,this.sessionId]);
	}
  handleMessage(message){
    this.message = JSON.parse(message).sessionId
    console.log(this.message)
  }
  onMessageReceived(message) {
    console.log("Message Recieved from Server :: " + message);
    this.handleMessage(message);
}
hidebyOther(){

}
}
