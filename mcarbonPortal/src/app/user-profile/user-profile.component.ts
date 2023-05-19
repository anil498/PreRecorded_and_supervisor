import { Component, OnInit } from "@angular/core";
import { RestService } from "../services/rest.service";
import { User } from "../model/user";

@Component({
  selector: "app-user-profile",
  templateUrl: "./user-profile.component.html",
  styleUrls: ["./user-profile.component.scss"],
})
export class UserProfileComponent implements OnInit {
  token: string;
  userId: string;
  user: User;
  checkStatus: number;
  accessList : any[] = [];
  featureList: any[] = [];
  constructor(private restService: RestService) {}

  async ngOnInit(): Promise<void> {
    this.token = this.restService.getToken();
    this.userId = this.restService.getUserId();
    this.accessList = this.restService.getData().Access;
    this.featureList = this.restService.getData().Features;
    this.restService.getUserById(this.token, this.userId).then(
      (response) => {
        console.warn(response);
        this.user = <User>response;
        console.log(this.user);
      },
      (error) => {
        console.log(error.status);
      }
    );

    if (this.user.status == 1) this.checkStatus = 1;
    else {
      this.checkStatus = 0;
    }
  }
}
