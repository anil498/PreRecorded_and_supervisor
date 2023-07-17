import { HttpClient } from "@angular/common/http";
import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ObserverService } from "app/services/observer.service";
import { Subscription } from "rxjs";

@Component({
  selector: "app-question-template",
  templateUrl: "./question-template.component.html",
  styleUrls: ["./question-template.component.scss"],
})
export class QuestionTemplateComponent implements OnInit {
  questionForm: FormGroup;
  questionId: any;
  types = [
    {
      name: "Text",
      type: "text",
      value: "text",
      icon: "short_text",
    },
    {
      name: "Mutliple Choice",
      type: "checkbox",
      value: "multiple_choice",
      icon: "check_box",
    },
    {
      name: "Radio",
      type: "radio",
      value: "radio",
      icon: "radio_button_checked",
    },
    {
      name: "Drop Down",
      type: "dropdown",
      value: "drop_down",
      icon: "arrow_drop_down_circle",
    },
  ];
  selectedValue: any;
  questionTypeSub: Subscription;
  constructor(
    private observerService: ObserverService,
    private fb: FormBuilder
  ) {
    this.selectedValue = this.types[0];
  }

  ngOnInit(): void {
    console.log(this.selectedValue);
    this.questionTypeSub = this.observerService.totalQuestionObs.subscribe(
      (quesId) => {
        this.questionId = quesId;
      }
    );
    this.questionForm = this.fb.group({
      qId: [this.questionId, [Validators.required]],
      question: ["", [Validators.required]],
      type: [this.selectedValue.type, [Validators.required]],
    });
    this.observerService.questionType.next(this.selectedValue.type);
    this.questionForm.value.type = this.selectedValue.type;
    this.observerService.questionForm.next(this.questionForm);

    this.questionForm.valueChanges.subscribe((ques) => {
      console.log(ques);
      this.questionForm.value.question = ques.question;
      this.observerService.questionType.next(this.selectedValue.type);
      this.questionForm.value.type = this.selectedValue.type;
      this.observerService.questionForm.next(this.questionForm);
      console.log(this.questionForm);
    });
    this.observerService.quesEditObs.subscribe((ques) => {});
  }

  onTypeChange(event: any) {
    console.log(this.selectedValue);
    console.log(event);
    this.selectedValue = event.value;
    console.log(this.selectedValue);
    console.log(this.questionForm);
    this.observerService.questionType.next(this.selectedValue.type);
    this.questionForm.value.type = this.selectedValue.type;
    this.observerService.questionForm.next(this.questionForm);
  }

  ngOnDestroy() {
    if (this.questionTypeSub) this.questionTypeSub.unsubscribe();
  }
}
