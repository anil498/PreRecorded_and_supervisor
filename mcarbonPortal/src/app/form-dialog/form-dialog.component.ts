import {
  Component,
  OnInit,
  ViewChild,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Inject,
  OnDestroy,
} from "@angular/core";

import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatTabGroup } from "@angular/material/tabs";
import { MatDialogRef } from "@angular/material/dialog";
import { RestService } from "app/services/rest.service";
import { Router } from "@angular/router";
import { MatCalendar } from "@angular/material/datepicker";
import { MatDateFormats } from "@angular/material/core";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { Time } from "@angular/common";
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
} from "@angular/material/core";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import { MomentDateAdapter } from "@angular/material-moment-adapter";
import * as moment from "moment";
import { DomSanitizer } from "@angular/platform-browser";

@Component({
  selector: "app-form-dialog",
  templateUrl: "./form-dialog.component.html",
  styleUrls: ["./form-dialog.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FormDialogComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;

  currentTabIndex: number = 0;
  userForm: FormGroup;
  messageResponse: any;

  user_fname: string;
  user_lname: string;
  mobile: number;
  email: string;
  login_id: string;
  acc_exp_date: Date;
  exp_date: string;

  password: string;
  confirm_password: string;

  // featuresData: any = this.restService.getData().features;
  // accessData: any = this.restService.getData().access;
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
  ];

  featuresData = [
    {
      feature_id: 1,
      name: "Recording",
      meta_list: {
        max_time: 0,
        max_dur: 0,
      },
    },
    {
      feature_id: 2,
      name: "Screen Sharing",
      meta_list: {
        refresh_rate: 0,
      },
    },
    {
      feature_id: 3,
      name: "Live Chat",
      meta_list: {
        color: "",
      },
    },
  ];
  
  access: number[];
  accessList = [
    { label: "User Creation", value: 1 },
    { label: "Session Updation", value: 2 },
    { label: "Report Download", value: 3 },
  ];

  features: number[];
  featureList = [
    { label: "Recording", value: 1 },
    { label: "Live Chat", value: 2 },
    { label: "Screen Sharing", value: 3 },
    { label: "Video Sharing", value: 4 },
  ];

  max_duration: number;
  max_active_sessions: number;
  max_participants: number;

  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<FormDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer
  ) {}

  async ngOnInit(): Promise<void> {
    this.userForm = this.fb.group({
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

      screen_share: false,
      live_chat: false,
      recording: false,
      broadcasting: false,
      transcoding: false,
    });
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

  onTabChanged(event: any) {
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
    this.access = this.userForm.value.accessList;

    this.max_duration = this.userForm.value.max_duration;
    this.max_participants = this.userForm.value.max_participants;
    this.max_active_sessions = this.userForm.value.max_active_sessions;
    this.features = this.userForm.value.featureList;
    // this.features = Object.keys(this.userForm.value);

    let response: any;
    try {
      response = await this.restService.create(
        "CreateUser",

        this.user_fname,
        this.user_lname,
        this.mobile,
        this.email,
        this.login_id,
        this.exp_date,
        this.password,
        this.access.toString(),

        this.max_duration,
        this.max_participants,
        this.max_active_sessions,

        this.features.toString()
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
}
