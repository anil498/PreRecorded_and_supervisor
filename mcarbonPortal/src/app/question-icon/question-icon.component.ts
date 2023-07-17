import {
  Component,
  ElementRef,
  EventEmitter,
  OnInit,
  Output,
  Renderer2,
  ViewContainerRef,
} from "@angular/core";
import { Form } from "app/model/form";
import { ObserverService } from "app/services/observer.service";
import { SharedService } from "app/services/shared.service";
import { AnswerTemplateComponent } from "app/templates/answer-template/answer-template.component";
import { QuestionTemplateComponent } from "app/templates/question-template/question-template.component";
import { Subscription } from "rxjs";

@Component({
  selector: "app-question-icon",
  templateUrl: "./question-icon.component.html",
  styleUrls: ["./question-icon.component.scss"],
})
export class QuestionIconComponent implements OnInit {
  questionId: number;
  question: any;
  questionForm: Form[] = [];
  totalAnswer: number;
  uniqueId: string;
  private questionIdSub: Subscription;
  private formDataSub: Subscription;
  constructor(
    private observerService: ObserverService,
    private sharedService: SharedService,
    private viewComponentRef: ViewContainerRef,
    private elementRef: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.question = this.sharedService.getForm();
    console.log(this.question);
    this.questionForm = this.sharedService.getFormArray();
    console.log(this.questionForm);
    this.totalAnswer = this.question.meta.length;
    console.log(this.totalAnswer);
  }

  ngOnDestroy() {
    if (this.formDataSub) this.formDataSub.unsubscribe();
  }
  showQuestion() {
    console.log("click");
    console.log(this.question);
    this.totalAnswer = this.question.meta.length;
    console.log(this.totalAnswer);
    this.observerService.isAnswerDisable.next(false);
    const quesComponent = this.viewComponentRef.createComponent(
      QuestionTemplateComponent
    );
    //quesComponent.instance.questionId = this.question.qId + 1;
    //quesComponent.instance.question = this.question;
    const quesDiv = document.getElementById("question-div");
    this.sharedService.setForm(this.question);
    this.renderer.appendChild(quesDiv, quesComponent.location.nativeElement);
    this.observerService.isQuestionDisable.next(true);
    while (this.totalAnswer--) {
      const ansComponent = this.viewComponentRef.createComponent(
        AnswerTemplateComponent
      );
      const ansDiv = document.getElementById("answer-div");
      const ansElement = ansComponent.location.nativeElement;
      this.uniqueId = "ans" + this.totalAnswer;
      this.renderer.setAttribute(ansElement, "id", this.uniqueId);
      this.renderer.appendChild(ansDiv, ansElement);
    }
  }
}
