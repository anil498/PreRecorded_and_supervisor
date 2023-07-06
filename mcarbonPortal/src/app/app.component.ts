import { HttpClient } from "@angular/common/http";
import { Component } from "@angular/core";
import { NavigationStart, Router } from "@angular/router";
import { Subscription } from "rxjs";
import { RouteInfo } from "./model/ROUTE";

export let browserRefresh = false;
export let authKey: string = "";
export let baseHref: string = "";
export var ROUTE: RouteInfo[] = [];
@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent {
  private subscription: Subscription;
  constructor(private router: Router, private http: HttpClient) {
    this.subscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        browserRefresh = !router.navigated;
      }
    });
  }
  ngOnInit() {
    this.http.get<any>("assets/json/headers.json").subscribe((response) => {
      authKey = response.authKey;
      baseHref = response.url;
      console.log(authKey);
      console.log(baseHref);
    });
    console.log(authKey);
    console.log(baseHref);
    this.http
      .get<RouteInfo[]>("assets/json/access.json")
      .subscribe((response: RouteInfo[]) => {
        console.warn(response);
        ROUTE = response;
      });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
