import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  ViewContainerRef,
  ApplicationRef,
  Renderer2,
} from "@angular/core";
import {
  CdkDrag,
  CdkDragDrop,
  moveItemInArray,
  CdkDragHandle,
  CdkDragPlaceholder,
} from "@angular/cdk/drag-drop";
import { QuestionTemplateComponent } from "app/templates/question-template/question-template.component";
import { AnswerTemplateComponent } from "app/templates/answer-template/answer-template.component";
import { ObserverService } from "app/services/observer.service";
import { Form } from "app/model/form";
import { Subscription, timeout } from "rxjs";
@Component({
  selector: "app-feedback-form",
  templateUrl: "./feedback-form.component.html",
  styleUrls: ["./feedback-form.component.scss"],
})
export class FeedbackFormComponent implements OnInit {
  questions: any[];
  droppedItems: string[] = [];
  index = 0;
  totalAnswer: number;
  questionDisable: boolean = false;
  answerDisable: boolean;
  questionType: string;
  questionArray: Form[] = [];
  ansArray = [];
  uniqueId: string;
  feedbackForm: Form;
  metaArray: any[] = [];
  questionObject: any;
  ansObject: any;
  questionId: any;
  questionTypeSub: Subscription;
  isAnswerDisableSub: Subscription;
  questionFormSub: Subscription;
  answerFormSub: Subscription;
  totalAnswerSub: Subscription;
  totalQuestionSub: Subscription;
  ansMetaSub: Subscription;

  @ViewChild("answerContainer", { read: ViewContainerRef })
  answerContainer: ViewContainerRef;

  constructor(
    private viewComponentRef: ViewContainerRef,
    private elementRef: ElementRef,
    private appRef: ApplicationRef,
    private renderer: Renderer2,
    private observerService: ObserverService
  ) {}

  ngOnInit(): void {
    this.questionTypeSub = this.observerService.questionTypeObs.subscribe(
      (type) => {
        console.log(
          "🚀 ~ file: feedback-form.component.ts:64 ~ FeedbackFormComponent ~ ngOnInit ~ type:",
          type
        );
        this.questionType = type;
      }
    );
    this.isAnswerDisableSub = this.observerService.isAnswerDisableObs.subscribe(
      (isDisable) => {
        console.log(
          "🚀 ~ file: feedback-form.component.ts:73 ~ FeedbackFormComponent ~ ngOnInit ~ isDisable:",
          isDisable
        );
        this.answerDisable = isDisable;
      }
    );
    this.questionFormSub = this.observerService.questionFormObs.subscribe(
      (ques) => {
        console.log(ques.value);
        this.questionObject = ques.value;
      }
    );
    this.answerFormSub = this.observerService.ansFormObs.subscribe((ans) => {
      console.log(ans);
      this.ansObject = ans.value;
      // if (this.questionType !== "text") {
      //   this.metaArray.push(this.ansObject.meta);
      // }
    });
    this.totalAnswerSub = this.observerService.totalAnswerObs.subscribe(
      (totalAns) => {
        console.log(
          "🚀 ~ file: feedback-form.component.ts:95 ~ FeedbackFormComponent ~ ngOnInit ~ totalAns:",
          totalAns
        );
        this.totalAnswer = totalAns;
      }
    );
    this.totalQuestionSub = this.observerService.totalQuestionObs.subscribe(
      (totalQues) => {
        console.log(totalQues);
        this.questionId = totalQues;
      }
    );

    this.observerService.formDataObs.subscribe((formValue) => {
      console.log(formValue);
    });

    // this.observerService.ansMetaObs.subscribe((ansArray) => {
    //   console.log(ansArray);
    //   this.ansArray = ansArray;
    // });

    this.ansMetaSub = this.observerService.ansMetaObs.subscribe((ansMeta) => {
      console.log(ansMeta);
      this.metaArray = ansMeta;
    });
  }

