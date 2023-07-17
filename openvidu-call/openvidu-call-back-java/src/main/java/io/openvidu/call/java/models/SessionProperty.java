package io.openvidu.call.java.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.openvidu.call.java.util.CustomDateDeserializer;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.Recording;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionProperty {
  private String sessionId;
  private String sessionName;
  private String userId;
  private int userMaxSessions=0;
  private String accountId;
  private int accountMaxSessions=0;
  private String participantName="Customer";
  private int totalParticipants=2;
  private Settings settings;
  private String sessionKey;
  @JsonDeserialize(using = CustomDateDeserializer.class)
  private Date creationDate;
  @JsonDeserialize(using = CustomDateDeserializer.class)
  private Date expDate;
  private String cameraToken="";
  private String screenToken="";
  private List<Recording> recordings;
  private String type="Customer";
  private String recordingMode="MANUAL"; // for auto recording need to set {ALWAYS}
  private Boolean isSessionExpired=false;
  private Boolean hold=false;

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getSessionName() {
    return sessionName;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getUserMaxSessions() {
    return userMaxSessions;
  }

  public void setUserMaxSessions(int userMaxSessions) {
    this.userMaxSessions = userMaxSessions;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public int getAccountMaxSessions() {
    return accountMaxSessions;
  }

  public void setAccountMaxSessions(int accountMaxSessions) {
    this.accountMaxSessions = accountMaxSessions;
  }

  public String getParticipantName() {
    return participantName;
  }

  public void setParticipantName(String participantName) {
    if(participantName!=null && participantName.length()>0)
    this.participantName = participantName;
  }

  public int getTotalParticipants() {
    return totalParticipants;
  }

  public void setTotalParticipants(int totalParticipants) {
    this.totalParticipants = totalParticipants;
  }

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public String getSessionKey() {
    return sessionKey;
  }

  public void setSessionKey(String sessionKey) {
    this.sessionKey = sessionKey;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getExpDate() {
    return expDate;
  }

  public void setExpDate(Date expDate) {
    this.expDate = expDate;
  }

  public String getCameraToken() {
    return cameraToken;
  }

  public void setCameraToken(String cameraToken) {
    this.cameraToken = cameraToken;
  }

  public String getScreenToken() {
    return screenToken;
  }

  public void setScreenToken(String screenToken) {
    this.screenToken = screenToken;
  }

  public List<Recording> getRecordings() {
    return recordings;
  }

  public void setRecordings(List<Recording> recordings) {
    this.recordings = recordings;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    if(type!=null && type.length()>0)
    this.type = type;
  }

  public String getRecordingMode() {
    return recordingMode;
  }

  public void setRecordingMode(String recordingMode) {
    if(recordingMode!=null && recordingMode.length()>0)
    this.recordingMode = recordingMode;
  }

  public Boolean getSessionExpired() {
    return isSessionExpired;
  }

  public void setSessionExpired(Boolean sessionExpired) {
    if(sessionExpired!=null)
    isSessionExpired = sessionExpired;
  }

  public Boolean getHold() {
    return hold;
  }

  public void setHold(Boolean hold) {
    this.hold = hold;
  }

  @Override
  public String toString() {
    return "SessionProperty{" +
            "sessionId='" + sessionId + '\'' +
            ", sessionName='" + sessionName + '\'' +
            ", userId='" + userId + '\'' +
            ", userMaxSessions=" + userMaxSessions +
            ", accountId='" + accountId + '\'' +
            ", accountMaxSessions=" + accountMaxSessions +
            ", participantName='" + participantName + '\'' +
            ", totalParticipants=" + totalParticipants +
            ", settings=" + settings +
            ", sessionKey='" + sessionKey + '\'' +
            ", creationDate=" + creationDate +
            ", expDate=" + expDate +
            ", cameraToken='" + cameraToken + '\'' +
            ", screenToken='" + screenToken + '\'' +
            ", recordings=" + recordings +
            ", type='" + type + '\'' +
            ", recordingMode='" + recordingMode + '\'' +
            ", isSessionExpired=" + isSessionExpired +
            ", hold=" + hold +
            '}';
  }
}
