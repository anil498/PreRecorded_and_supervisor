import { Component, OnInit, ChangeDetectorRef, ViewChild } from "@angular/core";
import { Router } from "@angular/router";
import { RestService } from "../services/rest.service";
// import { faWhatsappSquare, faSquareWhatsapp } from '@fortawesome/free-brands-svg-icons'
// import { faMessage } from '@fortawesome/free-solid-svg-icons';
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";
import {
  animals,
  colors,
  Config,
  countries,
  names,
  uniqueNamesGenerator,
} from "unique-names-generator";

@Component({
  selector: "app-dynamic-support",
  templateUrl: "./dynamic-support.component.html",
  styleUrls: ["./dynamic-support.component.scss"],
})
export class DynamicSupportComponent implements OnInit {
  title = "Dynamic Support";
  accessList: any[];
  showSMSTab = false;
  showWhatsappTab = false;
  showAppTab = false;

  userForm: FormGroup;
  showDescription: boolean;
  failedMessage: boolean;
  state: any;
  phoneError: any;
  roomError: boolean;
  callUrl: any;
  failedMessageShow: any;
  url: string;
  titleValue: string = "mCarbon Support";
  bodyValue: string = "Join Video Call";
  rooms = [
    { label: "mCarbon Room 1", value: "mcarbon1" },
    { label: "mCarbon Room 2", value: "mcarbon2" },
  ];

  constructor(
    private router: Router,
    private restService: RestService,
    private fb: FormBuilder
  ) {
    this.url = "https://demo2.progate.mobi/dynamicsupport/#/call/";
  }

  async ngOnInit(): Promise<void> {
    this.show();
    this.userForm = this.fb.group({
      title: ["", [Validators.required]],
      body: ["", [Validators.required]],
      msisdn: ["", [Validators.required, Validators.pattern("^[0-9]+$")]],
      room: ["", Validators.required],
    });
  }

  show() {
    this.accessList.forEach((access) => {
      if (access.pId == 5000) {
        if (access.apiId == 5001) {
          this.showSMSTab = true;
        }

        if (access.apiId == 5002) {
          this.showWhatsappTab = true;
        }

        if (access.apiId == 5003) {
          this.showAppTab = true;
        }
      }
    });
  }
  ngAfterViewInit() {}

  goTo(sessionId: string) {
    window.open(this.url + sessionId, "_blank");
    // this.router.navigate([`/${path}`, sessionId]);
  }

  async submitForm(type: string) {
    this.showDescription = false;
    this.failedMessage = false;
    this.phoneError = false;
    this.roomError = false;
    var numbers: any = [];

    console.log("Form Submitted with value: ", this.userForm.value.msisdn);
    const msisdn = this.userForm.value.msisdn;
    var splitted = msisdn.split(",");
    for (let i = 0; i < splitted.length; i++) {
      numbers.push(splitted[i]);
    }

    const sessionId = this.userForm.value.room;
    this.titleValue = this.userForm.value.title;
    this.bodyValue = this.userForm.value.body;

    if (msisdn == null || msisdn == "") {
      this.phoneError = true;
      this.timeOut(3000);
      return;
    }
    if (sessionId == null || sessionId == "") {
      this.roomError = true;
      this.timeOut(3000);
      return;
    }

    const callUrl = "/customers/#/" + sessionId;
    let messageResponse: any;

    if (type == "sms") {
      try {
        for (let i = 0; i < numbers.length; i++) {
          messageResponse = await this.restService.sendSMS(
            sessionId,
            numbers[i],
            callUrl
          );
        }
        console.warn(messageResponse);
        this.goTo(sessionId);
      } catch (error) {
        console.log(error);
        this.state = error.statusText;
        this.failedMessage = true;
        this.failedMessageShow = "";
        this.timeOut(3000);
        return;
      }
      this.failedMessage = false;
      if (messageResponse.state == "SUBMIT_FAILED") {
        this.state =
          "Message Submit failed due to " + messageResponse.description;
        this.failedMessage = true;
      } else {
        this.state = messageResponse.description;
        this.showDescription = true;
        this.timeOut(3000);
      }
    } else if (type == "whatsapp") {
      const type = "template";
      const from = "919811026184";
      const templateId = "53571";
      try {
        messageResponse = await this.restService.sendWhatsapp(
          sessionId,
          msisdn,
          callUrl,
          from,
          type,
          templateId
        );
      } catch (error) {
        console.log(error);
        this.state = error.statusText;
        this.failedMessage = true;
        this.timeOut(3000);
        return;
      }
      this.failedMessage = false;
      if (messageResponse.status == "FAILED") {
        this.state = "Message Submit failed due to " + messageResponse.message;
        this.failedMessage = true;
      } else {
        this.state = messageResponse.message;
        this.showDescription = true;
        this.failedMessageShow = "";
        this.timeOut(3000);
        console.warn(messageResponse);
        this.goTo(sessionId);
        //this.goTo("/call", sessionId);
      }
    } else if (type == "notify") {
      try {
        for (let i = 0; i < numbers.length; i++) {
          messageResponse = await this.restService.sendNotify(
            this.titleValue,
            this.bodyValue,
            sessionId,
            numbers[i]
          );
        }
        //this.goTo("/call", sessionId);
      } catch (error) {
        console.log(error);
        this.state = error.statusText;
        this.failedMessage = true;
        this.timeOut(3000);
        return;
      }
      this.failedMessage = false;
      console.warn(messageResponse);
      this.goTo(sessionId);
    }
    //console.log("Message Sent: ", messageResponse);
    this.callUrl = messageResponse.callUrl;
    this.failedMessageShow = " Please find video call link:- " + this.callUrl;
    this.timeOut(3000);
  }

  private timeOut(time: number) {
    setTimeout(() => {
      this.showDescription = false;
      this.failedMessage = false;
      this.phoneError = false;
      this.roomError = false;
    }, time);
  }

  private getRandomName(): string {
    const configName: Config = {
      dictionaries: [names, countries, colors, animals],
      separator: "-",
      style: "lowerCase",
    };
    return uniqueNamesGenerator(configName).replace(/[^\w-]/g, "");
  }
}
