import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatTabGroup } from "@angular/material/tabs";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-view-account-dialog",
  templateUrl: "./view-account-dialog.component.html",
  styleUrls: ["./view-account-dialog.component.scss"],
})
export class ViewAccountDialogComponent implements OnInit {
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
  photoUrl: string;
  photoControl: boolean = false;
  name: string;
  address: string;
  acc_exp_date: Date;
  exp_date: string;
  max_user: number;
  logo: any;
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
    private dialogRef: MatDialogRef<ViewAccountDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public account: any
  ) {
    this.topLevelAccess = this.accessData.filter((item) => item.pId === 0);
    const featureHalf = Math.ceil(this.featuresData.length / 2);
    this.featuresData1 = this.featuresData.slice(0, featureHalf);
    this.featuresData2 = this.featuresData.slice(featureHalf);

    const accessHalf = Math.ceil(this.topLevelAccess.length / 2);
    this.topLevelAccess1 = this.topLevelAccess.slice(0, accessHalf);
    this.topLevelAccess2 = this.topLevelAccess.slice(accessHalf);
  }

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

  async ngOnDestroy(): Promise<void> {
    this.accessData.forEach((access) => {
      access.status = 1;
    });
    this.featuresData.forEach((feature) => {
      feature.status = 1;
    });
  }

  checkImg(): boolean {
    return this.photoControl;
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

    this.userForm.disable();
    this.logo = this.userForm.value.logo;

    if (
      !this.userForm.value.accessList ||
      this.userForm.value.accessList.length == 0
    ) {
      this.userForm.value.accessList = [];
    }
    if (
      !this.userForm.value.featureList ||
      this.userForm.value.featureList.length == 0
    ) {
      this.userForm.value.featureList = [];
    }
    if (!this.userForm.value.featureMeta) {
      this.userForm.value.featureMeta = {};
    }

    if (this.logo !== null && Object.keys(this.logo).length !== 0) {
      this.photoUrl = this.logo.byte;
      this.photoControl = true;
    }

    this.selectedAccessId = this.userForm.value.accessList;
    this.selectedFeatures = this.userForm.value.featureList;
    this.selectedFeaturesMeta = this.userForm.value.featureMeta;
    this.topLevelAccess = this.accessData.filter((item) => item.pId === 0);
    // For diplaying previous checked Access
    if (this.accessData) {
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
    }
    // for displaying previous checked features
    if (this.featuresData) {
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
        if (flag === true) this.featuresData[i].status = 0;
      }
    }
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
}
