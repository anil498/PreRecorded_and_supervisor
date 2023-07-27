import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from "@angular/core";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { MatTabChangeEvent, MatTabGroup } from "@angular/material/tabs";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { ImageFile } from "app/model/image-file";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-update-user-dialog",
  templateUrl: "./update-user-dialog.component.html",
  styleUrls: ["./update-user-dialog.component.scss"],
})
export class UpdateUserDialogComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;
  title: string;
  currentTabIndex: number = 0;
  userForm1: FormGroup;
  userForm2: FormGroup;
  userForm3: FormGroup;
  userForm4: FormGroup;
  loginResponse: any;

  emptyField: boolean = false;

  user_fname: string;
  user_lname: string;
  mobile: number;
  email: string;
  login_id: string;
  acc_exp_date: Date;
  exp_date: string;
  logo: any = null;
  photoUrl: any;
  photoControl: boolean = false;
  max_duration: number;
  max_active_sessions: number;
  max_participants: number;

  featuresData: any = this.restService.getData().Features;
  featuresData1: any;
  featuresData2: any;
  accessData: any = this.restService.getData().Access;
  selectedAccessId: number[] = [];
  topLevelAccess: any[] = [];
  selectedFeatures: number[] = [];
  topLevelAccess1: any[] = [];
  topLevelAccess2: any[] = [];
  videoSelect: any = {};
  formData: FormData = null;
  icdcId: number = 0;
  forms: any;
  selectedFeaturesMeta = {};
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<UpdateUserDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    private changeDetectorRef: ChangeDetectorRef,
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
        console.log(reader.result);
        this.photoUrl = reader.result;
        this.photoControl = true;
        const type = file.type.split("/")[1];
        const byte = (reader.result as string).split(",")[1];
        this.logo = { byte: byte, type: type, name: file.name };
        //this.logo = { byte: reader.result, type: file.type };
        console.log(this.logo);
        console.log(this.photoControl);
        console.log(this.photoUrl);
        this.changeDetectorRef.detectChanges();
      };
    }
    event.target.value = "";
  }

  onPhotoDeselected() {
    this.photoUrl = {};
    this.photoControl = false;
    this.logo = {};
  }

  onFileInputChange(event: any, meta: any, featureId: number): void {
    const file: File = event.target.files[0];
    console.log(file);
    this.videoSelect[featureId] = file.name;
    this.formData = new FormData();
    this.formData.append("prerecorded_video_file", file, file.name);
    console.log(this.formData);
    this.selectedFeaturesMeta[featureId.toString()][meta.key] = this.formData;
    console.warn(featureId);
    console.warn(this.selectedFeaturesMeta[featureId]);
    // const file: File = event.target.files[0];
    // //const file: File = files[0];
    // const reader = new FileReader();
    // reader.readAsBinaryString(file);
    // console.log(file);
    // this.videoSelect[featureId] = file.name;

    // reader.onload = () => {
    //   const binaryData = reader.result;
    //   console.log(typeof binaryData);
    //   console.log(binaryData);
    //   const blob = new Blob([binaryData], { type: file.type });
    //   console.log(blob);
    //   this.formData = new FormData();
    //   this.formData.append("prerecorded_video_file", blob, file.name);
    //   console.log(this.formData);
    //   console.log(this.formData.get("prerecorded_video_file"));
    //   this.selectedFeaturesMeta[featureId.toString()][meta.key] = this.formData;
    //   console.warn(featureId);
    //   console.warn(this.selectedFeaturesMeta[featureId]);
    // };
  }

  videoSelected(featureId: number) {
    return this.videoSelect.hasOwnProperty(featureId);
  }

  toggleFeatureSelection(feature: any): void {
    console.log(feature);
    const index = this.selectedFeatures.indexOf(feature.featureId);
    if (index > -1) {
      this.selectedFeatures.splice(index, 1);
      delete this.selectedFeaturesMeta[feature.featureId.toString()];
    } else {
      this.selectedFeatures.push(feature.featureId);
      this.selectedFeaturesMeta[feature.featureId.toString()] = {};
      feature.metaList.forEach((meta) => {
        if (meta.type == "bool")
          this.setMetaValue(feature.featureId, meta.key, false);
        else if (meta.type == "text")
          this.setMetaValue(feature.featureId, meta.key, "");
        else if (meta.type == "radio")
          this.setMetaValue(feature.featureId, meta.key, meta.name[0]);
        //this.selectedFeaturesMeta[feature.featureId.toString()] = {};
      });
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
    // if (this.user.featuresMeta.hasOwnProperty("16")) {
    //   await this.restService
    //     .getIcdcData("Icdc/GetNames", this.user.userId, this.user.accountId)
    //     .then((response) => {
    //       this.forms = response;
    //     });
    //   console.log(this.forms);
    // }
    this.userForm1 = this.fb.group({
      user_fname: [
        this.user.fname,
        [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(20),
          Validators.pattern("^[a-zA-Z]+$"),
        ],
      ],
      user_lname: [
        this.user.lname,
        [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(20),
          Validators.pattern("^[a-zA-Z]+$"),
        ],
      ],
      mobile: [
        this.user.contact,
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(10),
          Validators.pattern("^[0-9]+$"),
        ],
      ],
      email: [this.user.email, [Validators.required, Validators.email]],
      login_id: [this.user.loginId],
      logo: [this.user.logo],
      acc_exp_date: [
        new Date(this.user.expDate),
        [Validators.required, this.dateValidator],
      ],
    });

    this.userForm2 = this.fb.group({
      accessList: [this.user.accessId, Validators.required],
    });
    this.userForm3 = this.fb.group({
      max_duration: [
        this.user.session.max_duration,
        [Validators.required, Validators.min(0)],
      ],
      max_active_sessions: [
        this.user.session.max_active_sessions,
        [Validators.required, Validators.min(0)],
      ],
      max_participants: [
        this.user.session.max_participants,
        [Validators.required, Validators.min(1)],
      ],
    });
    this.userForm4 = this.fb.group({
      featureList: [this.user.features],
      featureMeta: [this.user.featuresMeta],
    });

    this.accessData.forEach((access) => {
      console.log(access);
      if (access.systemName == "user_update") {
        this.title = access.name;
      }
    });
    if (
      !this.userForm2.value.accessList ||
      this.userForm2.value.accessList.length == 0
    ) {
      this.userForm2.value.accessList = [];
    }
    if (
      !this.userForm4.value.featureList ||
      this.userForm4.value.featureList.length == 0
    ) {
      this.userForm4.value.featureList = [];
    }
    if (!this.userForm4.value.featureMeta) {
      this.userForm4.value.featureMeta = {};
    }
    this.userForm1.controls["login_id"].disable();
    this.selectedAccessId = this.userForm2.value.accessList;
    this.selectedFeatures = this.userForm4.value.featureList;
    this.selectedFeaturesMeta = this.userForm4.value.featureMeta;
    this.logo = this.userForm1.value.logo;
    if (this.logo !== null && Object.keys(this.logo).length !== 0) {
      this.photoControl = true;
      this.photoUrl = this.logo.byte;
    }
    // For diplaying previous checked Access

    for (let i = 0; i < this.accessData.length; i++) {
      var flag = true;
      for (let j = 0; j < this.userForm2.value.accessList.length; j++) {
        if (
          this.userForm2.value.accessList[j] === this.accessData[i].accessId
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
      for (let j = 0; j < this.userForm4.value.featureList.length; j++) {
        if (
          this.userForm4.value.featureList[j] === this.featuresData[i].featureId
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

  dateValidator(control: AbstractControl): ValidationErrors | null {
    const selectedDate = new Date(control.value);
    const currentDate = new Date();
    if (
      selectedDate < currentDate &&
      selectedDate.toDateString() !== currentDate.toDateString()
    ) {
      return { mismatch: true };
    }
    return null;
  }

  previous() {
    if (this.currentTabIndex > 0) {
      this.currentTabIndex--;
      this.tabGroup.selectedIndex = this.currentTabIndex;
    }
  }

  next() {
    console.warn("Current Tab " + this.tabGroup.selectedIndex);
    if (this.tabGroup.selectedIndex < this.tabGroup._tabs.length - 1) {
      if (this.tabGroup.selectedIndex === 0) {
        if (this.userForm1.invalid) {
          console.log(this.userForm1.invalid);
          this.focusOnInvalidFields();
          console.log(this.userForm1.value.acc_exp_date);
          return;
        }
      } else if (this.tabGroup.selectedIndex === 1) {
        if (this.userForm2.invalid) {
          console.log(this.userForm2.invalid);
          this.focusOnInvalidFields();
          return;
        }
      } else if (this.tabGroup.selectedIndex === 2) {
        if (this.userForm3.invalid) {
          console.log(this.userForm3.invalid);
          this.focusOnInvalidFields();
          return;
        }
      }

      //this.currentTabIndex++;
      this.tabGroup.selectedIndex = this.tabGroup.selectedIndex + 1;
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

  async submit() {
    if (this.userForm4.invalid) {
      console.log(this.userForm4.invalid);
      this.focusOnInvalidFields();
      return;
    }

    if (this.selectedFeaturesMeta.hasOwnProperty("4")) {
      if (this.formData == null) {
        this.openSnackBar("Please Select a video file", "snackbar");
        return;
      }
    }
    this.emptyField = false;
    this.user_fname = this.userForm1.value.user_fname;
    this.user_lname = this.userForm1.value.user_lname;
    this.mobile = this.userForm1.value.mobile;
    this.email = this.userForm1.value.email;
    this.login_id = this.user.loginId;
    this.acc_exp_date = this.userForm1.value.acc_exp_date;
    this.acc_exp_date.setHours(23, 59, 59);
    this.exp_date = this.acc_exp_date.toISOString().split("T")[0];
    this.exp_date = this.exp_date + " 23:59:59";

    this.max_duration = this.userForm3.value.max_duration;
    this.max_participants = this.userForm3.value.max_participants;
    this.max_active_sessions = this.userForm3.value.max_active_sessions;

    console.log(this.userForm1.value.acc_exp_date);
    console.log(this.acc_exp_date);
    console.log(this.exp_date);
    let response: any;
    // if (
    //   this.selectedFeaturesMeta.hasOwnProperty("16") &&
    //   this.selectedFeaturesMeta["16"]["id_icdc"]
    // ) {
    //   this.icdcId = this.selectedFeaturesMeta["16"]["id_icdc"];
    // }
    // console.log(this.icdcId);
    try {
      response = await this.restService.updateUser(
        "Update",
        this.user.accountId,
        this.user.userId,
        this.user_fname,
        this.user_lname,
        this.mobile,
        this.email,
        this.login_id,
        this.logo,
        this.exp_date,

        this.selectedAccessId.sort(),
        this.max_active_sessions,
        this.max_duration,
        this.max_participants,

        this.selectedFeatures.sort(),
        this.selectedFeaturesMeta,
      );
      console.warn(response);
      console.log(this.login_id);
      if (response.status_code == 200) {
        if (this.formData !== null) {
          let videoResponse = await this.restService.uploadVideo(
            "",
            this.login_id,
            this.formData
          );
          console.log(videoResponse);
        }
        this.openSnackBar(response.msg, "snackBar");
        this.timeOut(3000);
        this.dialogRef.close();
        this.restService.closeDialog();
      }
    } catch (error) {
      console.warn(error);
      this.openSnackBar(error.error, "snackBar");
      this.timeOut(3000);
    }
  }

  openSnackBar(message: string, color: string) {
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = [color];
    this.snackBar.open(message, "Dismiss", snackBarConfig);
  }

  private timeOut(time: number) {
    setTimeout(() => {
      this.emptyField = false;
    }, time);
  }

  focusOnInvalidFields() {
    console.log("focus");
    const invalidFields =
      this.elementRef.nativeElement.querySelectorAll(".ng-invalid");
    console.log(invalidFields);
    if (invalidFields.length > 0) {
      invalidFields.forEach((invalid) => {
        invalid.focus();
      });
    }
  }

  getErrorMessage1(controlName: string): string {
    const control = this.userForm1.get(controlName);
    if (control.hasError("required")) {
      return "This field is required";
    }

    if (controlName === "user_fname") {
      if (control?.hasError("minlength")) {
        return "First Name should be at least 4 characters";
      }

      if (control?.hasError("maxlength")) {
        return "First Name should not exceed 20 characters";
      }

      if (control?.hasError("pattern")) {
        return "First Name should only contain letters";
      }
    }

    if (controlName === "user_lname") {
      if (control?.hasError("minlength")) {
        return "Last Name should be at least 4 characters";
      }

      if (control?.hasError("maxlength")) {
        return "Last Name should not exceed 20 characters";
      }

      if (control?.hasError("pattern")) {
        return "Last Name should only contain letters";
      }
    }

    if (controlName === "mobile") {
      if (control?.hasError("minlength") || control?.hasError("maxlength")) {
        return "Mobile number should be of 10-digits";
      }
      if (control?.hasError("pattern")) {
        return "Mobile number should only contain digits";
      }
    }

    if (controlName === "email") {
      if (control?.hasError("email")) {
        return "Invalid email format";
      }
    }

    if (controlName === "login_id") {
      if (control?.hasError("minlength")) {
        return "Login ID should be at least 6 characters";
      }

      if (control?.hasError("maxlength")) {
        return "Login ID should not exceed 20 characters";
      }
    }
    if (control.hasError("mismatch")) {
      return "Account Expiry Date should not be in the past";
    }
    return "";
  }

  getErrorMessage2(controlName: string): string {
    const control = this.userForm2.get(controlName);
    if (control.hasError("required")) {
      return "This field is required";
    }
    return "";
  }

  getErrorMessage3(controlName: string): string {
    const control = this.userForm3.get(controlName);
    if (control.hasError("required")) {
      return "This field is required";
    }

    if (controlName === "max_active_sessions") {
      if (control?.hasError("min")) {
        return "Max Active Sessions should be greater than 0";
      }
    }
    if (controlName === "max_duration") {
      if (control?.hasError("min")) {
        return "Max Duration should be greater than 0";
      }
    }
    if (controlName === "max_participants") {
      if (control?.hasError("min")) {
        return "Max Participants should be greater than 0";
      }
    }

    return "";
  }

  getErrorMessage4(controlName: string): string {
    const control = this.userForm4.get(controlName);
    return "";
  }
}
