package io.openvidu.call.java.models;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.Recording;

import java.util.Date;
import java.util.List;

public class SessionRequest {
  private String sessionId;
  private String sessionName;
  private String userId;
  private Integer max_user_session;
  private String accountId;
  private Integer max_account_session;
  private String participantName;
  private String participants;
  private Settings settings;
  private String sessionKey;
  private Date creationDate;
  private Date expDate;
  private Connection cameraToken;
  private Connection screenToken;
  private List<Recording> recordings;

  public SessionRequest(Object entity) {

  }

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

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getParticipantName() {
    return participantName;
  }

  public void setParticipantName(String participantName) {
    this.participantName = participantName;
  }

  public String getParticipants() {
    return participants;
  }

  public void setParticipants(String participants) {
    this.participants = participants;
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

  public Connection getCameraToken() {
    return cameraToken;
  }

  public void setCameraToken(Connection cameraToken) {
    this.cameraToken = cameraToken;
  }

  public Connection getScreenToken() {
    return screenToken;
  }

  public void setScreenToken(Connection screenToken) {
    this.screenToken = screenToken;
  }

  public List<Recording> getRecordings() {
    return recordings;
  }

  public void setRecordings(List<Recording> recordings) {
    this.recordings = recordings;
  }

  public Integer getMax_user_session() {
    return max_user_session;
  }

  public void setMax_user_session(Integer max_user_session) {
    this.max_user_session = max_user_session;
  }

  public Integer getMax_account_session() {
    return max_account_session;
  }

  public void setMax_account_session(Integer max_account_session) {
    this.max_account_session = max_account_session;
  }
}
