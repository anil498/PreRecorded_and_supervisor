package io.openvidu.call.java.services;

import io.openvidu.call.java.models.SessionProperty;
import io.openvidu.call.java.util.VideoPlatform;
import org.apache.hc.core5.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
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
  public SessionProperty getVideoPlatformProperties(String authorization, String token, String sessionKey) throws IOException, HttpException {
    return videoPlatform.getVideoPlatformProperties(authorization,token,sessionKey);
  }
  public ResponseEntity<?> sendLink(String authorization, String token, String sessionId) throws IOException, HttpException {
    return videoPlatform.sendLink(authorization,token,sessionId);
  }
  public ResponseEntity<?> updateSession(String authorization, String token, String sessionKey,Boolean isOnHold) throws IOException, HttpException {
    return videoPlatform.updateSession(authorization,token,sessionKey,isOnHold);
  }
  public ResponseEntity<?> saveICDC(String authorization, String token, String sessionId,String icdcId,String icdcResult) throws IOException, HttpException {
    return videoPlatform.saveICDC(authorization,token,sessionId,icdcId,icdcResult);
  }
  public HashMap<String,Integer> getExpiredSession(){
//    return videoPlatform.getExpiredTimer();
    HashMap<String,Integer> hashMap=new HashMap<>();
    hashMap.put("test1234",20);
    return hashMap;
  }

}
