import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";

@Component({
  selector: "app-view-session-setting-dialog",
  templateUrl: "./view-session-setting-dialog.component.html",
  styleUrls: ["./view-session-setting-dialog.component.scss"],
})
export class ViewSessionSettingDialogComponent implements OnInit {
  accessData: any;
  accessName: any[] = [];
  constructor(@Inject(MAT_DIALOG_DATA) public settings: any) {}

  ngOnInit(): void {}

  checkType(value: any) {
    const type = typeof value;

    if (type == "object") return false;
    return true;
  }
}
