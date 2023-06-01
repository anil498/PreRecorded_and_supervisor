import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";
import { ViewAccessDialogComponent } from "app/view-access-dialog/view-access-dialog.component";

@Component({
  selector: "app-view-feature-dialog",
  templateUrl: "./view-feature-dialog.component.html",
  styleUrls: ["./view-feature-dialog.component.scss"],
})
export class ViewFeatureDialogComponent implements OnInit {
  featureData: any;
  featureName: any[] = [];
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<ViewAccessDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public features: any
  ) {
    this.featureData = this.restService.getData().Features;
  }

  ngOnInit(): void {
    this.features.forEach((featureId) => {
      this.showFeatureName(featureId);
    });
  }

  showFeatureName(featureId: number) {
    this.featureData.forEach((access) => {
      if (featureId == access.accessId) this.featureName.push(access.name);
    });
  }
}
