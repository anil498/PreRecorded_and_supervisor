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
    private FeatureRepository featureRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    MessagingService messagingService;
    @Value("${call.prefix}")
    private String callPrefix;

    @Override
    public SessionEntity createSession(String authKey, String token,Boolean moderator, String sessionId, String sessionKey, String description, String participantName) {

        SessionEntity session=new SessionEntity();
        Date creation = TimeUtils.getDate();
        Date expDate = TimeUtils.increaseDateTimeSession(creation);
        logger.info("Session ExpDate = {}", expDate);

        UserAuthEntity userAuth = userAuthRepository.findByToken(token);
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        AccountEntity account = accountRepository.findByAccountId(acc.getAccountId());
        UserEntity userEntity = userRepository.findByUserId(userAuth.getUserId());

//        HashMap<String,Object> fMeta = (HashMap<String, Object>) userEntity.getFeaturesMeta().get("4");
//        if(moderator==true)
//        if(fMeta.get("share_pre_recorded_video").equals(false)){
//            return null;
//        }

        Gson gson = new Gson();
        if(moderator==false){
            session.setType("Customer");
            sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        }
        else{
            if(participantName.equals("Supervisor")){
                session.setType("Supervisor");
                sessionKey = sessionKey+"_2";
            }
            else {
                session.setType("Support");
                sessionKey = sessionKey + "_1";
            }
        }
//        if (sessionKey.length()==0)
//            sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
//        else
//            sessionKey = sessionKey +"_1";
        if(sessionId.length()==0)
            sessionId = account.getAccountId() + "_" + userAuth.getUserId() + "_" + sessionKey;

        session.setSessionId(sessionId);
        session.setSessionKey(sessionKey);
        session.setSessionName(account.getName());
        session.setUserId(userAuth.getUserId());
        session.setAccountId(account.getAccountId());
        session.setUserMaxSessions(Integer.valueOf(userEntity.getSession().get("max_active_sessions").toString()));
        session.setAccountMaxSessions(Integer.valueOf(account.getSession().get("max_active_sessions").toString()));
        session.setCreationDate(creation);
        session.setExpDate(expDate);
        if(participantName==null){
            session.setParticipantName("");
        }else{
            session.setParticipantName(participantName);
        }

        SettingsEntity settingsEntity = new SettingsEntity();

        session.setTotalParticipants(Integer.valueOf(userEntity.getSession().get("max_participants").toString()));
        settingsEntity.setDuration(Integer.valueOf(userEntity.getSession().get("max_duration").toString()));
        if (userEntity.getLogo() != null) {
            settingsEntity.setLogo(userEntity.getLogo());
        }
        else if(account.getLogo() != null) {
            settingsEntity.setLogo(account.getLogo());
        }
        else{
            settingsEntity.setLogo("{}");
        }
        for (Integer featureId : userEntity.getFeatures()) {
            if (1 == featureId) {
                settingsEntity.setRecording(true);
                settingsEntity.setRecordingDetails(userEntity.getFeaturesMeta().get(featureId.toString()));
            }
            else if (2 == featureId) {
                settingsEntity.setScreenShare(true);
            }
            else if (3 == featureId) {
                settingsEntity.setChat(true);
            }
            else if (4 == featureId && moderator==true) {
                settingsEntity.setPreRecorded(true);
                try{
                    logger.info("Pre recorded val : {}",userEntity.getFeaturesMeta().get(featureId.toString()));
                    settingsEntity.setPreRecordedDetails(userEntity.getFeaturesMeta().get(featureId.toString()));
                }
                catch (Exception e){
                    logger.info("Getting null value from pre_recorded_video_file 1 !",e);
                }
            }
            else if (5 == featureId && moderator==false) {
                settingsEntity.setDisplayTicker(true);
                try{
                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
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
                    settingsEntity.setLayoutType(map.get("customised_layout"));
                }
                catch (Exception e){
                    logger.info("Getting null value from custom layout !");
                }
            }
            else if (9 == featureId && moderator==true) {
                settingsEntity.setSupervisor(true);
            }
//            else if (10 == featureId) {
//                try{
//                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
//                    settingsEntity.setPreRecordedDetails(map.get("pre_recorded_video_file"));
//                }
//                catch (Exception e){
//                    logger.info("Getting null value from pre_recorded_video_file !");
//                }
//            }
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
                    settingsEntity.setLandingPage(map.get("admin_landing_page"));
                }
                catch (Exception e){
                    logger.info("Getting null value from admin_landing_page !");
                }
            }
            else if (15 == featureId && moderator==false) {
                try{
                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
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
                    if(moderator == false){
                        map1.replace("icdc",false);
                        map1.replace("display_icdc",true);
                        map1.replace("edit_icdc",true);
                        map1.replace("title_icdc","ICDC Panel");

                        logger.info("Map1 : {}",map1);
                        settingsEntity.setIcdcDetails(map1);
                    }
                    else {
                        map2.replace("icdc",true);
                        map2.replace("display_icdc",true);
                        map2.replace("edit_icdc",false);
                        map2.replace("title_icdc","ICDC Panel");

                        logger.info("Map1 : {}",map2);
                        settingsEntity.setIcdcDetails(map2);
                    }
                }
                catch (Exception e){
                    logger.info("Getting null value from title_icdc !");
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
    public List<SessionEntity> getAllSupportSessions(String authKey,String token) {
        UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
        Integer userId = userAuthEntity.getUserId();
        return sessionRepository.findSupportSessions(userId);
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
    public String deleteSession(String sessionKey) {
        sessionRepository.deleteSession(sessionKey);
        return "Session deleted";
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
    public ResponseEntity<?> sendLink(String authKey, String token, Map<String,?> params, HttpServletRequest request, HttpServletResponse response){
        logger.info("Params Values : {} ",params);
        String sessionId = (String) params.get("sessionId");
        String type = "Customer";
        if(params.containsKey("type")) {
            type = (String) params.get("type");
        }
        SessionEntity sessionEntity = sessionRepository.findBySessionId(sessionId,"Customer");
        if(sessionEntity==null){
            logger.info("Invalid sessionId");
            return new ResponseEntity<>(commonService.responseData("401","Invalid sessionId"),HttpStatus.UNAUTHORIZED);

        }
        List<String> contactArray = new ArrayList<>(){};
        if(params.containsKey("contactArray")){
            contactArray = (List<String>) params.get("contactArray");
            logger.info("Contact list is : {}",contactArray);
            if(type.equalsIgnoreCase("Supervisor")){
                createSession(authKey,token,true,sessionId,sessionEntity.getSessionKey(),"","Supervisor");
            }
        }
        else{
            UserEntity userEntity = userRepository.findByUserId(sessionEntity.getUserId());
            HashMap<String,Object> map = (HashMap<String, Object>) userEntity.getFeaturesMeta().get("9");
            if(map.containsKey("supervisor_contacts") && type.equalsIgnoreCase("Supervisor")){
                contactArray = (List<String>) map.get("supervisor_contacts");
                createSession(authKey,token,true,sessionId,sessionEntity.getSessionKey(),"","Supervisor");

            }
            else if(map.containsKey("customer_contacts") && type.equals("Customer")){
                contactArray = (List<String>) map.get("customer_contacts");
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
        sessionEntity = sessionRepository.findBySessionId(sessionId,"Supervisor");
        String callUrl = callPrefix+sessionEntity.getSessionKey();
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
