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
    // "contact",
    // "email",
    "access",
    "features",
    "expDate",
    "status",
    "Action",
  ];

  accounts: Accounts[] = [];
  dataSourceWithPageSize = new MatTableDataSource(this.accounts);

  accessList: any[];
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
    private snackBar: MatSnackBar
  ) {
    this.token = this.restService.getToken();
    this.userId = this.restService.getUserId();
  }

  async ngOnInit(): Promise<void> {
    console.warn(this.token + "\n" + this.userId);

    this.accessList = this.restService.getData().Access;

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

  viewTable() {
    this.restService.getAccountList(this.token, this.userId).then(
      (response) => {
        this.accounts = response;
        console.log(this.accounts);
        this.dataSourceWithPageSize.data = this.accounts;
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
      if (access.pId == 1000) {
        if (access.apiId == 1001) {
          this.showCreateButton = true;
        }

        if (access.apiId == 1002) {
          this.showEdit = true;
        }

        if (access.apiId == 1003) {
          this.showDelete = true;
        }
      }
    });
  }

  ngAfterViewInit() {
    this.dataSourceWithPageSize.paginator = this.paginator;
    this.dataSourceWithPageSize.sort = this.sort;
  }

  openSnackBar(message: string, color: string) {
    console.warn(this.token + "\n" + this.userId);
    this.viewTable();

    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = [color];
    this.snackBar.open(message, "Dismiss", snackBarConfig);
  }

  createDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "690px";
    dialogConfig.height = "550px";
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(CreateAccountComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
    });
  }

  searchData(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceWithPageSize.filter = filterValue.trim().toLowerCase();
  }

  deleteAccount(account: any) {
    //const dialogConfig = new MatDialogConfig();
    this.restService.deleteAccount(this.token, account.accountId);
    console.log("Confirm Delete");

    // const dialogref = this.dialog.open(DeleteDialog,dialogConfig);
  }

  viewAccountDialog(account: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "690px";
    dialogConfig.height = "550px";
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
    const dialogRef = this.dialog.open(
      ViewAccessDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
    });
  }
  viewFeature(account: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    dialogConfig.height = "50%";
    dialogConfig.data = account.features;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      ViewFeatureDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
    });
  }

  updateAccountDialog(account: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "690px";
    dialogConfig.height = "550px";
    dialogConfig.data = account;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      UpdateAccountDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
    });
  }
}
