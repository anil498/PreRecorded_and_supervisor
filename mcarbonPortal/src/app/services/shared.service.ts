import { Injectable } from "@angular/core";
import { Form } from "app/model/form";
@Injectable()
export class SharedService {
  formInstance: Form;
  formArray: Form[];
  constructor() {}

  setForm(form: Form) {
    this.formInstance = form;
  }

  getForm() {
    return this.formInstance;
  }

  setFormArray(form: Form[]) {
    this.formArray = form;
  }

  getFormArray() {
    return this.formArray;
  }
}
