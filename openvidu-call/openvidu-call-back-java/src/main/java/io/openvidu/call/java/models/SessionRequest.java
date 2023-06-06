package io.openvidu.call.java.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.JsonObject;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.Recording;
import org.apache.hc.core5.http.HttpEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionRequest {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String sessionId;
  private String sessionName;
  private String userId;
  private Integer userMaxSession;
  private String accountId;
  private Integer accountMaxSession;
  private String participantName;
  private String totalParticipants="5";
  private Settings settings;
  private String sessionKey;
  private Date creationDate;
  private Date expDate;
  private Connection cameraToken;
  private Connection screenToken;
  private List<Recording> recordings;
  private String type;

  public SessionRequest(JsonObject json) throws ParseException {
    this.sessionKey=json.get("sessionKey").getAsString();
    this.sessionId=json.get("sessionId").getAsString();
    this.sessionName=json.get("sessionName").getAsString();
    this.userId=json.get("userId").getAsString();
    this.accountId=json.get("accountId").getAsString();
    this.userMaxSession=json.get("userMaxSessions").getAsInt();
    this.accountMaxSession=json.get("accountMaxSessions").getAsInt();
    this.participantName=json.get("participantName").getAsString();
    this.totalParticipants=json.get("totalParticipants").getAsString();
    String settingsJson=json.get("settings").toString();
    this.settings= new Settings(settingsJson);
    this.type=json.get("type").toString();
    String creationDateStr = json.get("creationDate").getAsString();
    this.creationDate = DATE_FORMAT.parse(creationDateStr);
    String expDateStr = json.get("expDate").getAsString();
    this.expDate = DATE_FORMAT.parse(expDateStr);

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

  public Integer getUserMaxSession() {
    return userMaxSession;
  }

  public void setUserMaxSession(Integer userMaxSession) {
    this.userMaxSession = userMaxSession;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public Integer getAccountMaxSession() {
    return accountMaxSession;
  }

  public void setAccountMaxSession(Integer accountMaxSession) {
    this.accountMaxSession = accountMaxSession;
  }

  public String getParticipantName() {
    return participantName;
  }

  public void setParticipantName(String participantName) {
    this.participantName = participantName;
  }

  public String getTotalParticipants() {
    return totalParticipants;
  }

  public void setTotalParticipants(String totalParticipants) {
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "SessionRequest{" +
      "sessionId='" + sessionId + '\'' +
      ", sessionName='" + sessionName + '\'' +
      ", userId='" + userId + '\'' +
      ", userMaxSession=" + userMaxSession +
      ", accountId='" + accountId + '\'' +
      ", accountMaxSession=" + accountMaxSession +
      ", participantName='" + participantName + '\'' +
      ", totalParticipants='" + totalParticipants + '\'' +
      ", settings=" + settings +
      ", sessionKey='" + sessionKey + '\'' +
      ", creationDate=" + creationDate +
      ", expDate=" + expDate +
      ", cameraToken=" + cameraToken +
      ", screenToken=" + screenToken +
      ", recordings=" + recordings +
      ", type='" + type + '\'' +
      '}';
  }
}
