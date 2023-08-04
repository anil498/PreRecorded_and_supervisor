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

import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from "@angular/forms";
import { MatTabGroup } from "@angular/material/tabs";
import { MatDialogRef } from "@angular/material/dialog";
import { RestService } from "app/services/rest.service";
import { Router } from "@angular/router";
import { DomSanitizer } from "@angular/platform-browser";
import { ImageFile } from "app/model/image-file";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { HttpClient } from "@angular/common/http";
import { DateAdapter } from "@angular/material/core";

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
  userForm1: FormGroup;
  userForm2: FormGroup;
  userForm3: FormGroup;
  userForm4: FormGroup;
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
  logo: any = null;
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
  croppedImage: any = null;
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

  selectedFeaturesMeta: { [key: string]: any } = {};
  videoSelect: any = {};
  formData: FormData = null;
  hide = true;
  hide2 = true;
  constructor(
    private dateAdapter: DateAdapter<Date>,
    private router: Router,
    private dialogRef: MatDialogRef<CreateAccountComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    private http: HttpClient,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.dateAdapter.setLocale("en-IN");
    this.loginResponse = this.restService.getToken();
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
    console.log("image selected");
    const files: FileList = event.target.files;
    if (files && files.length > 0) {
      const file: File = files[0];
      const image: ImageFile = {
        file: file,
        url: this.sanitizer.bypassSecurityTrustUrl(
          window.URL.createObjectURL(file)
        ),
      };
      console.log(image.url);
      const reader = new FileReader();
      console.log(file);
      reader.readAsDataURL(file);
      reader.onloadend = () => {
        this.photoUrl = image.url;
        this.photoControl = true;
        const type = file.type.split("/")[1];
        const byte = reader.result as string;
        this.logo = { byte: byte, name: file.name };
        console.log(reader.result);
        console.log(this.logo);
        console.log(this.photoControl);
        console.log(this.photoUrl);
        this.changeDetectorRef.detectChanges();
      };
    }
    event.target.value = "";
  }

  onPhotoDeselected() {
    console.log("image removed");
    this.photoUrl = null;
    this.photoControl = false;
    this.logo = null;
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
    console.log(featureId + metaKey + metaValue);
    this.selectedFeaturesMeta[featureId.toString()][metaKey] = metaValue;
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
    this.userForm1 = this.fb.group({
      name: [
        "",
        [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(20),
        ],
      ],
      address: ["", Validators.required],
      acc_exp_date: [new Date(), [Validators.required, this.dateValidator]],
      max_user: [0, [Validators.required, Validators.min(0)]],
      logo: [null],
    });

    this.userForm2 = this.fb.group({
      accessList: [""],

      max_duration: ["", [Validators.required, Validators.min(0)]],
      max_active_sessions: ["", [Validators.required, Validators.min(0)]],
      max_participants: ["", [Validators.required, Validators.min(1)]],
    });

    this.userForm3 = this.fb.group({
      featureList: [[]],
    });

    this.userForm4 = this.fb.group(
      {
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
          null,
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
      },
      { validators: this.passwordMatchValidator }
    );

    this.accessData.forEach((access) => {
      if (access.systemName == "customer_creation") {
        this.title = access.name;
      }
    });
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const confirmPassword = control.get("confirm_password")?.value;
    const password = control.get("password")?.value;
    console.log(password, confirmPassword);
    if (password !== confirmPassword) {
      console.log(" not same");
      control.get("confirm_password")?.setErrors({ passMismatch: true });
      return { passMismatch: true };
    }
    return null;
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
    if (this.tabGroup.selectedIndex === this.tabGroup._tabs.length - 1)
      return true;
    else return false;
  }

  async submit() {
    console.log(this.userForm1.invalid);
    console.log(this.userForm2.invalid);
    console.log(this.userForm3.invalid);
    console.log(this.userForm4.invalid);
    if (
      this.userForm4.invalid ||
      this.userForm1.invalid ||
      this.userForm2.invalid ||
      this.userForm3.invalid
    ) {
      console.warn("Selected Feature Meta: ");
      console.log(this.selectedFeaturesMeta);
      this.focusOnInvalidFields();
      return false;
    }

    if (this.selectedFeaturesMeta.hasOwnProperty("4")) {
      if (this.formData == null) {
        this.openSnackBar("Please Select a video file", "snackbar");
        return;
      }
    }

    this.emptyError = false;

    this.name = this.userForm1.value.name;
    this.address = this.userForm1.value.address;
    this.max_user = this.userForm1.value.max_user;
    this.acc_exp_date = this.userForm1.value.acc_exp_date;
    this.acc_exp_date.setHours(23, 59, 59);
    this.exp_date = this.acc_exp_date.toISOString().split("T")[0];
    this.exp_date = this.exp_date + " 23:59:59";

    this.max_duration = this.userForm2.value.max_duration;
    this.max_participants = this.userForm2.value.max_participants;
    this.max_active_sessions = this.userForm2.value.max_active_sessions;

    this.user_fname = this.userForm4.value.user_fname;
    this.user_lname = this.userForm4.value.user_lname;
    this.mobile = this.userForm4.value.mobile;
    this.email = this.userForm4.value.email;
    this.login_id = this.userForm4.value.login_id;

    this.password = this.userForm4.value.password;

    console.log("From Date selection: " + this.userForm1.value.acc_exp_date);
    console.log("function toISOString(): " + this.acc_exp_date.toISOString());
    console.log(this.exp_date);
    if (this.photoControl) {
      this.logo.byte = this.logo.byte.split(",")[1];
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
      if (response.status_code == 200) {
        this.openSnackBar(response.msg, "snackBar");
        console.log("Account Created");
        this.dialogRef.close();
        this.restService.closeDialog();
      }
      console.log("uploading file");
      console.log(this.formData);
      if (this.formData !== null) {
        let videoResponse;
        try {
          videoResponse = await this.restService.uploadVideo(
            this.name,
            this.login_id,
            this.formData
          );
          console.log(videoResponse);
          if (videoResponse.status_code == 200) {
            this.openSnackBar(videoResponse.msg, "snackBar");
            console.log("Video Saved");
            this.dialogRef.close();
            this.restService.closeDialog();
          }
        } catch (error) {
          this.openSnackBar(error.error.error, "snackBar");
        }
      }
    } catch (error) {
      console.warn(error);
      if (error.status == 401) {
        this.openSnackBar(error.error, "snackBar");
        this.timeOut(3000);
      } else {
        this.openSnackBar(error.error.error, "snackBar");
        this.timeOut(3000);
      }
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
      this.emptyError = false;
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

  getErrorMessage2(controlName: string): string {
    const control = this.userForm2.get(controlName);
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

  getErrorMessage3(controlName: string): string {
    const control = this.userForm1.get(controlName);
    if (control.hasError("required")) {
      return "This field is required";
    }

    return "";
  }

  getErrorMessage4(controlName: string): string {
    const control = this.userForm4.get(controlName);
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
    if (controlName === "confirm_password") {
      if (control?.hasError("passMismatch")) {
        return "Password should be same";
      }
    }

    return "";
  }
}
