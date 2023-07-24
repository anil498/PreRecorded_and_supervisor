import { HttpClient } from "@angular/common/http";
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnInit,
  Renderer2,
} from "@angular/core";
import { FormArray, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-update-feature-dialog",
  templateUrl: "./update-feature-dialog.component.html",
  styleUrls: ["./update-feature-dialog.component.scss"],
})
export class UpdateFeatureDialogComponent implements OnInit {
  metaListNumber: number;
  metaTypes: string[];
  featureForm: FormGroup;
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<UpdateFeatureDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    private http: HttpClient,
    private changeDetectorRef: ChangeDetectorRef,
    private renderer: Renderer2,
    @Inject(MAT_DIALOG_DATA) public featureData: any
  ) {
    this.metaTypes = ["text", "bool", "radio", "file", "dropdown"];
    this.metaListNumber = 0;
  }

  async ngOnInit(): Promise<void> {
    this.featureForm = this.fb.group({
      featureId: [this.featureData.featureId, [Validators.required]],
      name: [this.featureData.name, [Validators.required]],
      metaList: this.fb.array([]),
      status: [this.featureData.status, [Validators.required]],
    });
    this.featureForm.get("featureId").disable();
    console.log(this.featureForm.get("featureId").value);
    console.log(this.featureForm.value.featureId);
    this.initMeta();
    console.log(this.featureForm.value.metaList);
  }

  initMeta() {
    // initialize our address
    const metaList = this.featureData.metaList;
    const control = this.featureForm.get("metaList") as FormArray;
    if (metaList && metaList.length > 0) {
      metaList.forEach((meta) => {
        control.push(
          this.fb.group({
            name: [meta.name, Validators.required],
            type: [meta.type, Validators.required],
            key: [meta.key, Validators.required],
          })
        );
      });
    } else {
      console.log("empty");
    }
  }
  removeMeta(i: number) {
    const control = <FormArray>this.featureForm.controls["metaList"];
    control.removeAt(i);
  }

  addMeta() {
    const metaGroup = this.fb.group({
      name: [, Validators.required],
      type: [, Validators.required],
      key: [, Validators.required],
    });

    const control = <FormArray>this.featureForm.controls["metaList"];
    control.push(metaGroup);
    console.log(this.featureForm.value.metaList);
    this.metaListNumber++;
    console.log(this.metaListNumber);
  }

  get metaList() {
    return this.featureForm.value.metaList as FormArray;
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
    if (this.featureForm.invalid) {
      this.focusOnInvalidFields();
      return;
    }
    const featureId = this.featureForm.get("featureId").value;
    const name = this.featureForm.value.name;
    const metaList = this.featureForm.value.metaList;
    const status = this.featureForm.value.status;
    metaList.forEach((meta) => {
      if (meta.type == "radio") {
        if (typeof meta.name !== "object") {
          console.log(typeof meta.name);
          console.log(meta.name);
          const nameArray = meta.name.split(",");
          console.log(nameArray);
          meta.name = nameArray;
        }
      }
    });
    console.log(featureId);
    console.log(name);
    console.log(metaList);
    let response: any;
    try {
      response = await this.restService.updateFeature(
        "Feature/Update",
        featureId,
        name,
        metaList,
        status
      );
      console.log(response);
      if (response.status_code == 200) {
        this.openSnackBar(response.msg, "snackBar");
        this.dialogRef.close();
        this.restService.closeDialog();
      }
    } catch {
      this.openSnackBar("Error while Updating Feature", "snackBar");
      console.log("feature not updated");
    }
  }
}
