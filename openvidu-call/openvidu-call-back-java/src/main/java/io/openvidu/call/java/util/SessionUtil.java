package io.openvidu.call.java.util;

import io.openvidu.call.java.core.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionUtil {
  private static final Logger logger= LoggerFactory.getLogger(SessionUtil.class);
  private static final SessionUtil instance=new SessionUtil();
  private ConcurrentMap<String, SessionContext> sessionIdToSessionContextMap;
  private ConcurrentMap<String, String> sessionKeyMap;

  private SessionUtil(){
    sessionIdToSessionContextMap=new ConcurrentHashMap<>();
    sessionKeyMap=new ConcurrentHashMap<>();
  }
  public static SessionUtil getInstance(){
    return instance;
  }
  public ConcurrentMap<String, SessionContext> getSessionIdToSessionContextMap() {
    return sessionIdToSessionContextMap;
  }
  public ConcurrentMap<String,String> getSessionKeyMap(){
    return sessionKeyMap;
  }
}
