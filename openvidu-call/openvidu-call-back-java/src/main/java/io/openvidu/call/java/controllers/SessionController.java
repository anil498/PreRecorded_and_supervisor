package io.openvidu.call.java.controllers;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.openvidu.call.java.Constants.SessionConstant;
import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.models.SessionRequest;
import io.openvidu.call.java.services.VideoPlatformService;
import io.openvidu.call.java.util.CommonUtil;
import io.openvidu.call.java.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.openvidu.call.java.models.RecordingData;
import io.openvidu.call.java.services.OpenViduService;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Recording;
import io.openvidu.java.client.Session;

@CrossOrigin(origins = "*")
@RestController
public class SessionController {
  public static final Logger logger= LoggerFactory.getLogger(SessionController.class);
	@Value("${CALL_RECORDING}")
	private String CALL_RECORDING;
  @Value("${PARTICIPANT_NAME:Customer}")
  private String PARTICIPANT_NAME;

	@Value("${CALL_BROADCAST}")
	private String CALL_BROADCAST;
  @Value(("${ACCOUNT_AUTH}"))
  String accountIdToken;
  @Value(("${USER_AUTH}"))
  String userIdToken;


	@Autowired
	private OpenViduService openviduService;
  @Autowired
  private VideoPlatformService videoPlatformService;

	private final int cookieAdminMaxAge = 24 * 60 * 60;

