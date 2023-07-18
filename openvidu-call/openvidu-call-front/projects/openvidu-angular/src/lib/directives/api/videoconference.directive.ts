import { AfterViewInit, Directive, ElementRef, Input, OnDestroy, OnInit } from '@angular/core';
import { CaptionsLangOption } from '../../models/caption.model';
import { CaptionService } from '../../services/caption/caption.service';
import { OpenViduAngularConfigService } from '../../services/config/openvidu-angular.config.service';
import { TranslateService } from '../../services/translate/translate.service';

/**
 * The **minimal** directive applies a minimal UI hiding all controls except for cam and mic.
 *
 * It is only available for {@link VideoconferenceComponent}.
 *
 *  Default: `false`
 *
 * @example
 * <ov-videoconference [minimal]="true"></ov-videoconference>
 */
@Directive({
	selector: 'ov-videoconference[minimal]'
})
export class MinimalDirective implements OnDestroy {
	/**
	 * @ignore
	 */
	@Input() set minimal(value: boolean) {
		this.update(value);
	}

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**
	 * @ignore
	 */
	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update(false);
	}

	/**
	 * @ignore
	 */
	update(value: boolean) {
		if (this.libService.minimal.getValue() !== value) {
			this.libService.minimal.next(value);
		}
	}
}

/**
 * The **lang** directive allows set the UI language to a default language.
 *
 * It is only available for {@link VideoconferenceComponent}.
 *
 * **Default:** English `en`
 *
 * **Available:**
 *
 * * English: `en`
 * * Spanish: `es`
 * * German: `de`
 * * French: `fr`
 * * Chinese: `cn`
 * * Hindi: `hi`
 * * Italian: `it`
 * * Japanese: `ja`
 * * Netherlands: `nl`
 * * Portuguese: `pt`
 *
 * @example
 * <ov-videoconference [lang]="'es'"></ov-videoconference>
 */
@Directive({
	selector: 'ov-videoconference[lang]'
})
export class LangDirective implements OnDestroy {
	/**
	 * @ignore
	 */
	@Input() set lang(value: string) {
		this.update(value);
	}

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private translateService: TranslateService) {}

	/**
	 * @ignore
	 */
	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update('en');
	}

	/**
	 * @ignore
	 */
	update(value: string) {
		this.translateService.setLanguage(value);
	}
}

/**
 * The **captionsLang** directive allows specify the deafult language that OpenVidu will try to recognise.
 *
 * It is only available for {@link VideoconferenceComponent}.
 *
 * It must be a valid [BCP-47](https://tools.ietf.org/html/bcp47) language tag like "en-US" or "es-ES".
 *
 *
 * **Default:** English `en-US`
 *
 * **Available:**
 *
 * * English: `en-US`
 * * Spanish: `es-ES`
 * * German: `de-DE`
 * * French: `fr-FR`
 * * Chinese: `zh-CN`
 * * Hindi: `hi-IN`
 * * Italian: `it-IT`
 * * Japanese: `jp-JP`
 * * Portuguese: `pt-PT`
 *
 * @example
 * <ov-videoconference [captionsLang]="'es-ES'"></ov-videoconference>
 */
@Directive({
	selector: 'ov-videoconference[captionsLang]'
})
export class CaptionsLangDirective implements OnDestroy {
	/**
	 * @ignore
	 */
	@Input() set captionsLang(value: string) {
		this.update(value);
	}

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private captionService: CaptionService) {}

	/**
	 * @ignore
	 */
	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update('en-US');
	}

	/**
	 * @ignore
	 */
	update(value: string) {
		this.captionService.setLanguage(value);
	}
}

/**
 * The **captionsLangOptions** directive allows to set the language options for the captions.
 * It will override the languages provided by default.
 * This propety is an array of objects which must comply with the {@link CaptionsLangOption} interface.
 *
 * It is only available for {@link VideoconferenceComponent}.
 *
 * Default: ```
 * [
 * 	{ name: 'English', ISO: 'en-US' },
 * 	{ name: 'Español', ISO: 'es-ES' },
 * 	{ name: 'Deutsch', ISO: 'de-DE' },
 * 	{ name: 'Français', ISO: 'fr-FR' },
 * 	{ name: '中国', ISO: 'zh-CN' },
 * 	{ name: 'हिन्दी', ISO: 'hi-IN' },
 * 	{ name: 'Italiano', ISO: 'it-IT' },
 * 	{ name: 'やまと', ISO: 'jp-JP' },
 * 	{ name: 'Português', ISO: 'pt-PT' }
 * ]```
 *
 * @example
 * <ov-videoconference [captionsLangOptions]="[{name:'Spanish', ISO: 'es-ES'}]"></ov-videoconference>
 */
@Directive({
	selector: 'ov-videoconference[captionsLangOptions]'
})
export class CaptionsLangOptionsDirective implements OnDestroy {
	/**
	 * @ignore
	 */
	@Input() set captionsLangOptions(value: CaptionsLangOption[]) {
		this.update(value);
	}

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private captionService: CaptionService) {}

	/**
	 * @ignore
	 */
	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update(undefined);
	}

	/**
	 * @ignore
	 */
	update(value: CaptionsLangOption[] | undefined) {
		this.captionService.setLanguageOptions(value);
	}
}

