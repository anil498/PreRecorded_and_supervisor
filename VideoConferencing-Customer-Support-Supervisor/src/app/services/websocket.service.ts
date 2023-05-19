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
        // let socket = new SockJs("http://172.17.0.122:5000/socket");
        //let socket = new WebSocket("ws://10.200.208.71:5000/socket");
        this.stompClient = Stomp.over(socket);
        this.stompClient.reconnect_delay = 5000;
        this.status=true;

        return this.stompClient;
    }
    public send(message){
        const body = { "sessionId": message};
        this.stompClient.send("/topic/support", {}, JSON.stringify(body));       
    }

    public alert(message){
        const body = { "sessionId": message};
        this.stompClient.send("/topic/alert", {}, JSON.stringify(body));    
    }
    public available(message){
        const body = { "sessionId": message};
        this.stompClient.send("/topic/available", {}, JSON.stringify(body));    
    }

   
}