	@PostMapping("/sessions")
	public ResponseEntity<Map<String, Object>> createConnection(
			@RequestBody(required = false) Map<String, Object> params,
			@CookieValue(name = OpenViduService.MODERATOR_TOKEN_NAME, defaultValue = "") String moderatorCookie,
			@CookieValue(name = OpenViduService.PARTICIPANT_TOKEN_NAME, defaultValue = "") String participantCookie,HttpServletRequest request,
			HttpServletResponse res) {
    Map<String, Object> response = new HashMap<String, Object>();
    Map<String,String> headers=getHeaders(request);
    ConcurrentMap<String,SessionContext> sessionIdToSessionContextMap=SessionUtil.getInstance().getSessionIdToSessionContextMap();
    logger.info("Request Headers {} and Parameters {}",headers,params);
    try {
      if (validateRequest(params)) {
        long date = -1;
        String nickname = "";

        if (params.containsKey("nickname")) {
          nickname = params.get("nickname").toString();
        }
        if(accountIdToken==null){
          response.put("reason","Authorization Key not found");
          return new ResponseEntity<>(response,HttpStatus.NOT_ACCEPTABLE);
        } else if (userIdToken==null) {
          response.put("reason","Token not found");
          return new ResponseEntity<>(response,HttpStatus.NOT_ACCEPTABLE);
        }
        String callType=null;
        if(params.containsKey("callType")){
           callType= params.get("callType").toString();
        }else {
          callType="Default";
        }
        logger.info("Call Type of this session is {}",callType);
        String sessionIdKey = params.get("sessionId").toString();
        String sessionId=null;
        SessionRequest sessionRequest=null;
        try {
        sessionRequest= CommonUtil.getInstance().getSessionRequest(accountIdToken,userIdToken,sessionId);
      }catch (Exception e){
          logger.error("Getting Exception while fetching record {}",e);
        }
        String sessionKey=sessionRequest.getAccountId()+SessionConstant.SESSION_PREFIX+sessionRequest.getUserId()+SessionConstant.SESSION_PREFIX+sessionId;
        sessionRequest.setSessionUniqueId(sessionKey);
        Session sessionCreated = null;
        if (validateSession(sessionRequest, sessionKey,sessionIdToSessionContextMap)) {
          sessionCreated = this.openviduService.createSession(sessionId, sessionRequest.getRecordingMode());
        } else {
          response.clear();
          response.put("reason", "Session limit exceed");
          return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        String MODERATOR_TOKEN_NAME = OpenViduService.MODERATOR_TOKEN_NAME;
        String PARTICIPANT_TOKEN_NAME = OpenViduService.PARTICIPANT_TOKEN_NAME;
//			boolean IS_RECORDING_ENABLED = CALL_RECORDING.toUpperCase().equals("ENABLED");
        boolean IS_RECORDING_ENABLED = sessionRequest.getRecording();
        if (!sessionIdKey.equals(sessionRequest.getSessionSupportKey())) {
          sessionRequest.setParticipantName("Customer");
          IS_RECORDING_ENABLED = false;
        }
//			boolean IS_BROADCAST_ENABLED = CALL_BROADCAST.toUpperCase().equals("ENABLED");
        boolean IS_BROADCAST_ENABLED = sessionRequest.getBroadCasting();
        boolean PRIVATE_FEATURES_ENABLED = IS_RECORDING_ENABLED || IS_BROADCAST_ENABLED;

        boolean hasModeratorValidToken = this.openviduService.isModeratorSessionValid(sessionId, moderatorCookie);
        boolean hasParticipantValidToken = this.openviduService.isParticipantSessionValid(sessionId,
          participantCookie);
        boolean hasValidToken = hasModeratorValidToken || hasParticipantValidToken;
//			boolean iAmTheFirstConnection = sessionCreated.getActiveConnections().size() == 0;
        boolean iAmSessionCreator=false;
        if(sessionIdKey.equals(sessionRequest.getSessionSupportKey())) {
           iAmSessionCreator = true;
        }
        sessionRequest.setSessionCreator(iAmSessionCreator);
        boolean isSessionCreator = hasModeratorValidToken || iAmSessionCreator;

        OpenViduRole role = isSessionCreator ? OpenViduRole.MODERATOR : OpenViduRole.PUBLISHER;

        response.put("recordingEnabled", IS_RECORDING_ENABLED);
        response.put("recordings", new ArrayList<Recording>());
        response.put("broadcastingEnabled", IS_BROADCAST_ENABLED);
        response.put("isRecordingActive", sessionCreated.isBeingRecorded());
        response.put("isBroadcastingActive", sessionCreated.isBeingBroadcasted());
        response.put("sessionLogo",sessionRequest.getSessionLogo());
        response.put("participantName",sessionRequest.getParticipantName());
        response.put("isChatEnabled",sessionRequest.getChatEnabled());
        response.put("showSessionId",sessionRequest.getShowSessionId());
        response.put("isScreenSharing",sessionRequest.getScreenSharing());
        response.put("isSessionCreator",sessionRequest.getSessionCreator());
        Connection cameraConnection = null;
        if (validateParticipantJoined(sessionRequest, sessionKey,sessionIdToSessionContextMap)) {
          cameraConnection = this.openviduService.createConnection(sessionCreated, nickname, role,sessionKey);
          if(sessionRequest.getScreenSharing()) {
            Connection screenConnection = this.openviduService.createConnection(sessionCreated, nickname, role, sessionKey);
            response.put("screenToken", screenConnection.getToken());
          }
          response.put("cameraToken", cameraConnection.getToken());

        } else {
          response.clear();
          response.put("reason", "Max participant joined");
          return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        if(!sessionIdToSessionContextMap.containsKey(sessionKey)) {
          SessionContext sessionContext = new SessionContext.Builder().sessionObject(sessionCreated).connectionObject(cameraConnection).sessionRequest(sessionRequest).sessionUniqueID(sessionId + SessionConstant.SESSION_PREFIX + sessionCreated.createdAt()).callType(callType).build();
          sessionIdToSessionContextMap.put(sessionKey, sessionContext);
          SessionUtil.getInstance().getSessionKeyMap().put(sessionCreated.getSessionId() + SessionConstant.SESSION_PREFIX + sessionCreated.createdAt(), sessionKey);
        }


        if (!hasValidToken && PRIVATE_FEATURES_ENABLED) {
          /**
           * ! *********** WARN *********** !
           *
           * To identify who is able to manage session recording and streaming, the code
           * sends a cookie
           * with a token to the session creator. The relation between cookies and
           * sessions are stored in backend memory.
           *
           * This authentication & authorization system is pretty basic and it is not for
           * production. We highly recommend IMPLEMENT YOUR OWN USER MANAGEMENT with
           * persistence for a properly and secure recording feature.
           *
           * ! *********** WARN *********** !
           **/
          String uuid = UUID.randomUUID().toString();
          date = System.currentTimeMillis();

          if (isSessionCreator) {
            String moderatorToken = cameraConnection.getToken() + "&" + MODERATOR_TOKEN_NAME + "="
              + uuid + "&createdAt=" + date;

            Cookie cookie = new Cookie(MODERATOR_TOKEN_NAME, moderatorToken);
            cookie.setMaxAge(cookieAdminMaxAge);
            res.addCookie(cookie);
            // Remove participant cookie if exists
            Cookie oldCookie = new Cookie(PARTICIPANT_TOKEN_NAME, "");
            oldCookie.setMaxAge(0);
            res.addCookie(oldCookie);

            RecordingData recData = new RecordingData(moderatorToken, "");
            this.openviduService.moderatorsCookieMap.put(sessionId, recData);

          } else {
            String participantToken = cameraConnection.getToken() + "&" + PARTICIPANT_TOKEN_NAME + "="
              + uuid + "&createdAt=" + date;

            Cookie cookie = new Cookie(PARTICIPANT_TOKEN_NAME, participantToken);
            cookie.setMaxAge(cookieAdminMaxAge);
            res.addCookie(cookie);
            // Remove moderator cookie if exists
            Cookie oldCookie = new Cookie(MODERATOR_TOKEN_NAME, "");
            oldCookie.setMaxAge(0);
            res.addCookie(oldCookie);

            List<String> tokens = this.openviduService.participantsCookieMap.containsKey(sessionId)
              ? this.openviduService.participantsCookieMap.get(sessionId)
              : new ArrayList<String>();
            tokens.add(participantToken);
            this.openviduService.participantsCookieMap.put(sessionId, tokens);
          }
        }

        if (IS_RECORDING_ENABLED) {
          if (date == -1) {
            date = openviduService.getDateFromCookie(moderatorCookie);
          }
          List<Recording> recordings = openviduService.listRecordingsBySessionIdAndDate(sessionId, date);
          response.put("recordings", recordings);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
      }else {
        response.clear();
        response.put("reason","sessionId not found");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
      }

      } catch(OpenViduJavaClientException | OpenViduHttpException e){

        if (e.getMessage() != null && Integer.parseInt(e.getMessage()) == 501) {
          System.err.println("OpenVidu Server recording module is disabled");
          return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (e.getMessage() != null && Integer.parseInt(e.getMessage()) == 401) {
          System.err.println("OpenVidu credentials are wrong.");
          return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {
          e.printStackTrace();
          System.err.println(e.getMessage());
          return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
  }

  public boolean validateSession(SessionRequest sessionRequest,String sessionKey,ConcurrentMap<String,SessionContext> sessionContextConcurrentMap){
      int activeSession = 0;
      int activeUserSession = 0;
      for (String key : sessionContextConcurrentMap.keySet()) {
        if (key.startsWith(sessionRequest.getAccountId())) {
          activeSession += 1;
        }
        if (key.startsWith(sessionRequest.getAccountId() + SessionConstant.SESSION_PREFIX + sessionRequest.getUserId())) {
          activeUserSession += 1;
        }
      }
      logger.info("Active session for this account {} is {} and max active allowed is {} ", sessionRequest.getAccountId(), activeSession, sessionRequest.getMaxActiveSessions());
      logger.info("Active session for this user {} is {} and max active allowed is {} ", sessionRequest.getUserId(), activeUserSession, sessionRequest.getMaxUserActiveSessions());
        if (activeSession < sessionRequest.getMaxActiveSessions() && activeUserSession < sessionRequest.getMaxUserActiveSessions())
          return true;
        else if(sessionContextConcurrentMap.containsKey(sessionKey))
          return true;
        else
          return false;

  }
  public boolean validateParticipantJoined(SessionRequest sessionRequest,String sessionKey,ConcurrentMap<String,SessionContext> sessionContextConcurrentMap){
    SessionContext sessionContext=sessionContextConcurrentMap.get(sessionKey);
    if(sessionContext!=null) {
         logger.info("Active Participant for sessionId is {} and max Participant allowed for this session is {} ", sessionContext.getParticipantJoined(), sessionRequest.getMaxParticipants());
         if (sessionContext.getParticipantJoined() < sessionRequest.getMaxParticipants()) {
          return true;
        } else {
          return false;
        }
    }
    return true;
  }
  private Map<String, String> getHeaders(HttpServletRequest request) {
    Enumeration<String> headers = request.getHeaderNames();
    Map<String, String> headerMap = new HashMap<>();
    while (headers.hasMoreElements()) {
      String header = headers.nextElement();
      headerMap.put(header, request.getHeader(header));
    }
    return headerMap;
  }
  public boolean validateRequest(Map<String, Object> params){
    if(params.isEmpty()){
      return false;
    } else if (params.containsKey("sessionId")) {
      return true;
    }else{
      return false;
    }
  }
}
