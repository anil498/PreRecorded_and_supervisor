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
  selector: "app-update-dialog",
  templateUrl: "./update-dialog.component.html",
  styleUrls: ["./update-dialog.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateDialogComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;

  currentTabIndex: number = 0;
  userForm: FormGroup;
  messageResponse: any;

  user_fname: string;
  user_lname: string;
  mobile: number;
  email: string;
  address: string;
  user_id: string;
  parent_id: string;
  acc_type: string;
  service_type: string;
  services = [
    { label: "Service 1", value: "service1" },
    { label: "Service 2", value: "service2" },
    { label: "Service 3", value: "service3" },
  ];
  accounts = [
    { label: "Admin", value: "admin" },
    { label: "Customer", value: "customer" },
    { label: "Super Admin", value: "superadmin" },
  ];

  password: string;
  acc_status: string;
  status = [
    { label: "Active", value: "active" },
    { label: "Suspended", value: "suspended" },
  ];
  acc_exp_date: Date;
  exp_date: string;
  max_users: number;
  max_duration: number;
  active_sessions: number;
  max_participants: number;

  video_share: boolean;
  screen_share: boolean;
  live_chat: boolean;
  recording: boolean;
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<UpdateDialogComponent>,
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
      address: ["", Validators.required],
      user_id: ["", Validators.required],
      parent_id: ["", Validators.required],
      accounts: ["", Validators.required],
      services: ["", Validators.required],
      password: ["", Validators.required],
      status: ["", Validators.required],
      acc_exp_date: ["", Validators.required],
      max_users: ["", Validators.required],
      max_duration: ["", Validators.required],
      active_sessions: ["", Validators.required],
      max_participants: ["", Validators.required],

      video_share: false,
      screen_share: false,
      recording: false,
      live_chat: false,
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
    this.address = this.userForm.value.address;
    this.user_id = this.userForm.value.user_id;
    this.parent_id = this.userForm.value.parent_id;
    this.acc_type = this.userForm.value.accounts;
    this.service_type = this.userForm.value.services;

    this.password = this.userForm.value.password;
    this.acc_status = this.userForm.value.status;
    this.acc_exp_date = this.userForm.value.acc_exp_date;
    this.exp_date = this.acc_exp_date.toISOString().split("T")[0];
    this.exp_date =
      this.exp_date +
      " " +
      this.acc_exp_date.toISOString().split("T")[1].substring(0, 8);
    this.max_users = this.userForm.value.max_users;
    this.max_duration = this.userForm.value.max_duration;
    this.max_participants = this.userForm.value.max_participants;
    this.active_sessions = this.userForm.value.active_sessions;

    this.video_share = this.userForm.value.video_share;
    this.screen_share = this.userForm.value.screen_share;
    this.recording = this.userForm.value.recording;
    this.live_chat = this.userForm.value.live_chat;
    // console.warn(
    //   "Form submitted: " +
    //     this.user_fname +
    //     " " +
    //     this.user_lname +
    //     " " +
    //     this.mobile +
    //     " " +
    //     this.email +
    //     " " +
    //     this.address +
    //     " " +
    //     this.user_id +
    //     " " +
    //     this.parent_id +
    //     " " +
    //     this.acc_type +
    //     " " +
    //     this.service_type +
    //     " " +
    //     this.password +
    //     " " +
    //     this.acc_status +
    //     " " +
    //     this.acc_exp_date +
    //     " " +
    //     this.max_users +
    //     " " +
    //     this.max_duration +
    //     " " +
    //     this.max_participants +
    //     " " +
    //     this.active_sessions
    // );

    const response = await this.restService.update(
      "update",
      this.user_fname,
      this.user_lname,
      this.mobile,
      this.email,
      this.address,
      this.user_id,
      this.parent_id,
      this.acc_type,
      this.service_type,

      this.password,
      this.acc_status,
      this.exp_date,

      this.max_users,
      this.max_duration,
      this.max_participants,
      this.active_sessions,

      this.screen_share,
      this.video_share,
      this.live_chat,
      this.recording
    );

    // console.log(response);

    this.messageResponse = response;
    this.dialogRef.close();
    this.restService.closeDialog();
  }
  
}

