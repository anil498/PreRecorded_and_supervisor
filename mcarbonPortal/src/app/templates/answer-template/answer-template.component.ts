import { Component, OnInit, Renderer2 } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ObserverService } from "app/services/observer.service";
import { Subscription } from "rxjs";
import {
  CdkDrag,
  CdkDragHandle,
  CdkDragPlaceholder,
} from "@angular/cdk/drag-drop";
@Component({
  selector: "app-answer-template",
  templateUrl: "./answer-template.component.html",
  styleUrls: ["./answer-template.component.scss"],
})
export class AnswerTemplateComponent implements OnInit {
  private questionTypeSub: Subscription;
  private totalAnswerSub: Subscription;
  private uniqueIdSub: Subscription;
  ansForm: FormGroup;
  id: any;
  ansIndex: any;
  uniqueId: any;
  componentId: string;
  totalAnswer: number;
  questionType: string;
  ansArray: any[] = [];
  constructor(
    private observerService: ObserverService,
    private fb: FormBuilder,
    private renderer: Renderer2
  ) {
    this.questionType = "text";
  }

  ngOnInit(): void {
    this.ansForm = this.fb.group({
      meta: ["", [Validators.required]],
    });

    this.totalAnswerSub = this.observerService.totalAnswerObs.subscribe(
      (value: number) => {
        console.log(value);
        this.totalAnswer = value;
      }
    );
    this.uniqueIdSub = this.observerService.ansUniqueIdObs.subscribe(
      (id: string) => {
        this.componentId = id;
        console.log(this.componentId);
        this.uniqueId = this.componentId.match(/\d+/)[0];
        console.log(this.uniqueId);
      }
    );
    this.ansIndex = this.uniqueId;
    this.id = this.componentId;
    this.questionTypeSub = this.observerService.questionTypeObs.subscribe(
      (value: string) => {
        console.log("TYPE CHANGED");
        console.log(value);
        this.questionType = value;
        if (this.questionType == "text") {
          this.observerService.isAnswerDisable.next(true);
          this.ansForm.get("meta").setValue("");
        } else {
          this.observerService.isAnswerDisable.next(false);
          this.ansForm.get("meta").setValue(`Option ${this.ansIndex}`);
        }
      }
    );

    this.observerService.ansForm.next(this.ansForm);

    this.observerService.ansMetaObs.subscribe((ansArray) => {
      console.log(ansArray);
      this.ansArray = ansArray;
    });
    this.ansArray[this.ansIndex] = this.ansForm.value.meta;
    this.observerService.ansMeta.next(this.ansArray);
  }

  ngOnDestroy() {
    if (this.questionTypeSub) this.questionTypeSub.unsubscribe();
    if (this.totalAnswerSub) this.totalAnswerSub.unsubscribe();
    if (this.uniqueIdSub) this.uniqueIdSub.unsubscribe();
  }

  removeAnswer() {
    const ansEle = document.getElementById(this.id);
    const ansDiv = document.getElementById("answer-div");
    this.renderer.removeChild(ansDiv, ansEle);
    console.log("Deleted" + ansEle);
    console.log(
      "🚀 ~ file: answer-template.component.ts:93 ~ AnswerTemplateComponent ~ removeAnswer ~ this.totalAnswer:",
      this.totalAnswer
    );
    this.totalAnswer = this.totalAnswer - 1;
    this.observerService.totalAnswer.next(this.totalAnswer);
    console.log(
      "🚀 ~ file: answer-template.component.ts:99 ~ AnswerTemplateComponent ~ removeAnswer ~ this.totalAnswer:",
      this.totalAnswer
    );
    this.ansArray.splice(parseInt(this.ansIndex), 1);
    console.log(this.ansArray);
    this.observerService.ansMeta.next(this.ansArray);
  }

  onKey(event) {
    console.log(event);
    this.ansArray[this.ansIndex] = this.ansForm.value.meta;
    console.log(this.ansArray);
    this.observerService.ansMeta.next(this.ansArray);
  }
}
