import { Component, OnInit } from "@angular/core";
import { RestService } from "../services/rest.service";
import { User } from "../model/user";
import { browserRefresh } from "app/app.component";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";

@Component({
  selector: "app-user-profile",
  templateUrl: "./user-profile.component.html",
  styleUrls: ["./user-profile.component.scss"],
})
export class UserProfileComponent implements OnInit {
  isLogo: boolean;
  token: string;
  userId: string;
  userData: any;
  user: User;
  active: boolean = false;
  deactive: boolean = false;
  accessList: any[] = [];
  featureList: any[] = [];
  srcImg: any;
  browserRefresh: any;
  constructor(
    private restService: RestService,
    private snackBar: MatSnackBar
  ) {}

  async ngOnInit(): Promise<void> {
    this.browserRefresh = browserRefresh;
    console.log("refreshed?:", browserRefresh);
    if (this.browserRefresh == true) {
      const body = {
        loginId: localStorage.getItem("loginId"),
        password: localStorage.getItem("password"),
      };
      console.log(body);
      let loginResponse = await this.restService.login(
        "login",
        localStorage.getItem("loginId"),
        localStorage.getItem("password")
      );
      if (loginResponse.status_code == 200) {
        let logoResponse;
        try {
          logoResponse = await this.restService.getLogo(
            "User/getImage",
            null,
            loginResponse.user_data.userId
          );
          console.log("logo1", logoResponse);
          loginResponse.user_data.logo = logoResponse;
        } catch (err) {
          console.log(err);
          loginResponse.user_data.logo = null;
          this.openSnackBar("Something Went Wrong", "error");
        }
        this.restService.setData(loginResponse);
        this.restService.setToken(loginResponse.token);
        this.restService.setAuthKey(loginResponse.auth_key);
        this.restService.setUserId(localStorage.getItem("loginId"));
      }
    }
    this.active = false;
    this.deactive = false;
    this.token = this.restService.getToken();
    this.user = this.restService.getData().user_data;
    if (this.user.logo == null || Object.keys(this.user.logo).length === 0) {
      this.isLogo = false;
      this.srcImg = "./assets/img/faces/marc.jpeg";
    } else {
      this.isLogo = true;
      this.srcImg = this.user.logo.byte;
    }
    this.user.expDate = this.user.expDate.substring(0, 10);
    console.log(this.user);
    this.accessList = this.restService.getData().Access;
    this.featureList = this.restService.getData().Features;
    console.log(this.featureList);
  }

  openSnackBar(message: string, color: string) {
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = [color];
    console.log(snackBarConfig.panelClass);
    this.snackBar.open(message, null, snackBarConfig);
  }
}
