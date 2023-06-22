import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { MatTabChangeEvent, MatTabGroup } from "@angular/material/tabs";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-view-user-dialog",
  templateUrl: "./view-user-dialog.component.html",
  styleUrls: ["./view-user-dialog.component.scss"],
})
export class ViewUserDialogComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;

  currentTabIndex: number = 0;
  userForm: FormGroup;

  emptyField: boolean = false;

  user_fname: string;
  user_lname: string;
  mobile: number;
  email: string;
  login_id: string;
  acc_exp_date: Date;
  exp_date: string;
  logo: any;
  photoUrl: string;
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
  topLevelAccess1: any[] = [];
  topLevelAccess2: any[] = [];
  selectedFeaturesMeta = {};
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<ViewUserDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public user: any
  ) {
    this.topLevelAccess = this.accessData.filter((item) => item.pId === 0);
    const featureHalf = Math.ceil(this.featuresData.length / 2);
    this.featuresData1 = this.featuresData.slice(0, featureHalf);
    this.featuresData2 = this.featuresData.slice(featureHalf);

    const accessHalf = Math.ceil(this.topLevelAccess.length / 2);
    this.topLevelAccess1 = this.topLevelAccess.slice(0, accessHalf);
    this.topLevelAccess2 = this.topLevelAccess.slice(accessHalf);
  }

  checkImg(): boolean {
    return this.photoControl;
  }

  updateSelectedFeaturesMeta(featureId: string | number, metaValue: any) {
    if (!this.selectedFeaturesMeta[featureId]) {
      this.selectedFeaturesMeta[featureId] = {};
    }
    this.selectedFeaturesMeta[featureId] = metaValue;
  }

  setMetaValue(
    featureId: string | number,
    metaKey: string | number,
    metaValue: any
  ) {
    this.selectedFeaturesMeta[featureId][metaKey] = metaValue;
  }
  showMetaList(feature: any, isChecked: boolean) {
    if (isChecked) {
      this.selectedFeatures.push(feature.featureId);
      feature.showMetaList = true;
      feature.selectedMetaList = Object.keys(feature.metaList).map((key) => {
        return { key: key, value: feature.metaList[key] };
      });
    } else {
      const index = this.selectedFeatures.indexOf(feature.featureId);
      this.selectedFeatures.splice(index, 1);
      feature.showMetaList = false;
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
    this.userForm = this.fb.group({
      user_fname: [this.user.fname, Validators.required],
      user_lname: [this.user.lname, Validators.required],
      mobile: [this.user.contact, Validators.required],
      email: [this.user.email, Validators.required],
      login_id: [this.user.loginId, Validators.required],
      acc_exp_date: [new Date(this.user.expDate), Validators.required],
      logo: [this.user.logo],
      password: ["", Validators.required],
      confirm_password: ["", Validators.required],
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
    });

    this.userForm.disable();

    this.selectedAccessId = this.userForm.value.accessList;
    this.selectedFeatures = this.userForm.value.featureList;
    this.selectedFeaturesMeta = this.userForm.value.featureMeta;
    this.logo = this.userForm.value.logo;
    if (this.logo !== null && Object.keys(this.logo).length !== 0) {
      this.photoControl = true;
      this.photoUrl = this.logo.byte;
    }
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
    console.log("Tab changed from Label" + event.index);
    this.currentTabIndex = event.index;
    this.tabGroup.selectedIndex = this.currentTabIndex;
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
}
