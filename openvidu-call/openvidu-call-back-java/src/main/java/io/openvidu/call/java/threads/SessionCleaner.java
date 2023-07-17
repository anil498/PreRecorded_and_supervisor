package io.openvidu.call.java.threads;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.openvidu.call.java.config.SessionApplicationContext;
import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.services.OpenViduService;
import io.openvidu.call.java.util.SessionUtil;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionType;
import io.openvidu.java.client.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class SessionCleaner implements Runnable {
  private static final Logger looger= LoggerFactory.getLogger(SessionCleaner.class);

  @Override
  public void run() {
    looger.info("Going to clean session context map");
    try {
      OpenViduService openViduService= SessionApplicationContext.getBean(OpenViduService.class);
      ConcurrentMap<String, SessionContext> sessionContextConcurrentMap = SessionUtil.getInstance().getSessionIdToSessionContextMap();
      for (String key : sessionContextConcurrentMap.keySet()) {
        SessionContext sessionContext = sessionContextConcurrentMap.get(key);
        Session session=openViduService.getActiveSession(sessionContext.getSessionUniqueId());
        List<Connection> connectionList=session.getActiveConnections();
        List<Connection> filterConnectionList=connectionList.stream()
                .filter(connection -> {
                  ConnectionType type = connection.getType();
                  if (type != null && type.equals("IPCAM")) {
                    return !type.equals("IPCAM");
                  }
                  return true;
                })
                .collect(Collectors.toList());
        if (connectionList.size()==1) {
          if (filterConnectionList.size()==0){
               session.close();
          }
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
