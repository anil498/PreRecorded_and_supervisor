package io.openvidu.call.java.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionCallback {
  private String sessionId;
  private String uniqueSessionId;
  private String accountId;
  private String userId;
  private String startTime;
  private String endTime;
  private String duration;
  private String reason;
  private List<String> connectionIds;
  private String recordingUrl;
  private String recordingDuration;
  private String recordingSize;

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
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

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public List<String> getConnectionIds() {
    return connectionIds;
  }

  public void setConnectionIds(List<String> connectionIds) {
    this.connectionIds = connectionIds;
  }

  public String getRecordingUrl() {
    return recordingUrl;
  }

  public void setRecordingUrl(String recordingUrl) {
    this.recordingUrl = recordingUrl;
  }

  public String getRecordingDuration() {
    return recordingDuration;
  }

  public void setRecordingDuration(String recordingDuration) {
    this.recordingDuration = recordingDuration;
  }

  public String getRecordingSize() {
    return recordingSize;
  }

  public void setRecordingSize(String recordingSize) {
    this.recordingSize = recordingSize;
  }

  public String getUniqueSessionId() {
    return uniqueSessionId;
  }

  public void setUniqueSessionId(String uniqueSessionId) {
    this.uniqueSessionId = uniqueSessionId;
  }

  @Override
  public String toString() {
    return "SessionCallback{" +
      "sessionId='" + sessionId + '\'' +
      ", uniqueSessionId='" + uniqueSessionId + '\'' +
      ", accountId='" + accountId + '\'' +
      ", userId='" + userId + '\'' +
      ", startTime='" + startTime + '\'' +
      ", endTime='" + endTime + '\'' +
      ", duration='" + duration + '\'' +
      ", reason='" + reason + '\'' +
      ", connectionIds=" + connectionIds +
      ", recordingUrl='" + recordingUrl + '\'' +
      ", recordingDuration='" + recordingDuration + '\'' +
      ", recordingSize='" + recordingSize + '\'' +
      '}';
  }
}
