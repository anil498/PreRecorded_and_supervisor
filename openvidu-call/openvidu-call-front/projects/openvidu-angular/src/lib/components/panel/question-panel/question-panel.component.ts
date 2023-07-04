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
	styleUrls: ['../panel.component.css', './question-panel.component.css'],
})
export class QuestionPanelComponent implements OnInit, AfterViewInit {
	/**
	 * @ignore
	 */
	@ViewChild('chatScroll') chatScroll: ElementRef;
	@ViewChild('checkboxRef', { static: true }) checkboxRef!: ElementRef<HTMLInputElement>;

	private log: ILogger;

	form: FormGroup;
	questions: any[] = [];
	answer: any;
	selectedAnswers: any[] = [];
	isSubmit: boolean = true;
	isFormDisabled: boolean =false;
	selectedOptions: any;
	name: string;
	ischecked: boolean=false;
	
	constructor(
		private panelService: PanelService,
		private cd: ChangeDetectorRef,
		private http: HttpClient,
		protected loggerSrv: LoggerService,
		private formBuilder: FormBuilder,
		protected openviduService: OpenViduService,
		protected participantService: ParticipantService
	) {
		this.log = this.loggerSrv.get('Question Panel');

		this.form = this.formBuilder.group({});

		this.http.get<any>('assets/json/data.json').subscribe((response) => {
			this.questions = response.Question_data;

			// this.questions.forEach((question) => {
			// 	if (question.type === 'checkbox') {
			// 		const checkboxOptions = question.meta.reduce((options, option) => {
			// 			options[option] = false; // Set all options to false initially
			// 			return options;
			// 		}, {});
			// 		this.selectedAnswers[question.q_id] = []; // Initialize selectedAnswers for checkbox question
			// 		this.form.addControl(question.q_id , this.formBuilder.control(Object.values(checkboxOptions)));
			// 	} else {
			// 		this.form.addControl(question.q_id, this.formBuilder.control('', Validators.required));
			// 	}
			// 	this.name=question.q_id;
			// });

			this.questions.forEach((question) => {

				if (question.type === 'checkbox') {
				
					const checkboxOptions = question.meta.reduce((options, option) => {
									options[option] = false; // Set all options to false initially
									return options;
								}, {});
								this.selectedAnswers[question.q_id] = [];
					this.form.addControl(question.q_id , this.formBuilder.control(checkboxOptions));
				
				}else{
					this.form.addControl(question.q_id, this.formBuilder.control('', Validators.required));
				}

				this.name=question.q_id;
			});
			

			

			  if(this.participantService.getMyRole() == 'MODERATOR'){

				if(this.isFormDisabled==true){
				this.form.disable();
			}
				this.isSubmit=false;
			}

			if (this.panelService.isQuestionPanelOpened()) {
				this.scrollToBottom();
				this.cd.markForCheck();
			}
		});
	}

	isOptionSelected(questionId: string, option: string): boolean {

		const control = this.form.get(questionId);
  	    const value = control.value;
		// this.log.d("Checkbox Value For :"+option+" is "+value[option]);
    	if(value[option] == true){
			return true;
		}else{
			return false;
		}
    	
		
	}

	ngOnInit(): void {
		  

		  this.subscribeToSignal();
		  this.form.valueChanges.subscribe((value) => {
			// Handle the real-time form values here
			console.log(value); 
			
			if(!this.form.pristine){
			this.openviduService.sendSignal(Signal.SUPPORT, undefined, value);
		}

		  });
		


		console.log("My Role -" +this.participantService.getMyRole() )

	}

	ngAfterViewInit() {
		// setTimeout(() => {
		// 	this.scrollToBottom();
		// }, 100);
	}

	scrollToBottom(): void {
		setTimeout(() => {
			try {
				this.chatScroll.nativeElement.scrollTop = this.chatScroll.nativeElement.scrollHeight;
			} catch (err) {}
		}, 20);
	}

	protected subscribeToSignal() {
		const session = this.openviduService.getSession();
		session.on(`signal:${Signal.SUPPORT}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);
			
			if(!isMyOwnConnection){
			this.log.d("Connection ID From Signal: "+connectionId);
			this.log.d("Is the Signal for this id ? "+!isMyOwnConnection);

			if(data.options){
				this.log.d(data.options);
				const control = this.form.get(data.qid);
  	    		const value = control.value;
				if(data.check == true){
					value[data.options] = true; // Set the specific option to true
				}else{
					value[data.options] = false; // Set the specific option to true
				}
				control.setValue(value);
				this.form.markAsPristine();

			}else{
			this.log.w(data);
			this.form.patchValue(data);
			this.form.markAsPristine();
		}

		}
		});

		session.on(`signal:${Signal.CLOSE}`, (event: any) => {
			const connectionId = event.from.connectionId;
			const data = JSON.parse(event.data);
			const isMyOwnConnection = this.openviduService.isMyOwnConnection(connectionId);
			if(!isMyOwnConnection){
				this.log.d("Connection ID From Signal: "+connectionId);
				this.log.d("Is the Signal for this id ? "+!isMyOwnConnection);
				this.log.d("Recieved : "+ data);

				this.close();
			}
		});
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
		// this.log.d(selectedOptions);
		// this.form.get(questionId).setValue(this.selectedOptions);

		const data: object =  {
			qid: questionId,
			options: option,
			check: checked
		}

		this.openviduService.sendSignal(Signal.SUPPORT, undefined, data);
		
	}
	
	submitForm() {
		this.log.d('Use form Data to Submit the Form');
		console.log(this.form.value);
		this.form.reset();
		this.selectedAnswers = [];
		this.close();

		const data = {
			data: "Submit Form Signal"
		}
		this.openviduService.sendSignal(Signal.CLOSE, undefined, data);
	}

	close() {
		this.panelService.togglePanel(PanelType.QUESTIONS);
	}
}
