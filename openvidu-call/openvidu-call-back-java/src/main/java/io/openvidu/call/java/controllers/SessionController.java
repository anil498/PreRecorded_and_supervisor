package io.openvidu.call.java.controllers;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import io.openvidu.call.java.Constants.SessionConstant;
import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.models.SessionRequest;
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
  @Value(("${LOGO_FILE_PATH}"))
  String logoFilPath;
  @Value(("${LOGO_FILE_EXT}"))
  String fileExtension;


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
//    Map<String, Object> response = new HashMap<String, Object>();
    Map<String,String> headers=getHeaders(request);
    ConcurrentMap<String,SessionContext> sessionIdToSessionContextMap=SessionUtil.getInstance().getSessionIdToSessionContextMap();
    logger.info("Request Headers {} and Parameters {}",headers,params);
    String sessionId=null;
    SessionRequest sessionRequest=null;
    try {
      if (validateRequest(params)) {
        long date = -1;
        String nickname = "";

        if (params.containsKey("nickname")) {
          nickname = params.get("nickname").toString();
        }
        if(authorization==null){
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Authorization not found");
        } else if (token==null) {
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Token not found");
        }
        String sessionIdKey = params.get("sessionId").toString();
        try {
             sessionRequest = videoPlatformService.getVideoPlatformProperties(authorization, token, sessionIdKey);
             logger.info("Session Request: {}",sessionRequest);
             sessionId=sessionRequest.getSessionId();
        }catch (Exception e){
          logger.error("Getting Exception while fetching record {}",e);
        }
        Session sessionCreated = null;
        if (validateSession(sessionRequest, sessionIdKey,sessionIdToSessionContextMap)) {
          sessionCreated = this.openviduService.createSession(sessionId, sessionRequest.getSettings().getRecordingDetails().toString());
        } else {
          ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Session limit exceed");
        }
        String MODERATOR_TOKEN_NAME = OpenViduService.MODERATOR_TOKEN_NAME;
        String PARTICIPANT_TOKEN_NAME = OpenViduService.PARTICIPANT_TOKEN_NAME;
        boolean IS_RECORDING_ENABLED = sessionRequest.getSettings().getRecording();
        boolean IS_BROADCAST_ENABLED = sessionRequest.getSettings().getBroadcast();
        boolean PRIVATE_FEATURES_ENABLED = IS_RECORDING_ENABLED || IS_BROADCAST_ENABLED;

        boolean hasModeratorValidToken = this.openviduService.isModeratorSessionValid(sessionId, moderatorCookie);
        boolean hasParticipantValidToken = this.openviduService.isParticipantSessionValid(sessionId,
          participantCookie);
        boolean hasValidToken = hasModeratorValidToken || hasParticipantValidToken;
        boolean iAmSessionCreator=sessionRequest.getSettings().getModerators();
        boolean isSessionCreator = hasModeratorValidToken || iAmSessionCreator;

        OpenViduRole role = isSessionCreator ? OpenViduRole.MODERATOR : OpenViduRole.PUBLISHER;


        Connection cameraConnection = null;
        if (validateParticipantJoined(sessionRequest, sessionCreated,sessionIdToSessionContextMap)) {
          cameraConnection = this.openviduService.createConnection(sessionCreated, nickname, role);
          sessionRequest.setCameraToken(cameraConnection);
          if(sessionRequest.getSettings().getScreenShare()) {
            Connection screenConnection = this.openviduService.createConnection(sessionCreated, nickname, role);
            sessionRequest.setScreenToken(screenConnection);
          }
        } else {
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Max participant joined");
        }
        if(sessionRequest.getSettings().isShowLogo()){
          byte[] file= (byte[]) sessionRequest.getSettings().getLogo();
          String fileName=sessionId+fileExtension;
          if(file!=null){
            sessionService.writeLogoFile(file,fileName,logoFilPath);
          }
        }
        if(!sessionIdToSessionContextMap.containsKey(sessionId)) {
          SessionContext sessionContext = new SessionContext.Builder().sessionObject(sessionCreated).connectionObject(cameraConnection).sessionRequest(sessionRequest).sessionKey(sessionIdKey).sessionUniqueID(sessionId).build();
          sessionIdToSessionContextMap.put(sessionIdKey, sessionContext);
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
          sessionRequest.setRecordings(recordings);
        }
        return ResponseEntity.status(HttpStatus.OK).body(sessionRequest);
      }else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SessionId not found");
      }

      } catch(OpenViduJavaClientException | OpenViduHttpException e){

        if (e.getMessage() != null && Integer.parseInt(e.getMessage()) == 501) {
          System.err.println("OpenVidu Server recording module is disabled");
          return ResponseEntity.status(HttpStatus.OK).body(sessionRequest);
        } else if (e.getMessage() != null && Integer.parseInt(e.getMessage()) == 401) {
          System.err.println("OpenVidu credentials are wrong.");
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
          e.printStackTrace();
          System.err.println(e.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sessionRequest);
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
  private boolean validateSession(SessionRequest sessionRequest,String sessionKey,ConcurrentMap<String,SessionContext> sessionContextConcurrentMap) throws OpenViduJavaClientException, OpenViduHttpException {
      int activeSession = 0;
      int activeUserSession = 0;
      sessionDestroyed(sessionKey);
      for (String key : sessionContextConcurrentMap.keySet()) {
        if (key.startsWith(sessionRequest.getAccountId())) {
          activeSession += 1;
        }
        if (key.startsWith(sessionRequest.getAccountId() + SessionConstant.SESSION_PREFIX + sessionRequest.getUserId())) {
          activeUserSession += 1;
        }
      }
      logger.info("Active session for this account {} is {} and max active allowed is {} ", sessionRequest.getAccountId(), activeSession, sessionRequest.getAccountMaxSession());
      logger.info("Active session for this user {} is {} and max active allowed is {} ", sessionRequest.getUserId(), activeUserSession, sessionRequest.getUserMaxSession());
        if (activeSession < sessionRequest.getAccountMaxSession() && activeUserSession < sessionRequest.getUserMaxSession())
          return true;
        else if(sessionContextConcurrentMap.containsKey(sessionKey))
          return true;
        else
          return false;

  }
  private boolean validateParticipantJoined(SessionRequest sessionRequest,Session session,ConcurrentMap<String,SessionContext> sessionContextConcurrentMap){
    if(session!=null) {
      List<Connection> connectionList=session.getActiveConnections();
          if (connectionList.size() < Integer.parseInt(sessionRequest.getTotalParticipants())) {
            logger.info("Active Participant for sessionId is {} and max Participant allowed for this session is {} ", connectionList.size(), sessionRequest.getTotalParticipants());
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
    if(params.isEmpty()){
      return false;
    } else if (params.containsKey("sessionId")) {
      return true;
    }else{
      return false;
    }
  }
  private boolean sessionDestroyed(String sessionKey) throws OpenViduJavaClientException, OpenViduHttpException {
    ConcurrentMap<String,SessionContext> sessionContextConcurrentMap=SessionUtil.getInstance().getSessionIdToSessionContextMap();
    if(sessionContextConcurrentMap.containsKey(sessionKey)) {
      logger.info("Session exists in context map {}",sessionKey);
      String sessionId=sessionContextConcurrentMap.get(sessionKey).getSessionUniqueId();
      Session session=openviduService.getActiveSession(sessionId);
      if(session!=null){
        if(session.getActiveConnections().size()==0){
          session.close();
          sessionContextConcurrentMap.remove(sessionKey);
          return true;
        }
      }
    }
    return false;
  }
}
