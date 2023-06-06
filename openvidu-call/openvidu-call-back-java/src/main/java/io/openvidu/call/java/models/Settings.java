package io.openvidu.call.java.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {
  private Integer duration;
  private boolean showLogo;
  private Object logo;
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
  private Boolean broadcast=false;

  public Settings(String settingsJson) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode jsonNode = mapper.readTree(settingsJson);
      this.duration = jsonNode.get("duration").asInt();
//      this.showLogo = jsonNode.get("showLogo").asBoolean();
      this.logo=jsonNode.get("logo");
      this.chat=jsonNode.get("chat").asBoolean();
      this.recording=jsonNode.get("recording").asBoolean();
      this.recordingDetails=jsonNode.get("recordingDetails");
      this.screenShare=jsonNode.get("screenShare").asBoolean();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Object getLogo() {
    return logo;
  }

  public void setLogo(Object logo) {
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

  public Boolean getBroadcast() {
    return broadcast;
  }

  public void setBroadcast(Boolean broadcast) {
    this.broadcast = broadcast;
  }

  public boolean isShowLogo() {
    return showLogo;
  }

  public void setShowLogo(boolean showLogo) {
    this.showLogo = showLogo;
  }
}
