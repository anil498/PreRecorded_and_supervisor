package io.openvidu.call.java.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import java.util.Arrays;

public class SessionRequest {
  private String sessionUniqueId;
  private String accountId;
  private String userId;
  private Boolean isRecording;
  private Boolean isBroadCasting;
  private String recordingMode;
  private Boolean isSessionCreator;
  private Boolean isScreenSharing;
  private Boolean isChatEnabled;
  private Boolean allowTransCoding;
  private Integer maxActiveSessions;
  private Integer maxParticipants=0;
  private Integer maxDuration;
  private Integer maxUserActiveSessions;
  private Integer maxUserParticipants;
  private Integer maxUserDuration;
  private String recordingExpiredTime;
  private String sessionExpiredTime;
  private Byte[] sessionLogo;
  private Boolean showSessionId;
  private String participantName;

  public SessionRequest(JsonObject jsonObject) throws JsonProcessingException {
    this.isRecording=jsonObject.get("isRecording").getAsBoolean();
    this.isBroadCasting=jsonObject.get("isBroadCasting").getAsBoolean();
    this.recordingMode=jsonObject.get("recordingMode").getAsString();
//    this.isSessionCreator=jsonObject.get("isSessionCreator").getAsBoolean();
    this.isScreenSharing=jsonObject.get("isScreenSharing").getAsBoolean();
    this.isChatEnabled=jsonObject.get("isChatEnabled").getAsBoolean();
    this.allowTransCoding=jsonObject.get("allowTransCoding").getAsBoolean();
    this.maxActiveSessions=jsonObject.get("maxActiveSessions").getAsInt();
    this.maxParticipants=jsonObject.get("maxParticipants").getAsInt();
    this.maxDuration=jsonObject.get("maxDuration").getAsInt();
    this.maxUserActiveSessions=jsonObject.get("maxUserActiveSessions").getAsInt();
    this.maxUserParticipants=jsonObject.get("maxUserParticipants").getAsInt();
    this.maxUserDuration=jsonObject.get("maxUserDuration").getAsInt();
    this.accountId=jsonObject.get("accountId").getAsString();
    this.userId=jsonObject.get("userId").getAsString();
    this.showSessionId=jsonObject.get("showSessionId").getAsBoolean();
    ObjectMapper objectMapper=new ObjectMapper();
    this.sessionLogo=objectMapper.readValue(jsonObject.get("sessionLogo").toString(),Byte[].class);
  }

  public Boolean getRecording() {
    return isRecording;
  }

  public void setRecording(Boolean recording) {
    isRecording = recording;
  }

  public Boolean getBroadCasting() {
    return isBroadCasting;
  }

  public void setBroadCasting(Boolean broadCasting) {
    isBroadCasting = broadCasting;
  }

  public String getRecordingMode() {
    return recordingMode;
  }

  public void setRecordingMode(String recordingMode) {
    this.recordingMode = recordingMode;
  }

  public Boolean getSessionCreator() {
    return isSessionCreator;
  }

  public void setSessionCreator(Boolean isSessionCreator) {
    this.isSessionCreator = isSessionCreator;
  }

  public Boolean getScreenSharing() {
    return isScreenSharing;
  }

  public void setScreenSharing(Boolean screenSharing) {
    isScreenSharing = screenSharing;
  }

  public Boolean getChatEnabled() {
    return isChatEnabled;
  }

  public void setChatEnabled(Boolean chatEnabled) {
    isChatEnabled = chatEnabled;
  }

  public Boolean getAllowTransCoding() {
    return allowTransCoding;
  }

  public void setAllowTransCoding(Boolean allowTransCoding) {
    this.allowTransCoding = allowTransCoding;
  }

  public Integer getMaxActiveSessions() {
    return maxActiveSessions;
  }

  public void setMaxActiveSessions(Integer maxActiveSessions) {
    this.maxActiveSessions = maxActiveSessions;
  }

  public Integer getMaxParticipants() {
    return maxParticipants;
  }

  public void setMaxParticipants(Integer maxParticipants) {
    this.maxParticipants = maxParticipants;
  }

  public Integer getMaxDuration() {
    return maxDuration;
  }

  public void setMaxDuration(Integer maxDuration) {
    this.maxDuration = maxDuration;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Integer getMaxUserActiveSessions() {
    return maxUserActiveSessions;
  }

  public void setMaxUserActiveSessions(Integer maxUserActiveSessions) {
    this.maxUserActiveSessions = maxUserActiveSessions;
  }

  public Integer getMaxUserParticipants() {
    return maxUserParticipants;
  }

  public void setMaxUserParticipants(Integer maxUserParticipants) {
    this.maxUserParticipants = maxUserParticipants;
  }

  public Integer getMaxUserDuration() {
    return maxUserDuration;
  }

  public void setMaxUserDuration(Integer maxUserDuration) {
    this.maxUserDuration = maxUserDuration;
  }

  public String getSessionUniqueId() {
    return sessionUniqueId;
  }

  public void setSessionUniqueId(String sessionUniqueId) {
    this.sessionUniqueId = sessionUniqueId;
  }

  public String getRecordingExpiredTime() {
    return recordingExpiredTime;
  }

  public void setRecordingExpiredTime(String recordingExpiredTime) {
    this.recordingExpiredTime = recordingExpiredTime;
  }

  public String getSessionExpiredTime() {
    return sessionExpiredTime;
  }

  public void setSessionExpiredTime(String sessionExpiredTime) {
    this.sessionExpiredTime = sessionExpiredTime;
  }

  public Byte[] getSessionLogo() {
    return sessionLogo;
  }

  public void setSessionLogo(Byte[] sessionLogo) {
    this.sessionLogo = sessionLogo;
  }

  public Boolean getShowSessionId() {
    return showSessionId;
  }

  public String getParticipantName() {
    return participantName;
  }

  public void setParticipantName(String participantName) {
    this.participantName = participantName;
  }

  public void setShowSessionId(Boolean showSessionId) {
    this.showSessionId = showSessionId;
  }

  @Override
  public String toString() {
    return "SessionRequest{" +
      "sessionUniqueId='" + sessionUniqueId + '\'' +
      ", accountId='" + accountId + '\'' +
      ", userId='" + userId + '\'' +
      ", isRecording=" + isRecording +
      ", isBroadCasting=" + isBroadCasting +
      ", recordingMode='" + recordingMode + '\'' +
      ", isSessionCreator=" + isSessionCreator +
      ", isScreenSharing=" + isScreenSharing +
      ", isChatEnabled=" + isChatEnabled +
      ", allowTransCoding=" + allowTransCoding +
      ", maxActiveSessions=" + maxActiveSessions +
      ", maxParticipants=" + maxParticipants +
      ", maxDuration=" + maxDuration +
      ", maxUserActiveSessions=" + maxUserActiveSessions +
      ", maxUserParticipants=" + maxUserParticipants +
      ", maxUserDuration=" + maxUserDuration +
      ", recordingExpiredTime='" + recordingExpiredTime + '\'' +
      ", sessionExpiredTime='" + sessionExpiredTime + '\'' +
      ", sessionLogo=" + Arrays.toString(sessionLogo) +
      ", showSessionId=" + showSessionId +
      ", participantName='" + participantName + '\'' +
      '}';
  }
}
