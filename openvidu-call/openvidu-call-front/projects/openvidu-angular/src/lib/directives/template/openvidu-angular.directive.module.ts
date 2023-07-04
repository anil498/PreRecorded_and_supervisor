import { NgModule } from '@angular/core';
import {
	ChatPanelDirective,
	LayoutDirective,
	PanelDirective,
	ParticipantPanelItemElementsDirective,
	ParticipantPanelItemDirective,
	ParticipantsPanelDirective,
	QuestionPanelDirective,
	StreamDirective,
	ToolbarAdditionalButtonsDirective,
	ToolbarDirective,
	ToolbarAdditionalPanelButtonsDirective,
	AdditionalPanelsDirective,
	ActivitiesPanelDirective,
	BackgroundEffectsPanelDirective
} from './openvidu-angular.directive';

@NgModule({
	declarations: [
		ChatPanelDirective,
		LayoutDirective,
		PanelDirective,
		AdditionalPanelsDirective,
		ParticipantPanelItemDirective,
		ParticipantsPanelDirective,
		QuestionPanelDirective,
		StreamDirective,
		ToolbarDirective,
		ToolbarAdditionalButtonsDirective,
		ToolbarAdditionalPanelButtonsDirective,
		ParticipantPanelItemElementsDirective,
		ActivitiesPanelDirective,
		BackgroundEffectsPanelDirective
	],
	exports: [
		ChatPanelDirective,
		LayoutDirective,
		PanelDirective,
		AdditionalPanelsDirective,
		ParticipantPanelItemDirective,
		ParticipantsPanelDirective,
		QuestionPanelDirective,
		StreamDirective,
		ToolbarDirective,
		ToolbarAdditionalButtonsDirective,
		ToolbarAdditionalPanelButtonsDirective,
		ParticipantPanelItemElementsDirective,
		ActivitiesPanelDirective,
		BackgroundEffectsPanelDirective
	]
})
export class OpenViduAngularDirectiveModule {}
