package io.openvidu.call.java.controllers;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import io.openvidu.call.java.Constants.SessionConstant;
import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.models.ErrorResponse;
import io.openvidu.call.java.models.SessionProperty;
import io.openvidu.call.java.services.SessionService;
import io.openvidu.call.java.services.VideoPlatformService;
import io.openvidu.call.java.util.SessionUtil;
import io.openvidu.java.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.openvidu.call.java.models.RecordingData;
import io.openvidu.call.java.services.OpenViduService;

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
  @Value(("${Authorization}"))
  String authorization;
  @Value(("${Token}"))
  String token;


	@Autowired
	private OpenViduService openviduService;
  @Autowired
  private VideoPlatformService videoPlatformService;

	private final int cookieAdminMaxAge = 24 * 60 * 60;
  @Autowired
  SessionService sessionService;

	@PostMapping("/sessions")
	public ResponseEntity<?> createConnection(
			@RequestBody(required = false) Map<String, Object> params,
			@CookieValue(name = OpenViduService.MODERATOR_TOKEN_NAME, defaultValue = "") String moderatorCookie,
			@CookieValue(name = OpenViduService.PARTICIPANT_TOKEN_NAME, defaultValue = "") String participantCookie,HttpServletRequest request,
			HttpServletResponse res) {
    Map<String,String> headers=getHeaders(request);
    ConcurrentMap<String,SessionContext> sessionIdToSessionContextMap=SessionUtil.getInstance().getSessionIdToSessionContextMap();
    logger.info("Request API /sessions Headers {} and Parameters {}",headers,params);
    String sessionId=null;
    SessionProperty sessionProperty =null;
    ErrorResponse errorResponse=new ErrorResponse();
    try {
      if (validateRequest(params)) {
        long date = -1;
        String nickname = "";

        if (params.containsKey("nickname")) {
          nickname = params.get("nickname").toString();
        }
        if(authorization==null){
          errorResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.toString());
          errorResponse.setReason("Authorization Key not found");
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        } else if (token==null) {
          errorResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.toString());
          errorResponse.setReason("Token not found");
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        }
        String sessionKey = params.get("sessionKey").toString();
        try {
             sessionProperty = videoPlatformService.getVideoPlatformProperties(authorization, token, sessionKey);
             logger.info("Session Property: {}", sessionProperty);
             sessionId= sessionProperty.getSessionId();
             sessionProperty.setBase64Logo(sessionService.convertByteToBase64(sessionProperty.getSettings().getLogo()));
        }catch (Exception e){
          logger.error("Getting Exception while fetching Session property from videoplatform {}",e);
          errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
          errorResponse.setReason("Session not found");
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
        Session sessionCreated = null;
        if (validateSession(sessionProperty, sessionKey,sessionIdToSessionContextMap)) {
          logger.info("Going to Create session {} with recordingMode {}",sessionProperty.getSessionId(),sessionProperty.getRecordingMode());
          sessionCreated = this.openviduService.createSession(sessionId, sessionProperty.getRecordingMode());
        } else {
          errorResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.toString());
          errorResponse.setReason("Session limit exist");
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        }
        String MODERATOR_TOKEN_NAME = OpenViduService.MODERATOR_TOKEN_NAME;
        String PARTICIPANT_TOKEN_NAME = OpenViduService.PARTICIPANT_TOKEN_NAME;
        boolean IS_RECORDING_ENABLED = sessionProperty.getSettings().getRecording();
        boolean IS_BROADCAST_ENABLED = sessionProperty.getSettings().getBroadcast();
        boolean PRIVATE_FEATURES_ENABLED = IS_RECORDING_ENABLED || IS_BROADCAST_ENABLED;

        boolean hasModeratorValidToken = this.openviduService.isModeratorSessionValid(sessionId, moderatorCookie);
        boolean hasParticipantValidToken = this.openviduService.isParticipantSessionValid(sessionId,
          participantCookie);
        boolean hasValidToken = hasModeratorValidToken || hasParticipantValidToken;
        boolean iAmSessionCreator= sessionProperty.getSettings().getModerators();
        boolean isSessionCreator = hasModeratorValidToken || iAmSessionCreator;

        OpenViduRole role = isSessionCreator ? OpenViduRole.MODERATOR : OpenViduRole.PUBLISHER;


        Connection cameraConnection = null;
        if (validateParticipantJoined(sessionProperty, sessionCreated,sessionIdToSessionContextMap)) {
          if(sessionCreated!=null) {
            cameraConnection = this.openviduService.createConnection(sessionCreated, nickname, role);
            sessionProperty.setCameraToken(cameraConnection.getToken());
            if (sessionProperty.getSettings().getScreenShare()) {
              Connection screenConnection = this.openviduService.createConnection(sessionCreated, nickname, role);
              sessionProperty.setScreenToken(screenConnection.getToken());
            }
          }
        } else {
          errorResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.toString());
          errorResponse.setReason("Max participant joined");
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        }
        if(!sessionIdToSessionContextMap.containsKey(sessionId)) {
          SessionContext sessionContext = new SessionContext.Builder().sessionObject(sessionCreated).connectionObject(cameraConnection).sessionRequest(sessionProperty).sessionKey(sessionKey).sessionUniqueID(sessionId).build();
          sessionIdToSessionContextMap.put(sessionKey, sessionContext);
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
          sessionProperty.setRecordings(recordings);
        }
        return ResponseEntity.status(HttpStatus.OK).body(sessionProperty);
      }else {
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorResponse.setReason("SessionKey not found");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
      }

      } catch(OpenViduJavaClientException | OpenViduHttpException e){

        if (e.getMessage() != null && Integer.parseInt(e.getMessage()) == 501) {
          System.err.println("OpenVidu Server recording module is disabled");
          return ResponseEntity.status(HttpStatus.OK).body(sessionProperty);
        } else if (e.getMessage() != null && Integer.parseInt(e.getMessage()) == 401) {
          System.err.println("OpenVidu credentials are wrong.");
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
          e.printStackTrace();
          System.err.println(e.getMessage());
          errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
          errorResponse.setReason("Session Expired");
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        }
      }
  }
  @DeleteMapping("/sessions/{sessionId}")
  public ResponseEntity<?> deleteSession(@PathVariable String sessionId) {
    try {
      ConcurrentMap<String,SessionContext> sessionIdToSessionContextMap=SessionUtil.getInstance().getSessionIdToSessionContextMap();
      if(sessionIdToSessionContextMap.containsKey(sessionId)){
        logger.info("Going to close the session {}",sessionId);
        Session session=openviduService.getActiveSession(sessionId);
        session.close();
        logger.info("Session {} removed from session context map",sessionId);
        sessionIdToSessionContextMap.remove(sessionId);
        return new ResponseEntity<>(HttpStatus.OK);
      }
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }catch (Exception e){
      logger.error("Getting error while remove session from session context map {}",e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  private boolean validateSession(SessionProperty sessionProperty, String sessionKey, ConcurrentMap<String,SessionContext> sessionContextConcurrentMap) throws OpenViduJavaClientException, OpenViduHttpException {
      int activeAccountSession = 0;
      int activeUserSession = 0;
      sessionDestroyed(sessionKey);
      for (String key : sessionContextConcurrentMap.keySet()) {
        if (key.startsWith(sessionProperty.getAccountId())) {
          activeAccountSession += 1;
        }
        if (key.startsWith(sessionProperty.getAccountId() + SessionConstant.SESSION_PREFIX + sessionProperty.getUserId())) {
          activeUserSession += 1;
        }
      }
      logger.info("Active session of Account Id {} is {} and max active session allowed is {} ", sessionProperty.getAccountId(), activeAccountSession, sessionProperty.getAccountMaxSessions());
      logger.info("Active session of {} is User Id {} and max active session allowed is {} ", sessionProperty.getUserId(), activeUserSession, sessionProperty.getUserMaxSessions());
        if (activeAccountSession < sessionProperty.getAccountMaxSessions() && activeUserSession < sessionProperty.getUserMaxSessions())
          return true;
//        else if(sessionContextConcurrentMap.containsKey(sessionKey)) // Need to review this line
//          return true;
        else
          return false;

  }
  private boolean validateParticipantJoined(SessionProperty sessionProperty, Session session, ConcurrentMap<String,SessionContext> sessionContextConcurrentMap){
    if(session!=null) {
      List<Connection> connectionList=session.getActiveConnections();
      logger.info("Active Participant for sessionId is {} and max Participant allowed for this session is {} ", connectionList.size(), sessionProperty.getTotalParticipants());
      if (connectionList.size() < sessionProperty.getTotalParticipants()) {
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
  private boolean validateRequest(Map<String, Object> params){
    if (params.containsKey("sessionKey")){
      return true;
    }
    return false;
  }
  private boolean sessionDestroyed(String sessionKey) throws OpenViduJavaClientException, OpenViduHttpException {
    ConcurrentMap<String,SessionContext> sessionContextConcurrentMap=SessionUtil.getInstance().getSessionIdToSessionContextMap();
    if(sessionContextConcurrentMap.containsKey(sessionKey)) {
      logger.info("Session exists in context map {}",sessionKey);
      Session session=sessionContextConcurrentMap.get(sessionKey).getSessionObject();
      if(session!=null){
          if(openviduService.isSessionExist(session) && openviduService.getActiveSession(session.getSessionId()).getActiveConnections().size()==0) {
            try {
              session.close();
            }catch (Exception e){
              logger.error("Getting Session While closing {}",e);
            }
          }
          sessionContextConcurrentMap.remove(sessionKey);
          return true;
        }
      }
    return false;
  }
}
