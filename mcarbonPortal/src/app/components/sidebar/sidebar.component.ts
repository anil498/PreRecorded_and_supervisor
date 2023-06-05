import { HttpClient } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
import { Route } from "@angular/router";
import { RestService } from "app/services/rest.service";
declare const $: any;
declare interface RouteInfo {
  path: string;
  title: string;
  icon: string;
  class: string;
  show: boolean;
  systemName: string;
}

export var ROUTE: RouteInfo[];
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
  menuItems: any[];
  accessList: any[];
  constructor(private restService: RestService, private http: HttpClient) {}

  ngOnInit() {
    this.http
      .get<RouteInfo[]>("assets/json/access.json")
      .subscribe((response: RouteInfo[]) => {
        console.warn(response);
        ROUTE = response;
      });
    this.menuItems = ROUTE.filter((menuItem) => menuItem);
    this.accessList = this.restService.getData().Access;
    this.showSideNav();
  }

  showSideNav() {
    this.accessList.forEach((access) => {
      this.menuItems.forEach((menuItem) => {
        if (access.apiId === menuItem.apiId) {
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
