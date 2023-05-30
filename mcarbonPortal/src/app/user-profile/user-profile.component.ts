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
  active: boolean = false;
  deactive: boolean = false;
  accessList: any[] = [];
  featureList: any[] = [];
  constructor(private restService: RestService) {}

  async ngOnInit(): Promise<void> {
    this.token = this.restService.getToken();
    this.restService.getUserById(this.token).then(
      (response) => {
        console.warn(response);
        this.user = response;
        console.log(this.user);
      },
      (error) => {
        console.log(error.status);
      }
    );
    this.active = false;
    this.deactive = false;

    this.accessList = this.restService.getData().Access;
    this.featureList = this.restService.getData().Features;

    if (this.user.status == 1) this.active = true;
    else {
      this.deactive = true;
    }
  }
}
