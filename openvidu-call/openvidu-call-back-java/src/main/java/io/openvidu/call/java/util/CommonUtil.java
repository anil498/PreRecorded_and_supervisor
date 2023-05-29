package io.openvidu.call.java.util;

//import io.openvidu.call.java.config.Config;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.openvidu.call.java.config.SessionApplicationContext;
import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.models.SessionCallback;
import io.openvidu.call.java.models.SessionRequest;
import io.openvidu.call.java.models.SessionWebhook;
import io.openvidu.call.java.services.VideoPlatformService;
import io.openvidu.java.client.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonUtil {
  private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);
  private static CommonUtil instance = null;
  private CommonUtil() {

  }
  public static CommonUtil getInstance() {
    if (instance == null) {
      // synchronized (ScriptUtil.class) {
      if (instance == null) {
        instance = new CommonUtil();
      }
      // }
    }
    return instance;
  }
  public void addSessionTimer(SessionRequest request, int timeOut) throws Exception {
    if (timeOut == 0 ) {
      logger.info("Timer Duration is 0 so return from timer without add for sessionId:[" + request.getAccountId()
        + "]");
    } else {
      try {
//        Config.timer.add(request.getSessionUniqueId(), request, timeOut);
        logger.info("Timer is added for[" + timeOut + "] sec for sessionId:[" + request.getSessionUniqueId() + "]");
      } catch (Exception e) {
        e.printStackTrace();
        logger.info("Exception:[" + e.getMessage() + "], while adding in timer for sessionId:["
          + request.getSessionUniqueId() + "]");
        throw e;
      }
    }
  }
  public SessionRequest getSessionRequest(String accountIdToken,String userIdToken,String sessionIdKey) {
    SessionRequest sessionRequest = new SessionRequest();
    VideoPlatformService videoPlatformService= SessionApplicationContext.getBean(VideoPlatformService.class);
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
}
