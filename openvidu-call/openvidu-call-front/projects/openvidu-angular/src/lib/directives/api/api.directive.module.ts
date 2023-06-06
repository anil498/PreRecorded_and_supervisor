import { NgModule } from '@angular/core';
import { ActivitiesPanelRecordingActivityDirective } from './activities-panel.directive';
import { AdminLoginDirective, AdminRecordingsListDirective } from './admin.directive';
import { LogoDirective } from './internals.directive';
import { ParticipantPanelItemMuteButtonDirective } from './participant-panel-item.directive';
import { RecordingActivityRecordingErrorDirective, RecordingActivityRecordingsListDirective } from './recording-activity.directive';
import {
	StreamDisplayAudioDetectionDirective,
	StreamDisplayParticipantNameDirective,
	StreamSettingsButtonDirective
} from './stream.directive';
import {
	ToolbarActivitiesPanelButtonDirective,
	ToolbarBackgroundEffectsButtonDirective,
	ToolbarCaptionsButtonDirective,
	ToolbarChatPanelButtonDirective,
	ToolbarDisplayLogoDirective,
	ToolbarDisplaySessionNameDirective,
	ToolbarFullscreenButtonDirective,
	ToolbarLeaveButtonDirective,
	ToolbarParticipantsPanelButtonDirective,
	ToolbarRecordingButtonDirective,
	ToolbarScreenshareButtonDirective,
	ToolbarSettingsButtonDirective,
	ToolbarPublishVideoButtonDirective,
	ToolbarVideoControlButtonDirective
} from './toolbar.directive';
import {
	AudioMutedDirective,
	CaptionsLangDirective, CaptionsLangOptionsDirective, LangDirective, MinimalDirective,
	ParticipantNameDirective,
	displayTickerDirective,
	PrejoinDirective,
	ShowFullScreenButtonDirective,
	VideoMutedDirective,
	DisplayTickerDirective
} from './videoconference.directive';

@NgModule({
	declarations: [
		MinimalDirective,
		LangDirective,
		CaptionsLangOptionsDirective,
		CaptionsLangDirective,
		PrejoinDirective,
		ShowFullScreenButtonDirective,
		VideoMutedDirective,
		AudioMutedDirective,
		ToolbarScreenshareButtonDirective,
		ToolbarPublishVideoButtonDirective,
		ToolbarVideoControlButtonDirective,
		ToolbarFullscreenButtonDirective,
		ToolbarBackgroundEffectsButtonDirective,
		ToolbarCaptionsButtonDirective,
		ToolbarLeaveButtonDirective,
		ToolbarRecordingButtonDirective,
		ToolbarParticipantsPanelButtonDirective,
		ToolbarChatPanelButtonDirective,
		ToolbarActivitiesPanelButtonDirective,
		ToolbarDisplaySessionNameDirective,
		ToolbarDisplayLogoDirective,
		ToolbarSettingsButtonDirective,
		StreamDisplayParticipantNameDirective,
		StreamDisplayAudioDetectionDirective,
		StreamSettingsButtonDirective,
		LogoDirective,
		ParticipantPanelItemMuteButtonDirective,
		ParticipantNameDirective,
		DisplayTickerDirective,
		displayTickerDirective,
		ActivitiesPanelRecordingActivityDirective,
		RecordingActivityRecordingsListDirective,
		RecordingActivityRecordingErrorDirective,
		AdminRecordingsListDirective,
		AdminLoginDirective
	],
	exports: [
		MinimalDirective,
		LangDirective,
		CaptionsLangOptionsDirective,
		CaptionsLangDirective,
		PrejoinDirective,
		ShowFullScreenButtonDirective,
		VideoMutedDirective,
		AudioMutedDirective,
		ToolbarScreenshareButtonDirective,
		ToolbarPublishVideoButtonDirective,
		ToolbarVideoControlButtonDirective,
		ToolbarFullscreenButtonDirective,
		ToolbarBackgroundEffectsButtonDirective,
		ToolbarCaptionsButtonDirective,
		ToolbarLeaveButtonDirective,
		ToolbarRecordingButtonDirective,
		ToolbarParticipantsPanelButtonDirective,
		ToolbarChatPanelButtonDirective,
		ToolbarActivitiesPanelButtonDirective,
		ToolbarDisplaySessionNameDirective,
		ToolbarDisplayLogoDirective,
		ToolbarSettingsButtonDirective,
		StreamDisplayParticipantNameDirective,
		StreamDisplayAudioDetectionDirective,
		StreamSettingsButtonDirective,
		LogoDirective,
		ParticipantPanelItemMuteButtonDirective,
		ParticipantNameDirective,
		DisplayTickerDirective,
		displayTickerDirective,
		ActivitiesPanelRecordingActivityDirective,
		RecordingActivityRecordingsListDirective,
		RecordingActivityRecordingErrorDirective,
		AdminRecordingsListDirective,
		AdminLoginDirective
	]
})
export class ApiDirectiveModule {}
