import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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
	@ViewChild('chatScroll') chatScroll: ElementRef;

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
	scrolling:boolean=true;
	usertype: string = '';
	editicdc: boolean;
	titleicdc: string='';

	private usertypeSub: Subscription;
	private editicdcSub: Subscription;
	private titleicdcSub: Subscription;

	constructor(
		private panelService: PanelService,
		private cd: ChangeDetectorRef,
		private http: HttpClient,
		protected loggerSrv: LoggerService,
		private formBuilder: FormBuilder,
		protected openviduService: OpenViduService,
		protected participantService: ParticipantService,
		private libService: OpenViduAngularConfigService
	) {
		this.log = this.loggerSrv.get('Question Panel');
		this.session = this.openviduService.getSession();

		this.form = this.formBuilder.group({});

		this.http.get<any>('assets/json/data.json').subscribe((response) => {
			this.questions = response.Question_data;

			this.questions.forEach((question) => {
				if (question.type === 'checkbox') {
					const checkboxOptions = question.meta.reduce((options, option) => {
						options[option] = false; // Set all options to false initially
						return options;
					}, {});
					this.selectedAnswers[question.q_id] = [];
					this.form.addControl(question.q_id, this.formBuilder.control(checkboxOptions));
				} else {
					this.form.addControl(question.q_id, this.formBuilder.control('', Validators.required));
				}

				this.name = question.q_id;
			});

			this.log.d('My Role -' + this.usertype);
			this.log.d('Edit ICDC -' + this.editicdc);
			this.log.d('Title ICDC -' + this.titleicdc);


			if (!this.editicdc) {
					this.form.disable();
					this.isSubmit = false;
				}else{
					this.isSubmit = true;
				}
				
			

			if (this.panelService.isQuestionPanelOpened()) {
				// this.scrollToBottom();
				this.cd.markForCheck();
			}
		});
	}

	isOptionSelected(questionId: string, option: string): boolean {
		const control = this.form.get(questionId);
		const value = control.value;
		if (value[option] == true) {
			return true;
		} else {
			return false;
		}
	}

	ngOnInit(): void {
		this.subscribetouser();
		this.subscribeToSignal();
		this.form.valueChanges.subscribe((value) => {
			// Handle the real-time form values here
			this.log.d(value);

			if (!this.form.pristine) {
				this.openviduService.sendSignal(Signal.SUPPORT, undefined, value);
			}
		});

	}

	ngAfterViewInit() {}


	protected subscribeToSignal() {
		this.session.on(`signal:${Signal.SUPPORT}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);

			if (!isMyOwnConnection) {
				this.log.d('Connection ID From Signal: ' + connectionId);
				this.log.d('Is the Signal for this id ? ' + !isMyOwnConnection);

				if (data.options) {

					this.log.d(data.options);
					const control = this.form.get(data.qid);
					const value = control.value;
					
					if (data.check == true) {
						value[data.options] = true; // Set the specific option to true
					} else {
						value[data.options] = false; // Set the specific option to true
					}
					control.setValue(value);
					this.form.markAsPristine();
				} else {
					this.log.w(data);
					this.form.patchValue(data);
					this.form.markAsPristine();
				}
			}
		});

		this.session.on(`signal:${Signal.CLOSE}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);
			if (!isMyOwnConnection) {
				this.log.d('Connection ID From Signal: ' + connectionId);
				this.log.d('Is the Signal for this id ? ' + !isMyOwnConnection);
				this.log.d('Recieved : ' + data);

				this.log.w('Panel Closing');
				this.close();
			}
		});


		this.session.on(`signal:${Signal.SCROLL}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);
			if (!isMyOwnConnection) {
				this.log.d('Connection ID From Signal: ' + connectionId);
				this.log.d('Is the Signal for this id ? ' + !isMyOwnConnection);
				this.log.d('Recieved : ' + data);

				const formElement = this.chatScroll.nativeElement;
				formElement.scrollTop=data.scrollValue;
				this.scrolling= false;
			}
		});
	}

	protected subscribetouser() {
		this.usertypeSub = this.libService.usertype.subscribe((usertype: string)=>{
			this.usertype = usertype;
		})

		this.editicdcSub = this.libService.editicdc.subscribe((editicdc: boolean)=>{
			this.editicdc = editicdc;
		})

		this.titleicdcSub = this.libService.titleicdc.subscribe((titleicdc: string)=>{
			this.titleicdc = titleicdc;
		})
	}

	// For CheckBox

	updateSelectedAnswers(questionId: string, option: string, checked: boolean) {
		this.selectedOptions = this.selectedAnswers[questionId];
		if (!this.selectedOptions) {
			this.selectedOptions = [];
			this.selectedAnswers[questionId] = this.selectedOptions;
		}

		const control = this.form.get(questionId);
		const value = control.value;
		value[option] = true; // Set the specific option to true
		control.setValue(value);

		if (checked) {
			// Add the selected option to the selectedOptions array
			this.selectedOptions.push(option);
		} else {
			// Remove the deselected option from the selectedOptions array
			const index = this.selectedOptions.indexOf(option);
			if (index !== -1) {
				this.selectedOptions.splice(index, 1);
			}
		}
		
		const data: object = {
			qid: questionId,
			options: option,
			check: checked
		};

		this.openviduService.sendSignal(Signal.SUPPORT, undefined, data);
	}

	submitForm() {
		this.log.d('Use form Data to Submit the Form');
		this.log.d(this.form.value);
		this.form.reset();
		this.selectedAnswers = [];
		this.close();

		const data = {
			data: 'Submit Form Signal'
		};
		this.openviduService.sendSignal(Signal.CLOSE, undefined, data);
	}

	close() {
		if(this.panelService.isQuestionPanelOpened()==true){
			this.panelService.togglePanel(PanelType.QUESTIONS);
		}
	}


	closeForm(){
		this.close();
		const data = {
			data: 'Close Form Signal'
		};
		this.openviduService.sendSignal(Signal.CLOSE, undefined, data);
	}

	onScroll() {

		if(this.scrolling == true){
		const formElement = this.chatScroll.nativeElement;
		const scrollTop = formElement.scrollTop;
		// Use the scrollTop value as needed

		this.log.d("Scroll Value : "+ scrollTop);

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
