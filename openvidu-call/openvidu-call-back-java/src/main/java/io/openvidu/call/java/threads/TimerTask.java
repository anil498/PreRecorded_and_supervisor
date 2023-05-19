package io.openvidu.call.java.threads;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.openvidu.call.java.config.SessionApplicationContext;
import io.openvidu.call.java.models.SessionRequest;
import io.openvidu.call.java.services.OpenViduService;
import io.openvidu.call.java.services.VideoPlatformService;
import io.openvidu.call.java.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;

@Component
public class TimerTask implements Runnable{
  @Autowired
  OpenViduService openViduService;
  private static final Logger logger= LoggerFactory.getLogger(TimerTask.class);
  @Override
  public void run() {
    logger.info("Fetching Details of session/recording which will expired in Next 5 Min");
    try {
      VideoPlatformService videoPlatformService= SessionApplicationContext.getBean(VideoPlatformService.class);
      HashMap<String,Integer> sessionIds=videoPlatformService.getExpiredSession();

      for(String sessionId: sessionIds.keySet()){
        try {
          String jsonString = "{\"accountId\":\"mcarbon\",\"userId\":\"admin\",\"isRecording\":\"true\",\"isBroadCasting\":\"false\",\"recordingMode\":\"custom\",\"isSessionCreator\":\"true\",\"isScreenSharing\":\"true\",\"isChatEnabled\":\"true\",\"allowTransCoding\":\"false\",\"maxActiveSessions\":\"5\",\"maxParticipants\":\"2\",\"maxDuration\":\"1000\",\"maxUserActiveSessions\":\"2\",\"maxUserParticipants\":\"2\",\"maxUserDuration\":\"1000\"}";
          Gson gson = new Gson();
          JsonObject jsonObject = gson.fromJson(jsonString.toString(), JsonObject.class);
//          ObjectMapper objectMapper=new ObjectMapper();
          SessionRequest sessionRequest = new SessionRequest(jsonObject);
          sessionRequest.setSessionUniqueId(String.valueOf(System.currentTimeMillis()));
          sessionRequest.setSessionExpiredTime(String.valueOf(System.currentTimeMillis()+500000));
          sessionRequest.setRecordingExpiredTime(String.valueOf(System.currentTimeMillis()+500000));
          logger.info("Session Request {}", sessionRequest);
          long diff= Long.parseLong(sessionRequest.getSessionExpiredTime())-System.currentTimeMillis();
          logger.info(String.valueOf(diff));
          if(!(diff<0l) && diff<500000) {
            CommonUtil.getInstance().addSessionTimer(sessionRequest, sessionIds.get(sessionId));
            logger.info("Timer added");
          }
        } catch (Exception e) {
         logger.error("Error {}",e);
        }
      }
    }catch (Exception e){
      logger.error("Getting Exception while fetching details {}",e);
    }

  }
}
