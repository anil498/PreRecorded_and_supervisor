import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { MatTabChangeEvent, MatTabGroup } from '@angular/material/tabs';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RestService } from 'app/services/rest.service';

@Component({
  selector: 'app-update-user-dialog',
  templateUrl: './update-user-dialog.component.html',
  styleUrls: ['./update-user-dialog.component.scss']
})
export class UpdateUserDialogComponent implements OnInit {

  @ViewChild("tabGroup") tabGroup: MatTabGroup;

  samePassword = false;
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

  password: string;
  confirm_password: string;

  max_duration: number;
  max_active_sessions: number;
  max_participants: number;

  // featuresData: any = this.restService.getData().features;
  // accessData: any = this.restService.getData().access;
  selectedAccessId: number[] = [];
  accessData = [
    {
      access_id: 16,
      name: "Account Creation",
      order: 1,
      p_id: 1,
    },
    {
      access_id: 17,
      name: "Account Deletion",
      order: 1,
      p_id: 1,
    },
    {
      access_id: 18,
      name: "Account Updation",
      order: 1,
      p_id: 1,
    },
    {
      access_id: 26,
      name: "User Creation",
      order: 1,
      p_id: 2,
    },
    {
      access_id: 27,
      name: "User Deletion",
      order: 1,
      p_id: 2,
    },
  ];

  selectedFeatures: number[] = [];
  featuresData = [
    {
      feature_id: 1,
      name: "Recording",
      meta_list: {
        max_time: null,
        max_dur: null,
      },
    },
    {
      feature_id: 2,
      name: "Screen Sharing",
      meta_list: {
        refresh_rate: null,
      },
    },
    {
      feature_id: 3,
      name: "Live Chat",
      meta_list: {
        color: null,
      },
    },
  ];

  selectedFeaturesMeta = {};
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<UpdateUserDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar
  ) {}

  updateSelectedFeaturesMeta(featureId, metaValue) {
    if (!this.selectedFeaturesMeta[featureId]) {
      this.selectedFeaturesMeta[featureId] = {};
    }
    this.selectedFeaturesMeta[featureId] = metaValue;
  }

  setMetaValue(featureId, metaKey, metaValue) {
    this.selectedFeaturesMeta[featureId][metaKey] = metaValue;
  }
  showMetaList(feature: any, isChecked: boolean) {
    if (isChecked) {
      this.selectedFeatures.push(feature.feature_id);
      feature.showMetaList = true;
      feature.selectedMetaList = Object.keys(feature.meta_list).map((key) => {
        return { key: key, value: feature.meta_list[key] };
      });
    } else {
      const index = this.selectedFeatures.indexOf(feature.feature_id);
      this.selectedFeatures.splice(index, 1);
      feature.showMetaList = false;
      feature.selectedMetaList = [];
    }
  }

  addAccessId(access: any, isChecked: boolean) {
    if (isChecked) {
      this.selectedAccessId.push(access.access_id);
    } else {
      const index = this.selectedAccessId.indexOf(access.access_id);
      this.selectedAccessId.splice(index, 1);
    }
  }
  async ngOnInit(): Promise<void> {
    this.userForm = this.fb.group(
      {
        user_fname: ["", Validators.required],
        user_lname: ["", Validators.required],
        mobile: ["", Validators.required],
        email: ["", Validators.required],
        login_id: ["", Validators.required],
        acc_exp_date: ["", Validators.required],

        password: ["", Validators.required],
        confirm_password: ["", Validators.required],
        accessList: ["", Validators.required],

        max_duration: ["", Validators.required],
        max_active_sessions: ["", Validators.required],
        max_participants: ["", Validators.required],

        featureList: ["", Validators.required],
      },
      {
        validator: [this.passwordMatchValidator],
      }
    );
  }

  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get("password").value;
    const confirm_password = formGroup.get("confirm_password").value;

    if (password !== confirm_password) {
      formGroup.get("confirm_password").setErrors({ mismatch: true });
    } else {
      formGroup.get("confirm_password").setErrors(null);
    }
  }

  dateValidator(formGroup: FormGroup) {
    const expdate = formGroup.get("acc_exp_date").value;
    const currentDate = new Date();
    if (expdate < currentDate) {
      formGroup.get("acc_exp_date").setErrors({ mismatch: true });
    } else {
      formGroup.get("confirm_password").setErrors(null);
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

    this.password = this.userForm.value.password;
    this.confirm_password = this.userForm.value.confirm_password;

    this.max_duration = this.userForm.value.max_duration;
    this.max_participants = this.userForm.value.max_participants;
    this.max_active_sessions = this.userForm.value.max_active_sessions;

    if (
      this.user_lname === "" ||
      this.user_fname === "" ||
      this.mobile == null ||
      this.email === "" ||
      this.login_id === "" ||
      this.password === "" ||
      this.confirm_password === "" ||
      this.max_active_sessions === null ||
      this.max_duration === null ||
      this.max_participants === null
    ) {
      //this.emptyField = true;
      this.openSnackBar("All fields are mandatory", "snackBar");
      //this.timeOut(3000);
      return;
    }

    if (this.password !== this.confirm_password) {
      return;
    }

    let response: any;
    try {
      response = await this.restService.createUser(
        "create",

        this.user_fname,
        this.user_lname,
        this.mobile,
        this.email,
        this.login_id,
        this.exp_date,

        this.password,
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