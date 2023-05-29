package io.openvidu.call.java.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.openvidu.call.java.models.SessionRequest;
import io.openvidu.java.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
  @Autowired
  VideoPlatformService videoPlatformService;
  private static final Logger logger= LoggerFactory.getLogger(SessionService.class);
  public SessionRequest getSessionRequest(String accountIdToken, String userIdToken, String sessionIdKey) {
    SessionRequest sessionRequest = new SessionRequest();
    try {
//          String jsonString = "{\"accountId\":\"mcarbon\",\"userId\":\"admin\",\"isRecording\":\"true\",\"isBroadCasting\":\"false\",\"recordingMode\":\"custom\",\"isSessionCreator\":\"true\",\"isScreenSharing\":\"true\",\"isChatEnabled\":\"true\",\"allowTransCoding\":\"false\",\"maxActiveSessions\":\"5\",\"maxParticipants\":\"2\",\"maxDuration\":\"1000\",\"maxUserActiveSessions\":\"2\",\"maxUserParticipants\":\"2\",\"maxUserDuration\":\"1000\"}";
//          Gson gson=new Gson();
//          JsonObject jsonObject=gson.fromJson(jsonString.toString(),JsonObject.class);
      Gson gson = new Gson();
      String jsonString=null;
      try {
        jsonString = videoPlatformService.getVideoPlatformProperties(accountIdToken, userIdToken, sessionIdKey);
      }catch (Exception e){
        logger.error("Getting Exception while fetching details from videoPlatform {}",e);
      }
      logger.info(jsonString);
      JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
      JsonElement jsonElement = jsonObject.getAsJsonObject("session").get("sessionId");
      JsonElement jsonElement1 = jsonObject.getAsJsonObject("user").get("userId");
      JsonElement jsonElement2 = jsonObject.getAsJsonObject("user").get("accountId");
      JsonElement jsonElement3 = jsonObject.getAsJsonObject("session").get("sessionKey");
      JsonElement jsonElement4 = jsonObject.getAsJsonObject("session").get("sessionSupportKey");
      JsonElement jsonElement5 = jsonObject.getAsJsonObject("session").get("expDate");
      JsonElement fname = jsonObject.getAsJsonObject("user").get("fname");
      JsonElement lname = jsonObject.getAsJsonObject("user").get("lname");
      sessionRequest.setParticipantName(fname.getAsString() + lname.getAsString());
      JsonArray jsonArray = jsonObject.getAsJsonArray("feature");
      for (JsonElement jsonElement6 : jsonArray) {
        JsonObject featureObject = jsonElement6.getAsJsonObject();
        JsonElement nameElement = featureObject.get("name");
        String name = nameElement.getAsString();
        logger.info(name);
        if (name.equals("Recording")) {
          sessionRequest.setRecording(true);
        } else if (name.equals("Screen Share")) {
          sessionRequest.setScreenSharing(true);
        } else if (name.equals("Chat")) {
          sessionRequest.setChatEnabled(true);
        }
      }
      sessionRequest.setSessionUniqueId(jsonElement.getAsString());
      sessionRequest.setUserId(jsonElement1.getAsString());
      sessionRequest.setAccountId(jsonElement2.getAsString());
      sessionRequest.setSessionKey(jsonElement3.getAsString());
      sessionRequest.setSessionSupportKey(jsonElement4.getAsString());
      sessionRequest.setSessionExpiredTime(jsonElement5.getAsString());
      sessionRequest.setMaxParticipants(20);
      sessionRequest.setMaxActiveSessions(20);
      sessionRequest.setMaxUserActiveSessions(20);
      sessionRequest.setMaxUserParticipants(20);
      logger.info(sessionRequest.toString());
      logger.info("Fetched Details from video platform {}", sessionRequest.toString());
      return sessionRequest;
    } catch (Exception e) {
      logger.error("Getting Exception while fetching details from video Platform {}", e);
      return null;
    }
  }
  public void autoPlay(Session session, String cameraUri, String cameraName) throws OpenViduJavaClientException, OpenViduHttpException {
    session.fetch();
    List<String> alreadyPublishedCameras = session.getActiveConnections().stream()
      .filter(connection -> "IPCAM".equals(connection.getPlatform()))
      .map(connection -> connection.getServerData()).collect(Collectors.toList());
    try {
      // Publish the camera only if it is not already published
      if (!alreadyPublishedCameras.contains(cameraName)) {
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
          .type(ConnectionType.IPCAM)
          .data(cameraName)
          .rtspUri(cameraUri)
          .adaptativeBitrate(true)
          .onlyPlayWithSubscribers(true)
          .build();
        session.createConnection(connectionProperties);
      }
    } catch (Exception e) {
      logger.error("Error publishing camera {}", session.getSessionId());
    }
  }
}
