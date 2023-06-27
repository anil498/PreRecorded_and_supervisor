import { Component, OnInit, AfterViewInit } from "@angular/core";
import { RestService } from "app/services/rest.service";
import * as Chartist from "chartist";
import { DashSession } from "app/model/dash_session";
import { DashAccount } from "app/model/dash_account";
import { DashUser } from "app/model/dash_user";
@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.css"],
})
export class DashboardComponent implements OnInit, AfterViewInit {
  dashboard;
  sessions: DashSession;
  accounts: DashAccount;
  users: DashUser;
  participants: number;

  constructor(private restService: RestService) {
    this.dashboard = this.restService.getData().Dashboard;
  }

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
  ngOnInit() {
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

  ngAfterViewInit() {
    // DAILY ACCOUNT CREATION CHART
    if (this.checkAccount()) {
      var accountArray = Object.values(
        this.accounts.monthlyAccountCreation
      ).map(Number);

      const monthAccountCreation: any = {
        labels: ["J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"],
        series: [accountArray],
      };

      const optionsDailySalesChart: any = {
        lineSmooth: Chartist.Interpolation.cardinal({
          tension: 0,
        }),
        low: 0,
        high: Math.max(...accountArray),
        chartPadding: { top: 0, right: 0, bottom: 0, left: 0 },
      };

      var dailySalesChart = new Chartist.Line(
        "#dailySalesChart",
        monthAccountCreation,
        optionsDailySalesChart
      );
      this.startAnimationForLineChart(dailySalesChart);
    }

    /* ----------==========     Monthly Session Creation    ==========---------- */
    if (this.checkSession()) {
      var sessionArray = Object.values(
        this.sessions.monthlySessionCreation
      ).map(Number);

      const monthSessionCreation: any = {
        labels: ["J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"],
        series: [sessionArray],
      };

      const optionsCompletedTasksChart: any = {
        lineSmooth: Chartist.Interpolation.cardinal({
          tension: 0,
        }),
        low: 0,
        high: Math.max(...sessionArray),
        chartPadding: { top: 0, right: 0, bottom: 0, left: 0 },
      };

      var completedTasksChart = new Chartist.Line(
        "#completedTasksChart",
        monthSessionCreation,
        optionsCompletedTasksChart
      );

      // start animation for the Completed Tasks Chart - Line Chart
      this.startAnimationForLineChart(completedTasksChart);
    }
    /* ----------==========     Monthly User Creation    ==========---------- */
    if (this.checkUser()) {
      var userArray = Object.values(this.users.monthlyUserCreation).map(Number);
      var monthUserCreation = {
        labels: ["J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"],
        series: [userArray],
      };
      var optionswebsiteViewsChart = {
        axisX: {
          showGrid: false,
        },
        low: 0,
        high: Math.max(...userArray),
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
      this.startAnimationForBarChart(websiteViewsChart);
    }
  }
}
