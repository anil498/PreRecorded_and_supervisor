import { HttpClient } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
import { Route } from "@angular/router";
import { RestService } from "app/services/rest.service";
import { RouteInfo } from "../../model/ROUTE";
// import { ROUTE } from "app/login/login.component";
import { ROUTE } from "app/app.component";
import { browserRefresh } from "app/app.component";
import { Subscription } from "rxjs";
//import { ObserverService } from "app/services/observer.service";
declare const $: any;

// export const ROUTES: RouteInfo[] = [
//   {
//     path: "/app/dashboard",
//     title: "Dashboard",
//     icon: "dashboard",
//     class: "",
//     show: false,
//     systemName: ""
//   },
//   {
//     path: "/app/user-profile",
//     title: "User Profile",
//     icon: "person",
//     class: "",
//     show: false,
//     systemName: ""
//   },
//   {
//     path: "/app/account-management",
//     title: "Account Management",
//     icon: "supervisor_account",
//     class: "",
//     show: false,
//     systemName: ""
//   },
//   {
//     path: "/app/user-management",
//     title: "User Management",
//     icon: "people",
//     class: "",
//     show: false,
//     systemName: ""
//   },
//   {
//     path: "/app/session-management",
//     title: "Session Management",
//     icon: "videocam",
//     class: "",
//     show: false,
//     systemName: ""
//   },
//   {
//     path: "/app/dynamic-support",
//     title: "Dynamic Support",
//     icon: "headset_mic",
//     class: "",
//     show: false,
//     systemName: ""
//   },
// ];

@Component({
  selector: "app-sidebar",
  templateUrl: "./sidebar.component.html",
  styleUrls: ["./sidebar.component.css"],
})
export class SidebarComponent implements OnInit {
  browserRefresh: boolean;
  menuItems: any[];
  accessList: any[];
  logo: any;
  isLogo: boolean;
  srcImg: string;
  constructor(private restService: RestService, private http: HttpClient) {}

  async ngOnInit() {
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
    this.logo = this.restService.getData().user_data.logo;
    this.menuItems = ROUTE.filter((menuItem) => menuItem);
    this.accessList = this.restService
      .getData()
      .Access.filter((item) => item.pId === 0);
    if (this.logo == null || Object.keys(this.logo).length === 0) {
      this.isLogo = false;
      this.srcImg = "./assets/img/logo.png";
    } else {
      this.isLogo = true;
      this.srcImg = this.logo.byte;
    }
    this.showSideNav();
  }

  ngOnDestroy() {
    this.accessList.forEach((access) => {
      this.menuItems.forEach((menuItem) => {
        if (access.systemName === menuItem.systemName) {
          menuItem.show = false;
          menuItem.title = access.name;
        }
      });
    });
  }
  showSideNav() {
    console.log(this.accessList);
    console.log(this.menuItems);

    this.accessList.forEach((access) => {
      this.menuItems.forEach((menuItem) => {
        if (access.systemName === menuItem.systemName) {
          menuItem.show = true;
          menuItem.title = access.name;
        }
      });
    });
  }

  isMobileMenu() {
    if ($(window).width() > 991) {
      return false;
    }
    return true;
  }
}
