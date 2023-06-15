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
import { ImageFile } from "app/model/image-file";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";

@Component({
  selector: "app-create-account",
  templateUrl: "./create-account.component.html",
  styleUrls: ["./create-account.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateAccountComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;
  title: string;
  samePassword = false;
  currentTabIndex: number = 0;
  userForm: FormGroup;
  messageResponse: any;
  loginResponse: any;

  emptyError = false;
  maxUserError: boolean;
  expDateError: boolean;
  addressError: boolean;

  maxSessionError: boolean;
  maxDurationError: boolean;
  maxParticiapntsError: boolean;

  fNameError: boolean;
  lNameError: boolean;
  contactError: boolean;
  emailError: boolean;
  loginIdError: boolean;
  passwordError: boolean;
  confirmPasswordError: boolean;

  name: string;
  address: string;
  logo: any;
  acc_exp_date: Date;
  exp_date: string;
  max_user: number;

  user_fname: string;
  user_lname: string;
  mobile: number;
  email: string;
  login_id: string;
  photoUrl: any;
  photoControl: boolean = false;
  password: string;
  confirm_password: string;
  max_duration: number;
  max_active_sessions: number;
  max_participants: number;

  featuresData: any = this.restService.getData().Features;
  featuresData1: any;
  featuresData2: any;
  accessData: any = this.restService.getData().Access;
  selectedAccessId: number[] = [];

  selectedFeatures: number[] = [];
  topLevelAccess: any[] = [];

  selectedFeaturesMeta: { [key: string]: any } = {};

  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<CreateAccountComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar
  ) {
    this.loginResponse = this.restService.getToken();
    const half = Math.ceil(this.featuresData.length / 2);
    this.featuresData1 = this.featuresData.slice(0, half);
    this.featuresData2 = this.featuresData.slice(half);
  }

  onPhotoSelected(event) {
    const files: FileList = event.target.files;
    if (files && files.length > 0) {
      const file: File = files[0];

      const image: ImageFile = {
        file: file,
        url: this.sanitizer.bypassSecurityTrustUrl(
          window.URL.createObjectURL(file)
        ),
      };
      const reader = new FileReader();
      console.log(file);
      reader.readAsDataURL(file);
      reader.onloadend = () => {
        console.log();
        this.photoUrl = reader.result;
        this.photoControl = true;

        this.logo = { byte: reader.result, type: file.type };
        console.log(this.logo);
        console.log(this.photoControl);
        console.log(this.photoUrl);
      };
    }
  }

  onFileInputChange(event: any, meta: any, featureId: number): void {
    const files: FileList = event.target.files;
    if (files && files.length > 0) {
      const file: File = files[0];
      const reader = new FileReader();
      reader.readAsDataURL(file);
      //reader.readAsArrayBuffer(file);
      console.warn(file);

      reader.onloadend = () => {
        console.warn(reader.result);
        const blob = { byte: reader.result, type: file.type };
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
      console.log(this.selectedFeaturesMeta);
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

  toggleAccessId(access: any, isChecked: boolean) {
    if (isChecked) {
      this.selectedAccessId.push(access.accessId);
    } else {
      if (access.pId === 0) {
        this.accessData.forEach((accessData) => {
          if (accessData.pId === access.accessId) {
            if (this.selectedAccessId.includes(accessData.accessId)) {
              console.log(accessData);
              const index = this.selectedAccessId.indexOf(accessData.accessId);
              this.selectedAccessId.splice(index, 1);
            }
          }
        });
        const index = this.selectedAccessId.indexOf(access.accessId);
        this.selectedAccessId.splice(index, 1);
      } else {
        const index = this.selectedAccessId.indexOf(access.accessId);
        this.selectedAccessId.splice(index, 1);
      }
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
        logo: [null, Validators.required],
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
      },
      {
        validator: this.passwordMatchValidator,
      }
    );
    this.accessData.forEach((access) => {
      console.log(access);
      if (access.systemName == "customer_creation") {
        this.title = access.name;
      }
    });
    console.log(this.title);

    this.topLevelAccess = this.accessData.filter((item) => item.pId === 0);
    console.log(this.topLevelAccess);
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
    this.emptyError = false;

    this.name = this.userForm.value.name;
    this.address = this.userForm.value.address;
    this.max_user = this.userForm.value.max_user;
    this.acc_exp_date = this.userForm.value.acc_exp_date;
    this.exp_date = this.acc_exp_date.toISOString().split("T")[0];
    this.exp_date =
      this.exp_date +
      " " +
      this.acc_exp_date.toISOString().split("T")[1].substring(0, 8);
    //this.logo = this.userForm.value.logo;

    this.max_duration = this.userForm.value.max_duration;
    this.max_participants = this.userForm.value.max_participants;
    this.max_active_sessions = this.userForm.value.max_active_sessions;

    this.user_fname = this.userForm.value.user_fname;
    this.user_lname = this.userForm.value.user_lname;
    this.mobile = this.userForm.value.mobile;
    this.email = this.userForm.value.email;
    this.login_id = this.userForm.value.login_id;

    this.password = this.userForm.value.password;
    console.log("Access ID: " + this.selectedAccessId);
    console.log("Feature ID: " + this.selectedFeatures);
    console.log("FeatureMetas " + `${this.selectedFeaturesMeta}`);
    console.log(this.logo);
    if (
      this.name == null ||
      this.max_user == null ||
      this.acc_exp_date == null ||
      this.address == null ||
      this.max_active_sessions == null ||
      this.max_duration == null ||
      this.max_participants == null ||
      this.password == null ||
      this.user_fname == null ||
      this.user_lname == null ||
      this.mobile == null ||
      this.email == null ||
      this.login_id == null ||
      this.confirm_password == null
    ) {
      console.warn(this.emptyError);
      this.emptyError = true;
      this.timeOut(3000);
      console.warn(this.emptyError);
      return;
    }

    let response: any;

    try {
      response = await this.restService.createAccountUser(
        "Create",
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
      if (response.status_code === 200) {
        this.openSnackBar(response.msg, "snackBar");
        this.timeOut(3000);
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
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = [color];
    this.snackBar.open(message, "Dismiss", snackBarConfig);
  }
  private timeOut(time: number) {
    setTimeout(() => {
      this.emptyError = false;
    }, time);
  }
}
