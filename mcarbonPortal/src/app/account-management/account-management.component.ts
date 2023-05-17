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
import { UpdateDialogComponent } from "app/update-dialog/update-dialog.component";
import { CreateAccountComponent } from "app/create-account/create-account.component";

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
  pageSizes = [5, 10, 25, 50, 100, 500];
  // displayedColumns: string[] = [
  //   "User Code",
  //   "User ID",
  //   "First Name",
  //   "Last Name",
  //   "Parent ID",
  //   "User Type",
  //   "Account Status",
  //   "Service Type",
  //   "Account Expiry Date"
  // ];
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
  cols: any[] = [
    "Name",
    "Contact",
    "Email",
    "Access Details",
    "Feature Details",
    "Expiry date",
    "Account Status",
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

    // this.accessList = this.restService.getData().access;
    this.accessList = [
      {
        accessId: 1,
        name: "Account Management",
        order: 1,
        pId: 0,
        status: 1,
      },
      {
        accessId: 2,
        name: "User Management",
        order: 2,
        pId: 0,
        status: 1,
      },
      {
        accessId: 3,
        name: "Session Management",
        order: 3,
        pId: 0,
        status: 1,
      },
      {
        accessId: 4,
        name: "Dynamic Support",
        order: 1,
        pId: 0,
        status: 1,
      },
      {
        accessId: 5,
        name: "Account Creation",
        order: 1,
        pId: 1,
        status: 1,
      },
      {
        accessId: 6,
        name: "Account Deletion",
        order: 1,
        pId: 1,
        status: 1,
      },
      {
        accessId: 7,
        name: "Account Updation",
        order: 1,
        pId: 1,
        status: 1,
      },
      {
        accessId: 8,
        name: "View Account Table",
        order: 1,
        pId: 1,
        status: 1,
      },
    ];

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
    // this.restService.getAccountList(this.token, this.userId).then(
    //   (response) => {
    //     this.accounts = response;
    //     console.log(this.accounts);
    //     this.dataSourceWithPageSize.data = this.accounts;
    //   },
    //   (error) => {
    //     console.log(error.status);
    //   }
    // );

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

  deleteData(usercode: number) {
    const dialogConfig = new MatDialogConfig();
    console.log("Confirm Delete");
    // const dialogref = this.dialog.open(DeleteDialog,dialogConfig);
  }

  updateDialog(usercode: number) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "690px";
    dialogConfig.height = "550px";
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(UpdateDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
    });
  }
}
