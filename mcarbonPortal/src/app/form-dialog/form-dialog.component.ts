import {
  Component,
  OnInit,
  ViewChild,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Inject,
  OnDestroy,
  ElementRef,
} from "@angular/core";

import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from "@angular/forms";
import { MatTabChangeEvent, MatTabGroup } from "@angular/material/tabs";
import { MatDialogRef } from "@angular/material/dialog";
import { RestService } from "app/services/rest.service";
import { Router } from "@angular/router";
import { DomSanitizer } from "@angular/platform-browser";
import { ImageFile } from "app/model/image-file";

@Component({
  selector: "app-form-dialog",
  templateUrl: "./form-dialog.component.html",
  styleUrls: ["./form-dialog.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FormDialogComponent implements OnInit {
  @ViewChild("tabGroup") tabGroup: MatTabGroup;
  title: string;
  samePassword = false;
  currentTabIndex: number = 0;
  userForm: FormGroup;
  messageResponse: any;
  loginResponse: any;

  emptyField: boolean = false;
  logo: any;
  photoUrl: any;
  photoControl: boolean = false;
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

  featuresData: any = this.restService.getData().Features;
  featuresData1: any;
  featuresData2: any;
  accessData: any = this.restService.getData().Access;
  selectedAccessId: number[] = [];
  selectedFeatures: number[] = [];
  topLevelAccess1: any[] = [];
  topLevelAccess2: any[] = [];
  topLevelAccess: any[] = [];

  videoSelect: any = {};

  selectedFeaturesMeta = {};
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<FormDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef
  ) {
    this.topLevelAccess = this.accessData.filter((item) => item.pId === 0);
    const featureHalf = Math.ceil(this.featuresData.length / 2);
    this.featuresData1 = this.featuresData.slice(0, featureHalf);
    this.featuresData2 = this.featuresData.slice(featureHalf);

    const accessHalf = Math.ceil(this.topLevelAccess.length / 2);
    this.topLevelAccess1 = this.topLevelAccess.slice(0, accessHalf);
    this.topLevelAccess2 = this.topLevelAccess.slice(accessHalf);
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
      reader.readAsArrayBuffer(file);
      reader.onloadend = async () => {
        const arrBuffer: any = await file.arrayBuffer();
        const buffer = new Uint8Array(arrBuffer);
        var array = Array.from(buffer);
        this.photoUrl = reader.result;
        this.photoControl = true;

        this.logo = { byte: array, type: file.type };
        console.log(this.logo);
        console.log(this.photoControl);
        console.log(this.photoUrl);
      };
    }
  }
  // onFileInputChange(event: any, meta: any, featureId: number): void {
  //   const files: FileList = event.target.files;
  //   if (files && files.length > 0) {
  //     const file: File = files[0];
  //     const reader = new FileReader();
  //     //reader.readAsArrayBuffer(file);
  //     reader.readAsDataURL(file);
  //     console.log(file);
  //     this.videoSelect[featureId] = file.name;

  //     reader.onloadend = () => {
  //       const blob = { byte: reader.result, type: file.type, name: file.name };
  //       this.selectedFeaturesMeta[featureId.toString()][meta.key] = blob;
  //           //    const videoEl = document.createElement("video");
  //             //    videoEl.src = <string>reader.result;
  //           //      videoEl.controls = true;
  //           //    document.body.appendChild(videoEl);
  //       console.warn(featureId);
  //       console.warn(this.selectedFeaturesMeta[featureId]);
  //     };
  //   }
  // }

  onFileInputChange(event: any, meta: any, featureId: number): void {
    const files: FileList = event.target.files;
    if (files && files.length > 0) {
      const file: File = files[0];
      const reader = new FileReader();

      reader.readAsBinaryString(file);
      reader.onload = () => {
        const binaryData = reader.result;
        const blob = new Blob([binaryData], { type: file.type });

        const formData = new FormData();
        formData.append("file", blob, file.name);
        console.log(blob);
        console.warn(formData);
        this.selectedFeaturesMeta[featureId.toString()][meta.key] = formData;
        console.warn(featureId);
        console.warn(this.selectedFeaturesMeta[featureId]);
      };
    }
  }

  videoSelected(featureId: number) {
    console.log(featureId);
    console.log(this.videoSelect);
    return this.videoSelect.hasOwnProperty(featureId);
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
    this.userForm = this.fb.group({
      user_fname: [
        "",
        [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(20),
          Validators.pattern("^[a-zA-Z]+$"),
        ],
      ],
      user_lname: [
        "",
        [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(20),
          Validators.pattern("^[a-zA-Z]+$"),
        ],
      ],
      mobile: [
        "",
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(10),
          Validators.pattern("^[0-9]+$"),
        ],
      ],
      email: ["", [Validators.required, Validators.email]],
      login_id: [
        "",
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(20),
        ],
      ],
      acc_exp_date: [new Date(), [Validators.required, this.dateValidator]],

      password: [
        "",
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(20),
          Validators.pattern(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
          ),
        ],
      ],
      confirm_password: ["", [Validators.required]],
      accessList: ["", Validators.required],

      max_duration: ["", [Validators.required, Validators.min(0)]],
      max_active_sessions: ["", [Validators.required, Validators.min(0)]],
      max_participants: ["", [Validators.required, Validators.min(1)]],

      featureList: [[]],
    });
    this.accessData.forEach((access) => {
      console.log(access);
      if (access.systemName == "user_creation") {
        this.title = access.name;
      }
    });
    this.topLevelAccess = this.accessData.filter((item) => item.pId === 0);
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
        "Create",

        this.user_fname,
        this.user_lname,
        this.mobile,
        this.email,
        this.login_id,
        this.exp_date,
        this.logo,

        this.password,
        this.selectedAccessId.sort(),

        this.max_active_sessions,
        this.max_duration,
        this.max_participants,

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

  focusOnInvalidFields() {
    const invalidFields =
      this.elementRef.nativeElement.querySelectorAll(".ng-invalid");

    if (invalidFields.length > 0) {
      invalidFields[0].focus();
    }
  }

  getErrorMessage(controlName: string): string {
    const control = this.userForm.get(controlName);
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

    if (controlName === "password") {
      if (control?.hasError("minlength")) {
        return "Password should be at least 8 characters";
      }

      if (control?.hasError("maxlength")) {
        return "Password should not exceed 20 characters";
      }

      if (control?.hasError("pattern")) {
        return "Password must contain at least one capital letter, one small letter, one digit, and one special character";
      }
    }

    if (control.hasError("minlength")) {
      return "Name should be at least 4 characters long";
    }
    if (control.hasError("maxlength")) {
      return "Name should not exceed 20 characters";
    }
    if (control.hasError("min")) {
      return "Max User should be 0 or greater";
    }
    if (control.hasError("mismatch")) {
      return "Account Expiry Date should not be in the past";
    }
    return "";
  }
}
