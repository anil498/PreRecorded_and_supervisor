import { Component, OnInit } from '@angular/core';

declare const $: any;
declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
}
export const ROUTES: RouteInfo[] = [
    { path: '/app/dashboard', title: 'Dashboard',  icon: 'dashboard', class: '' },
    { path: '/app/user-profile', title: 'User Profile',  icon:'person', class: '' },
    { path: '/app/account-management', title: 'Account Management', icon: 'supervisor_account', class: ''},
    { path: '/app/user-management', title: 'User Management', icon:'people', class:''},
    { path: '/app/session-management', title: 'Session Management', icon:'videocam', class:''},
    { path: '/app/dynamic-support', title: 'Dynamic Support', icon: 'headset_mic', class: ''},
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  menuItems: any[];

  constructor() { }

  ngOnInit() {
    this.menuItems = ROUTES.filter(menuItem => menuItem);
  }
  isMobileMenu() {
      if ($(window).width() > 991) {
          return false;
      }
      return true;
  };
}
