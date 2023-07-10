import { Component, OnInit, AfterViewInit } from "@angular/core";
import { RestService } from "app/services/rest.service";
import * as Chartist from "chartist";
import { DashSession } from "app/model/dash_session";
import { DashAccount } from "app/model/dash_account";
import { DashUser } from "app/model/dash_user";
import { browserRefresh } from "app/app.component";
@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.css"],
})
export class DashboardComponent implements OnInit, AfterViewInit {
  browserRefresh: boolean;
  dashboard;
  sessions: DashSession;
  accounts: DashAccount;
  users: DashUser;
  participants: number;
  selectedGraph: string = "daily";
  accountTitle: string = "Daily Account Creation";
  userTitle: string = "Daily User Creation";
  sessionTitle: string = "Daily Session Creation";
  constructor(private restService: RestService) {}

  checkAccount() {
    if (this.accounts !== null && this.accounts.isDisplay === true) return true;
    return false;
  }
  checkSession() {
    if (this.sessions !== null && this.sessions.isDisplay === true) return true;
    return false;
  }
  checkUser() {
    if (this.users !== null && this.users.isDisplay === true) return true;
    return false;
  }
  startAnimationForLineChart(chart) {
    let seq: any, delays: any, durations: any;
    seq = 0;
    delays = 80;
    durations = 500;

    chart.on("draw", function (data) {
      if (data.type === "line" || data.type === "area") {
        data.element.animate({
          d: {
            begin: 600,
            dur: 700,
            from: data.path
              .clone()
              .scale(1, 0)
              .translate(0, data.chartRect.height())
              .stringify(),
            to: data.path.clone().stringify(),
            easing: Chartist.Svg.Easing.easeOutQuint,
          },
        });
      } else if (data.type === "point") {
        seq++;
        data.element.animate({
          opacity: {
            begin: seq * delays,
            dur: durations,
            from: 0,
            to: 1,
            easing: "ease",
          },
        });
      }
    });

    seq = 0;
  }
  startAnimationForBarChart(chart) {
    let seq2: any, delays2: any, durations2: any;

    seq2 = 0;
    delays2 = 80;
    durations2 = 500;
    chart.on("draw", function (data) {
      if (data.type === "bar") {
        seq2++;
        data.element.animate({
          opacity: {
            begin: seq2 * delays2,
            dur: durations2,
            from: 0,
            to: 1,
            easing: "ease",
          },
        });
      }
    });

    seq2 = 0;
  }
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
    this.dashboard = this.restService.getData().Dashboard;
    this.participants = this.dashboard.participants;

