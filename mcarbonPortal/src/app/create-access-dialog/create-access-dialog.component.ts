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
import { AccessObject } from "app/model/access_object";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-create-access-dialog",
  templateUrl: "./create-access-dialog.component.html",
  styleUrls: ["./create-access-dialog.component.scss"],
})
export class CreateAccessDialogComponent implements OnInit {
  accessForm: FormGroup;
  parentAccess: any;
  accessIds: number[] = [];
  systemName: string[] = [];
  accessObject: AccessObject;
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<CreateAccessDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    private http: HttpClient,
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public accesses: any
  ) {
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
      parentId: [null, [Validators.required]],
      name: ["", [Validators.required]],
      accessId: [null, [Validators.required, this.checkAccessId]],
      seq: [null, [Validators.required]],
      systemName: ["", [Validators.required, this.checkSystemName]],
    });

    console.log(this.accesses);
    console.log(this.accessIds);
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

    this.accessObject = {
      path: "/app/" + this.accessForm.value.systemName,
      title: this.accessForm.value.name,
      icon: "",
      class: this.accessForm.value.systemName,
      show: false,
      systemName: this.accessForm.value.systemName,
    };
    console.log(this.accessObject);
    //this.addAccess(this.accessObject);
    let response: any;

    try {
      response = await this.restService.createAccess(
        "Access/Create",
        this.accessForm.value.parentId,
        this.accessForm.value.accessId,
        this.accessForm.value.seq,
        this.accessForm.value.name,
        this.accessForm.value.systemName
      );
      console.log(response);
      if (response.status_code == 200) {
        this.openSnackBar(response.msg, "snackBar");
        console.log("Access Created");
        this.dialogRef.close();
        this.restService.closeDialog();
      }
    } catch (error) {
      this.openSnackBar(error, "snackBar");
      console.log(error);
      console.log("access not created");
    }
  }

  // addAccess(accessObject: AccessObject): void {
  //   const url = "./assets/json/access.json";
  //   this.http.get(url).subscribe(
  //     (data: AccessObject[]) => {
  //       data.push(accessObject);
  //       console.log(
  //         "🚀 ~ file: create-access-dialog.component.ts:145 ~ CreateAccessDialogComponent ~ addAccess ~ ̥:",
  //         data
  //       );

  //       this.http.put(url, data).subscribe(
  //         () => {
  //           console.log("Access object saved successfully.");
  //         },
  //         (error) => {
  //           console.error("Error saving access object:", error);
  //         }
  //       );
  //     },
  //     (error) => {
  //       console.error("Error retrieving access.json:", error);
  //     }
  //   );
  //}
}
