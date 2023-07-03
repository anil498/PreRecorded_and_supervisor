import { HttpClient } from "@angular/common/http";
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnInit,
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
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";
@Component({
  selector: "app-update-access-dialog",
  templateUrl: "./update-access-dialog.component.html",
  styleUrls: ["./update-access-dialog.component.scss"],
})
export class UpdateAccessDialogComponent implements OnInit {
  accessForm: FormGroup;
  parentAccess: any;
  accesses: any[];
  access: any;
  accessIds: number[] = [];
  systemName: string[] = [];

  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<UpdateAccessDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    private http: HttpClient,
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public accessData: any
  ) {
    this.accesses = accessData.accesses;
    this.access = accessData.access;
    this.parentAccess = this.accesses.filter((item) => item.pId === 0);
    this.parentAccess.sort((a, b) => a.accessId - b.accessId);
    this.accesses.forEach((access) => {
      this.accessIds.push(access.accessId);
    });
    this.accesses.forEach((access) => {
      this.systemName.push(access.systemName);
    });
  }

  async ngOnInit(): Promise<void> {
    this.accessForm = this.fb.group({
      parentId: [this.access.pId, [Validators.required]],
      name: [this.access.name, [Validators.required]],
      accessId: [this.access.accessId, [Validators.required]],
      seq: [this.access.seq, [Validators.required]],
      systemName: [
        this.access.systemName,
        [Validators.required],
      ],
      status: [this.access.status, [Validators.required]],
    });
    console.log(this.access);
  }

  checkAccessId = (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (this.accessIds.length > 0 && this.accessIds.includes(value)) {
      console.log("ID already axist");
      return { existingAccessId: true };
    }
    return null;
  };
  checkSystemName = (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (this.systemName.length > 0 && this.systemName.includes(value)) {
      console.log("name already axist");
      return { existingSystemName: true };
    }
    return null;
  };

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
    if (this.accessForm.invalid) {
      this.focusOnInvalidFields();
      return;
    }
    let response: any;
    try {
      response = await this.restService.updateAccess(
        "Access/Update",
        this.accessForm.value.parentId,
        this.accessForm.value.accessId,
        this.accessForm.value.seq,
        this.accessForm.value.name,
        this.accessForm.value.systemName,
        this.accessForm.value.status
      );
      console.log(response);
      if (response.status_code == 200) {
        this.openSnackBar(response.msg, "snackBar");
        console.log("Access Updated");
        this.dialogRef.close();
        this.restService.closeDialog();
      }
    } catch (error) {
      this.openSnackBar(error.error.error, "snackBar");
      console.log(error);
      console.log("access not created");
    }
  }
}
