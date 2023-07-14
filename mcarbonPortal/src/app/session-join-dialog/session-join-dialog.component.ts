import { Component, ElementRef, Inject, OnInit } from "@angular/core";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-session-join-dialog",
  templateUrl: "./session-join-dialog.component.html",
  styleUrls: ["./session-join-dialog.component.scss"],
})
export class SessionJoinDialogComponent implements OnInit {
  sessionForm: FormGroup;
  parentAccess: any;
  types: any[];
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<SessionJoinDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    @Inject(MAT_DIALOG_DATA) public session: any
  ) {
    this.types = [
      { name: "SMS", value: "sms" },
      { name: "Whatsapp", value: "whatsapp" },
      { name: "Call on App", value: "notify" },
    ];
  }

  async ngOnInit(): Promise<void> {
    console.log(this.session);
    this.sessionForm = this.fb.group({
      msisdn: [
        "",
        [
          Validators.required,
          Validators.pattern(/^([6789]\d{9})(,[6789]\d{9})*$/),
        ],
      ],
      type: ["", [Validators.required]],
    });
  }

  openSnackBar(message: string, color: string) {
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = [color];
    this.snackBar.open(message, "Dismiss", snackBarConfig);
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

  async submit() {
    if (this.sessionForm.invalid) {
      this.focusOnInvalidFields();
      return;
    }
    console.log(this.sessionForm.value);
    let response: any;
    try {
      response = await this.restService.joinSession(
        "Session/sendLink",
        this.sessionForm.value.msisdn,
        this.sessionForm.value.type,
        this.session.sessionId
      );
      console.log(response);
      if (response.status_code == 200) {
        this.openSnackBar(response.msg, "snackBar");
        console.log("Successful");
        this.dialogRef.close();
        this.restService.closeDialog();
      }
    } catch (error) {
      this.openSnackBar(error.error.error, "snackBar");
      console.log(error);
      console.log("Unsuccessful");
    }
  }

  getErrorMessage(controlName: string): string {
    const control = this.sessionForm.get(controlName);

    if (control.hasError("required")) {
      return "This field is required";
    }

    if (controlName === "msisdn") {
      if (control?.hasError("pattern")) {
        return "Enter Valid mobile numbers";
      }
    }

    return "";
  }
}
