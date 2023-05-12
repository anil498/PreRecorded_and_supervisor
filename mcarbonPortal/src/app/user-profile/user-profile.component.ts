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
  user: any;
  constructor(private restService: RestService) {}

  async ngOnInit(): Promise<void> {
    this.token = this.restService.getToken();
    this.userId = this.restService.getUserId();
    this.restService.getUserById(this.token, this.userId).then(
      (response) => {
        this.user = <User>response[0];
        console.log(this.user);
      },
      (error) => {
        console.log(error.status);
      }
    );
  }
}
