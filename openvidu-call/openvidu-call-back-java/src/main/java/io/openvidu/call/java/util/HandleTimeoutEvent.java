package io.openvidu.call.java.util;

import io.openvidu.call.java.config.SessionApplicationContext;
import io.openvidu.call.java.models.SessionRequest;
import io.openvidu.call.java.services.OpenViduService;
import io.openvidu.call.java.threads.NamedThreadPoolFactory;
import io.openvidu.java.client.*;
import mcarbon.util.McQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HandleTimeoutEvent implements Runnable{
  private static Logger logger= LoggerFactory.getLogger(HandleTimeoutEvent.class);
  McQueue timeoutQueue = null;
  OpenViduService openViduService= SessionApplicationContext.getBean(OpenViduService.class);
  public HandleTimeoutEvent(McQueue timeoutQueue) {
    this.timeoutQueue = timeoutQueue;
  }
  @Override
  public void run() {
    while (!Thread.interrupted()) {

      SessionRequest request = (SessionRequest) timeoutQueue.pull();
      try {

        if (request == null || request.getSessionUniqueId() == null) {
          logger.error("Timer expired for request whose details are null");
          continue;
        }
        logger.error("TIME OUT for SESSION_ID {}",request.getSessionUniqueId());

      }catch (Exception e){
        logger.info("Error {}",e);
      }
    }

  }

  //this method will call when recording time/size of particular user exceed we will stop the recording
  private void handleRecordingTimeout(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
    String recordingId =openViduService.moderatorsCookieMap.get(sessionId).getRecordingId();
    Recording recording=openViduService.stopRecording(recordingId);
    logger.info("Recording stopped {}",recording.getDuration());
  }
  //this method will call when session time of particular user expire so need to close the session
  private void handleSessionTimeout(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
    Session sessionObject=SessionUtil.getInstance().getSessionIdToSessionContextMap().get(sessionId).getSessionObject();
    sessionObject.close();
    logger.info("Session stopped due to session time expired {}",sessionId);
  }
}
