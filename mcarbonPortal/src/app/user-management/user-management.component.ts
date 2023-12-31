import { Component, OnInit, ViewChild, Input } from "@angular/core";
import {
  MatDialog,
  MatDialogConfig,
  MatDialogRef,
} from "@angular/material/dialog";
import { FormDialogComponent } from "app/form-dialog/form-dialog.component";
import { RestService } from "app/services/rest.service";
import { interval, Subscription } from "rxjs";
import { MatTableDataSource } from "@angular/material/table";
import { MatIconRegistry } from "@angular/material/icon";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { DomSanitizer } from "@angular/platform-browser";
import { MatPaginator } from "@angular/material/paginator";
import { Router } from "@angular/router";
import { Users } from "../model/users";
import { MatSort, Sort } from "@angular/material/sort";
import { UpdateUserDialogComponent } from "app/update-user-dialog/update-user-dialog.component";
import { ViewFeatureDialogComponent } from "app/view-feature-dialog/view-feature-dialog.component";
import { ViewAccessDialogComponent } from "app/view-access-dialog/view-access-dialog.component";
import { ViewUserDialogComponent } from "app/view-user-dialog/view-user-dialog.component";
import { DeleteDialogComponent } from "app/delete-dialog/delete-dialog.component";
import { browserRefresh } from "app/app.component";

@Component({
  selector: "app-user-management",
  templateUrl: "./user-management.component.html",
  styleUrls: ["./user-management.component.scss"],
})
export class UserManagementComponent implements OnInit {
  search: String = "";
  token: string;
  userId: string;
  accessList: any[];
  featureList: any[];
  showCreateButton = false;
  showEdit = false;
  showDelete = false;
  showExpire = false;
  private updateSubscription: Subscription;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  pageSizes = [10, 25, 50, 100, 500];

  displayedColumns: string[] = [
    "userFname",
    "userLname",
    "loginId",
    "contact",
    "email",
    "access",
    "features",
    "expDate",
    "status",
    "Action",
  ];

  users: Users[] = [];
  dataSourceWithPageSize = new MatTableDataSource(this.users);
  browserRefresh: boolean;

  constructor(
    private dialog: MatDialog,
    private router: Router,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
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
        let logoResponse;
        try {
          logoResponse = await this.restService.getLogo(
            "User/getImage",
            null,
            loginResponse.user_data.userId
          );
          console.log("logo1", logoResponse);
          loginResponse.user_data.logo = logoResponse;
        } catch (err) {
          console.log(err);
          loginResponse.user_data.logo = null;
          this.openSnackBar("Something Went Wrong", "error");
        }
        this.restService.setData(loginResponse);
        this.restService.setToken(loginResponse.token);
        this.restService.setAuthKey(loginResponse.auth_key);
        this.restService.setUserId(localStorage.getItem("loginId"));
      }
    }
    this.token = this.restService.getToken();
    this.userId = this.restService.getUserId();
    this.accessList = this.restService.getData().Access;
    this.featureList = this.restService.getData().Features;
    // this.restService.dialogClosed$.subscribe(() => {
    //   this.openSnackBar("User Created", "snackBar");
    // });

    console.warn(this.token + "\n" + this.userId);

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

  checkStatus(user: Users) {
    let currentDate = new Date();
    let time = currentDate.toTimeString().split(" ")[0];
    let currentDateString = currentDate.toISOString().split("T")[0];
    currentDateString = currentDateString + " " + time;

    if (user.expDate < currentDateString && user.status !== 2) {
      console.log(user.expDate + "  " + currentDateString);
      user.status = 3;
    }
  }

  show() {
    if (this.accessList.length > 0) {
      this.accessList.forEach((access) => {
        if (access.systemName == "user_creation") {
          this.showCreateButton = true;
        }

        if (access.systemName == "user_update") {
          this.showEdit = true;
        }

        if (access.systemName == "user_delete") {
          this.showDelete = true;
        }
      });
    }
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
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(FormDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }

  viewTable() {
    this.restService.getUserList(this.token, this.userId).then(
      (response) => {
        this.users = response;
        console.log(this.users);
        this.users.forEach(async (user) => {
          await this.checkStatus(user);
        });
        this.dataSourceWithPageSize.data = this.users;
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
          msg = "Server Not Responding";
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

  searchData(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceWithPageSize.filter = filterValue.trim().toLowerCase();
  }

  ngAfterViewInit() {
    this.dataSourceWithPageSize.paginator = this.paginator;
    this.dataSourceWithPageSize.sort = this.sort;
  }

  viewUserDialog(user: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "60%";
    dialogConfig.maxHeight = "77%";
    dialogConfig.data = user;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(ViewUserDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
    });
  }

  viewAccess(user: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    dialogConfig.height = "50%";
    dialogConfig.data = user.accessId;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(ViewAccessDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
      this.accessList.forEach((access) => {
        access.status = 1;
      });
      this.restService.closeDialog();
    });
  }
  viewFeature(user: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    dialogConfig.height = "60%";
    dialogConfig.data = {
      featureId: user.features,
      featuresMeta: user.featuresMeta,
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

  deleteUser(userId: number) {
    const dialogConfig = new MatDialogConfig();
    console.log("Confirm Delete");

    const dialogRef = this.dialog.open(DeleteDialogComponent, dialogConfig);
    dialogRef.afterClosed().subscribe(async (confirmed: boolean) => {
      console.log(confirmed);
      if (confirmed) {
        let response = await this.restService.deleteUser(userId);
        console.log(response);
        if (response.status_code == 200) {
          console.log("user Deleted");
          this.openSnackBar(response.msg, "success");
        } else {
          this.openSnackBar("User Not Deleted", "error");
        }
      } else {
        console.log("user not deleted");
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }

  updateUserDialog(user: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "60%";
    dialogConfig.maxHeight = "77%";
    dialogConfig.data = user;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(UpdateUserDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }
}
