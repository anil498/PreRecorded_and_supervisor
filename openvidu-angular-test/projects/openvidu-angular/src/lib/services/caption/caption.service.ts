import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { CaptionsLangOption } from '../../models/caption.model';
import { StorageService } from '../storage/storage.service';

/**
 * @internal
 */
@Injectable({
	providedIn: 'root'
})
export class CaptionService {
	private langs: CaptionsLangOption [] = [
		{ name: 'English', ISO: 'en-US' },
		{ name: 'Español', ISO: 'es-ES' },
		{ name: 'Deutsch', ISO: 'de-DE' },
		{ name: 'Français', ISO: 'fr-FR' },
		{ name: '中国', ISO: 'zh-CN' },
		{ name: 'हिन्दी', ISO: 'hi-IN' },
		{ name: 'Italiano', ISO: 'it-IT' },
		{ name: 'やまと', ISO: 'jp-JP' },
		{ name: 'Português', ISO: 'pt-PT' }
	];
	captionLangSelected: { name: string; ISO: string };
	captionLangObs: Observable<{ name: string; ISO: string }>;
	private _captionLang: Subject<{ name: string; ISO: string }> = new Subject();
	private captionsEnabled: boolean = false;

	constructor(private storageService: StorageService) {
		this.updateLangSelected();
		this.captionLangObs = this._captionLang.asObservable();

	}

	setLanguageOptions(options: CaptionsLangOption [] | undefined) {
		if(options && options.length > 0) {
			this.langs = options;
			this.updateLangSelected();
		}
	}

	setCaptionsEnabled(value: boolean) {
		this.captionsEnabled = value;
	}

	areCaptionsEnabled(): boolean {
		return this.captionsEnabled;
	}

	setLanguage(lang: string) {
		const newLang = this.langs.find((l) => l.ISO === lang);
		if (!!newLang && newLang.ISO !== this.captionLangSelected.ISO) {
			this.captionLangSelected = newLang;
			this.storageService.setCaptionLang(lang);
			this._captionLang.next(this.captionLangSelected);
		}
	}

	getLangSelected(): { name: string; ISO: string } {
		return this.captionLangSelected;
	}

	getCaptionLanguages(): { name: string; ISO: string }[] {
		return this.langs;
	}

	private updateLangSelected(): void {
		const iso = this.storageService.getCaptionsLang();
		const lang = this.langs.find((lang) => lang.ISO === iso);
		if (iso && lang) {
			this.captionLangSelected = lang;
		} else {
			this.captionLangSelected = this.langs[0];
		}
	}
}
