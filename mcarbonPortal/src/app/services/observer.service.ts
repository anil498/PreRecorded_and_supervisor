import { Injectable } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { BehaviorSubject, Observable, Subscription } from "rxjs";
import { Form } from "app/model/form";
@Injectable()
export class ObserverService {
  isBrowserRefreshed = <BehaviorSubject<boolean>>new BehaviorSubject(false);
  isBrowserRefreshedObs: Observable<boolean>;
  questionType = <BehaviorSubject<string>>new BehaviorSubject("text");
  questionTypeObs: Observable<string>;
  totalQuestion = <BehaviorSubject<number>>new BehaviorSubject(0);
  totalQuestionObs: Observable<number>;
  totalAnswer = <BehaviorSubject<number>>new BehaviorSubject(0);
  totalAnswerObs: Observable<number>;
  isAnswerDisable = <BehaviorSubject<boolean>>new BehaviorSubject(true);
  isAnswerDisableObs: Observable<boolean>;

  formData: BehaviorSubject<Form[]> = new BehaviorSubject<Form[]>([]);
  formDataObs: Observable<Form[]>;
  ansUniqueId = <BehaviorSubject<string>>new BehaviorSubject("");
  ansUniqueIdObs: Observable<string>;
  questionForm = <BehaviorSubject<FormGroup>>new BehaviorSubject(null);
  questionFormObs: Observable<FormGroup>;
  ansForm = <BehaviorSubject<FormGroup>>new BehaviorSubject(null);
  ansFormObs: Observable<FormGroup>;

  url = <BehaviorSubject<string>>new BehaviorSubject("");
  urlObs: Observable<string>;
  authKey = <BehaviorSubject<string>>new BehaviorSubject("");
  authKeyObs: Observable<string>;

  //ansMeta = <BehaviorSubject<any>>new BehaviorSubject([]);
  ansMeta: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([]);
  ansMetaObs: Observable<any[]>;
  constructor() {
    this.isBrowserRefreshedObs = this.isBrowserRefreshed.asObservable();
    this.questionTypeObs = this.questionType.asObservable();
    this.totalAnswerObs = this.totalAnswer.asObservable();
    this.totalQuestionObs = this.totalQuestion.asObservable();
    this.isAnswerDisableObs = this.isAnswerDisable.asObservable();
    this.formDataObs = this.formData.asObservable();
    this.questionFormObs = this.questionForm.asObservable();
    this.ansFormObs = this.ansForm.asObservable();
    this.ansUniqueIdObs = this.ansUniqueId.asObservable();

    this.ansMetaObs = this.ansMeta.asObservable();
    this.urlObs = this.url.asObservable();
    this.authKeyObs = this.authKey.asObservable();
  }
}
