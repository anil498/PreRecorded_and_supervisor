package io.openvidu.call.java.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.openvidu.call.java.Constants.CallType;
import io.openvidu.call.java.Constants.SessionConstant;
import io.openvidu.call.java.Constants.WebhookEvent;
import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.models.SessionWebhook;
import io.openvidu.call.java.services.SessionCallbackSender;
import io.openvidu.call.java.services.SessionService;
import io.openvidu.call.java.threads.NamedThreadPoolFactory;
import io.openvidu.call.java.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin(origins = "*")
@RestController
public class WebhookController {
  private static final Logger logger= LoggerFactory.getLogger(WebhookController.class);
  @Autowired
  SessionService sessionService;
  @Autowired
  SessionCallbackSender sessionCallbackSender;
  private ExecutorService executorService = Executors.newFixedThreadPool(5,new NamedThreadPoolFactory(SessionConstant.CALLBACK_INVOKER_THREAD));
  @PostMapping("/sessionWebhook")
  public ResponseEntity<?> sessionWebhookController(@RequestBody String webhookJson){
    try {
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(webhookJson, JsonObject.class);
      ObjectMapper objectMapper=new ObjectMapper();
      SessionWebhook sessionWebhook=objectMapper.readValue(jsonObject.toString(),SessionWebhook.class);
      logger.info("Session Webhook {} ",sessionWebhook);
      try {
        String sessionKey=SessionUtil.getInstance().getSessionKeyMap().get(sessionWebhook.getUniqueSessionId());
        ConcurrentMap<String,SessionContext> sessionContextMap= SessionUtil.getInstance().getSessionIdToSessionContextMap();
        logger.info("Webhook event {}",sessionWebhook.getEvent());
        if(sessionKey!=null) {
          logger.info("Webhook event {} and sessionKey {}",sessionWebhook.getEvent(),sessionKey);
          SessionContext sessionContext = sessionContextMap.get(sessionKey);
          AtomicInteger participant = new AtomicInteger(sessionContext.getParticipantJoined());
          logger.info(WebhookEvent.getKey2EnumMap().toString(), sessionWebhook.getEvent().toUpperCase());
          switch (WebhookEvent.getEnumByKey(sessionWebhook.getEvent().toUpperCase())) {
            case SESSIONCREATED:
              logger.info("Session created");
              logger.info(sessionContext.toString());
              break;
            case PARTICIPANTJOINED:
              participant.getAndIncrement();
              sessionContext.setParticipantJoined(participant.get());
              SessionUtil.getInstance().getSessionIdToSessionContextMap().put(sessionKey, sessionContext);
              logger.info("Participant join {}", sessionContext);
              String callType=sessionContext.getSessionRequest().getCallType().toUpperCase();
              switch (CallType.getEnumByKey(callType)){
                case PRERECORDED:
                  try {
                    sessionService.autoPlay(sessionContext.getSessionObject(), sessionContext.getSessionRequest().getVideoUri(), callType);
                  }catch (Exception e){
                    logger.error("Getting Exception while playing video {}",e);
                  }
              }
              break;
            case PARTICIPANTLEFT:
              participant.getAndDecrement();
              sessionContext.setParticipantJoined(participant.get());
              sessionContextMap.put(sessionKey, sessionContext);
              logger.info("Participant left {}", sessionContext);
              break;
            case SESSIONDESTROYED:
              logger.info("Session Destroyed {}", sessionContext);
              //Send session Callback
//              executeCallbackAsync(sessionKey, sessionWebhook);
          }
        }else{
          logger.info("No need of this Webhook event sessionId {}",sessionWebhook.getUniqueSessionId());
        }
      }catch (Exception e){
        logger.error("Getting Exception on Switch case {}",e);
      }
    }catch (Exception e){
      logger.error("Getting Exception in webhook {}",e);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }
  public void executeCallbackAsync(String sessionKey, SessionWebhook sessionWebhook) {
    executorService.execute(() -> {
      sessionCallbackSender.generateAndInvokeSessionCallback(sessionKey, sessionWebhook);
    });
  }
}
