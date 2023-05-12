import { Component, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Sessions } from "../model/sessions";
import { MatDialog, MatDialogConfig } from "@angular/material/dialog";
import { RestService } from "app/services/rest.service";
import { SessionDialogComponent } from "../session-dialog/session-dialog.component";

@Component({
  selector: "app-session-management",
  templateUrl: "./session-management.component.html",
  styleUrls: ["./session-management.component.scss"],
})
export class SessionManagementComponent implements OnInit {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  search = "";
  pageSizes = [5, 10, 15];

  displayedColumns: string[] = ["Session ID", "Sessions Name"];
  sessions: Sessions[] = [];
  dataSourceWithPageSize = new MatTableDataSource<Sessions>(this.sessions);
  constructor(
    private dialog: MatDialog,
    private restService: RestService
  ) {}

  ngOnInit(): void {}

  searchData(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceWithPageSize.filter = filterValue.trim().toLowerCase();
  }

  openDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "690px";
    dialogConfig.height = "550px";
    console.log("Dialog Form Opened");
    const dialogRef = this.dialog.open(SessionDialogComponent, dialogConfig);
  }
}
