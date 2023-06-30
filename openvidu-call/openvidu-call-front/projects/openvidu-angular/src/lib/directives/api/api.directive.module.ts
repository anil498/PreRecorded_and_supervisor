import { NgModule } from '@angular/core';
import { ActivitiesPanelRecordingActivityDirective } from './activities-panel.directive';
import { AdminLoginDirective, AdminRecordingsListDirective } from './admin.directive';
import { LogoDirective } from './internals.directive';
import { ParticipantPanelItemMuteButtonDirective } from './participant-panel-item.directive';
import { RecordingActivityRecordingErrorDirective, RecordingActivityRecordingsListDirective } from './recording-activity.directive';
import {
	StreamDisplayAudioDetectionDirective,
	StreamDisplayParticipantNameDirective,
	StreamSettingsButtonDirective,
	FloatingLayoutDirective,
	FloatingLayoutTypeDirective
} from './stream.directive';
import {
	ToolbarActivitiesPanelButtonDirective,
	ToolbarBackgroundEffectsButtonDirective,
	ToolbarCaptionsButtonDirective,
	ToolbarChatPanelButtonDirective,
	ToolbarDisplayLogoDirective,
	ToolbarDisplayLogoValueDirective,
	ToolbarDisplaySessionNameDirective,
	ToolbarLeaveButtonDirective,
	ToolbarParticipantsPanelButtonDirective,
	ToolbarRecordingButtonDirective,
	ToolbarScreenshareButtonDirective,
	ToolbarSettingsButtonDirective,
	ToolbarPublishVideoButtonDirective,
	ToolbarVideoControlButtonDirective,
	ToolbarDisplayTimereDirective,
	SessionDurationDirective,
	SessionNameDirective,
	ToolbarVideoFilePathDirective,
	ToolbarScreenShareWithAudioDirective,
	ToolbarAutoFullScreenDirective,
	ToolbarSupervisorButtonDirective
} from './toolbar.directive';
import {
	AudioMutedDirective,
	CaptionsLangDirective, CaptionsLangOptionsDirective, LangDirective, MinimalDirective,
	ParticipantNameDirective,
	displayTickerDirective,
	PrejoinDirective,
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
		VideoMutedDirective,
		AudioMutedDirective,
		ToolbarScreenshareButtonDirective,
		ToolbarPublishVideoButtonDirective,
		ToolbarVideoControlButtonDirective,
		ToolbarAutoFullScreenDirective,
		ToolbarBackgroundEffectsButtonDirective,
		ToolbarCaptionsButtonDirective,
		ToolbarLeaveButtonDirective,
		ToolbarRecordingButtonDirective,
		ToolbarParticipantsPanelButtonDirective,
		ToolbarChatPanelButtonDirective,
		ToolbarActivitiesPanelButtonDirective,
		ToolbarDisplaySessionNameDirective,
		ToolbarDisplayLogoDirective,
		ToolbarDisplayLogoValueDirective,
		ToolbarSettingsButtonDirective,
		ToolbarScreenShareWithAudioDirective,
		ToolbarSupervisorButtonDirective,
		StreamDisplayParticipantNameDirective,
		StreamDisplayAudioDetectionDirective,
		StreamSettingsButtonDirective,
		FloatingLayoutDirective,
		FloatingLayoutTypeDirective,
		LogoDirective,
		ParticipantPanelItemMuteButtonDirective,
		ParticipantNameDirective,
		DisplayTickerDirective,
		displayTickerDirective,
		ActivitiesPanelRecordingActivityDirective,
		RecordingActivityRecordingsListDirective,
		RecordingActivityRecordingErrorDirective,
		AdminRecordingsListDirective,
		AdminLoginDirective,
		SessionDurationDirective,
		SessionNameDirective,
		ToolbarDisplayTimereDirective,
		ToolbarVideoFilePathDirective
	],
	exports: [
		MinimalDirective,
		LangDirective,
		CaptionsLangOptionsDirective,
		CaptionsLangDirective,
		PrejoinDirective,
		VideoMutedDirective,
		AudioMutedDirective,
		ToolbarScreenshareButtonDirective,
		ToolbarPublishVideoButtonDirective,
		ToolbarVideoControlButtonDirective,
		ToolbarAutoFullScreenDirective,
		ToolbarBackgroundEffectsButtonDirective,
		ToolbarCaptionsButtonDirective,
		ToolbarLeaveButtonDirective,
		ToolbarRecordingButtonDirective,
		ToolbarParticipantsPanelButtonDirective,
		ToolbarChatPanelButtonDirective,
		ToolbarActivitiesPanelButtonDirective,
		ToolbarDisplaySessionNameDirective,
		ToolbarDisplayLogoDirective,
		ToolbarDisplayLogoValueDirective,
		ToolbarSettingsButtonDirective,
		ToolbarScreenShareWithAudioDirective,
		ToolbarSupervisorButtonDirective,
		StreamDisplayParticipantNameDirective,
		StreamDisplayAudioDetectionDirective,
		StreamSettingsButtonDirective,
		FloatingLayoutDirective,
		FloatingLayoutTypeDirective,
		LogoDirective,
		ParticipantPanelItemMuteButtonDirective,
		ParticipantNameDirective,
		DisplayTickerDirective,
		displayTickerDirective,
		ActivitiesPanelRecordingActivityDirective,
		RecordingActivityRecordingsListDirective,
		RecordingActivityRecordingErrorDirective,
		AdminRecordingsListDirective,
		AdminLoginDirective,
		SessionDurationDirective,
		SessionNameDirective,
		ToolbarDisplayTimereDirective,
		ToolbarVideoFilePathDirective
	]
})
export class ApiDirectiveModule {}
