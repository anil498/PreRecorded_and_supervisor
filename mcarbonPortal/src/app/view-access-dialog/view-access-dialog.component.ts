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
    this.accessId.forEach((accessId) => {
      this.showAccessName(accessId);
    });
  }

  showAccessName(accessId: number) {
    this.accessData.forEach((access) => {
      if (accessId == access.accessId) this.accessName.push(access.name);
    });
  }
}
