package io.openvidu.call.java.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {
  private int duration=0;
  private boolean showLogo=false;
  private Object logo;
  private Boolean moderators = false;
  private String description="";
  private Boolean displayTicker=false;
  private Boolean displayTimer=false;
  private Boolean recording=false;
  private Object recordingDetails;
  private Boolean screenShare=false;
  private Boolean waitForModerator=false;
  private Boolean chat=false;
  private Boolean activitiesButton=false;
  private Boolean participantsButton=false;
  private Boolean floatingLayout=false;
  private Boolean supervisor=false;
  private Boolean preRecorded=false;
  private Object preRecordedDetails;
  private Boolean broadcast=false;
  private String landingPage="https://in.linkedin.com/company/mcarbon";
  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public  Object getLogo() {
    return logo;
  }

  public void setLogo(Object logo) {
    this.logo = logo;
  }

  public Boolean getModerators() {
    return moderators;
  }

  public void setModerators(Boolean moderators) {
    if(moderators!=null)
    this.moderators = moderators;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    if(moderators!=null)
    this.description = description;
  }

  public Boolean getDisplayTicker() {
    return displayTicker;
  }

  public void setDisplayTicker(Boolean displayTicker) {
    if(moderators!=null)
    this.displayTicker = displayTicker;
  }

  public Boolean getDisplayTimer() {
    return displayTimer;
  }

  public void setDisplayTimer(Boolean displayTimer) {
    if(moderators!=null)
    this.displayTimer = displayTimer;
  }

  public Boolean getRecording() {
    return recording;
  }

  public void setRecording(Boolean recording) {
    if(moderators!=null)
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
    if(moderators!=null)
    this.screenShare = screenShare;
  }

  public Boolean getWaitForModerator() {
    return waitForModerator;
  }

  public void setWaitForModerator(Boolean waitForModerator) {
    if(moderators!=null)
    this.waitForModerator = waitForModerator;
  }

  public Boolean getChat() {
    return chat;
  }

  public void setChat(Boolean chat) {
    if(moderators!=null)
    this.chat = chat;
  }

  public Boolean getActivitiesButton() {
    return activitiesButton;
  }

  public void setActivitiesButton(Boolean activitiesButton) {
    if(moderators!=null)
    this.activitiesButton = activitiesButton;
  }

  public Boolean getParticipantsButton() {
    return participantsButton;
  }

  public void setParticipantsButton(Boolean participantsButton) {
    if(moderators!=null)
    this.participantsButton = participantsButton;
  }

  public Boolean getFloatingLayout() {
    return floatingLayout;
  }

  public void setFloatingLayout(Boolean floatingLayout) {
    if(moderators!=null)
    this.floatingLayout = floatingLayout;
  }

  public Boolean getSupervisor() {
    return supervisor;
  }

  public void setSupervisor(Boolean supervisor) {
    if(moderators!=null)
    this.supervisor = supervisor;
  }

  public Boolean getPreRecorded() {
    return preRecorded;
  }

  public void setPreRecorded(Boolean preRecorded) {
    if(moderators!=null)
    this.preRecorded = preRecorded;
  }

  public Object getPreRecordedDetails() {
    return preRecordedDetails;
  }

  public void setPreRecordedDetails(Object preRecordedDetails) {
    this.preRecordedDetails = preRecordedDetails;
  }

  public Boolean getBroadcast() {
    return broadcast;
  }

  public void setBroadcast(Boolean broadcast) {
    if(moderators!=null)
    this.broadcast = broadcast;
  }

  public boolean isShowLogo() {
    return showLogo;
  }

  public void setShowLogo(boolean showLogo) {
    if(moderators!=null)
    this.showLogo = showLogo;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getLandingPage() {
    return landingPage;
  }

  public void setLandingPage(String landingPage) {
    if(moderators!=null)
    this.landingPage = landingPage;
  }

  @Override
  public String toString() {
    return "Settings{" +
      "duration=" + duration +
      ", showLogo=" + showLogo +
      ", logo=" + logo +
      ", moderators=" + moderators +
      ", description='" + description + '\'' +
      ", displayTicker=" + displayTicker +
      ", displayTimer=" + displayTimer +
      ", recording=" + recording +
      ", recordingDetails=" + recordingDetails +
      ", screenShare=" + screenShare +
      ", waitForModerator=" + waitForModerator +
      ", chat=" + chat +
      ", activitiesButton=" + activitiesButton +
      ", participantsButton=" + participantsButton +
      ", floatingLayout=" + floatingLayout +
      ", supervisor=" + supervisor +
      ", preRecorded=" + preRecorded +
      ", preRecordedDetails='" + preRecordedDetails + '\'' +
      ", broadcast=" + broadcast +
      '}';
  }
}
