import { HttpClient } from "@angular/common/http";
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnInit,
  Renderer2,
} from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-create-feature-dialog",
  templateUrl: "./create-feature-dialog.component.html",
  styleUrls: ["./create-feature-dialog.component.scss"],
})
export class CreateFeatureDialogComponent implements OnInit {
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
    this.metaTypes = ["text", "bool", "radio"];
  }

  async ngOnInit(): Promise<void> {
    this.featureForm = this.fb.group({
      featureId: [null, [Validators.required]],
      name: ["", [Validators.required]],
      metaList: [[], [Validators.required]],
    });
  }

  addMeta() {
    const rowDiv = this.renderer.createElement("div");
    this.renderer.addClass(rowDiv, "row");

    const colDiv1 = this.renderer.createElement("div");
    this.renderer.addClass(colDiv1, "col-4");
    const colDiv2 = this.renderer.createElement("div");
    this.renderer.addClass(colDiv2, "col-4");
    const colDiv3 = this.renderer.createElement("div");
    this.renderer.addClass(colDiv3, "col-3");
    const colDiv4 = this.renderer.createElement("div");
    this.renderer.addClass(colDiv4, "col-1");

    const matFormField1 = this.renderer.createElement("mat-form-field");

    this.renderer.appendChild(rowDiv, colDiv1);
    this.renderer.appendChild(rowDiv, colDiv2);
    this.renderer.appendChild(rowDiv, colDiv3);
    this.renderer.appendChild(rowDiv, colDiv4);

    const metaListContainer =
      this.elementRef.nativeElement.querySelectorAll("#metaList");
    this.renderer.appendChild(metaListContainer, rowDiv);
  }

  submit() {
    if (this.featureForm.invalid) {
      return;
    }

    try {
      const response = this.restService.createFeature(
        "Feature/create",
        this.featureForm.value.parentId,
        this.featureForm.value.featureId,
        this.featureForm.value.metaList
      );
      console.log(response);
    } catch {
      console.log("feature not created");
    }
  }
}
