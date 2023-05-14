import {
  Component,
  OnInit,
  ViewChild,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Inject,
  OnDestroy,
} from "@angular/core";

import { FormArray, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatTabGroup } from "@angular/material/tabs";
import { MatDialogRef } from "@angular/material/dialog";
import { RestService } from "app/services/rest.service";
import { Router } from "@angular/router";
import { DomSanitizer } from "@angular/platform-browser";

@Component({
  selector: "app-create-account",
  templateUrl: "./create-account.component.html",
  styleUrls: ["./create-account.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateAccountComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;

  samePassword: false;
  currentTabIndex: number = 0;
  userForm: FormGroup;
  messageResponse: any;
  loginResponse: any;

  name: string;
  address: string;
  logo: Blob;
  acc_exp_date: Date;
  exp_date: string;
  max_user: number;

  user_fname: string;
  user_lname: string;
  mobile: number;
  email: string;
  login_id: string;

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
    private dialogRef: MatDialogRef<CreateAccountComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer
  ) {
    this.loginResponse = this.restService.getToken();
  }

  updateSelectedFeaturesMeta(featureId, metaValue) {
    if (!this.selectedFeaturesMeta[featureId]) {
      this.selectedFeaturesMeta[featureId] = {}; 
    }
    this.selectedFeaturesMeta[featureId] = metaValue;
  }

  setMetaValue(featureId, metaKey, metaValue){
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
        name: ["", Validators.required],
        address: ["", Validators.required],
        acc_exp_date: ["", Validators.required],
        max_admin: ["", Validators.required],
        max_user: ["", Validators.required],

        user_fname: ["", Validators.required],
        user_lname: ["", Validators.required],
        mobile: ["", Validators.required],
        email: ["", Validators.required],
        login_id: ["", Validators.required],

        password: ["", Validators.required],
        confirm_password: ["", Validators.required],

        accessList: ["", Validators.required],

        max_duration: ["", Validators.required],
        max_active_sessions: ["", Validators.required],
        max_participants: ["", Validators.required],

        featureList: ["", Validators.required],

        options: this.fb.array([]),
        video_share: false,
        screen_share: false,
        live_chat: false,
        recording: false,
      },
      {
        validator: this.checkPasswords,
      }
    );
  }

  // onPhotoSelected(event) {
  //   const file: File = event.target.files[0];
  //   const reader: FileReader = new FileReader();

  //   reader.onloadend = (e) => {
  //     this.photoUrl = reader.result as string;
  //     this.photoControl = true;
  //   };

  //   if (file) {
  //     reader.readAsDataURL(file);
  //   }
  // }

  checkPasswords(group: FormGroup) {
    const password = group.controls.password.value;
    const confirmPassword = group.controls.confirm_password.value;
    return password === confirmPassword ? null : { passwordsDoNotMtach: true };
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
    this.name = this.userForm.value.name;
    this.address = this.userForm.value.address;
    this.max_user = this.userForm.value.max_user;
    this.acc_exp_date = this.userForm.value.acc_exp_date;
    this.exp_date = this.acc_exp_date.toISOString().split("T")[0];
    this.exp_date =
      this.exp_date +
      " " +
      this.acc_exp_date.toISOString().split("T")[1].substring(0, 8);

    this.max_duration = this.userForm.value.max_duration;
    this.max_participants = this.userForm.value.max_participants;
    this.max_active_sessions = this.userForm.value.max_active_sessions;

    this.user_fname = this.userForm.value.user_fname;
    this.user_lname = this.userForm.value.user_lname;
    this.mobile = this.userForm.value.mobile;
    this.email = this.userForm.value.email;
    this.login_id = this.userForm.value.login_id;

    this.password = this.userForm.value.password;

    console.warn(this.selectedFeaturesMeta);      
    let response: any;

    try {
      response = await this.restService.createAccountUser(
        "create",
        this.name,
        this.address,
        this.logo,
        this.max_user,
        this.exp_date,

        this.max_active_sessions,
        this.max_duration,
        this.max_participants,
        this.selectedAccessId.sort(),

        this.selectedFeatures.sort(),
        this.selectedFeaturesMeta,
        this.user_fname,
        this.user_lname,
        this.mobile,
        this.email,
        this.login_id,
        this.password
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
