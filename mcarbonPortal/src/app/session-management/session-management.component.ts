import { Component, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Sessions } from "../model/sessions";
import { MatDialog, MatDialogConfig } from "@angular/material/dialog";
import { RestService } from "app/services/rest.service";
import { SessionDialogComponent } from "../session-dialog/session-dialog.component";
import { MatSnackBar } from "@angular/material/snack-bar";
import { ViewSessionSettingDialogComponent } from "app/view-session-setting-dialog/view-session-setting-dialog.component";
import { browserRefresh } from "app/app.component";

@Component({
  selector: "app-session-management",
  templateUrl: "./session-management.component.html",
  styleUrls: ["./session-management.component.scss"],
})
export class SessionManagementComponent implements OnInit {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  token: string;
  userId: string;
  accessList: any[];
  search = "";
  pageSizes = [10, 25, 50, 100, 500];

  displayedColumns: string[] = [
    "sessionName",
    "participantName",
    "totalParticipants",
    "accountMaxSessions",
    "userMaxSessions",
    "settings",
    "creationDate",
    "expDate",
    "status",
    "Action",
  ];
  session: Sessions[] = [];
  dataSourceWithPageSize = new MatTableDataSource(this.session);
  browserRefresh: any;
  constructor(
    private dialog: MatDialog,
    private restService: RestService,
    private snackBar: MatSnackBar
  ) {}

  async ngOnInit(): Promise<void> {
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
    this.token = this.restService.getToken();
    this.userId = this.restService.getUserId();
    this.accessList = this.restService.getData().Access;
    this.viewTable();
  }

  ngAfterViewInit() {
    this.dataSourceWithPageSize.paginator = this.paginator;
    this.dataSourceWithPageSize.sort = this.sort;
  }

  viewTable() {
    this.restService.getSessionList(this.token, this.userId).then(
      (response) => {
        this.session = response;
        console.log(this.session);
        this.dataSourceWithPageSize.data = this.session;
      },
      (err) => {
        let msg = "";
        if (err.status == 0) {
          msg = "Server Not Responding";
        } else {
          msg = err.error.error;
        }
        // this.snackBar.open(msg, "Close", {
        //   duration: 3000,
        //   panelClass: ["snackBar"],
        // });
      }
    );
  }

  searchData(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceWithPageSize.filter = filterValue.trim().toLowerCase();
  }

  openDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "60%";
    dialogConfig.height = "70%";
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(SessionDialogComponent, dialogConfig);
  }

  viewSessionSettings(session: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    dialogConfig.height = "50%";
    dialogConfig.data = session.settings;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      ViewSessionSettingDialogComponent,
      dialogConfig
    );
  }
}
