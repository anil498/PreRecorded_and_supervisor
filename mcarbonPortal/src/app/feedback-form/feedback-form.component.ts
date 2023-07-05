import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  ViewContainerRef,
  ApplicationRef,
  Renderer2,
} from "@angular/core";
import { CdkDragDrop, moveItemInArray } from "@angular/cdk/drag-drop";
import { QuestionTemplateComponent } from "app/templates/question-template/question-template.component";
import { AnswerTemplateComponent } from "app/templates/answer-template/answer-template.component";
@Component({
  selector: "app-feedback-form",
  templateUrl: "./feedback-form.component.html",
  styleUrls: ["./feedback-form.component.scss"],
})
export class FeedbackFormComponent implements OnInit {
  questions: any[];
  droppedItems: string[] = [];
  index = 1;
  questionDisable = false;
  answerDisable = true;
  @ViewChild("answerContainer", { read: ViewContainerRef })
  answerContainer: ViewContainerRef;

  constructor(
    private viewComponentRef: ViewContainerRef,
    private elementRef: ElementRef,
    private appRef: ApplicationRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {}

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
    this.answerDisable = false;
  }
  onAnswerDrop(event) {
    console.log("Dropped");
    console.log(event);
    const droppedIndex = event.currentIndex;
    const ansComponent = this.viewComponentRef.createComponent(
      AnswerTemplateComponent
    );
    const ansDiv = document.getElementById("answer-div");
    this.renderer.appendChild(ansDiv, ansComponent.location.nativeElement);
    console.log(ansComponent.location.nativeElement);
    console.log(ansComponent);

    // const formFieldTemplate = `<app-question-template></app-question-template>`;
    // const answersContainer = document.querySelector(".answer-body");
    // if (answersContainer) {
    //   answersContainer.insertAdjacentHTML("beforeend", formFieldTemplate);
    // }
    // this.index++;
  }
}
