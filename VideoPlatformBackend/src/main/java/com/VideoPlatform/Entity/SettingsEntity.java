package com.VideoPlatform.Entity;



public class SettingsEntity {

    private Integer duration;
    private byte logo;
    private Boolean moderators = false;
    private String description;
    private Boolean displayTicker;
    private Boolean displayTimer;
    private Boolean recording;
    private Object recordingDetails;
    private Boolean screenShare;
    private Boolean waitForModerator;
    private Boolean chat;
    private Boolean activitiesButton;
    private Boolean participantsButton;
    private Boolean floatingLayout;
    private Boolean supervisor;
    private Boolean preRecorded;
    private String preRecordedDetails;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public byte getLogo() {
        return logo;
    }

    public void setLogo(byte logo) {
        this.logo = logo;
    }

    public Boolean getModerators() {
        return moderators;
    }

    public void setModerators(Boolean moderators) {
        this.moderators = moderators;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDisplayTicker() {
        return displayTicker;
    }

    public void setDisplayTicker(Boolean displayTicker) {
        this.displayTicker = displayTicker;
    }

    public Boolean getDisplayTimer() {
        return displayTimer;
    }

    public void setDisplayTimer(Boolean displayTimer) {
        this.displayTimer = displayTimer;
    }

    public Boolean getRecording() {
        return recording;
    }

    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    public Object getRecordingDetails() {
        return recordingDetails;
    }

    public void setRecordingDetails(Object recordingDetails) {
        this.recordingDetails = recordingDetails;
    }

    public Boolean getScreenShare() {
        return screenShare;
    }

    public void setScreenShare(Boolean screenShare) {
        this.screenShare = screenShare;
    }

    public Boolean getWaitForModerator() {
        return waitForModerator;
    }

    public void setWaitForModerator(Boolean waitForModerator) {
        this.waitForModerator = waitForModerator;
    }

    public Boolean getChat() {
        return chat;
    }

    public void setChat(Boolean chat) {
        this.chat = chat;
    }

    public Boolean getActivitiesButton() {
        return activitiesButton;
    }

    public void setActivitiesButton(Boolean activitiesButton) {
        this.activitiesButton = activitiesButton;
    }

    public Boolean getParticipantsButton() {
        return participantsButton;
    }

    public void setParticipantsButton(Boolean participantsButton) {
        this.participantsButton = participantsButton;
    }

    public Boolean getFloatingLayout() {
        return floatingLayout;
    }

    public void setFloatingLayout(Boolean floatingLayout) {
        this.floatingLayout = floatingLayout;
    }

    public Boolean getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Boolean supervisor) {
        this.supervisor = supervisor;
    }

    public Boolean getPreRecorded() {
        return preRecorded;
    }

    public void setPreRecorded(Boolean preRecorded) {
        this.preRecorded = preRecorded;
    }

    public String getPreRecordedDetails() {
        return preRecordedDetails;
    }

    public void setPreRecordedDetails(String preRecordedDetails) {
        this.preRecordedDetails = preRecordedDetails;
    }

    @Override
    public String toString() {
        return "SettingsEntity{" +
                "duration=" + duration +
                ", logo=" + logo +
                ", moderators=" + moderators +
                ", description='" + description + '\'' +
                ", displayTicker=" + displayTicker +
                ", displayTimer=" + displayTimer +
                ", recording=" + recording +
                ", recordingDetails='" + recordingDetails + '\'' +
                ", screenShare=" + screenShare +
                ", waitForModerator=" + waitForModerator +
                ", chat=" + chat +
                ", activitiesButton=" + activitiesButton +
                ", participantsButton=" + participantsButton +
                ", floatigLayout=" + floatingLayout +
                ", supervisor=" + supervisor +
                ", preRecorded=" + preRecorded +
                ", preRecordedDetails='" + preRecordedDetails + '\'' +
                '}';
    }
}