import { Component, OnInit, EventEmitter, Output, Input } from "@angular/core";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { RestService } from "../services/rest.service";
import { Router } from "@angular/router";
import { response } from "express";
import { error } from "console";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
})
export class LoginComponent implements OnInit {
  isLoggingIn = false;
  loginForm: FormGroup;
  hide = true;
  token: string;
  username: string = "";
  password: string = "";
  emptyUname = false;
  emptyPass = false;

  showDescription: boolean;
  failedMessage: boolean;
  state: string;
  failedMessageShow: any;

  constructor(
    private fb: FormBuilder,
    private restService: RestService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      username: ["", Validators.required],
      password: ["", Validators.required],
    });
  }

  ngOnInit(): void {}

  async login() {
    this.showDescription = false;
    this.failedMessage = false;
    this.emptyUname = false;
    this.emptyPass = false;
    this.username = this.loginForm.value.username;
    this.password = this.loginForm.value.password;

    if (
      (this.username == null || this.username == "") &&
      (this.password == null || this.password == "")
    ) {
      this.emptyUname = true;
      this.emptyPass = true;
      this.timeOut(3000);
      console.warn("Please Enter Username & Password");
      return;
    }

    if (this.username == null || this.username == "") {
      this.emptyUname = true;
      this.timeOut(3000);
      console.warn("Please Enter Username!!");
      return;
    }

    if (this.password == null || this.password == "") {
      this.emptyPass = true;
      this.timeOut(3000);
      console.warn("Please Enter Password!! ");
      return;
    }

    let loginResponse: any;

    try {
      console.warn(this.username + this.password);
      loginResponse = await this.restService.login(
        "login",
        this.username,
        this.password
      );
      this.token = loginResponse.token;
      loginResponse.user_data = JSON.parse(loginResponse.user_data);
      console.warn(loginResponse);
      this.restService.setData(loginResponse);
      this.restService.setToken(this.token);
      this.restService.setUserId(this.username);
      this.router.navigate(["/app/dashboard"]);
    } catch (error) {
      console.log(error);
      this.state = error.statusText;
      this.failedMessage = true;
      this.failedMessageShow = "";

      const snackBarConfig = new MatSnackBarConfig();
      snackBarConfig.duration = 3000;
      if (error.status === 401) {
        this.state = "Unauthorized User";
      }

      this.snackBar.open(this.state, "Dismiss", snackBarConfig);
      this.timeOut(3000);
      return;
    }
    this.failedMessage = false;
    if (loginResponse.state == "SUBMIT_FAILED") {
      this.state = "Message Submit failed due to " + loginResponse.description;
      this.failedMessage = true;
    } else {
      this.state = loginResponse.description;
      this.showDescription = true;
      this.timeOut(3000);
    }
  }

  private timeOut(time: number) {
    setTimeout(() => {
      this.showDescription = false;
      this.failedMessage = false;
      this.emptyUname = false;
      this.emptyPass = false;
    }, time);
  }
}
