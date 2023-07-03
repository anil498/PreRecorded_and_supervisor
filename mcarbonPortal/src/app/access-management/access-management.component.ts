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
import { MatSort, Sort } from "@angular/material/sort";
import { Access } from "app/model/access";
import { CreateAccessDialogComponent } from "app/create-access-dialog/create-access-dialog.component";
import { DeleteDialogComponent } from "app/delete-dialog/delete-dialog.component";
import { UpdateAccessDialogComponent } from "app/update-access-dialog/update-access-dialog.component";

@Component({
  selector: "app-access-management",
  templateUrl: "./access-management.component.html",
  styleUrls: ["./access-management.component.scss"],
})
export class AccessManagementComponent implements OnInit {
  search: String = "";
  token: string;
  userId: string;
  showCreate: boolean = false;
  showEdit: boolean = false;
  showDelete: boolean = false;
  accessList: any[];

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  pageSizes = [10, 25, 50, 100];
  displayedColumns: any[] = [
    "accessId",
    "pId",
    "seq",
    "name",
    "systemName",
    "status",
    "Action",
  ];

  access: Access[] = [];
  dataSourceWithPageSize = new MatTableDataSource(this.access);
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
  }

  viewTable() {
    this.restService.getAccessList(this.token, this.userId).then(
      (response) => {
        this.access = response;
        console.log(this.access);
        this.dataSourceWithPageSize.data = this.access;
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
      if (access.systemName == "access_creation") {
        this.showCreate = true;
      }

      if (access.systemName == "access_update") {
        this.showEdit = true;
      }

      if (access.systemName == "acceess_delete") {
        this.showDelete = true;
      }
    });
  }

  ngAfterViewInit() {
    this.dataSourceWithPageSize.paginator = this.paginator;
    this.dataSourceWithPageSize.sort = this.sort;
  }

  openSnackBar(message: string, color: string) {
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = [color];
    this.snackBar.open(message, "Dismiss", snackBarConfig);
  }

  searchData(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceWithPageSize.filter = filterValue.trim().toLowerCase();
  }

  createDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    // dialogConfig.maxHeight = "77%";
    // dialogConfig.minHeight = "77%";
    dialogConfig.data = this.access;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      CreateAccessDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }

  updateAccess(access: any) {
    const dialogData = {
      accesses: this.access,
      access: access,
    };
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    dialogConfig.data = dialogData;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      UpdateAccessDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }
  async deleteAccess(accessId: number) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "25%";
    console.log("Confirm Delete");

    const dialogRef = this.dialog.open(DeleteDialogComponent, dialogConfig);
    dialogRef.afterClosed().subscribe(async (confirmed: boolean) => {
      console.log(confirmed);
      let response: any;
      if (confirmed) {
        response = await this.restService.deleteAccess(accessId);
        if (response.status_code == 200) {
          console.log("access Deleted");
          this.openSnackBar(response.msg, "snackBar");
        }
      } else {
        this.openSnackBar(response, "snackBar");
        console.log("access not deleted");
      }
      this.restService.closeDialog();
      this.viewTable();
    });
  }
}
