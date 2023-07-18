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
  photoControl: boolean = false;
  photoUrl: any;
  constructor(@Inject(MAT_DIALOG_DATA) public settings: any) {}

  async ngOnInit(): Promise<void> {
    console.log(this.settings);

    if (
      this.settings.hasOwnProperty("logo") &&
      this.settings.logo !== null &&
      Object.keys(this.settings.logo).length !== 0
    ) {
      console.log(this.settings.logo);
      this.photoUrl = this.settings.logo.byte;
      this.photoControl = true;
    }
  }
  checkImg(): boolean {
    return this.photoControl;
  }

  checkType(value: any) {
    const type = typeof value;

    if (type == "object") return false;
    return true;
  }
}
