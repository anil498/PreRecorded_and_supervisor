package io.openvidu.call.java.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {
  private int duration=0;
  private boolean showLogo=true;
  private Object logo;
  private String description="";
  private Boolean displayTicker=false;
  private Boolean displayTimer=false;
  private Boolean recording=false;
  private Object recordingDetails;
  private Boolean screenShare=false;
  private Boolean isScreenShareWithAudio=false;
  private Boolean isAutoFullScreen=true;
  private Boolean waitForModerator=false;
  private Boolean chat=false;
  private Boolean activitiesButton=false;
  private Boolean participantsButton=false;
  private Boolean floatingLayout=false;
  private String layoutType;
  private int layoutNumber=0;
  private Boolean supervisor=false;
  private Boolean preRecorded=false;
  private Object preRecordedDetails;
  private Boolean broadcast=false;
  private String landingPage="https://in.linkedin.com/company/mcarbon";
  private String fileUrl;
  private Boolean icdc;
  private Boolean displayIcdc;
  private Boolean editIcdc;
  private Object titleIcdc;

  
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    if(description!=null)
    this.description = description;
  }

  public Boolean getDisplayTicker() {
    return displayTicker;
  }

  public void setDisplayTicker(Boolean displayTicker) {
    if(displayTicker!=null)
    this.displayTicker = displayTicker;
  }

  public Boolean getDisplayTimer() {
    return displayTimer;
  }

  public void setDisplayTimer(Boolean displayTimer) {
    if(displayTimer!=null)
    this.displayTimer = displayTimer;
  }

  public Boolean getRecording() {
    return recording;
  }

  public void setRecording(Boolean recording) {
    if(recording!=null)
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
    if(screenShare!=null)
    this.screenShare = screenShare;
  }

  public Boolean getWaitForModerator() {
    return waitForModerator;
  }

  public void setWaitForModerator(Boolean waitForModerator) {
    if(waitForModerator!=null)
    this.waitForModerator = waitForModerator;
  }

  public Boolean getChat() {
    return chat;
  }

  public void setChat(Boolean chat) {
    if(chat!=null)
    this.chat = chat;
  }

  public Boolean getActivitiesButton() {
    return activitiesButton;
  }

  public void setActivitiesButton(Boolean activitiesButton) {
    if(activitiesButton!=null)
    this.activitiesButton = activitiesButton;
  }

  public Boolean getParticipantsButton() {
    return participantsButton;
  }

  public void setParticipantsButton(Boolean participantsButton) {
    if(participantsButton!=null)
    this.participantsButton = participantsButton;
  }

  public Boolean getFloatingLayout() {
    return floatingLayout;
  }

  public void setFloatingLayout(Boolean floatingLayout) {
    if(floatingLayout!=null)
    this.floatingLayout = floatingLayout;
  }

  public Boolean getSupervisor() {
    return supervisor;
  }

  public void setSupervisor(Boolean supervisor) {
    if(supervisor!=null)
    this.supervisor = supervisor;
  }

  public Boolean getPreRecorded() {
    return preRecorded;
  }

  public void setPreRecorded(Boolean preRecorded) {
    if(preRecorded!=null)
    this.preRecorded = preRecorded;
  }

  public Object getPreRecordedDetails() {
    return preRecordedDetails;
  }

  public void setPreRecordedDetails(Object preRecordedDetails) {
    if(preRecordedDetails!=null)
    this.preRecordedDetails = preRecordedDetails;
  }

  public Boolean getBroadcast() {
    return broadcast;
  }

  public void setBroadcast(Boolean broadcast) {
    if(broadcast!=null)
    this.broadcast = broadcast;
  }

  public boolean isShowLogo() {
    return showLogo;
  }

  public void setShowLogo(boolean showLogo) {
    this.showLogo = showLogo;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getLandingPage() {
    return landingPage;
  }

  public void setLandingPage(String landingPage) {
    if(landingPage!=null)
    this.landingPage = landingPage;
  }

  public String getLayoutType() {
    return layoutType;
  }

  public void setLayoutType(String layoutType) {
    if(layoutType!=null)
    this.layoutType = layoutType;
  }

  public int getLayoutNumber() {
    return layoutNumber;
  }

  public void setLayoutNumber(int layoutNumber) {
    this.layoutNumber = layoutNumber;
  }

  public String getFileUrl() {
    return fileUrl;
  }

  public void setFileUrl(String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public Boolean getScreenShareWithAudio() {
    return isScreenShareWithAudio;
  }

  public void setScreenShareWithAudio(Boolean screenShareWithAudio) {
    isScreenShareWithAudio = screenShareWithAudio;
  }

  public Boolean getAutoFullScreen() {
    return isAutoFullScreen;
  }

  public void setAutoFullScreen(Boolean autoFullScreen) {
    isAutoFullScreen = autoFullScreen;
  }

  public void setDescription(String description) {
    if(description!=null)
    this.description = description;
  }

  public Boolean getDisplayTicker() {
    return displayTicker;
  }

  public void setDescription(String description) {
    if(description!=null)
    this.description = description;
  }

  public Boolean getDisplayTicker() {
    return displayTicker;
  }

  public void setDescription(String description) {
    if(description!=null)
    this.description = description;
  }

  public Boolean getDisplayTicker() {
    return displayTicker;
  }

  public Boolean getIcdc() {
    return this.icdc;
  }

  public void setIcdc(Boolean icdc) {
    this.icdc = icdc;
  }

  public Object getTitleIcdc() {
    return this.titleIcdc;
  }

  public void setTitleIcdc(Object title) {
    this.titleIcdc = title;
  }

  public Boolean getDisplayIcdc() {
    return this.displayIcdc;
  }

  public void setDisplayIcdc(Boolean displayicdc) {
    this.displayIcdc = displayicdc;
  }

  public Boolean getEditIcdc() {
    return this.editIcdc;
  }

  public void setEditIcdc(Boolean editicdc) {
    this.editIcdc = editicdc;
  }

  @Override
  public String toString() {
    return "Settings{" +
            "duration=" + duration +
            ", showLogo=" + showLogo +
            ", logo=" + logo +
            ", description='" + description + '\'' +
            ", displayTicker=" + displayTicker +
            ", displayTimer=" + displayTimer +
            ", recording=" + recording +
            ", recordingDetails=" + recordingDetails +
            ", screenShare=" + screenShare +
            ", isScreenShareWithAudio=" + isScreenShareWithAudio +
            ", isAutoFullScreen=" + isAutoFullScreen +
            ", waitForModerator=" + waitForModerator +
            ", chat=" + chat +
            ", activitiesButton=" + activitiesButton +
            ", participantsButton=" + participantsButton +
            ", floatingLayout=" + floatingLayout +
            ", layoutType='" + layoutType + '\'' +
            ", layoutNumber=" + layoutNumber +
            ", supervisor=" + supervisor +
            ", preRecorded=" + preRecorded +
            ", preRecordedDetails=" + preRecordedDetails +
            ", broadcast=" + broadcast +
            ", landingPage='" + landingPage + '\'' +
            ", fileUrl='" + fileUrl + '\'' +
            ", icdc='" + icdc + '\'' +
            ", displayIcdc='" + displayIcdc + '\'' +
            ", editIcdc='" + editIcdc + '\'' +
            ", titleIcdc='" + titleIcdc + '\'' +
            '}';
  }
}
