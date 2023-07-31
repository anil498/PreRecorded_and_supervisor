import { Component, OnInit, ViewChild } from "@angular/core";
import { DateAdapter } from "@angular/material/core";
import { MatDialog, MatDialogConfig } from "@angular/material/dialog";
import { MatIconRegistry } from "@angular/material/icon";
import { MatPaginator } from "@angular/material/paginator";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { browserRefresh } from "app/app.component";
import { DeleteDialogComponent } from "app/delete-dialog/delete-dialog.component";
import { FeedbackFormComponent } from "app/feedback-form/feedback-form.component";
import { Icdc } from "app/model/icdc";
import { RestService } from "app/services/rest.service";
import { UpdateAccountDialogComponent } from "app/update-account-dialog/update-account-dialog.component";
import { ViewAccountDialogComponent } from "app/view-account-dialog/view-account-dialog.component";
import { ViewQuestionDialogComponent } from "app/view-question-dialog/view-question-dialog.component";
import { Subscription } from "rxjs/internal/Subscription";

@Component({
  selector: "app-icdc-management",
  templateUrl: "./icdc-management.component.html",
  styleUrls: ["./icdc-management.component.scss"],
})
export class IcdcManagementComponent implements OnInit {
  search: String = "";
  token: string;
  userId: string;
  private updateSubscription: Subscription;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  pageSizes = [10, 25, 50, 100, 500];
  displayedColumns: any[] = [
    "icdcId",
    "formName",
    "icdcData",
    "creationDate",
    "status",
    "Action",
  ];

  icdcs: Icdc[] = [];
  dataSourceWithPageSize = new MatTableDataSource(this.icdcs);
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

  // checkStatus(icdc: Icdc) {
  //   let currentDate = new Date();
  //   let time = currentDate.toTimeString().split(" ")[0];
  //   console.log("time " + time);
  //   let currentDateString = currentDate.toISOString().split("T")[0];
  //   console.log(currentDateString);
  //   currentDateString = currentDateString + " " + time;

  //   if (icdc.expDate < currentDateString && icdc.status !== 2) {
  //     console.log(icdc.expDate + "  " + currentDateString);
  //     icdc.status = 3;
  //   }
  // }

  viewTable() {
    this.restService.getIcdcList(this.token, this.userId).then(
      (response) => {
        this.icdcs = response;
        console.log(this.icdcs);
        // this.icdcs.forEach(async (account) => {
        //   await this.checkStatus(account);
        // });
        this.dataSourceWithPageSize.data = this.icdcs;
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
      if (access.systemName == "icdc_creation") {
        this.showCreateButton = true;
      }

      if (access.systemName == "icdc_update") {
        this.showEdit = true;
      }

      if (access.systemName == "icdc_delete") {
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

  createIcdc() {
    console.log("ICDC create");
    this.router.navigate(["app", "icdc", "create"]);
    // const dialogConfig = new MatDialogConfig();
    // dialogConfig.width = "60%";
    // dialogConfig.maxHeight = "77%";
    // dialogConfig.minHeight = "77%";
    // console.log("Dialog Form Opened");
    // const dialogRef = this.dialog.open(FeedbackFormComponent, dialogConfig);

    // dialogRef.afterClosed().subscribe(() => {
    //   this.restService.closeDialog();
    //   this.viewTable();
    // });
  }

  searchData(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceWithPageSize.filter = filterValue.trim().toLowerCase();
  }

  deleteIcdc(formId: number) {
    const dialogConfig = new MatDialogConfig();
    console.log("Confirm Delete");
    const dialogRef = this.dialog.open(DeleteDialogComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      console.log(confirmed);
      if (confirmed) {
        this.restService.deleteIcdc(formId);
        console.log("IDCD Deleted");
      } else {
        console.log("ICDC not deleted");
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
  viewQuestionDialog(icdc: Icdc) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = icdc;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      ViewQuestionDialogComponent,
      dialogConfig
    );
  }
}
