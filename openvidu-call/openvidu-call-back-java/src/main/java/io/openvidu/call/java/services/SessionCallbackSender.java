package io.openvidu.call.java.services;

import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.models.SessionCallback;
import io.openvidu.call.java.models.SessionWebhook;
import io.openvidu.call.java.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionCallbackSender {
  private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);
  @Autowired
  private VideoPlatformService videoPlatformService;
  @Value("${RETRY_TIME:10,20,30}")
  List<Integer> retryTime;

  public void generateAndInvokeSessionCallback(String sessionKey, SessionWebhook sessionWebhook){
    SessionCallback sessionCallback=new SessionCallback();
    try {
      SessionContext sessionContext = SessionUtil.getInstance().getSessionIdToSessionContextMap().get(sessionKey);
      sessionCallback.setSessionId(sessionWebhook.getSessionId());
      sessionCallback.setUniqueSessionId(sessionWebhook.getUniqueSessionId());
      sessionCallback.setAccountId(sessionContext.getSessionRequest().getAccountId());
      sessionCallback.setUserId(sessionContext.getSessionRequest().getUserId());
      sessionCallback.setDuration(sessionWebhook.getDuration());
      sessionCallback.setReason(sessionWebhook.getReason());
      sessionCallback.setStartTime(sessionWebhook.getStartTime());
      sessionCallback.setEndTime(String.valueOf(System.currentTimeMillis()));
    }catch (Exception e){
      logger.error("Error in constructing CreateOBDCallback object [{}]", e.getMessage());
    }
    if(sessionKey!=null){
      SessionUtil.getInstance().getSessionIdToSessionContextMap().remove(sessionKey);
      SessionUtil.getInstance().getSessionKeyMap().remove(sessionWebhook.getUniqueSessionId());
    }
    sessionCallbackSender(sessionCallback);
  }
  public void sessionCallbackSender(SessionCallback sessionCallback){
    try{
      boolean outputSuccess =false;
      int retryCounter=0;
      while (!outputSuccess){
        try {
          outputSuccess=this.videoPlatformService.sendSessionCallback(sessionCallback);
          if (!outputSuccess) {
            logger.error("Video Platform is down will retry after [{}] seconds",retryTime.get(retryCounter));
            Thread.sleep(retryTime.get(retryCounter)*1000l);
            if(retryCounter<(retryTime.size()-1))
              retryCounter++;
          }
        }catch (Exception e){
          logger.error("Thread sleep issue",e);
          outputSuccess=true;
        }
      }
    }catch (Exception e){
      logger.error("Getting Exception while sending callback {}",e.getMessage());
    }
  }
}
