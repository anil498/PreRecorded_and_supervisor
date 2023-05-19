package io.openvidu.call.java.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionWebhook {
  private String event;
  private String timestamp;
  private String uniqueSessionId;
  private String sessionId;
  private String startTime;
  private String duration;
  private String reason;
  private String connectionId;
  private String ip;
  private String platform;
  private String location;
  private String clientData;
  private String serverData;
  private String streamId;
  private String connection;
  private String receivingFrom;
  private String videoSource;
  private String videoFrameRate;
  private String videoDimensions;
  private String audioEnabled;
  private String videoEnabled;
  private String id;
  private String name;
  private String outputMode;
  private String resolution;
  private String recordingLayout;
  private String hasAudio;
  private String hasVideo;
  private String size;
  private String status;

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
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

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getClientData() {
    return clientData;
  }

  public void setClientData(String clientData) {
    this.clientData = clientData;
  }

  public String getServerData() {
    return serverData;
  }

  public void setServerData(String serverData) {
    this.serverData = serverData;
  }

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }

  public String getReceivingFrom() {
    return receivingFrom;
  }

  public void setReceivingFrom(String receivingFrom) {
    this.receivingFrom = receivingFrom;
  }

  public String getVideoSource() {
    return videoSource;
  }

  public void setVideoSource(String videoSource) {
    this.videoSource = videoSource;
  }

  public String getVideoFrameRate() {
    return videoFrameRate;
  }

  public void setVideoFrameRate(String videoFrameRate) {
    this.videoFrameRate = videoFrameRate;
  }

  public String getVideoDimensions() {
    return videoDimensions;
  }

  public void setVideoDimensions(String videoDimensions) {
    this.videoDimensions = videoDimensions;
  }

  public String getAudioEnabled() {
    return audioEnabled;
  }

  public void setAudioEnabled(String audioEnabled) {
    this.audioEnabled = audioEnabled;
  }

  public String getVideoEnabled() {
    return videoEnabled;
  }

  public void setVideoEnabled(String videoEnabled) {
    this.videoEnabled = videoEnabled;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOutputMode() {
    return outputMode;
  }

  public void setOutputMode(String outputMode) {
    this.outputMode = outputMode;
  }

  public String getResolution() {
    return resolution;
  }

  public void setResolution(String resolution) {
    this.resolution = resolution;
  }

  public String getRecordingLayout() {
    return recordingLayout;
  }

  public void setRecordingLayout(String recordingLayout) {
    this.recordingLayout = recordingLayout;
  }

  public String getHasAudio() {
    return hasAudio;
  }

  public void setHasAudio(String hasAudio) {
    this.hasAudio = hasAudio;
  }

  public String getHasVideo() {
    return hasVideo;
  }

  public void setHasVideo(String hasVideo) {
    this.hasVideo = hasVideo;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUniqueSessionId() {
    return uniqueSessionId;
  }

  public void setUniqueSessionId(String uniqueSessionId) {
    this.uniqueSessionId = uniqueSessionId;
  }

  @Override
  public String toString() {
    return "SessionWebhook{" +
      "event='" + event + '\'' +
      ", timestamp='" + timestamp + '\'' +
      ", uniqueSessionId='" + uniqueSessionId + '\'' +
      ", sessionId='" + sessionId + '\'' +
      ", startTime='" + startTime + '\'' +
      ", duration='" + duration + '\'' +
      ", reason='" + reason + '\'' +
      ", connectionId='" + connectionId + '\'' +
      ", ip='" + ip + '\'' +
      ", platform='" + platform + '\'' +
      ", location='" + location + '\'' +
      ", clientData='" + clientData + '\'' +
      ", serverData='" + serverData + '\'' +
      ", streamId='" + streamId + '\'' +
      ", connection='" + connection + '\'' +
      ", receivingFrom='" + receivingFrom + '\'' +
      ", videoSource='" + videoSource + '\'' +
      ", videoFrameRate='" + videoFrameRate + '\'' +
      ", videoDimensions='" + videoDimensions + '\'' +
      ", audioEnabled='" + audioEnabled + '\'' +
      ", videoEnabled='" + videoEnabled + '\'' +
      ", id='" + id + '\'' +
      ", name='" + name + '\'' +
      ", outputMode='" + outputMode + '\'' +
      ", resolution='" + resolution + '\'' +
      ", recordingLayout='" + recordingLayout + '\'' +
      ", hasAudio='" + hasAudio + '\'' +
      ", hasVideo='" + hasVideo + '\'' +
      ", size='" + size + '\'' +
      ", status='" + status + '\'' +
      '}';
  }
}
