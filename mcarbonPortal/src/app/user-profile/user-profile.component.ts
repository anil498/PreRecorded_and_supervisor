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
    this.active = false;
    this.deactive = false;
    this.token = this.restService.getToken();
    this.restService.getUserById(this.token).then(
      (response) => {
        console.warn(response);
        this.user = response;
        this.user.expDate = this.user.expDate.substring(0, 10);
        console.log(this.user);

        if (this.user.status == 1) this.active = true;
        else {
          this.deactive = true;
        }
      },
      (error) => {
        console.log(error.status);
      }
    );

    this.accessList = this.restService.getData().Access;
    this.featureList = this.restService.getData().Features;
  }
}