/**
 * The **participantName** directive sets the participant name. It can be useful for aplications which doesn't need the prejoin page.
 *
 * It is only available for {@link VideoconferenceComponent}.
 *
 * @example
 * <ov-videoconference [participantName]="'OpenVidu'"></ov-videoconference>
 */
@Directive({
	selector: 'ov-videoconference[participantName]'
})
export class ParticipantNameDirective implements OnInit {
	// Avoiding update participantName dynamically.
	// The participantName must be updated from UI
	/**
	 * @ignore
	 */
	@Input() participantName: string;

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**
	 * @ignore
	 */
	ngOnInit(): void {
		this.update(this.participantName);
	}

	/**
	 * @ignore
	 */
	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update('');
	}

	/**
	 * @ignore
	 */
	update(value: string) {
		this.libService.participantName.next(value);
	}
}
/**
 * The **displayTicker** directive allows show/hide the tricker.
 *
 * Default: `true`
 *
 * It can be used in the parent element {@link VideoconferenceComponent} specifying the name of the `toolbar` component:
 *
 * @example
 * <ov-videoconference [displayTickerValue]="false"></ov-videoconference>
 *
 */
@Directive({
	selector: 'ov-videoconference[displayTickerName]'
})
export class DisplayTickerDirective implements OnInit {
	/**
	 * @ignore
	 */
	@Input() displayTickerName: string;

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**
	 * @ignore
	 */
	ngOnInit(): void {
		this.update(this.displayTickerName);
	}

	/**
	 * @ignore
	 */
	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update('');
	}

	/**
	 * @ignore
	 */
	update(value: string) {
		this.libService.displayTickerValue.next(value);
	}
}
/**
 * The **displayTicker** directive allows show/hide the tricker.
 *
 * Default: `true`
 *
 * It can be used in the parent element {@link VideoconferenceComponent} specifying the name of the `toolbar` component:
 *
 * @example
 * <ov-videoconference [displayTicker]="false"></ov-videoconference>
 *
 */
@Directive({
	selector: 'ov-videoconference[displayTicker]'
})
export class displayTickerDirective implements AfterViewInit, OnDestroy {
	/**
	 * @ignore
	 */
	@Input() set displayTicker(value: boolean) {
		this.displayTickerValue = value;
		this.update(this.displayTickerValue);
	}

	private displayTickerValue: boolean = true;

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	ngAfterViewInit() {
		this.update(this.displayTickerValue);
	}

	ngOnDestroy(): void {
		this.clear();
	}
	private clear() {
		this.displayTickerValue = true;
		this.update(true);
	}

	private update(value: boolean) {
		if (this.libService.displayTicker.getValue() !== value) {
			this.libService.displayTicker.next(value);
		}
	}
}
/**
 * The **prejoin** directive allows show/hide the prejoin page for selecting media devices.
 *
 * It is only available for {@link VideoconferenceComponent}.
 *
 * Default: `true`
 *
 * @example
 * <ov-videoconference [prejoin]="false"></ov-videoconference>
 */
@Directive({
	selector: 'ov-videoconference[prejoin]'
})
export class PrejoinDirective implements OnDestroy {
	/**
	 * @ignore
	 */
	@Input() set prejoin(value: boolean) {
		this.update(value);
	}

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**
	 * @ignore
	 */
	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update(true);
	}

	/**
	 * @ignore
	 */
	update(value: boolean) {
		if (this.libService.prejoin.getValue() !== value) {
			this.libService.prejoin.next(value);
		}
	}
}

/**
 * The **videoMuted** directive allows to join the session with camera muted/unmuted.
 *
 * It is only available for {@link VideoconferenceComponent}.
 *
 * Default: `false`
 *
 *
 * @example
 * <ov-videoconference [videoMuted]="true"></ov-videoconference>
 */
@Directive({
	selector: 'ov-videoconference[videoMuted]'
})
export class VideoMutedDirective implements OnDestroy {
	/**
	 * @ignore
	 */
	@Input() set videoMuted(value: boolean) {
		this.update(value);
	}

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**
	 * @ignore
	 */
	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update(false);
	}

	/**
	 * @ignore
	 */
	update(value: boolean) {
		if (this.libService.videoMuted.getValue() !== value) {
			this.libService.videoMuted.next(value);
		}
	}
}

/**
 * The **audioMuted** directive allows to join the session with microphone muted/unmuted.
 *
 * It is only available for {@link VideoconferenceComponent}.
 *
 * Default: `false`
 *
 * @example
 * <ov-videoconference [audioMuted]="true"></ov-videoconference>
 */

@Directive({
	selector: 'ov-videoconference[audioMuted]'
})
export class AudioMutedDirective implements OnDestroy {
	/**
	 * @ignore
	 */
	@Input() set audioMuted(value: boolean) {
		this.update(value);
	}

