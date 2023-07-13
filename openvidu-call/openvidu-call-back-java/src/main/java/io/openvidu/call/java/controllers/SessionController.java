package io.openvidu.call.java.controllers;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.openvidu.call.java.Constants.SessionConstant;
import io.openvidu.call.java.core.SessionContext;
import io.openvidu.call.java.models.ErrorResponse;
import io.openvidu.call.java.models.SessionProperty;
import io.openvidu.call.java.models.Settings;
import io.openvidu.call.java.services.SessionService;
import io.openvidu.call.java.services.VideoPlatformService;
import io.openvidu.call.java.util.SessionUtil;
import io.openvidu.java.client.*;
import org.apache.hc.core5.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.openvidu.call.java.models.RecordingData;
import io.openvidu.call.java.services.OpenViduService;
import org.springframework.web.client.HttpClientErrorException;

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
    @Value(("${OPENVIDU_URL}"))
    String OPENVIDU_URL;
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
    SessionProperty sessionProperty =new SessionProperty();
    sessionProperty.setSettings(new Settings());
    try {
      if (validateRequest(params)) {
        long date = -1;
        String nickname = "";

        if (params.containsKey("nickname")) {
          nickname = params.get("nickname").toString();
        }
        if(authorization==null){
          sessionProperty.setSessionExpired(true);
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(sessionProperty);
        } else if (token==null) {
          sessionProperty.setSessionExpired(true);
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(sessionProperty);
        }
        String sessionKey = params.get("sessionKey").toString();
        try {
             sessionProperty = videoPlatformService.getVideoPlatformProperties(authorization, token, sessionKey);
             logger.info("Session Property: {}", sessionProperty);
             sessionId= sessionProperty.getSessionId();
        }catch (HttpClientErrorException.Forbidden ex) {
          logger.error("Received 403 Forbidden: {}", ex.getLocalizedMessage());
          sessionProperty.setSessionExpired(true);
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(sessionProperty);
        }
        catch (HttpClientErrorException.NotFound ex) {
          logger.error("Received 404 Session Not fount: {}", ex.getLocalizedMessage());
          sessionProperty.setSessionExpired(true);
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sessionProperty);
        }
        catch (Exception e){
          logger.error("Getting Exception while fetching Session property from videoplatform {}",e);
          sessionProperty.setSessionExpired(true);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sessionProperty);
        }
        Session sessionCreated = null;
        if (validateSession(sessionProperty, sessionKey,sessionIdToSessionContextMap)) {
          logger.info("Going to Create session {} with recordingMode {}",sessionProperty.getSessionId(),sessionProperty.getRecordingMode());
          sessionCreated = this.openviduService.createSession(sessionId, sessionProperty.getRecordingMode());
        } else {
          sessionProperty.setSessionExpired(true);
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(sessionProperty);
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
        boolean iAmSessionCreator=false;
        if(sessionProperty.getSettings().getLayoutType()!=null) {
          int layoutNumber=layoutNumber(sessionProperty.getSettings().getLayoutType(),sessionProperty.getTotalParticipants());
          if(layoutNumber==0){
            sessionProperty.getSettings().setFloatingLayout(false);
          }
          sessionProperty.getSettings().setLayoutNumber(layoutNumber);
        }else{
          sessionProperty.getSettings().setFloatingLayout(false);
        }
        if(sessionProperty.getType().equals("Support")){
          iAmSessionCreator=true;
          sessionProperty.setParticipantName(sessionProperty.getSessionName());
        }
        boolean isSessionCreator = hasModeratorValidToken || iAmSessionCreator;

        OpenViduRole role = isSessionCreator ? OpenViduRole.MODERATOR : OpenViduRole.PUBLISHER;

        Connection cameraConnection = null;
        boolean isOnHold=sessionProperty.getSettings().getHold();
        if (validateParticipantJoined(sessionProperty, sessionCreated,sessionIdToSessionContextMap)) {
          if(sessionCreated!=null) {
            cameraConnection = this.openviduService.createConnection(sessionCreated, nickname, role,isOnHold);
            sessionProperty.setCameraToken(cameraConnection.getToken());
            if (sessionProperty.getSettings().getScreenShare()) {
              Connection screenConnection = this.openviduService.createConnection(sessionCreated, nickname, role,false);
              sessionProperty.setScreenToken(screenConnection.getToken());
            }
          }
        } else {
          sessionProperty.setSessionExpired(true);
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(sessionProperty);
        }
//      Pre-Recorded implementation
        if(sessionProperty.getSettings().getPreRecorded() && !"null".equals(sessionProperty.getSettings().getPreRecordedDetails())){
          HashMap<String,Object> map= (HashMap<String, Object>) sessionProperty.getSettings().getPreRecordedDetails();
            if (!Boolean.TRUE.equals(map.get("share_pre_recorded_video"))) {
              sessionService.autoPlay(sessionCreated, map.get("pre_recorded_video_file").toString(), "prerecorded");
              Connection screenConnection = this.openviduService.createConnection(sessionCreated, nickname, role,false);
              sessionProperty.setScreenToken(screenConnection.getToken());
            } else {
              sessionProperty.getSettings().setFileUrl(OPENVIDU_URL + "/downloadFile/" + map.get("pre_recorded_video_file").toString());
            }
        }else{
        sessionProperty.getSettings().setPreRecorded(false);
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
        sessionProperty.setSessionExpired(true);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sessionProperty);
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
          sessionProperty.setSessionExpired(true);
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(sessionProperty);
        }
      }
  }
  @PostMapping("/sendLink")
  public ResponseEntity<?> sendLink(@RequestBody(required = false) Map<String, Object> params,HttpServletRequest request,HttpServletResponse res) throws HttpException, IOException {
    Map<String,String> headers=getHeaders(request);
    logger.info("Request API /sendLink Headers {} and Parameters {}",headers,params);
    String sessionId=" ";
    if (params.containsKey("sessionId")){
      sessionId=params.get("sessionId").toString();
    }
    ResponseEntity<?> response=videoPlatformService.sendLink(authorization,token,sessionId);
    return response;
  }
  @PostMapping("/updateSession")
  public ResponseEntity<?> updateSession(@RequestBody(required = false) Map<String, Object> params,HttpServletRequest request,HttpServletResponse res) throws HttpException, IOException {
    Map<String,String> headers=getHeaders(request);
    logger.info("Request API /updateSession Headers {} and Parameters {}",headers,params);
    String sessionKey=" ";
    Boolean isOnHold=false;
    if (params.containsKey("sessionKey")){
      sessionKey=params.get("sessionKey").toString();
    }
    if (params.containsKey("isOnHold")){
      isOnHold= (Boolean) params.get("isOnHold");
    }
    ResponseEntity<?> response=videoPlatformService.updateSession(authorization,token,sessionKey,isOnHold);
    return response;
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
//      sessionDestroyed(sessionKey);
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
      List<Connection> filteredConnectionList = connectionList.stream()
              .filter(connection -> {
                String clientData = connection.getClientData();
                if (clientData != null) {
                  JsonObject clientDataJson = JsonParser.parseString(clientData).getAsJsonObject();
                  String type = clientDataJson.get("type").getAsString();
                  return !type.equalsIgnoreCase("SCREEN");
                }
                return true;
              })
              .collect(Collectors.toList());
      logger.info("Active Participant for sessionId is {} and max Participant allowed for this session is {} ", filteredConnectionList.size(), sessionProperty.getTotalParticipants());
      if (filteredConnectionList.size() < sessionProperty.getTotalParticipants()) {
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
  private int layoutNumber(String layoutType,int maxParticipant){
      if(layoutType.equals("Right Layout")){
        return -1;
      }
      if(layoutType.equals("Bottom Layout")){
        return 2;
      }
      if(layoutType.equals("Overlay Layout") && maxParticipant<=2){
        return 1;
      }
      return 0;
  }
}