    if (this.dashboard.hasOwnProperty("accounts")) {
      this.accounts = this.dashboard.accounts;
    } else {
      this.accounts = null;
    }
    if (this.dashboard.hasOwnProperty("sessions")) {
      this.sessions = this.dashboard.sessions;
    } else {
      this.sessions = null;
    }
    if (this.dashboard.hasOwnProperty("users")) {
      this.users = this.dashboard.users;
    } else {
      this.users = null;
    }
  }

  onChangeGraph(event) {
    console.log(event);
    console.log(this.selectedGraph);
    switch (this.selectedGraph) {
      case "daily":
        {
          var dayArray = Object.keys(this.accounts.dailyAccountCreation).map(
            Number
          );
          this.accountTitle = "Daily Account Creation";
          this.userTitle = "Daily User Creation";
          this.sessionTitle = "Daily Session Creation";
          if (this.checkAccount()) {
            var accountArray = Object.values(
              this.accounts.dailyAccountCreation
            ).map(Number);
            this.accountGraph(dayArray, accountArray);
          }
          if (this.checkUser()) {
            var userArray = Object.values(this.users.dailyUserCreation).map(
              Number
            );
            this.userGraph(dayArray, userArray);
          }
          if (this.checkSession()) {
            var sessionArray = Object.values(
              this.sessions.dailySessionCreation
            ).map(Number);
            this.sessionGraph(dayArray, sessionArray);
          }
        }
        break;
      case "monthly":
        {
          var monthArray = [
            "J",
            "F",
            "M",
            "A",
            "M",
            "J",
            "J",
            "A",
            "S",
            "O",
            "N",
            "D",
          ];
          this.accountTitle = "Monthly Account Creation";
          this.userTitle = "Monthly User Creation";
          this.sessionTitle = "Monthly Session Creation";
          if (this.checkAccount()) {
            var accountArray = Object.values(
              this.accounts.monthlyAccountCreation
            ).map(Number);
            this.accountGraph(monthArray, accountArray);
          }
          if (this.checkUser()) {
            var userArray = Object.values(this.users.monthlyUserCreation).map(
              Number
            );
            this.userGraph(monthArray, userArray);
          }
          if (this.checkSession()) {
            var sessionArray = Object.values(
              this.sessions.monthlySessionCreation
            ).map(Number);
            this.sessionGraph(monthArray, sessionArray);
          }
        }
        break;
      case "yearly":
        {
          var yearArray = Object.keys(this.accounts.yearlyAccountCreation).map(
            Number
          );
          this.accountTitle = "Annual Account Creation";
          this.userTitle = "Annual User Creation";
          this.sessionTitle = "Annual Session Creation";
          if (this.checkAccount()) {
            var accountArray = Object.values(
              this.accounts.yearlyAccountCreation
            ).map(Number);
            this.accountGraph(yearArray, accountArray);
          }
          if (this.checkUser()) {
            var userArray = Object.values(this.users.yearlyUserCreation).map(
              Number
            );
            this.userGraph(yearArray, userArray);
          }
          if (this.checkSession()) {
            var sessionArray = Object.values(
              this.sessions.yearlySessionCreation
            ).map(Number);
            this.sessionGraph(yearArray, sessionArray);
          }
        }
        break;
    }
  }

  accountGraph(labels, series) {
    const monthAccountCreation: any = {
      labels: labels,
      series: [series],
    };

    // const optionsDailySalesChart: any = {
    //   lineSmooth: Chartist.Interpolation.cardinal({
    //     tension: 0,
    //   }),
    //   low: 0,
    //   high: Math.max(...series),
    //   chartPadding: { top: 0, right: 0, bottom: 0, left: 0 },
    // };

    var optionsDailySalesChart = {
      axisX: {
        showGrid: false,
      },
      low: 0,
      high: Math.max(...series),
      chartPadding: { top: 0, right: 5, bottom: 0, left: 0 },
    };

    var dailySalesChart = new Chartist.Line(
      "#dailySalesChart",
      monthAccountCreation,
      optionsDailySalesChart
    );
    this.startAnimationForLineChart(dailySalesChart);
  }

  userGraph(labels, series) {
    var monthUserCreation = {
      labels: labels,
      series: [series],
    };
    var optionswebsiteViewsChart = {
      axisX: {
        showGrid: false,
      },
      low: 0,
      high: Math.max(...series),
      chartPadding: { top: 0, right: 5, bottom: 0, left: 0 },
    };
    var responsiveOptions: any[] = [
      [
        "screen and (max-width: 640px)",
        {
          seriesBarDistance: 5,
          axisX: {
            labelInterpolationFnc: function (value) {
              return value[0];
            },
          },
        },
      ],
    ];
    // var websiteViewsChart = new Chartist.Bar(
    //   "#websiteViewsChart",
    //   monthUserCreation,
    //   optionswebsiteViewsChart,
    //   responsiveOptions
    // );

    var websiteViewsChart = new Chartist.Line(
      "#websiteViewsChart",
      monthUserCreation,
      optionswebsiteViewsChart
    );

    //start animation for the Emails Subscription Chart
    this.startAnimationForLineChart(websiteViewsChart);
  }

  sessionGraph(label, series) {
    const monthSessionCreation: any = {
      labels: label,
      series: [series],
    };

    // const optionsCompletedTasksChart: any = {
    //   lineSmooth: Chartist.Interpolation.cardinal({
    //     tension: 0,
    //   }),
    //   low: 0,
    //   high: Math.max(...series),
    //   chartPadding: { top: 0, right: 0, bottom: 0, left: 0 },
    // };

    var optionsCompletedTasksChart = {
      axisX: {
        showGrid: false,
      },
      low: 0,
      high: Math.max(...series),
      chartPadding: { top: 0, right: 5, bottom: 0, left: 0 },
    };

    var completedTasksChart = new Chartist.Line(
      "#completedTasksChart",
      monthSessionCreation,
      optionsCompletedTasksChart
    );

    // start animation for the Completed Tasks Chart - Line Chart
    this.startAnimationForLineChart(completedTasksChart);
  }
  ngAfterViewInit() {
    /* ----------==========     Account Creation    ==========---------- */
    if (this.checkAccount()) {
      var dayArray = Object.keys(this.accounts.dailyAccountCreation).map(
        Number
      );
      var accountArray = Object.values(this.accounts.dailyAccountCreation).map(
        Number
      );
      this.accountGraph(dayArray, accountArray);
    }

    /* ----------==========      Session Creation    ==========---------- */
    if (this.checkSession()) {
      var dayArray = Object.keys(this.sessions.dailySessionCreation).map(
        Number
      );
      var sessionArray = Object.values(this.sessions.dailySessionCreation).map(
        Number
      );
      this.sessionGraph(dayArray, sessionArray);
    }

    /* ----------==========     User Creation    ==========---------- */
    if (this.checkUser()) {
      var dayArray = Object.keys(this.users.dailyUserCreation).map(Number);
      var userArray = Object.values(this.users.dailyUserCreation).map(Number);
      this.userGraph(dayArray, userArray);
    }
  }
}