	/**
	 * @ignore
	 */
	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	ngOnDestroy(): void {
		this.clear();
	}

	/**
	 * @ignore
	 */
	clear() {
		this.update(false);
	}

	/**
	 * @ignore
	 */
	update(value: boolean) {
		if (this.libService.audioMuted.getValue() !== value) {
			this.libService.audioMuted.next(value);
		}
	}
}

/**
 * The **usertype** directive allows get user type.
 *
 * Default: `true`
 *
 * It can be used in the parent element {@link VideoconferenceComponent} specifying the name of the `toolbar` component:
 *
 * @example
 * <ov-videoconference [usertype]="false"></ov-videoconference>
 *
 */
@Directive({
	selector: 'ov-videoconference[usertype]'
})


export class usertypeDirective implements OnInit {
	/**

     * @ignore

     */

	@Input() usertype: string;

	/**

     * @ignore

     */

	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**

     * @ignore

     */

	ngOnInit(): void {
		this.update(this.usertype);
	}

	/**

     * @ignore

     */

	ngOnDestroy(): void {
		this.clear();
	}

	/**

     * @ignore

     */

	clear() {
		this.update('');
	}

	/**

     * @ignore

     */

	update(value: string) {
		this.libService.usertype.next(value);
	}
}

/**
 * The **questionsicdc** directive allows get list of question in form.
 *
 * Default: ` `
 *
 * It can be used in the parent element {@link VideoconferenceComponent} specifying the name of the `questionpanel` component:
 *
 * @example
 * <ov-videoconference [questionsicdc]=""></ov-videoconference>
 *
 */
@Directive({
	selector: 'ov-videoconference[questionsicdc]'
})


export class questionsicdcDirective implements OnInit {
	/**

     * @ignore

     */

	@Input() questionsicdc: string;

	/**

     * @ignore

     */

	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**

     * @ignore

     */

	ngOnInit(): void {
		this.update(this.questionsicdc);
	}

	/**

     * @ignore

     */

	ngOnDestroy(): void {
		this.clear();
	}

	/**

     * @ignore

     */

	clear() {
		this.update('');
	}

	/**

     * @ignore

     */

	update(value: string) {
		this.libService.questionsicdc.next(value);
	}
}



/**
 * The **displayicdc** directive allows to set to display icdc or not.
 *
 * Default: `true`
 *
 * It can be used in the parent element {@link VideoconferenceComponent} specifying the name of the `toolbar` component:
 *
 * @example
 * <ov-videoconference [displayicdc]="false"></ov-videoconference>
 *
 */
@Directive({
	selector: 'ov-videoconference[displayicdc]'
})
export class displayicdcDirective implements OnInit {
	/**

     * @ignore

     */

	@Input() displayicdc: boolean;

	/**

     * @ignore

     */

	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**

     * @ignore

     */

	ngOnInit(): void {
		this.update(this.displayicdc);
	}

	/**

     * @ignore

     */

	ngOnDestroy(): void {
		this.clear();
	}

	/**

     * @ignore

     */

	clear() {
		this.update(false);
	}

	/**

     * @ignore

     */

	update(value: boolean) {
		this.libService.displayicdc.next(value);
	}
}

/**
 * The **editicdc** directive allows get edit type.
 *
 * Default: `true`
 *
 * It can be used in the parent element {@link VideoconferenceComponent} specifying the name of the `toolbar` component:
 *
 * @example
 * <ov-videoconference [editicdc]="false"></ov-videoconference>
 *
 */
@Directive({
	selector: 'ov-videoconference[editicdc]'
})
export class editicdcDirective implements OnInit {
	/**

     * @ignore

     */

	@Input() editicdc: boolean;

	/**

     * @ignore

     */

	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**

     * @ignore

     */

	ngOnInit(): void {
		this.update(this.editicdc);
	}

	/**

     * @ignore

     */

	ngOnDestroy(): void {
		this.clear();
	}

	/**

     * @ignore

     */

	clear() {
		this.update(false);
	}

	/**

     * @ignore

     */

	update(value: boolean) {
		this.libService.editicdc.next(value);
	}
}

/**
 * The **titleicdc** directive allows get icdc titlee.
 *
 * Default: `true`
 *
 * It can be used in the parent element {@link VideoconferenceComponent} specifying the name of the `toolbar` component:
 *
 * @example
 * <ov-videoconference [titleicdc]="false"></ov-videoconference>
 *
 */
@Directive({
	selector: 'ov-videoconference[titleicdc]'
})
export class titleicdcDirective implements OnInit {
	/**

     * @ignore

     */

	@Input() titleicdc: string;

	/**

     * @ignore

     */

	constructor(public elementRef: ElementRef, private libService: OpenViduAngularConfigService) {}

	/**

     * @ignore

     */

	ngOnInit(): void {
		this.update(this.titleicdc);
	}

	/**

     * @ignore

     */

	ngOnDestroy(): void {
		this.clear();
	}

	/**

     * @ignore

     */

	clear() {
		this.update('');
	}

	/**

     * @ignore

     */

	update(value: string) {
		this.libService.titleicdc.next(value);
	}
}
