package io.openvidu.call.java.core;

import io.openvidu.call.java.Constants.SessionConstant;
import io.openvidu.call.java.config.Config;
import io.openvidu.call.java.threads.NamedThreadPoolFactory;
import io.openvidu.call.java.threads.TimerTask;
import io.openvidu.call.java.util.HandleTimeoutEvent;
import mcarbon.timer.Timer;
import mcarbon.util.McQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

public class SessionManager {
  private static Logger logger = LogManager.getLogger(SessionManager.class);

  private static ExecutorService SessionProcessorExecutor = null;
  private long timerTaskInterval=1;

  public static void main(String args[]) {
    SessionManager session = new SessionManager();
    session.initialise();
  }
  @SuppressWarnings("static-access")
  private void initialise() {
    startThreads();
  }

  private void startThreads() {
    McQueue timeOutQueue = new McQueue();
    try {
//      logger.info("Time {}",timerTaskInterval);
//      ScheduledExecutorService timerTaskService = Executors.newSingleThreadScheduledExecutor(new NamedThreadPoolFactory(SessionConstant.TIMER_INVOKER_THREAD));
//      timerTaskService.scheduleAtFixedRate(new TimerTask(), 0, timerTaskInterval, TimeUnit.MINUTES);
    }catch (Exception e){
      logger.error("Getting Exception while starting thread {}",e);
    }
//    Config.timer = new Timer(timeOutQueue, 2500);
//    ExecutorService handleTimeoutExecutor = Executors
//      .newSingleThreadExecutor(new NamedThreadPoolFactory(SessionConstant.HANDLE_TIMEOUT_THREAD));
//     handleTimeoutExecutor.submit(new HandleTimeoutEvent(timeOutQueue));

  }

  public static ExecutorService getSessionProcessorExecutor() {
    return SessionProcessorExecutor;
  }

}
