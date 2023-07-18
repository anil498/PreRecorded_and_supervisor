import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-view-access-dialog",
  templateUrl: "./view-access-dialog.component.html",
  styleUrls: ["./view-access-dialog.component.scss"],
})
export class ViewAccessDialogComponent implements OnInit {
  accessData: any;
  topLevelAccess: any[] = [];
  topLevelAccess1: any[] = [];
  topLevelAccess2: any[] = [];
  accessName: any[] = [];

  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<ViewAccessDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public accessId: any
  ) {
    this.accessData = this.restService.getData().Access;
  }

  ngOnInit(): void {
    console.log(this.accessData);
    if (this.accessData) {
      for (let i = 0; i < this.accessData.length; i++) {
        var flag = true;
        for (let j = 0; j < this.accessId.length; j++) {
          if (this.accessId[j] === this.accessData[i].accessId) {
            flag = false;
            break;
          }
        }
        if (flag === true) this.accessData[i].status = 0;
      }
    }
    this.accessData = this.accessData.filter((item) => item.status === 1);
    console.log(this.accessData);
    this.topLevelAccess = this.accessData.filter((item) => item.pId === 0);
    const accessHalf = Math.ceil(this.topLevelAccess.length / 2);
    this.topLevelAccess1 = this.topLevelAccess.slice(0, accessHalf);
    this.topLevelAccess2 = this.topLevelAccess.slice(accessHalf);
  }

  ngOnDestroy() {
    this.accessData.forEach((access) => {
      access.status = 1;
    });
    this.topLevelAccess.forEach((access) => {
      access.status = 1;
    });
    this.topLevelAccess1.forEach((access) => {
      access.status = 1;
    });
    this.topLevelAccess2.forEach((access) => {
      access.status = 1;
    });
  }

  isCheckAccess(access: any) {
    if (access.status == 1) return true;
    return false;
  }
}
