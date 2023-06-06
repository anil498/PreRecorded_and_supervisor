package io.openvidu.call.java.core;

import io.openvidu.call.java.models.SessionProperty;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.Recording;
import org.springframework.data.annotation.Id;
import java.io.Serializable;

public class SessionContext implements Serializable {
  private static final long serialVersionUID = -4530028918010566689L;
  private SessionProperty sessionPropertyRequest;
  private Recording recordingRequest;
  private io.openvidu.java.client.Session sessionObject;
  private Connection connectionObject;
  @Id
  private String sessionUniqueId;
  private String sessionKey;
  private String callType;
  private int participantJoined;
  public static class Builder {
    private SessionProperty sessionPropertyRequest;
    private Recording recordingRequest;
    private io.openvidu.java.client.Session sessionObject;
    private Connection connectionObject;
    private String sessionUniqueId;
    private String sessionKey;
    private String callType;
    private int participantJoined;
    public Builder sessionRequest (SessionProperty sessionPropertyRequest){
    this.sessionPropertyRequest = sessionPropertyRequest;
    return this;
    }
    public Builder sessionObject (io.openvidu.java.client.Session sessionObject){
      this.sessionObject = sessionObject;
      return this;
    }
    public Builder connectionObject (Connection connectionObject){
      this.connectionObject = connectionObject;
      return this;
    }
    public Builder recordingRequest (Recording recordingRequest){
      this.recordingRequest=recordingRequest;
      return this;
    }
    public Builder sessionUniqueID (String sessionUniqueId){
      this.sessionUniqueId=sessionUniqueId;
      return this;
    }
    public Builder callType (String callType){
      this.callType=callType;
      return this;
    }
    public Builder participantJoined (int participantJoined){
      this.participantJoined=participantJoined;
      return this;
    }
    public Builder sessionKey(String sessionKey){
      this.sessionKey=sessionKey;
      return this;
    }
    public SessionContext build () {
    SessionContext sessionContext = new SessionContext();
    sessionContext.setSessionRequest(this.sessionPropertyRequest);
    sessionContext.setSessionObject(this.sessionObject);
    sessionContext.setConnectionObject(this.connectionObject);
    sessionContext.setRecordingRequest(this.recordingRequest);
    sessionContext.setSessionUniqueId(this.sessionUniqueId);
    sessionContext.setCallType(this.callType);
    sessionContext.setParticipantJoined(this.participantJoined);
    sessionContext.setSessionKey(this.sessionKey);
    return sessionContext;
    }
  }
  private SessionContext(){}

  public SessionProperty getSessionRequest() {
    return sessionPropertyRequest;
  }

  public void setSessionRequest(SessionProperty sessionPropertyRequest) {
    this.sessionPropertyRequest = sessionPropertyRequest;
  }

  public Recording getRecordingRequest() {
    return recordingRequest;
  }

  public void setRecordingRequest(Recording recordingRequest) {
    this.recordingRequest = recordingRequest;
  }

  public io.openvidu.java.client.Session getSessionObject() {
    return sessionObject;
  }

  public void setSessionObject(io.openvidu.java.client.Session sessionObject) {
    this.sessionObject = sessionObject;
  }

  public Connection getConnectionObject() {
    return connectionObject;
  }

  public void setConnectionObject(Connection connectionObject) {
    this.connectionObject = connectionObject;
  }

  public String getSessionUniqueId() {
    return sessionUniqueId;
  }

  public void setSessionUniqueId(String sessionUniqueId) {
    this.sessionUniqueId = sessionUniqueId;
  }

  public String getCallType() {
    return callType;
  }

  public void setCallType(String callType) {
    this.callType = callType;
  }

  public int getParticipantJoined() {
    return participantJoined;
  }

  public void setParticipantJoined(int participantJoined) {
    this.participantJoined = participantJoined;
  }

  public String getSessionKey() {
    return sessionKey;
  }

  public void setSessionKey(String sessionKey) {
    this.sessionKey = sessionKey;
  }

  @Override
  public String toString() {
    return "SessionContext{" +
      "sessionRequest=" + sessionPropertyRequest +
      ", recordingRequest=" + recordingRequest +
      ", sessionObject=" + sessionObject +
      ", connectionObject=" + connectionObject +
      ", sessionUniqueId='" + sessionUniqueId + '\'' +
      ", sessionKey='" + sessionKey + '\'' +
      ", callType='" + callType + '\'' +
      ", participantJoined=" + participantJoined +
      '}';
  }
}
