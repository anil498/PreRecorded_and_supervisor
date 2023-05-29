package io.openvidu.call.java.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import java.util.Arrays;

public class SessionRequest {
  private String sessionName;
  private String sessionUniqueId;
  private String accountId;
  private String userId;
  private Boolean isRecording;
  private Boolean isBroadCasting=false;
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
  private Boolean showSessionId=false;
  private String participantName;
  private String sessionKey;
  private String sessionSupportKey;
  private String callType;
  private String videoUri;

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

  public String getSessionName() {
    return sessionName;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public String getSessionKey() {
    return sessionKey;
  }

  public void setSessionKey(String sessionKey) {
    this.sessionKey = sessionKey;
  }

  public String getSessionSupportKey() {
    return sessionSupportKey;
  }

  public void setSessionSupportKey(String sessionSupportKey) {
    this.sessionSupportKey = sessionSupportKey;
  }

  public String getCallType() {
    return callType;
  }

  public void setCallType(String callType) {
    this.callType = callType;
  }

  public String getVideoUri() {
    return videoUri;
  }

  public void setVideoUri(String videoUri) {
    this.videoUri = videoUri;
  }

  @Override
  public String toString() {
    return "SessionRequest{" +
      "sessionName='" + sessionName + '\'' +
      ", sessionUniqueId='" + sessionUniqueId + '\'' +
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
      ", sessionKey='" + sessionKey + '\'' +
      ", sessionSupportKey='" + sessionSupportKey + '\'' +
      ", callType='" + callType + '\'' +
      ", videoUri='" + videoUri + '\'' +
      '}';
  }
}
