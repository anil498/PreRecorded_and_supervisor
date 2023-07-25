import { Component, OnInit, ViewChild, Input } from "@angular/core";
import {
  MatDialog,
  MatDialogConfig,
  MatDialogRef,
} from "@angular/material/dialog";
import { RestService } from "app/services/rest.service";
import { interval, Subscription } from "rxjs";
import { MatTableDataSource } from "@angular/material/table";
import { MatIconRegistry } from "@angular/material/icon";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { DomSanitizer } from "@angular/platform-browser";
import { MatPaginator } from "@angular/material/paginator";
import { Router } from "@angular/router";
import { Accounts } from "../model/accounts";
import { MatSort, Sort } from "@angular/material/sort";
import { CreateAccountComponent } from "app/create-account/create-account.component";
import { UpdateAccountDialogComponent } from "app/update-account-dialog/update-account-dialog.component";
import { ViewAccessDialogComponent } from "app/view-access-dialog/view-access-dialog.component";
import { ViewFeatureDialogComponent } from "app/view-feature-dialog/view-feature-dialog.component";
import { ViewAccountDialogComponent } from "app/view-account-dialog/view-account-dialog.component";
import { DeleteDialogComponent } from "app/delete-dialog/delete-dialog.component";
import { browserRefresh } from "app/app.component";
import { DateAdapter } from "@angular/material/core";
@Component({
  selector: "app-account-management",
  templateUrl: "./account-management.component.html",
  styleUrls: ["./account-management.component.scss"],
})
export class AccountManagementComponent implements OnInit {
  search: String = "";
  token: string;
  userId: string;
  private updateSubscription: Subscription;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  pageSizes = [10, 25, 50, 100, 500];
  displayedColumns: any[] = [
    "name",
    "access",
    "features",
    "expDate",
    "status",
    "Action",
  ];

  accounts: Accounts[] = [];
  dataSourceWithPageSize = new MatTableDataSource(this.accounts);
  browserRefresh: boolean;
  accessList: any[];
  featureList: any[];
  showCreateButton = false;
  showTable = false;
  showDelete = false;
  showEdit = false;

  constructor(
    private dialog: MatDialog,
    private router: Router,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private restService: RestService,
    private snackBar: MatSnackBar,
    private dateAdapter: DateAdapter<Date>
  ) {}

  async ngOnInit(): Promise<void> {
    this.dateAdapter.setLocale("en-IN");
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
    console.warn(this.token + "\n" + this.userId);

    this.accessList = this.restService.getData().Access;
    this.featureList = this.restService.getData().Features;

    this.show();
    this.viewTable();

    // this.updateSubscription = interval(6000).subscribe((val) => {
    //   this.restService.getUserList(this.token, this.userId).then(
    //     (response) => {
    //       this.users = response;
    //       console.log(this.users);
    //       this.dataSourceWithPageSize.data = this.users;
    //     },
    //     (error) => {
    //       console.log(error.status);
    //     }
    //   );
    // });
  }

  checkStatus(acc: Accounts) {
    let currentDate = new Date();
    let time = currentDate.toTimeString().split(" ")[0];
    console.log("time " + time);
    let currentDateString = currentDate.toISOString().split("T")[0];
    console.log(currentDateString);
    currentDateString = currentDateString + " " + time;

    if (acc.expDate < currentDateString && acc.status !== 2) {
      console.log(acc.expDate + "  " + currentDateString);
      acc.status = 3;
    }
  }

  viewTable() {
    this.restService.getAccountList(this.token, this.userId).then(
      (response) => {
        this.accounts = response;
        console.log(this.accounts);
        this.accounts.forEach(async (account) => {
          await this.checkStatus(account);
        });
        this.dataSourceWithPageSize.data = this.accounts;
        this.dataSourceWithPageSize.sortingDataAccessor = (
          data,
          sortHeaderId
        ) => {
          const value = data[sortHeaderId];
          if (typeof value !== "string") {
            return value;
          }
          return value.toLocaleLowerCase();
        };
      },
      (err) => {
        let msg = "";
        if (err.status == 0) {
          msg = "Server not Responding";
        } else {
          msg = err.error.error;
        }
        this.snackBar.open(msg, "Close", {
          duration: 3000,
          panelClass: ["snackBar"],
        });
      }
    );
  }

  show() {
    this.accessList.forEach((access) => {
      if (access.systemName == "customer_creation") {
        this.showCreateButton = true;
      }

      if (access.systemName == "customer_update") {
        this.showEdit = true;
      }

      if (access.systemName == "customer_delete") {
        this.showDelete = true;
      }
    });
  }

  ngAfterViewInit() {
    this.dataSourceWithPageSize.paginator = this.paginator;
    this.dataSourceWithPageSize.sort = this.sort;
  }

  openSnackBar(message: string, color: string) {
    console.warn(this.token + "\n" + this.userId);
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = [color];
    this.snackBar.open(message, "Dismiss", snackBarConfig);
  }

  createDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "60%";
    dialogConfig.maxHeight = "77%";
    dialogConfig.minHeight = "77%";
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(CreateAccountComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }

  searchData(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceWithPageSize.filter = filterValue.trim().toLowerCase();
  }

  deleteAccount(accountId: number) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "25%";
    dialogConfig.height = "20%";
    console.log("Confirm Delete");

    const dialogRef = this.dialog.open(DeleteDialogComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      console.log(confirmed);
      if (confirmed) {
        this.restService.deleteAccount(accountId);
        console.log("account Deleted");
      } else {
        console.log("account not deleted");
      }
      this.restService.closeDialog();
      this.viewTable();
    });
  }

  viewAccountDialog(account: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "60%";
    dialogConfig.maxHeight = "77%";
    dialogConfig.minHeight = "77%";
    dialogConfig.data = account;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      ViewAccountDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
    });
  }

  viewAccess(account: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    dialogConfig.height = "50%";
    dialogConfig.data = account.accessId;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(ViewAccessDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
      this.accessList.forEach((access) => {
        access.status = 1;
      });
      this.restService.closeDialog();
    });
  }
  viewFeature(account: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    dialogConfig.height = "60%";
    dialogConfig.data = {
      featureId: account.features,
      featuresMeta: account.featuresMeta,
    };
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      ViewFeatureDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.featureList.forEach((feature) => {
        feature.status = 1;
      });
      this.restService.closeDialog();
    });
  }

  updateAccountDialog(account: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "60%";
    dialogConfig.maxHeight = "77%";
    dialogConfig.minHeight = "77%";
    dialogConfig.data = account;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      UpdateAccountDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }
}
