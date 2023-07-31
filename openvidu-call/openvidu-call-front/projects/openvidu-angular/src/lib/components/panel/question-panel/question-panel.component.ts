import {
	AfterViewInit,
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	ElementRef,
	OnInit,
	ViewChild,
	Output,
	EventEmitter,
	QueryList,
	ViewChildren
} from '@angular/core';
import { PanelType } from '../../../models/panel.model';
import { PanelService } from '../../../services/panel/panel.service';
import { HttpClient } from '@angular/common/http';
import { LoggerService } from '../../../services/logger/logger.service';
import { ILogger } from '../../../models/logger.model';
import { FormArray, FormBuilder, FormControl, FormGroup, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { OpenViduService } from '../../../services/openvidu/openvidu.service';
import { Signal } from '../../../models/signal.model';
import { ParticipantService } from '../../../services/participant/participant.service';
import { Session } from 'openvidu-browser';
import { Subscription } from 'rxjs';
import { OpenViduAngularConfigService } from '../../../services/config/openvidu-angular.config.service';
import { TranslateService } from '../../../services/translate/translate.service';
import { ActionService } from '../../../services/action/action.service';
/**
 *
 * The **QuestionPanelComponent** is hosted inside of the {@link PanelComponent}.
 * It is in charge of displaying the session chat.
 *
 * <div class="custom-table-container">

 * <div>
 *
 * <h3>OpenVidu Angular Directives</h3>
 *
 * The ChatPanelComponent can be replaced with a custom component. It provides us the following {@link https://angular.io/guide/structural-directives Angular structural directives}
 * for doing this.
 *
 * |            **Directive**           |                 **Reference**                 |
 * |:----------------------------------:|:---------------------------------------------:|
 * |           ***ovQuestionPanel**          |           {@link QuestionPanelDirective}          |
 *
 * <p class="component-link-text">
 * 	<span class="italic">See all {@link OpenViduAngularDirectiveModule OpenVidu Angular Directives}</span>
 * </p>
 * </div>
 * </div>
 */
@Component({
	selector: 'ov-question-panel',
	templateUrl: './question-panel.component.html',
	styleUrls: ['../panel.component.css', './question-panel.component.css']
})
export class QuestionPanelComponent implements OnInit, AfterViewInit {
	/**
	 * @ignore
	 */
	@ViewChild('formScroll') formScroll: ElementRef;

	@ViewChildren('inputFieldRef') inputFieldRefs: QueryList<ElementRef>;

	/**
	 * Provides event notifications that fire when submit button has been clicked.
	 */
	@Output() onSubmitButtonClicked: EventEmitter<any> = new EventEmitter<any>();

	private log: ILogger;

	form: FormGroup;
	questions: any[] = [];
	answer: any;
	selectedAnswers: any[] = [];
	isSubmit: boolean = false;
	selectedOptions: any;
	name: string;
	ischecked: boolean = false;
	session: Session;
	scrolling: boolean = true;
	usertype: string = '';
	editicdc: boolean;
	titleicdc: string = '';
	displayicdc: boolean = true;
	questionsicdc: string = '';
	isClose: boolean = true;

	private usertypeSub: Subscription;
	private editicdcSub: Subscription;
	private displayicdcSub: Subscription;
	private titleicdcSub: Subscription;
	private questionsicdcSub: Subscription;
	public Selectformpanel: boolean = false;
	FormArray: string[] = [];
	selectedFormName: string = '';
	selectedFormindex: number;
	selectedIdx: number = -1;
	isformselected: boolean = false;
	ispreviewpermission:boolean=false;
	isformpreviewmode: boolean = false;

	myTextQuestionMap: Map<number, number> = new Map<number, number>();

	constructor(
		private panelService: PanelService,
		private cd: ChangeDetectorRef,
		private http: HttpClient,
		protected loggerSrv: LoggerService,
		private formBuilder: FormBuilder,
		protected openviduService: OpenViduService,
		protected participantService: ParticipantService,
		private libService: OpenViduAngularConfigService,
		private actionService: ActionService,
		private translateService: TranslateService
	) {
		//Get Logger Service
		this.log = this.loggerSrv.get('Question Panel');

		//Get Session Service
		this.session = this.openviduService.getSession();

		//Build a Form Group
		this.form = this.formBuilder.group({});
	}

	ngOnInit(): void {
		this.subscribetoParameters();
		this.subscribeToSignal();
		if (this.usertype === 'Support') {
			this.Selectformpanel = true;
		}
		this.form.valueChanges.subscribe((value) => {
			if (!this.form.pristine) {
				this.openviduService.sendSignal(Signal.SUPPORT, undefined, value);
			}
		});
		this.loadFormInArray();
	}

	ngAfterViewInit() {
		//this.fillform();
	}
	loadFormInArray() {
		const totQjsonarray = JSON.parse(this.questionsicdc); //this is a array of json object
		for (let i = 0; i < totQjsonarray.length; i++) {
			this.FormArray.push(totQjsonarray[i].form_name);
		}
	}

	selectItem(index: number) {
		this.isformselected = true;
		this.ispreviewpermission=this.displayicdc;
		this.selectedIdx = index;
		this.selectedFormindex = index;
		this.selectedFormName = this.FormArray[index];
	}

	previewToquestion(): void {
		this.isformpreviewmode = true;
		this.ShowquestionTouser();
	}

	ShowquestionTouser(): void {
		//send signal to open panel in customer side

		if (!this.isformpreviewmode) {
			const data = {
				index: String(this.selectedFormindex),
				formtitle: this.selectedFormName
			};
			//this signal subscribe in session.ts
			this.openviduService.sendSignal(Signal.QUESTION, undefined, data);
		}

		const totQjsonarray = JSON.parse(this.questionsicdc); //this is a array of json object

		for (let i = 0; i < totQjsonarray.length; i++) {
			for (const key in totQjsonarray[i]) {
				if (totQjsonarray[i].hasOwnProperty(key) && totQjsonarray[i][key] === this.selectedFormName) {
					this.selectedFormindex = i;
				}
			}
		}

		this.titleicdc = this.selectedFormName;
		this.Selectformpanel = !this.Selectformpanel;
		this.fillform(this.selectedFormindex);
	}

	fillform(formindex: number) {
		const totQjsonarray = JSON.parse(this.questionsicdc);

		this.questions = JSON.parse(totQjsonarray[formindex].icdc_data);

		let autoindex = 0;
		this.questions.forEach((question) => {
			//Set custom Controls for a Checkbox Question
			if (question.type === 'checkbox') {
				const checkboxOptions = question.meta.reduce((options, option) => {
					options[option] = false; // Set all options to false initially
					return options;
				}, {});
				this.selectedAnswers[question.q_id] = [];
				this.form.addControl(question.q_id, this.formBuilder.control(checkboxOptions));
			} else {
				//fill map

				if (question.type === 'text') {
					this.myTextQuestionMap.set(question.q_id, autoindex);
					autoindex++;
				}

				//Add Controls for Questions other than the checkbox
				this.form.addControl(question.q_id, this.formBuilder.control('', Validators.required));
			}
		});
		//Give Form Access to user based on the paramters Recieved from backend
		if (!this.editicdc) {
			this.form.disable();
			this.isSubmit = false;
		} else {
			this.isSubmit = true;
		}
		if (this.usertype == 'Customer') {
			this.isClose = false;
		}
		//Update UI
		if (this.panelService.isQuestionPanelOpened()) {
			this.cd.markForCheck();
		}
	} //fill form fun end

	Close_Submit_button(): void {
		if (this.isformpreviewmode) {
			//click on close button
			this.isformpreviewmode = false;
			this.Selectformpanel = true;
		} //click on submit button
		else {
			this.submitForm(this.selectedFormindex);
		}
	}

	submitForm(formindex:number) {
		const formresponse = JSON.stringify(this.form.value);
		const totalformjson = {};

		totalformjson['selectformindex'] = formindex;
		totalformjson['formresponsevalue'] =formresponse;
		this.onSubmitButtonClicked.emit(totalformjson);

		this.form.reset();
		this.selectedAnswers = [];
		this.close();
		const data = {
			message: 'Submit Form Signal',
			sendby:this.usertype
		};

		this.openviduService.sendSignal(Signal.FORMSUBMITTED, undefined, data);

		this.openviduService.sendSignal(Signal.CLOSE, undefined, data);
	}


	//Subscribe to Signals
	protected subscribeToSignal() {
		//signal for find selected form index in  Panel
		this.session.on(`signal:${Signal.FORMINDEX}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection: boolean = this.openviduService.isMyOwnConnection(connectionId);
			if (isMyOwnConnection) {
                this.titleicdc=data.formtitle;
				this.selectedFormindex=data.index;
				this.fillform(this.selectedFormindex);

			}
		});
		// Data Signal From Other Participant
		this.session.on(`signal:${Signal.SUPPORT}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);

			if (!isMyOwnConnection) {
				//For Checkbox Question
				if (data.options) {
					const control = this.form.get(data.qid);
					const value = control.value;

					if (data.check == true) {
						value[data.options] = true; // Set the specific option to true
					} else {
						value[data.options] = false; // Set the specific option to true
					}

					control.patchValue(value);
					this.form.markAsPristine();
				} else if (data.index) {
					this.form.patchValue(data);
					let qindex = data.index - 1;
					let inputElement22 = this.inputFieldRefs.toArray()[qindex].nativeElement;
					if (inputElement22.scrollWidth > inputElement22.clientWidth) {
						const scrollAmount = inputElement22.scrollWidth - inputElement22.clientWidth;
						inputElement22.scrollLeft = scrollAmount;
					}
					this.form.markAsPristine();
				} else {
					//For Questions other than the checkbox
					this.form.patchValue(data);
					this.form.markAsPristine();
				}
			}
		});

		// Close Form Signal
		this.session.on(`signal:${Signal.CLOSE}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);
			if (!isMyOwnConnection) {
				this.log.w('Panel Closing');
				this.close();
			}
		});

		//Scroll Form Signal
		this.session.on(`signal:${Signal.SCROLL}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);

			if (!isMyOwnConnection) {
				const formElement = this.formScroll.nativeElement;
				formElement.scrollTop = data.scrollValue;
				this.scrolling = false;
			}
		});
		//user submitted the form openDialog for support
		this.session.on(`signal:${Signal.FORMSUBMITTED}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);
			if (!isMyOwnConnection) {
				this.actionService.openDialog(this.translateService.translate('Form Submit'), 'From has been submitted by '+data.sendby, true);
			}
		});
		//submitted the form on customer or support leave
		this.session.on(`signal:${Signal.FORMSUBMITONLEAVE}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);
			//this.submitForm(this);
			// if (!isMyOwnConnection) {
			// this.actionService.openDialog(this.translateService.translate('Form Submit'), 'From has been submitted by user', true);
			// }
		});
	}

	// SUbscribe to Paramters recieved from backend
	protected subscribetoParameters() {
		this.usertypeSub = this.libService.usertype.subscribe((usertype: string) => {
			this.usertype = usertype;
		});

		this.questionsicdcSub = this.libService.questionsicdc.subscribe((questionsicdc: string) => {
			this.questionsicdc = questionsicdc;
		});

		this.editicdcSub = this.libService.editicdc.subscribe((editicdc: boolean) => {
			this.editicdc = editicdc;
		});
		this.displayicdcSub = this.libService.displayicdc.subscribe((displayicdc: boolean) => {
			this.displayicdc = displayicdc;
		});

		this.titleicdcSub = this.libService.titleicdc.subscribe((titleicdc: string) => {
			this.titleicdc = titleicdc;
		});
	}

	//Function To Check if checkbox is checked or not
	isOptionSelected(questionId: number, option: string): boolean {
		const control = this.form.get(questionId.toString());
		const value = control.value;
		if (value[option] == true) {
			return true;
		} else {
			return false;
		}
	}

	//Function to Update CheckBox Questions Selection Array
	updateSelectedAnswers(nquestionId: number, option: string, checked: boolean) {
		const questionId = String(nquestionId);
		this.selectedOptions = this.selectedAnswers[questionId];
		if (!this.selectedOptions) {
			this.selectedOptions = [];
			this.selectedAnswers[questionId] = this.selectedOptions;
		}

		const control = this.form.get(questionId);
		const value = control.value;

		// Add the selected option to the selectedOptions array
		if (checked) {
			value[option] = true; // Set the specific option to true
			this.selectedOptions.push(option);
		} else {
			// Remove the deselected option from the selectedOptions array
			value[option] = false;
			const index = this.selectedOptions.indexOf(option);
			if (index !== -1) {
				this.selectedOptions.splice(index, 1);
			}
		}

		control.setValue(value);

		//Send a Signal to other participant with checkbox data
		const data: object = {
			qid: questionId,
			options: option,
			check: checked
		};

		this.openviduService.sendSignal(Signal.SUPPORT, undefined, data);
	}

	updateValueOfTextQuestion(nquestionId: number) {
		let qindex = this.myTextQuestionMap.get(nquestionId);
		const questionId = String(nquestionId);
		const control = this.form.get(questionId);
		const value = control.value;

		const data: object = {
			qid: questionId,
			value: value,
			index: qindex + 1
		};
		this.openviduService.sendSignal(Signal.SUPPORT, undefined, data);
	}

	

	close() {
		if (this.panelService.isQuestionPanelOpened() == true) {
			this.panelService.togglePanel(PanelType.QUESTIONS);
		}
	}

	closeForm() {
		this.close();
		const data = {
			data: 'Close Form Signal'
		};
		this.openviduService.sendSignal(Signal.CLOSE, undefined, data);
	}

	//Function to calculate scroll Value
	onScroll() {
		if (this.scrolling == true) {
			const formElement = this.formScroll.nativeElement;
			const scrollTop = formElement.scrollTop;

			const data = {
				data: 'Scroll Signal',
				scrollValue: scrollTop,
				scrolling: false
			};
			this.openviduService.sendSignal(Signal.SCROLL, undefined, data);
		}

		setTimeout(() => {
			this.scrolling = true;
		}, 300);
	}
}
