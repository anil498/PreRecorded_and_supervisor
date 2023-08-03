package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Models.SubmitResponse;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.TimeUtils;
import com.google.gson.Gson;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class SessionServiceImpl implements SessionService{

    private static final Logger logger= LoggerFactory.getLogger(SessionService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    MessagingService messagingService;
    @Autowired
    private IcdcRepository icdcRepository;
    @Value("${call.prefix}")
    private String callPrefix;

    @Override
    public SessionEntity createSession(String authKey, String token,Boolean moderator, String sessionId, String sessionKey, String description, String participantName, String type) {

        SessionEntity session=new SessionEntity();
        Date creation = TimeUtils.getDate();
        Date expDate = TimeUtils.increaseDateTimeSession(creation);
        logger.info("Session ExpDate = {}", expDate);

        UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
        AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAuthKey(authKey);
        AccountEntity accountEntity = accountRepository.findByAccountId(accountAuthEntity.getAccountId());
        UserEntity userEntity = userRepository.findByUserId(userAuthEntity.getUserId());

        if(userAuthEntity==null || accountAuthEntity==null || accountEntity==null || userEntity==null){
            logger.info("Don't have required inputs to create session!");
            return null;
        }

        if(userEntity.getFeaturesMeta().containsKey("4")){
            HashMap<String,Object> fMeta = (HashMap<String, Object>) userEntity.getFeaturesMeta().get("4");
            if(moderator==true) {
                if (fMeta==null || fMeta.isEmpty()){
                    logger.info("Pre recorded video property is not present !");
                }
                else if(fMeta.containsKey("share_pre_recorded_video")){
                    if(fMeta.get("share_pre_recorded_video").equals(false)){
                        return null;
                    }
                }
            }
        }

        Gson gson = new Gson();
        if(type.equalsIgnoreCase("Customer")){
            session.setType("Customer");
            sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        }
        else if(type.equalsIgnoreCase("Supervisor")){
                session.setType("Supervisor");
                sessionKey = sessionKey+"_2";
        }
        else {
                session.setType("Support");
                sessionKey = sessionKey + "_1";
        }

        if(sessionId.length()==0)
            sessionId = accountEntity.getAccountId() + "_" + userAuthEntity.getUserId() + "_" + sessionKey;

        session.setSessionId(sessionId);
        session.setSessionKey(sessionKey);
        session.setSessionName(accountEntity.getName());
        session.setUserId(userAuthEntity.getUserId());
        session.setAccountId(accountEntity.getAccountId());
        if(userEntity.getSession().containsKey("max_active_sessions"))
            session.setUserMaxSessions(Integer.valueOf(userEntity.getSession().get("max_active_sessions").toString()));
        if(accountEntity.getSession().containsKey("max_active_sessions"))
            session.setAccountMaxSessions(Integer.valueOf(accountEntity.getSession().get("max_active_sessions").toString()));
        session.setCreationDate(creation);
        session.setExpDate(expDate);
        if(participantName==null){
            session.setParticipantName("");
        }else{
            session.setParticipantName(participantName);
        }

        SettingsEntity settingsEntity = new SettingsEntity();

        if(userEntity.getSession().containsKey("max_participants"))
            session.setTotalParticipants(Integer.valueOf(userEntity.getSession().get("max_participants").toString()));
        if(userEntity.getSession().containsKey("max_duration"))
            settingsEntity.setDuration(Integer.valueOf(userEntity.getSession().get("max_duration").toString()));
        if (userEntity.getLogo() != null) {
            settingsEntity.setLogo(userEntity.getLogo());
        }
        else if(accountEntity.getLogo() != null) {
            settingsEntity.setLogo(accountEntity.getLogo());
        }
        else{
            settingsEntity.setLogo("");
        }
        for (Integer featureId : userEntity.getFeatures()) {
            if (1 == featureId) {
                settingsEntity.setRecording(true);
                settingsEntity.setRecordingDetails(userEntity.getFeaturesMeta().get(featureId.toString()));
            }
            else if (2 == featureId) {
                if(type.equalsIgnoreCase("Supervisor")){
                    settingsEntity.setScreenShare(false);
                }else {
                    settingsEntity.setScreenShare(true);
                    try {
                        Map<String, Boolean> map = (Map<String, Boolean>) (userEntity.getFeaturesMeta().get(featureId.toString()));
                        if(map.containsKey("share_with_audio"))
                            settingsEntity.setScreenShareWithAudio(map.get("share_with_audio"));
                    } catch (Exception e) {
                        logger.info("Getting null value from participants_ticker_text 2 !");
                    }
                }
            }
            else if (3 == featureId) {
                settingsEntity.setChat(true);
            }
            else if (4 == featureId) {
                HashMap<String,Object> fmeta = (HashMap<String, Object>) userEntity.getFeaturesMeta().get(featureId.toString());
                if(type.equalsIgnoreCase("Supervisor")){
                    settingsEntity.setPreRecorded(false);
                }
                else {
                    if (moderator == true) {
                        settingsEntity.setPreRecorded(true);
                        try {
                            logger.info("Pre recorded val : {}", userEntity.getFeaturesMeta().get(featureId.toString()));
                            settingsEntity.setPreRecordedDetails(userEntity.getFeaturesMeta().get(featureId.toString()));
                        } catch (Exception e) {
                            logger.info("Getting null value from pre_recorded_video_file!", e);
                        }
                    } else if (fmeta.containsKey("share_pre_recorded_video")){
                        if (fmeta.get("share_pre_recorded_video").equals(false)) {
                            settingsEntity.setPreRecorded(true);
                            try {
                                logger.info("Pre recorded val : {}", userEntity.getFeaturesMeta().get(featureId.toString()));
                                settingsEntity.setPreRecordedDetails(userEntity.getFeaturesMeta().get(featureId.toString()));
                            } catch (Exception e) {
                                logger.info("Getting null value from pre_recorded_video_file!", e);
                            }
                        }
                    }
                }
            }
            else if (5 == featureId && moderator==false) {
                settingsEntity.setDisplayTicker(true);
                try{
                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
                    if(map.containsKey("participants_ticker_text"))
                        settingsEntity.setDescription(map.get("participants_ticker_text"));
                }
                catch (Exception e){
                    logger.info("Getting null value from participants_ticker_text 2 !");
                }
            }
            else if (6 == featureId && moderator==true) {
                settingsEntity.setDisplayTicker(true);
                try{
                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
                    if(map.containsKey("admin_ticker_text"))
                        settingsEntity.setDescription(map.get("admin_ticker_text"));
                }
                catch (Exception e){
                    logger.info("Getting null value from admin_ticker_text !");
                }
            }
            else if (7 == featureId && moderator==true) {
                settingsEntity.setDescription(description);
            }
            else if (8 == featureId) {
                settingsEntity.setFloatingLayout(true);
                try{
                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
                    if(map.containsKey("customised_layout"))
                        settingsEntity.setLayoutType(map.get("customised_layout"));
                }
                catch (Exception e){
                    logger.info("Getting null value from custom layout !");
                }
            }
            else if (9 == featureId && moderator==true) {
                settingsEntity.setSupervisor(true);
            }
            else if (11 == featureId) {
                settingsEntity.setDisplayTimer(true);
            }
            else if (12 == featureId) {
                settingsEntity.setActivitiesButton(true);
            }
            else if (13 == featureId) {
                settingsEntity.setParticipantsButton(true);
            }
            else if (14 == featureId && moderator==true) {
                try{
                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
                    if(map.containsKey("admin_landing_page"))
                        settingsEntity.setLandingPage(map.get("admin_landing_page"));
                }
                catch (Exception e){
                    logger.info("Getting null value from admin_landing_page !");
                }
            }
            else if (15 == featureId && moderator==false) {
                try{
                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
                    if(map.containsKey("admin_landing_page"))
                        settingsEntity.setLandingPage(map.get("participants_landing_page"));
                }
                catch (Exception e){
                    logger.info("Getting null value from participants_landing_page !");
                }
            }
            else if (16 == featureId) {
                try {
                    Map<String, Object> map1 = (Map<String, Object>) (userEntity.getFeaturesMeta().get(featureId.toString()));
                    Map<String, Object> map2 = map1;
                    Map<String, Object> ques = new HashMap<>();

                    if (type.equalsIgnoreCase("Customer")) {
                        map1.replace("icdc", false);
                        map1.replace("display_icdc", true);
                        map1.replace("edit_icdc", true);
                        map1.replace("title_icdc", "ICDC Panel");
                        settingsEntity.setIcdcDetails(map1);
                    } else if (type.equalsIgnoreCase("Support")) {
                        map2.replace("icdc", true);
                        map2.replace("display_icdc", true);
                        map2.replace("edit_icdc", false);
                        map2.replace("title_icdc", "ICDC Panel");
                        settingsEntity.setIcdcDetails(map2);
                    } else {
                        map2.replace("icdc", false);
                        map2.replace("display_icdc", true);
                        map2.replace("edit_icdc", false);
                        map2.replace("title_icdc", "ICDC Panel");
                        logger.info("Map1 : {}", map2);
                        settingsEntity.setIcdcDetails(map2);
                    }
                        List<Map<String, Object>> icdcData = icdcRepository.findNamesByUserId(userEntity.getUserId());
                        logger.info("ICDC DATA : {}", icdcData);
                        settingsEntity.setIcdcQuestions(icdcData);
                }
                catch (Exception e){
                    logger.error("Getting exception while setting object for featureId 16 i.e. icdc {}",e.getMessage());
                }
            }
        }
        String settingsJson = gson.toJson(settingsEntity);
        session.setSettings(settingsJson);

        logger.info("Session : {}",session);
        sessionRepository.save(session);
        return session;
    }

    @Override
    public List<SessionEntity> getAllSupportSessionsUser(String token) {
        UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
        Integer userId = userAuthEntity.getUserId();
        return sessionRepository.findSupportSessions(userId);
    }
    @Override
    public List<SessionEntity> getAllSupportSessions() {
        return sessionRepository.findAllSupportSessions();
    }

    @Override
    public SessionEntity getByKey(String key) {
        SessionEntity sessionEntity = sessionRepository.findBySessionKey(key);
        if(sessionEntity == null) return null;
        if(TimeUtils.isExpire(sessionEntity.getExpDate())) {
            return null;
        }
        return sessionEntity;
    }
    @Override
    public ResponseEntity<?> deleteSession(String sessionKey) {
        sessionRepository.deleteSession(sessionKey);
        return new ResponseEntity<>(commonService.responseData("200","Session Deleted!"),HttpStatus.OK);
    }
    @Override
    public void updateHold(Map<String, Object> params){
        String sessionKey = (String) params.get("sessionKey");
        Boolean hold = (Boolean) params.get("hold");
        SessionEntity sessionEntity = sessionRepository.findBySessionKey(sessionKey);
        if(sessionEntity==null)return;
        sessionRepository.updateHold(sessionKey,hold);
    }

    @Override
    public ResponseEntity<?> sendLink(Map<String,?> params, HttpServletRequest request, HttpServletResponse response){
        logger.info("Params Values : {} ",params);
        String sessionId = (String) params.get("sessionId");
        String type = "Customer";
        if(params.containsKey("type")) {
            type = (String) params.get("type");
        }
        SessionEntity sessionEntity = sessionRepository.findBySessionId(sessionId,"Customer");
        UserAuthEntity userAuthEntity = userAuthRepository.findByUId(sessionEntity.getUserId());
        SessionEntity sessionEntitySupervisor = null;
        if(sessionEntity==null){
            logger.info("Invalid sessionId");
            return new ResponseEntity<>(commonService.responseData("401","Invalid sessionId"),HttpStatus.UNAUTHORIZED);

        }
        String contacts="";
        List <String> contactArray = new ArrayList<>();
        if(params.containsKey("contactArray")){
            contacts = (String) params.get("contactArray");
            String[] myArray1 = contacts.toString().split(",");
            System.out.println("Contents of the array ::"+Arrays.toString(myArray1));
            contactArray = Arrays.asList(myArray1);
            logger.info("Contact list is : {}",contactArray);
            if(type.equalsIgnoreCase("Supervisor")){
              sessionEntitySupervisor = createSession(userAuthEntity.getAuthKey(),userAuthEntity.getToken(),true,sessionId,sessionEntity.getSessionKey(),"","Supervisor","Supervisor");
            }
        }
        else{
            UserEntity userEntity = userRepository.findByUserId(sessionEntity.getUserId());
            HashMap<String,Object> map = (HashMap<String, Object>) userEntity.getFeaturesMeta().get("9");
            if(map.containsKey("supervisor_contacts") && type.equalsIgnoreCase("Supervisor")){
                String[] myArray1 = map.get("supervisor_contacts").toString().split(",");
                System.out.println("Contents of the array ::"+Arrays.toString(myArray1));
                contactArray = Arrays.asList(myArray1);
                sessionEntitySupervisor = createSession(userAuthEntity.getAuthKey(),userAuthEntity.getToken(),true,sessionId,sessionEntity.getSessionKey(),"","Supervisor","Supervisor");
            }
            else if(map.containsKey("customer_contacts") && type.equals("Customer")){
                String[] myArray2 = map.get("customer_contacts").toString().split(",");
                System.out.println("Contents of the array ::"+Arrays.toString(myArray2));
                contactArray = Arrays.asList(myArray2);
            }
            else{
                logger.info("contact list not present in feature meta.");
                return new ResponseEntity<>(commonService.responseData("400","Getting null or invalid value from parameters."),HttpStatus.BAD_REQUEST);
            }
        }
        String sendTo = "whatsapp";
        if(params.containsKey("sendTo")){
            sendTo = (String) params.get("sendTo");
        }
        String callUrl="";
        if(type.equalsIgnoreCase("Supervisor"))
            callUrl = callPrefix+sessionEntitySupervisor.getSessionKey();
        else callUrl = callPrefix+sessionEntity.getSessionKey();

        if(sendTo.equals("sms")){
            for(String contact : contactArray){
                try {
                    SubmitResponse responseSms = messagingService.sendSms(request,response,contact,callUrl);
                    responseSms.setCallUrl(callUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        }
        String type1 = "template";
        String from = "919811026184";
        String templateId = "53571";
        if(sendTo.equals("whatsapp")) {
            for (String contact : contactArray) {

                try {
                    SubmitResponse responseSms = messagingService.sendWA(request,response,contact,callUrl,from,type1,templateId);
                    responseSms.setCallUrl(callUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (OpenViduJavaClientException e) {
                    e.printStackTrace();
                } catch (OpenViduHttpException e) {
                    e.printStackTrace();
                }

            }
        }
        return new ResponseEntity<>(commonService.responseData("200","Link sent successfully!"),HttpStatus.OK);
    }

    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        if(sessionRepository.findBySessionKey(generatedString) == null)
            return generatedString;
        else givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        return null;
    }
}
