package io.openvidu.call.java.util;

//import io.openvidu.call.java.config.Config;
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
}
