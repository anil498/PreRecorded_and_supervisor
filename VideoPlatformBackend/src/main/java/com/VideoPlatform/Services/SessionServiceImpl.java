package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.TimeUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            Gson gson = new Gson();
            if (sessionKey.length()==0)
                sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
            else
                sessionKey = sessionKey +"_1";
            if(sessionId.length()==0)
                sessionId = account.getAccountId() + "_" + userAuth.getUserId() + "_" + sessionKey;
            if(moderator==false){
                session.setType("Customer");
            }else{
                session.setType("Support");
            }
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
//                else if (4 == featureId) {
//                    settingsEntity.setPreRecorded(true);
//                    try{
//                        Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
//                        settingsEntity.setPreRecordedDetails(map.get("pre_recorded_video_file"));
//                    }
//                    catch (Exception e){
//                        logger.info("Getting null value from pre_recorded_video_file !");
//                    }
//
//                }
                else if (5 == featureId && moderator==false) {
                    settingsEntity.setDisplayTicker(true);
                    try{
                    Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
                    settingsEntity.setDescription(map.get("participants_ticker_text"));
                    }
                    catch (Exception e){
                    logger.info("Getting null value from participants_ticker_text !");
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
                }
                else if (9 == featureId) {
                    settingsEntity.setSupervisor(true);
                }
//                else if (10 == featureId) {
//                    try{
//                        Map<String,String> map= (Map<String, String>) (userEntity.getFeaturesMeta().get(featureId.toString()));
//                        settingsEntity.setPreRecordedDetails(map.get("pre_recorded_video_file"));
//                    }
//                    catch (Exception e){
//                        logger.info("Getting null value from pre_recorded_video_file !");
//                    }
//                }
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

    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        if(sessionRepository.findBySessionKey(generatedString) == null)
            return generatedString;
        else givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        return null;
    }
}
