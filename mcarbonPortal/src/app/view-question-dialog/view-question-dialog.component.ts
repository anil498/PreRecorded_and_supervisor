import { Component, Inject, OnInit } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";

@Component({
  selector: "app-view-question-dialog",
  templateUrl: "./view-question-dialog.component.html",
  styleUrls: ["./view-question-dialog.component.scss"],
})
export class ViewQuestionDialogComponent implements OnInit {
  form: FormGroup;
  questions: any[] = [];
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<ViewQuestionDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public icdc: any
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    console.log(this.icdc);
    this.fillForm();
  }

  fillForm() {
    console.log(this.icdc.icdcData);
    this.questions = this.icdc.icdcData;

    let autoindex = 0;
    this.questions.forEach((question) => {
      if (question.type === "checkbox") {
        const checkboxOptions = question.meta.reduce((options, option) => {
          options[option] = false;
          return options;
        }, {});
        this.form.addControl(question.q_id, this.fb.control(checkboxOptions));
      } else {
        if (question.type === "text") {
          autoindex++;
        }

        this.form.addControl(
          question.q_id,
          this.fb.control("", Validators.required)
        );
      }
    });
  }
  ngOnDestroy() {}
}
