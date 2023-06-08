package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.TimeUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value(("${call.access.time}"))
    private int callAccessTime;

    @Override
    public SessionEntity createSession(String authKey, String token,Boolean moderator, String sessionId, String sessionKey, String description, String participantName) {

        SessionEntity session=new SessionEntity();
            Date creation = TimeUtils.getDate();
            Date expDate = TimeUtils.increaseDateTimeSession(creation);
            logger.info("Session Localdatetime = {}", expDate);

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
            session.setParticipantName(participantName);
            session.setTotalParticipants(Integer.valueOf(userEntity.getSession().get("max_participants").toString()));

            SettingsEntity settingsEntity = new SettingsEntity();

            settingsEntity.setDuration(Integer.valueOf(userEntity.getSession().get("max_duration").toString()));
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
                else if (4 == featureId) {
                    settingsEntity.setPreRecorded(true);
                    String prdJson = gson.toJson(userEntity.getFeaturesMeta().get(featureId.toString()));
                    settingsEntity.setPreRecordedDetails(prdJson);
                }
                else if (5 == featureId && moderator==false) {
                    settingsEntity.setDisplayTicker(true);
                    settingsEntity.setDescription(userEntity.getFeaturesMeta().get(featureId.toString()));
                }
                else if (6 == featureId && moderator==true) {
                    settingsEntity.setDisplayTicker(true);
                    settingsEntity.setDescription(userEntity.getFeaturesMeta().get(featureId.toString()));
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
                else if (10 == featureId) {
                    settingsEntity.setPreRecordedDetails(userEntity.getFeaturesMeta().get(featureId.toString()));
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
                    settingsEntity.setLandingPage(userEntity.getFeaturesMeta().get(featureId.toString()));
                }
                else if (15 == featureId && moderator==false) {
                    settingsEntity.setLandingPage(userEntity.getFeaturesMeta().get(featureId.toString()));
                }
            }
            String settingsJson = gson.toJson(settingsEntity);
            session.setSettings(settingsJson);

        logger.info("Session : {}",session);
        sessionRepository.save(session);
        return session;
    }

    @Override
    public List<SessionEntity> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public SessionEntity getByKey(String key) {
        return sessionRepository.findBySessionKey(key);
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
