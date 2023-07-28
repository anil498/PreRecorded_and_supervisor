import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  ViewContainerRef,
  ApplicationRef,
  Renderer2,
  ChangeDetectorRef,
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
import { FormGroup } from "@angular/forms";
import { QuestionIconComponent } from "app/question-icon/question-icon.component";
import { SharedService } from "app/services/shared.service";
import { RestService } from "app/services/rest.service";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";
@Component({
  selector: "app-feedback-form",
  templateUrl: "./feedback-form.component.html",
  styleUrls: ["./feedback-form.component.scss"],
})
export class FeedbackFormComponent implements OnInit {
  showQuestionIcon: boolean = true;
  showAnswerIcon: boolean = true;
  formName: string;
  questions: any[];
  droppedItems: string[] = [];
  index = 0;
  totalAnswer: number;
  questionDisable: boolean = false;
  answerDisable: boolean = true;
  questionType: string;
  questionArray: Form[] = [];
  ansArray = [];
  uniqueId: string;
  feedbackForm: Form;
  metaArray: any[] = [];
  questionObject: FormGroup;
  ansObject: any;
  questionId: any;
  questionTypeSub: Subscription;
  isAnswerDisableSub: Subscription;
  questionFormSub: Subscription;
  answerFormSub: Subscription;
  totalAnswerSub: Subscription;
  totalQuestionSub: Subscription;
  ansMetaSub: Subscription;
  isQuestionDisableSub: Subscription;

  @ViewChild("answerContainer", { read: ViewContainerRef })
  answerContainer: ViewContainerRef;

