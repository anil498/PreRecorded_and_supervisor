import { HttpClient } from "@angular/common/http";
import { Component } from "@angular/core";
import { NavigationStart, Router } from "@angular/router";
import { Subscription } from "rxjs";
import { RouteInfo } from "./model/ROUTE";
import { ObserverService } from "./services/observer.service";

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
  constructor(
    private router: Router,
    private http: HttpClient,
    private observerService: ObserverService
  ) {
    this.subscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        browserRefresh = !router.navigated;
        this.observerService.isBrowserRefreshed.next(browserRefresh);
      }
    });
  }
  ngOnInit() {
    this.http.get<any>("assets/json/headers.json").subscribe((response) => {
      authKey = response.authKey;
      baseHref = response.url;
      console.log(authKey);
      console.log(baseHref);
      this.observerService.url.next(baseHref);
      this.observerService.authKey.next(authKey);
    });
    console.log(authKey);
    console.log(baseHref);
    this.http
      .get<RouteInfo[]>("assets/json/access.json")
      .subscribe((response: RouteInfo[]) => {
        console.warn(response);
        ROUTE = response;
      });

    if (
      localStorage.getItem("loginId") == null ||
      localStorage.getItem("password") == null
    ) {
      this.router.navigate([""]);
      return;
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
