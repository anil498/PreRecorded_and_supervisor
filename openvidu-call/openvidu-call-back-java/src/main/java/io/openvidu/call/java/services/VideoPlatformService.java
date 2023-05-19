package io.openvidu.call.java.services;

import com.google.gson.JsonObject;
import io.openvidu.call.java.models.SessionCallback;
import io.openvidu.call.java.models.SessionRequest;
import io.openvidu.call.java.util.VideoPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Service
public class VideoPlatformService {
  private static Logger logger =LoggerFactory.getLogger(VideoPlatformService.class);
  @Value("${VIDEOPLATFORM_URL}")
  public String VIDEOPLATFORM_URL;
  @Value("${CALLBACK_RETRY:3}")
  public int CALLBACK_RETRY;
  private VideoPlatform videoPlatform;
  @PostConstruct
  public void init() {
    this.videoPlatform = new VideoPlatform(VIDEOPLATFORM_URL);
  }
  public String getVideoPlatformProperties(String accountIdToken, String userIdToken, String sessionKey){
    return videoPlatform.getVideoPlatformProperties(accountIdToken,userIdToken,sessionKey);
  }
  public HashMap<String,Integer> getExpiredSession(){
//    return videoPlatform.getExpiredTimer();
    HashMap<String,Integer> hashMap=new HashMap<>();
    hashMap.put("test1234",20);
    return hashMap;
  }
  public boolean sendSessionCallback(SessionCallback sessionCallback){
    return videoPlatform.sendSessionCallback(sessionCallback,CALLBACK_RETRY);
  }
}
