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
  callUrl: any;
  failedMessageShow: any;
  titleValue: string = "mCarbon Support";
  bodyValue: string = "Join Video Call";

  constructor(
    private router: Router,
    private restService: RestService,
    private fb: FormBuilder
  ) {
    this.accessList = this.restService.getData().Access;
  }

  async ngOnInit(): Promise<void> {
    this.show();
    this.userForm = this.fb.group({
      title: ["", [Validators.required]],
      body: ["", [Validators.required]],
      msisdn: ["", [Validators.required, Validators.pattern("^[0-9]+$")]],
    });
  }

  show() {
    this.accessList.forEach((access) => {
      if (access.systemName == "sms") {
        this.showSMSTab = true;
      }

      if (access.systemName == "whatsapp") {
        this.showWhatsappTab = true;
      }

      if (access.systemName == "send_notification") {
        this.showAppTab = true;
      }
    });
  }
  ngAfterViewInit() {}

  goTo(link: string) {
    window.open(link, "_blank");
    // this.router.navigate([`/${path}`, sessionId]);
  }

  async submitForm(type: string) {
    this.showDescription = false;
    this.failedMessage = false;
    this.phoneError = false;
    var numbers: any = [];

    const msisdn = this.userForm.value.msisdn;
    if (msisdn == null || msisdn == "") {
      this.phoneError = true;
      this.timeOut(30000);
      return;
    }
    console.log("Form Submitted with value: ", this.userForm.value.msisdn);

    var splitted = msisdn.split(",");
    for (let i = 0; i < splitted.length; i++) {
      numbers.push(splitted[i]);
    }

    this.titleValue = this.userForm.value.title;
    this.bodyValue = this.userForm.value.body;

    let messageResponse: any;
    const getLink = "1";
    if (type == "sms") {
      try {
        
        for (let i = 0; i < numbers.length; i++) {
          messageResponse = await this.restService.sendSMS(numbers[i], getLink);
        }
        console.warn(messageResponse);
        if (messageResponse.statusCode == 200) {
          this.goTo(messageResponse.link);
        } else {
        }
      } catch (error) {
        console.log(error);
        this.state = error.error.error;
        this.failedMessage = true;
        this.failedMessageShow = "";
        this.timeOut(30000);
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
        this.timeOut(30000);
      }
    } else if (type == "whatsapp") {
      const type = "template";
      const from = "919811026184";
      const templateId = "53571";
      try {
        messageResponse = await this.restService.sendWhatsapp(
          msisdn,
          from,
          type,
          templateId,
          getLink
        );

        if (messageResponse.statusCode == 200) {
          this.goTo(messageResponse.link);
        } else {
        }
      } catch (error) {
        console.log(error);
        this.state = error.error.error;
        this.failedMessage = true;
        this.timeOut(30000);
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
        this.timeOut(30000);
        console.warn(messageResponse);
        this.goTo(messageResponse.callurl);
      }
    } else if (type == "notify") {
      try {
        for (let i = 0; i < numbers.length; i++) {
          messageResponse = await this.restService.sendNotify(
            this.titleValue,
            this.bodyValue,
            numbers[i],
            getLink
          );
        }
        if (messageResponse.statusCode == 200) {
          this.goTo(messageResponse.link);
        } else {
        }

        //this.goTo("/call", sessionId);
      } catch (error) {
        console.log(error);
        this.state = error.error.error;
        this.failedMessage = true;
        this.timeOut(30000);
        return;
      }
      this.failedMessage = false;
      console.warn(messageResponse);
      this.goTo(messageResponse.callurl);
    }
    //console.log("Message Sent: ", messageResponse);
    this.callUrl = messageResponse.callurl;
    this.failedMessageShow = " Please find video call link:- " + this.callUrl;
    this.timeOut(30000);
  }

  private timeOut(time: number) {
    setTimeout(() => {
      this.showDescription = false;
      this.failedMessage = false;
      this.phoneError = false;
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