  constructor(
    private viewComponentRef: ViewContainerRef,
    private elementRef: ElementRef,
    private appRef: ApplicationRef,
    private renderer: Renderer2,
    private snackBar: MatSnackBar,
    private restService: RestService,
    private observerService: ObserverService,
    private sharedService: SharedService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.questionTypeSub = this.observerService.questionTypeObs.subscribe(
      (type) => {
        this.questionType = type;
      }
    );
    this.isAnswerDisableSub = this.observerService.isAnswerDisableObs.subscribe(
      (isDisable) => {
        this.answerDisable = isDisable;
      }
    );
    this.isQuestionDisableSub =
      this.observerService.isQuestionDisableObs.subscribe((isDisable) => {
        console.log(isDisable);
        this.questionDisable = isDisable;
        this.changeDetectorRef.detectChanges();
      });
    this.questionFormSub = this.observerService.questionFormObs.subscribe(
      (ques) => {
        if (ques) {
          console.log(ques.value);
          this.questionObject = ques;
        }
      }
    );
    this.answerFormSub = this.observerService.ansFormObs.subscribe((ans) => {
      if (ans) {
        console.log(ans);
        this.ansObject = ans;
      }
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

  checkQuestionDisable() {
    return this.questionDisable;
  }
  ngOnDestroy() {
    this.onCancel();
    if (this.answerFormSub) this.answerFormSub.unsubscribe();
    if (this.questionFormSub) this.questionFormSub.unsubscribe();
    if (this.isAnswerDisableSub) this.isAnswerDisableSub.unsubscribe();
    if (this.totalAnswerSub) this.totalAnswerSub.unsubscribe();
    if (this.totalQuestionSub) this.totalQuestionSub.unsubscribe();
    if (this.questionTypeSub) this.questionTypeSub.unsubscribe();
    if (this.ansMetaSub) this.ansMetaSub.unsubscribe();
  }

  arrangeAnswer(event: CdkDragDrop<string[]>) {
    const answers = <any>document.getElementsByTagName("app-answer-template");
    console.log(answers, event.previousIndex, event.currentIndex);
    moveItemInArray(answers, event.previousIndex, event.currentIndex);
  }

  onQuestionDrop(event) {
    if (this.questionDisable == true) {
      console.log("please fill current question");
      this.openSnackBar("Please Add this Question First", "error");
      return;
    }
    //moveItemInArray(this.selectedAnswers, event.previousIndex, event.currentIndex);
    this.showQuestionIcon = false;
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
    this.observerService.isQuestionDisable.next(true);
    this.observerService.isAnswerDisable.next(false);
    this.answerDisable = false;
    this.onAnswerDrop(null);
    const quesBody = <HTMLElement>(
      document.getElementsByClassName("question-body")[0]
    );
    quesBody.style.border = "2px solid #ccc";
  }
  onAnswerDrop(event) {
    if (this.answerDisable == true) {
      console.log("please fill current current");
      this.openSnackBar("Please Add this Question FIrst", "error");
      return;
    }
    this.showAnswerIcon = false;
    if (this.questionType !== "text" || this.totalAnswer < 1) {
      console.log("Dropped");
      console.log(event);
      //const droppedIndex = event.currentIndex;
      const ansComponent = this.viewComponentRef.createComponent(
        AnswerTemplateComponent
      );
      const ansElement = ansComponent.location.nativeElement;
      //const ansDiv = document.getElementById("answer-div");
      const quesDiv = document.getElementById("question-div");
      this.uniqueId = "ans" + this.totalAnswer;
      this.renderer.setAttribute(ansElement, "id", this.uniqueId);
      this.renderer.setAttribute(ansElement, "cdkdrag", "");
      this.renderer.addClass(ansElement, "cdk-drag");
      this.observerService.ansUniqueId.next(this.uniqueId);
      // this.renderer.appendChild(ansDiv, ansElement);
      this.renderer.appendChild(quesDiv, ansElement);
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
    if (this.questionObject.value.question == "") {
      this.openSnackBar("Please Complete this Question First", "error");
      return;
    }
    console.warn(this.questionObject);
    console.log(this.metaArray);
    console.warn(this.ansObject);
    this.feedbackForm = Object.assign(
      this.questionObject.value,
      this.ansObject.value
    );
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
    // console.log(ansDiv.childElementCount);
    // while (ansDiv.childElementCount > 0) {
    //   ansDiv.removeChild(ansDiv.firstChild);
    // }

    this.questionDisable = false;
    this.observerService.isQuestionDisable.next(false);
    this.observerService.isAnswerDisable.next(true);
    this.observerService.totalAnswer.next(0);
    this.observerService.ansMeta.next([]);
    const quesIconComponent = this.viewComponentRef.createComponent(
      QuestionIconComponent
    );
    const quesTabDiv = document.getElementById("question-tab");
    this.renderer.appendChild(
      quesTabDiv,
      quesIconComponent.location.nativeElement
    );
    this.sharedService.setForm(this.feedbackForm);
    this.sharedService.setFormArray(this.questionArray);
    this.showQuestionIcon = true;
    this.showAnswerIcon = true;
  }

  openSnackBar(message: string, color: string) {
    const snackBarConfig = new MatSnackBarConfig();
    snackBarConfig.duration = 3000;
    snackBarConfig.panelClass = [color];
    this.snackBar.open(message, "Dismiss", snackBarConfig);
  }

  async onFormSave() {
    if (this.formName == null || this.formName == "") {
      this.openSnackBar("Please Enter a Form Name", "error");
      return;
    }
    if (this.questionArray.length < 1) {
      this.openSnackBar("PLease Add At least 1 Question", "error");
      return;
    }
    if (this.questionDisable == true) {
      this.openSnackBar("Please Add this Question FIrst", "error");
      return;
    }
    console.log("form saved");
    console.log(this.formName, this.questionArray);
    let response: any;
    try {
      response = await this.restService.createForm(
        "Icdc/Create",
        this.formName,
        this.questionArray
      );
      console.log(response);
      if (response.status_code == 200) {
        console.log("created");
        this.openSnackBar(response.msg, "success");
        this.onCancel();
      } else {
      }
    } catch (error) {
      console.log("error");
      this.openSnackBar(response.msg, "error");
      console.log(error);
    }
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

  onCancel() {
    this.formName = "";
    const quesDiv = document.getElementById("question-div");
    const ansDiv = document.getElementById("answer-div");
    const quesTabDiv = document.getElementById("question-tab");
    if (quesDiv) {
      while (quesDiv.childElementCount > 0) {
        quesDiv.removeChild(quesDiv.firstChild);
      }
    }
    // if (ansDiv) {
    //   console.log(ansDiv.childElementCount);
    //   while (ansDiv.childElementCount > 0) {
    //     ansDiv.removeChild(ansDiv.firstChild);
    //   }
    // }

    while (quesTabDiv.childElementCount > 0) {
      quesTabDiv.removeChild(quesTabDiv.firstChild);
    }

    const quesBody = <HTMLElement>(
      document.getElementsByClassName("question-body")[0]
    );
    quesBody.style.border = "2px dashed #ccc";

    this.questionDisable = false;
    this.observerService.isQuestionDisable.next(false);
    this.observerService.isAnswerDisable.next(true);
    this.observerService.totalAnswer.next(0);
    this.observerService.ansMeta.next([]);
    this.observerService.totalQuestion.next(0);
    this.observerService.totalAnswer.next(0);
    this.showQuestionIcon = true;
    this.showAnswerIcon = true;
  }
}
