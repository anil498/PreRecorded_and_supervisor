import { Component, OnInit } from "@angular/core";
import { RestService } from "../services/rest.service";
import { User } from "../model/user";
import { browserRefresh } from "app/app.component";

@Component({
  selector: "app-user-profile",
  templateUrl: "./user-profile.component.html",
  styleUrls: ["./user-profile.component.scss"],
})
export class UserProfileComponent implements OnInit {
  isLogo : boolean;
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
  constructor(private restService: RestService) {}

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
    if(this.user.logo == null || Object.keys(this.user.logo).length === 0){
      this.isLogo = false;
      this.srcImg = "./assets/img/faces/marc.jpeg"
    }
    else{
      this.isLogo = true;
      this.srcImg = this.user.logo.byte;
    }
    this.user.expDate = this.user.expDate.substring(0, 10);
    console.log(this.user);
    this.accessList = this.restService.getData().Access;
    this.featureList = this.restService.getData().Features;
    console.log(this.featureList);
  }
}
