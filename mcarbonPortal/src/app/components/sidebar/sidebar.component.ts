import { Component, OnInit } from "@angular/core";
import { RestService } from "app/services/rest.service";
declare const $: any;
declare interface RouteInfo {
  path: string;
  title: string;
  icon: string;
  class: string;
  apiId: number;
  show: boolean;
}

export const ROUTES: RouteInfo[] = [
  {
    path: "/app/dashboard",
    title: "Dashboard",
    icon: "dashboard",
    class: "",
    apiId: 100,
    show: false,
  },
  // {
  //   path: "/app/user-profile",
  //   title: "User Profile",
  //   icon: "person",
  //   class: "",
  //   apiId: null,
  //   show: true,
  // },
  {
    path: "/app/account-management",
    title: "Account Management",
    icon: "supervisor_account",
    class: "",
    apiId: 1000,
    show: false,
  },
  {
    path: "/app/user-management",
    title: "User Management",
    icon: "people",
    class: "",
    apiId: 2000,
    show: false,
  },
  {
    path: "/app/session-management",
    title: "Session Management",
    icon: "videocam",
    class: "",
    apiId: 4000,
    show: false,
  },
  {
    path: "/app/dynamic-support",
    title: "Dynamic Support",
    icon: "headset_mic",
    class: "",
    apiId: 5000,
    show: false,
  },
];

@Component({
  selector: "app-sidebar",
  templateUrl: "./sidebar.component.html",
  styleUrls: ["./sidebar.component.css"],
})
export class SidebarComponent implements OnInit {
  menuItems: any[];
  accessList: any[];
  constructor(private restService: RestService) {}

  ngOnInit() {
    this.menuItems = ROUTES.filter((menuItem) => menuItem);
    this.accessList = this.restService.getData().Access;

    this.showSideNav();
  }

  showSideNav() {
    this.accessList.forEach((access) => {
      if (access.pId == 0) {
        this.menuItems.forEach((menuItem) => {
          if (access.apiId === menuItem.apiId) {
            menuItem.show = true;
          }
        });
      }
    });
  }

  isMobileMenu() {
    if ($(window).width() > 991) {
      return false;
    }
    return true;
  }
}
