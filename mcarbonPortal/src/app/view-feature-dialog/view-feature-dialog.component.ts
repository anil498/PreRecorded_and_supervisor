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
  featureData1: any[];
  featureData2: any[];
  featureName: any[] = [];
  selectedFeaturesMeta = {};
  featureIds: number[];
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<ViewAccessDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public features: any
  ) {
    this.featureIds = features.featureId;
    this.selectedFeaturesMeta = features.featuresMeta;
    this.featureData = this.restService.getData().Features;
  }

  async ngOnInit(): Promise<void> {
    console.log(this.featureIds);
    console.log(this.selectedFeaturesMeta);
    this.featureIds.forEach((featureId) => {
      this.showFeatureName(featureId);
    });

    // for displaying previous checked features
    if (this.featureData) {
      for (let i = 0; i < this.featureData.length; i++) {
        var flag = true;
        for (let j = 0; j < this.featureIds.length; j++) {
          if (this.featureIds[j] === this.featureData[i].featureId) {
            console.log(
              this.featureIds[j] + " " + this.featureData[i].featureId
            );
            flag = false;
            break;
          }
        }
        if (flag === true) this.featureData[i].status = 0;
      }
    }
    const featureHalf = Math.ceil(this.featureData.length / 2);
    this.featureData1 = this.featureData.slice(0, featureHalf);
    this.featureData2 = this.featureData.slice(featureHalf);
    console.log(this.featureData1);
    console.log(this.featureData2);
  }

  isCheckFeature(feature: any) {
    if (feature.status == 1) return true;
    return false;
  }

  showFeatureName(featureId: number) {
    this.featureData.forEach((feature) => {
      if (featureId == feature.featureId) this.featureName.push(feature.name);
    });
  }

  ngOnDestroy() {
    this.featureData.forEach((feature) => {
      feature.status = 1;
    });
    this.featureData1.forEach((feature) => {
      feature.status = 1;
    });
    this.featureData2.forEach((feature) => {
      feature.status = 1;
    });
  }
}
