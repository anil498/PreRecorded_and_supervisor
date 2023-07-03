import {
  Component,
  OnInit,
  ChangeDetectorRef,
  ViewChild,
  ElementRef,
} from "@angular/core";
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

  userForm3: FormGroup;
  userForm1: FormGroup;
  userForm2: FormGroup;
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
    private fb: FormBuilder,
    private elementRef: ElementRef
  ) {
    this.accessList = this.restService.getData().Access;
  }

  async ngOnInit(): Promise<void> {
    this.show();

    this.userForm1 = this.fb.group({
      msisdn: [
        "",
        [Validators.required, Validators.pattern(/^(\d{10})(,\d{10})*$/)],
      ],
    });
    this.userForm2 = this.fb.group({
      msisdn: [
        "",
        [Validators.required, Validators.pattern(/^(\d{10})(,\d{10})*$/)],
      ],
    });

    this.userForm3 = this.fb.group({
      title: [
        "",
        [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(16),
        ],
      ],
      body: [
        "",
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(30),
        ],
      ],
      msisdn: [
        "",
        [Validators.required, Validators.pattern(/^(\d{10})(,\d{10})*$/)],
      ],
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
    var numbers: any = [];

    let messageResponse: any;
    const getLink = "1";

    if (type == "sms") {
      if (this.userForm1.invalid) {
        this.focusOnInvalidFields();
        return;
      }
      const msisdn = this.userForm1.value.msisdn;
      console.log("Form Submitted with value: ", this.userForm1.value.msisdn);
      var splitted = msisdn.split(",");
      for (let i = 0; i < splitted.length; i++) {
        numbers.push(splitted[i]);
      }
      try {
        for (let i = 0; i < numbers.length; i++) {
          messageResponse = await this.restService.sendSMS(numbers[i], getLink);
        }
        console.warn(messageResponse);
        if (messageResponse.status_code == 200) {
          console.log(messageResponse.link);
          this.goTo(messageResponse.link);
        } else {
        }
      } catch (error) {
        console.log(error);
        this.state = error.error.error;
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
      if (this.userForm2.invalid) {
        this.focusOnInvalidFields();
        return;
      }
      const msisdn = this.userForm2.value.msisdn;
      console.log("Form Submitted with value: ", this.userForm2.value.msisdn);
      var splitted = msisdn.split(",");
      for (let i = 0; i < splitted.length; i++) {
        numbers.push(splitted[i]);
      }
      const type = "template";
      const from = "919811026184";
      const templateId = "53571";

      try {
        for (let i = 0; i < numbers.length; i++) {
          messageResponse = await this.restService.sendWhatsapp(
            numbers[i],
            from,
            type,
            templateId,
            getLink
          );
        }
        if (messageResponse.status_code == 200) {
          console.log(messageResponse.link);
          this.goTo(messageResponse.link);
        } else {
        }
      } catch (error) {
        console.log(error);
        this.state = error.error.error;
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
        this.goTo(messageResponse.callurl);
      }
    } else if (type == "notify") {
      if (this.userForm3.invalid) {
        this.focusOnInvalidFields();
        return;
      }
      const msisdn = this.userForm3.value.msisdn;
      console.log("Form Submitted with value: ", this.userForm3.value.msisdn);
      var splitted = msisdn.split(",");
      for (let i = 0; i < splitted.length; i++) {
        numbers.push(splitted[i]);
      }

      this.titleValue = this.userForm3.value.title;
      this.bodyValue = this.userForm3.value.body;
      try {
        for (let i = 0; i < numbers.length; i++) {
          messageResponse = await this.restService.sendNotify(
            this.titleValue,
            this.bodyValue,
            numbers[i],
            getLink
          );
        }
        if (messageResponse.status_code == 200) {
          this.goTo(messageResponse.link);
        } else {
        }

        //this.goTo("/call", sessionId);
      } catch (error) {
        console.log(error);
        this.state = error.error.error;
        this.failedMessage = true;
        this.timeOut(3000);
        return;
      }
      this.failedMessage = false;
      console.warn(messageResponse);
      this.goTo(messageResponse.callurl);
    }
    //console.log("Message Sent: ", messageResponse);
    this.callUrl = messageResponse.callurl;
    this.failedMessageShow = " Please find video call link:- " + this.callUrl;
    this.timeOut(3000);
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

  focusOnInvalidFields() {
    console.log("focus");
    const invalidFields =
      this.elementRef.nativeElement.querySelectorAll(".ng-invalid");
    console.log(invalidFields);
    if (invalidFields.length > 0) {
      invalidFields.forEach((invalid) => {
        invalid.focus();
      });
    }
  }

  getErrorMessage1(controlName: string): string {
    const control = this.userForm1.get(controlName);
    if (control.hasError("required")) {
      return "This field is required";
    }

    if (controlName === "msisdn") {
      if (control?.hasError("pattern")) {
        return "Enter Valid mobile numbers";
      }
    }

    return "";
  }
  getErrorMessage2(controlName: string): string {
    const control = this.userForm2.get(controlName);
    if (control.hasError("required")) {
      return "This field is required";
    }

    if (controlName === "msisdn") {
      if (control?.hasError("pattern")) {
        return "Enter Valid mobile numbers";
      }
    }

    return "";
  }
  getErrorMessage3(controlName: string): string {
    const control = this.userForm3.get(controlName);
    if (control.hasError("required")) {
      return "This field is required";
    }

    if (controlName === "msisdn") {
      if (control?.hasError("pattern")) {
        return "Enter Valid mobile numbers";
      }
    }
    if (controlName === "title") {
      if (control?.hasError("minlength") || control?.hasError("maxlength")) {
        return "Title length should be between 4-16 characters";
      }
    }
    if (controlName === "body") {
      if (control?.hasError("minlength") || control?.hasError("maxlength")) {
        return "Body length should be between 6-30 characters";
      }
    }

    return "";
  }
}
