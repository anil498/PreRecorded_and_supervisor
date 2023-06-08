import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { MatTabChangeEvent, MatTabGroup } from "@angular/material/tabs";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-update-user-dialog",
  templateUrl: "./update-user-dialog.component.html",
  styleUrls: ["./update-user-dialog.component.scss"],
})
export class UpdateUserDialogComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;

  currentTabIndex: number = 0;
  userForm: FormGroup;
  messageResponse: any;
  loginResponse: any;

  emptyField: boolean = false;

  user_fname: string;
  user_lname: string;
  mobile: number;
  email: string;
  login_id: string;
  acc_exp_date: Date;
  exp_date: string;

  max_duration: number;
  max_active_sessions: number;
  max_participants: number;

  featuresData: any = this.restService.getData().Features;
  accessData: any = this.restService.getData().Access;
  selectedAccessId: number[] = [];

  selectedFeatures: number[] = [];

  selectedFeaturesMeta = {};
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<UpdateUserDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public user: any
  ) {}

  onFileInputChange(event: any, meta: any, featureId: number): void {
    const files: FileList = event.target.files;
    if (files && files.length > 0) {
      const file: File = files[0];
      const reader = new FileReader();
      //reader.readAsArrayBuffer(file);
      console.warn(file);

      reader.onloadend = () => {
        console.warn(reader.result);
        const blob = new Blob([reader.result], { type: file.type });
        console.warn(blob);
        this.selectedFeaturesMeta[featureId.toString()][meta.key] = blob;
        console.warn(this.selectedFeaturesMeta[featureId]);
      };
      // reader.onload = () => {
      //   console.warn([reader.result]);
      //   const blob = new Blob([reader.result]);
      //   console.warn(blob);
      //   this.selectedFeaturesMeta[featureId.toString()][meta.key] = blob;
      //   console.warn(this.selectedFeaturesMeta[featureId]);
      // };
      reader.readAsArrayBuffer(file);
    }
  }

  toggleFeatureSelection(featureId: number): void {
    const index = this.selectedFeatures.indexOf(featureId);
    if (index > -1) {
      this.selectedFeatures.splice(index, 1);
      delete this.selectedFeaturesMeta[featureId.toString()];
    } else {
      this.selectedFeatures.push(featureId);
      this.selectedFeaturesMeta[featureId.toString()] = {};
    }
  }
  updateSelectedFeaturesMeta(featureId, metaValue) {
    if (!this.selectedFeaturesMeta[featureId]) {
      this.selectedFeaturesMeta[featureId] = {};
    }
    this.selectedFeaturesMeta[featureId] = metaValue;
  }

  setMetaValue(featureId, metaKey, metaValue) {
    this.selectedFeaturesMeta[featureId][metaKey] = metaValue;
    console.warn(this.selectedFeaturesMeta[featureId]);
  }
  showMetaList(feature: any, isChecked: boolean) {
    if (isChecked) {
      this.selectedFeatures.push(feature.featureId);
      feature.showMeta = true;
      feature.selectedMetaList = Object.keys(feature.metaList).map((key) => {
        return { key: key, value: feature.metaList[key] };
      });
      console.log(feature.selectedMetaList);
    } else {
      const index = this.selectedFeatures.indexOf(feature.featureId);
      this.selectedFeatures.splice(index, 1);
      feature.showMeta = false;
      feature.selectedMetaList = [];
    }
  }

  addAccessId(access: any, isChecked: boolean) {
    if (isChecked) {
      this.selectedAccessId.push(access.accessId);
    } else {
      const index = this.selectedAccessId.indexOf(access.accessId);
      this.selectedAccessId.splice(index, 1);
    }
  }
  async ngOnInit(): Promise<void> {
    this.userForm = this.fb.group(
      {
        user_fname: [this.user.fname, Validators.required],
        user_lname: [this.user.lname, Validators.required],
        mobile: [this.user.contact, Validators.required],
        email: [this.user.email, Validators.required],
        login_id: [this.user.loginId, Validators.required],
        acc_exp_date: [new Date(this.user.expDate), Validators.required],

        accessList: [this.user.accessId, Validators.required],

        max_duration: [this.user.session.max_duration, Validators.required],
        max_active_sessions: [
          this.user.session.max_active_sessions,
          Validators.required,
        ],
        max_participants: [
          this.user.session.max_participants,
          Validators.required,
        ],

        featureList: [this.user.features, Validators.required],
        featureMeta: [this.user.featuresMeta, Validators.required],
      },
      {
        validator: [this.dateValidator],
      }
    );

    this.selectedAccessId = this.userForm.value.accessList;
    this.selectedFeatures = this.userForm.value.featureList;
    this.selectedFeaturesMeta = this.userForm.value.featureMeta;
    // For diplaying previous checked Access
    for (let i = 0; i < this.accessData.length; i++) {
      var flag = true;
      for (let j = 0; j < this.userForm.value.accessList.length; j++) {
        if (this.userForm.value.accessList[j] === this.accessData[i].accessId) {
          flag = false;
          break;
        }
      }
      if (flag === true) this.accessData[i].status = 0;
    }
    // for displaying previous checked features
    for (let i = 0; i < this.featuresData.length; i++) {
      var flag = true;
      for (let j = 0; j < this.userForm.value.featureList.length; j++) {
        if (
          this.userForm.value.featureList[j] === this.featuresData[i].featureId
        ) {
          flag = false;
          break;
        }
      }
      if (flag === true) this.featuresData[i].status = 0;
    }
  }

  async ngOnDestroy(): Promise<void> {
    this.accessData.forEach((access) => {
      access.status = 1;
    });
    this.featuresData.forEach((feature) => {
      feature.status = 1;
    });
  }

  isCheckFeature(feature: any) {
    if (feature.status == 1) return true;
    return false;
  }

  isCheckAccess(access: any) {
    if (access.status == 1) return true;
    return false;
  }

  dateValidator(formGroup: FormGroup) {
    const expdate = formGroup.get("acc_exp_date").value;
    const currentDate = new Date();
    if (expdate < currentDate) {
      formGroup.get("acc_exp_date").setErrors({ mismatch: true });
    } else {
      formGroup.get("acc_exp_date").setErrors(null);
    }
  }

  previous() {
    if (this.currentTabIndex > 0) {
      this.currentTabIndex--;
      this.tabGroup.selectedIndex = this.currentTabIndex;
    }
  }

  next() {
    if (this.currentTabIndex < this.tabGroup._tabs.length - 1) {
      this.currentTabIndex++;
      this.tabGroup.selectedIndex = this.currentTabIndex;
    }
  }

  onTabChanged(event: MatTabChangeEvent) {
    this.currentTabIndex = event.index;
  }

  isFirstTab(): boolean {
    return this.currentTabIndex === 0;
  }
  isLastTab(): boolean {
    return this.tabGroup
      ? this.currentTabIndex === this.tabGroup._tabs.length - 1
      : false;
  }
  showSubmit(): boolean {
    if (this.tabGroup.selectedIndex == this.tabGroup._tabs.length - 1)
      return true;
    else return false;
  }

  async submit() {
    this.emptyField = false;
    this.user_fname = this.userForm.value.user_fname;
    this.user_lname = this.userForm.value.user_lname;
    this.mobile = this.userForm.value.mobile;
    this.email = this.userForm.value.email;
    this.login_id = this.userForm.value.login_id;
    this.acc_exp_date = this.userForm.value.acc_exp_date;
    this.exp_date = this.acc_exp_date.toISOString().split("T")[0];
    this.exp_date =
      this.exp_date +
      " " +
      this.acc_exp_date.toISOString().split("T")[1].substring(0, 8);

    this.max_duration = this.userForm.value.max_duration;
    this.max_participants = this.userForm.value.max_participants;
    this.max_active_sessions = this.userForm.value.max_active_sessions;

    if (
      this.user_lname === "" ||
      this.user_fname === "" ||
      this.mobile == null ||
      this.email === "" ||
      this.login_id === "" ||
      this.max_active_sessions === null ||
      this.max_duration === null ||
      this.max_participants === null
    ) {
      //this.emptyField = true;
      this.openSnackBar("All fields are mandatory", "snackBar");
      //this.timeOut(3000);
      return;
    }

    let response: any;
    try {
      response = await this.restService.updateUser(
        "Update",
        this.user.userId,
        this.user_fname,
        this.user_lname,
        this.mobile,
        this.email,
        this.login_id,
        this.exp_date,

        this.selectedAccessId.sort(),

        this.max_duration,
        this.max_participants,
        this.max_active_sessions,

        this.selectedFeatures.sort(),
        this.selectedFeaturesMeta
      );
      console.warn(response);
      if (response.statusCode === 200) {
        this.dialogRef.close();
        this.restService.closeDialog();
      }
    } catch (error) {
      console.warn(error);
    }
    this.messageResponse = response;
    this.dialogRef.close();
    this.restService.closeDialog();
  }

  openSnackBar(message: string, color: string) {
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 30000;
    snackBarConfig.panelClass = [color];
    this.snackBar.open(message, "Dismiss", snackBarConfig);
  }

  private timeOut(time: number) {
    console.warn(this.emptyField);
    setTimeout(() => {
      this.emptyField = false;
    }, time);
    console.warn(this.emptyField);
  }
}
