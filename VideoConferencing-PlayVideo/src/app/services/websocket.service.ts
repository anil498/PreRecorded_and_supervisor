import {Injectable} from "@angular/core";
import { environment } from "../../environments/environment";

var SockJs = require("sockjs-client");
var Stomp = require("stompjs");

@Injectable()
export class WebSocketService {
    stompClient: any;
    status:boolean;

    // Open connection with the back-end socket
    public connect() {
   
        let socket = new SockJs(environment.websocket);
        //let socket = new WebSocket("ws://10.200.208.71:5000/socket");
        this.stompClient = Stomp.over(socket);
        this.stompClient.reconnect_delay = 5000;
        this.status=true;

        return this.stompClient;
    }
    public send(message){
        this.stompClient.send("/app/notification", {}, JSON.stringify(message));
        
    }
    public alert(message){
        this.stompClient.send("/app/alert", {}, JSON.stringify(message));    
    }
    public available(message){
        this.stompClient.send("/app/available", {}, JSON.stringify(message));    
    }
}
