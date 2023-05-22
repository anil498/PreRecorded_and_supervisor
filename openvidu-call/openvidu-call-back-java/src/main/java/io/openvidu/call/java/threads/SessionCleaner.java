package io.openvidu.call.java.threads;

import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

public class SessionCleaner implements Runnable {
  private static final Logger looger= LoggerFactory.getLogger(SessionCleaner.class);
  @Override
  public void run() {
    looger.info("Going to clean session context map");
    try {
      ConcurrentMap<String, SessionContext> sessionContextConcurrentMap = SessionUtil.getInstance().getSessionIdToSessionContextMap();
      for (String key : sessionContextConcurrentMap.keySet()) {
        SessionContext sessionContext = sessionContextConcurrentMap.get(key);
        if (sessionContext.getParticipantJoined() == 0) {
          looger.info("Removing session context from map sessionId is {} and joined participant is {}",sessionContext.getSessionUniqueId(),sessionContext.getParticipantJoined());
          sessionContextConcurrentMap.remove(key);
        }else {
          looger.info("No session removed from session context");
        }
      }
    }catch (Exception e){
      looger.error("Getting Exception while cleaning the session context {}",e);
    }
  }
}
