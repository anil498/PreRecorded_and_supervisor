import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-question-template",
  templateUrl: "./question-template.component.html",
  styleUrls: ["./question-template.component.scss"],
})
export class QuestionTemplateComponent implements OnInit {
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
      type: "select",
      value: "drop_down",
      icon: "arrow_drop_down_circle",
    },
  ];
  selectedValue: any;
  constructor() {
    this.selectedValue = this.types[0];
  }

  ngOnInit(): void {
    console.log(this.selectedValue);
  }

  onTypeChange(event: any) {
    console.log(this.selectedValue);
    console.log(event);
    this.selectedValue = structuredClone(event.value);
    console.log(this.selectedValue);
  }
}
