import { HttpClient } from "@angular/common/http";
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnInit,
  Renderer2,
} from "@angular/core";
import { FormArray, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-create-feature-dialog",
  templateUrl: "./create-feature-dialog.component.html",
  styleUrls: ["./create-feature-dialog.component.scss"],
})
export class CreateFeatureDialogComponent implements OnInit {
  metaListNumber: number;
  metaTypes: string[];
  featureForm: FormGroup;
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<CreateFeatureDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    private http: HttpClient,
    private changeDetectorRef: ChangeDetectorRef,
    private renderer: Renderer2
  ) {
    this.metaTypes = ["text", "bool", "radio", "file"];
    this.metaListNumber = 0;
  }

  async ngOnInit(): Promise<void> {
    this.featureForm = this.fb.group({
      featureId: [null, [Validators.required]],
      name: ["", [Validators.required]],
      metaList: this.fb.array([]),
    });
  }

  generateArray(n: number): number[] {
    return Array(n);
  }

  removeMeta(i: number) {
    const control = <FormArray>this.featureForm.controls["metaList"];
    control.removeAt(i);
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

  addMeta() {
    const metaGroup = this.fb.group({
      name: ["", Validators.required],
      type: ["", Validators.required],
      key: ["", Validators.required],
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
  async submit() {
    if (this.featureForm.invalid) {
      this.focusOnInvalidFields();
      return;
    }
    const featureId = this.featureForm.value.featureId;
    const name = this.featureForm.value.name;
    const metaList = this.featureForm.value.metaList;
    metaList.forEach((meta) => {
      if (meta.type == "radio") {
        console.log(meta.name);
        const nameArray = meta.name.split(",");
        console.log(nameArray);
        meta.name = nameArray;
      }
    });
    console.log(featureId);
    console.log(name);
    console.log(metaList);
    let response: any;
    try {
      response = await this.restService.createFeature(
        "Feature/Create",
        featureId,
        name,
        metaList
      );
      console.log(response);
      console.log(response.msg);
      console.log(response.status_code);
      if (response.status_code == 200) {
        this.openSnackBar(response.msg, "snackBar");
        console.log("Feature Created");
        this.dialogRef.close();
        this.restService.closeDialog();
      }
      //console.log(response.status_code);
    } catch {
      this.openSnackBar("Error while Creating Feature", "snackBar");
      console.log("feature not created");
    }
  }
}
