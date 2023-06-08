import { Component, OnInit, ViewChild, Inject } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatTabGroup } from "@angular/material/tabs";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-update-account-dialog",
  templateUrl: "./update-account-dialog.component.html",
  styleUrls: ["./update-account-dialog.component.scss"],
})
export class UpdateAccountDialogComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;

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

  name: string;
  address: string;
  logo: Blob = null;
  acc_exp_date: Date;
  exp_date: string;
  max_user: number;
  creationDate: string;

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
    private dialogRef: MatDialogRef<UpdateAccountDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public account: any
  ) {
    this.loginResponse = this.restService.getToken();
  }

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

  async ngOnDestroy(): Promise<void> {
    this.accessData.forEach((access) => {
      access.status = 1;
    });
    this.featuresData.forEach((feature) => {
      feature.status = 1;
    });
  }

  async ngOnInit(): Promise<void> {
    this.userForm = this.fb.group({
      name: [this.account.name, Validators.required],
      address: [this.account.address, Validators.required],
      acc_exp_date: [new Date(this.account.expDate), Validators.required],
      max_user: [this.account.maxUser, Validators.required],
      logo: [this.account.logo],

      accessList: [this.account.accessId, Validators.required],

      max_duration: [this.account.session.max_duration, Validators.required],
      max_active_sessions: [
        this.account.session.max_active_sessions,
        Validators.required,
      ],
      max_participants: [
        this.account.session.max_participants,
        Validators.required,
      ],

      featureList: [this.account.features, Validators.required],
      featureMeta: [this.account.featuresMeta, Validators.required],
    });

    this.selectedAccessId = this.userForm.value.accessList;
    this.selectedFeatures = this.userForm.value.featureList;
    this.selectedFeaturesMeta = this.userForm.value.featureMeta;
    console.warn(this.selectedFeaturesMeta);
    // For diplaying previous checked Access
    if (
      this.accessData.length !== 0 &&
      this.userForm.value.accessList.length !== 0
    ) {
      for (let i = 0; i < this.accessData.length; i++) {
        var flag = true;
        for (let j = 0; j < this.userForm.value.accessList.length; j++) {
          if (
            this.userForm.value.accessList[j] === this.accessData[i].accessId
          ) {
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
            this.userForm.value.featureList[j] ===
            this.featuresData[i].featureId
          ) {
            flag = false;
            break;
          }
        }
        if (flag === true) {
          this.featuresData[i].status = 0;
        } else {
        }
      }
    }
  }

  // onPhotoSelected(event) {
  //   const file: File = event.target.files[0];
  //   const reader: FileReader = new FileReader();

  //   reader.onloadend = (e) => {
  //     this.photoUrl = reader.result as string;
  //     this.photoControl = true;
  //   };

  //   if (file)
  //     reader.readAsDataURL(file);
  // }

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

    this.creationDate = this.account.creationDate;
    this.max_duration = this.userForm.value.max_duration;
    this.max_participants = this.userForm.value.max_participants;
    this.max_active_sessions = this.userForm.value.max_active_sessions;
    this.selectedAccessId = this.userForm.value.accessList;
    this.selectedFeatures = this.userForm.value.featureList;
    this.selectedFeaturesMeta = this.userForm.value.featureMeta;

    console.warn(this.userForm.value);
    if (
      this.name == null ||
      this.max_user == null ||
      this.acc_exp_date == null ||
      this.address == null ||
      this.max_active_sessions == null ||
      this.max_duration == null ||
      this.max_participants == null
    ) {
      this.emptyError = true;
      this.timeOut(3000);
      return;
    }

    let response: any;

    try {
      response = await this.restService.updateAccount(
        "Update",
        this.account.accountId,
        this.name,
        this.address,
        this.logo,
        this.max_user,
        this.exp_date,
        this.creationDate,
        this.max_active_sessions,
        this.max_duration,
        this.max_participants,
        this.selectedAccessId.sort(),

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

  private timeOut(time: number) {
    setTimeout(() => {
      this.emptyError = false;
    }, time);
  }
}