  ngOnDestroy() {
    if (this.answerFormSub) this.answerFormSub.unsubscribe();
    if (this.questionFormSub) this.questionFormSub.unsubscribe();
    if (this.isAnswerDisableSub) this.isAnswerDisableSub.unsubscribe();
    if (this.totalAnswerSub) this.totalAnswerSub.unsubscribe();
    if (this.totalQuestionSub) this.totalQuestionSub.unsubscribe();
    if (this.questionTypeSub) this.questionTypeSub.unsubscribe();
    if (this.ansMetaSub) this.ansMetaSub.unsubscribe();
  }

  onQuestionDrop(event) {
    console.log("Dropped");
    console.log(event);
    const droppedIndex = event.currentIndex;
    const quesComponent = this.viewComponentRef.createComponent(
      QuestionTemplateComponent
    );
    const quesDiv = document.getElementById("question-div");
    this.renderer.appendChild(quesDiv, quesComponent.location.nativeElement);
    console.log(quesComponent.location.nativeElement);
    console.log(quesComponent);
    this.index++;
    this.questionDisable = true;
    this.observerService.isAnswerDisable.next(false);
  }
  onAnswerDrop(event) {
    if (this.questionType !== "text" || this.totalAnswer < 1) {
      console.log("Dropped");
      console.log(event);
      const droppedIndex = event.currentIndex;
      const ansComponent = this.viewComponentRef.createComponent(
        AnswerTemplateComponent
      );
      const ansElement = ansComponent.location.nativeElement;
      const ansDiv = document.getElementById("answer-div");
      this.uniqueId = "ans" + this.totalAnswer;
      this.renderer.setAttribute(ansElement, "id", this.uniqueId);
      this.renderer.setAttribute(ansElement, "cdkdrag", "");
      this.renderer.addClass(ansElement, "cdk-drag");
      this.observerService.ansUniqueId.next(this.uniqueId);
      this.renderer.appendChild(ansDiv, ansElement);
      this.totalAnswer++;
      this.observerService.totalAnswer.next(this.totalAnswer);
    } else {
    }
  }

  drop(event: CdkDragDrop<string[]>) {
    const ansArray = document.getElementsByTagName("app-answer-template");

    moveItemInArray([], event.previousIndex, event.currentIndex);
  }

  saveQuestion() {
    const qId = this.index;
    //this.metaArray.push(this.ansObject);
    //console.log(this.ansArray);
    console.log(this.metaArray);
    console.log(this.questionObject);
    console.log(this.ansObject);
    this.feedbackForm = Object.assign(this.questionObject, this.ansObject);
    this.feedbackForm.meta = this.metaArray;
    console.log(this.feedbackForm);
    this.questionArray[this.questionId] = this.feedbackForm;

    console.log(this.questionArray);
    this.observerService.formData.next(this.questionArray);
    this.questionId++;
    this.observerService.totalQuestion.next(this.questionId);

    const quesDiv = document.getElementById("question-div");
    const ansDiv = document.getElementById("answer-div");

    console.log(quesDiv.childElementCount);
    while (quesDiv.childElementCount > 0) {
      quesDiv.removeChild(quesDiv.firstChild);
    }
    console.log(ansDiv.childElementCount);
    while (ansDiv.childElementCount > 0) {
      ansDiv.removeChild(ansDiv.firstChild);
    }

    this.questionDisable = false;
    this.observerService.isAnswerDisable.next(true);
    this.observerService.totalAnswer.next(0);
    this.observerService.ansMeta.next([]);
  }

  // removeAns(id: any) {
  //   const ansEle = document.getElementById(`${id}`);
  //   const ansDiv = document.getElementById("answer-div");
  //   this.renderer.removeChild(ansDiv, ansEle);
  //   console.log("Deleted" + ansEle);
  //   this.metaArray.slice(<number>id, 1);
  //   console.log(this.metaArray);
  //   this.observerService.ansMeta.next(this.metaArray);
  // }
}
