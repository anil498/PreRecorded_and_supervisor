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
import { Features } from "app/model/features";
import { CreateFeatureDialogComponent } from "app/create-feature-dialog/create-feature-dialog.component";
import { DeleteDialogComponent } from "app/delete-dialog/delete-dialog.component";
import { UpdateFeatureDialogComponent } from "app/update-feature-dialog/update-feature-dialog.component";
import { browserRefresh } from "app/app.component";
@Component({
  selector: "app-feature-management",
  templateUrl: "./feature-management.component.html",
  styleUrls: ["./feature-management.component.scss"],
})
export class FeatureManagementComponent implements OnInit {
  search: string = "";
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
    "featureId",
    "name",
    "metaList",
    "status",
    "Action",
  ];

  features: Features[] = [];
  dataSourceWithPageSize = new MatTableDataSource(this.features);
  browserRefresh: any;

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
          console.log("logo1",logoResponse);
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
    console.warn(this.token + "\n" + this.userId);

    this.accessList = this.restService.getData().Access;

    this.show();
    this.viewTable();
  }

  viewTable() {
    this.restService.getFeatureList(this.token, this.userId).then(
      (response) => {
        this.features = response;
        console.log(this.features);
        this.dataSourceWithPageSize.data = this.features;
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
      if (access.systemName == "feature_creation") {
        this.showCreate = true;
      }

      if (access.systemName == "feature_update") {
        this.showEdit = true;
      }

      if (access.systemName == "feature_delete") {
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
    dialogConfig.maxHeight = "70%";
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      CreateFeatureDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }

  updateFeature(feature: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "50%";
    dialogConfig.maxHeight = "70%";
    dialogConfig.data = feature;
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(
      UpdateFeatureDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe(() => {
      this.restService.closeDialog();
      this.viewTable();
    });
  }
  async deleteFeature(featureId: number) {
    const dialogConfig = new MatDialogConfig();
    console.log("Confirm Delete");

    const dialogRef = this.dialog.open(DeleteDialogComponent, dialogConfig);
    dialogRef.afterClosed().subscribe(async (confirmed: boolean) => {
      console.log(confirmed);
      let response: any;
      if (confirmed) {
        response = await this.restService.deleteFeature(featureId);
        console.log(response);
        if (response.status_code === 200) {
          console.log("Feature Deleted");
          this.openSnackBar(response.msg, "snackBar");
        }
      } else {
        this.openSnackBar(response, "snackBar");
        console.log("Feature not deleted");
      }
      this.restService.closeDialog();
      this.viewTable();
    });
  }
}
