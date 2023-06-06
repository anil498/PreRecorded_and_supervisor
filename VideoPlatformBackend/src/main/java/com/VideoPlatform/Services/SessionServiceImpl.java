package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
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
    public SessionEntity createSession(SessionEntity session,String authKey, String token,Boolean moderator) {
        //logger.info("User details {}",session.toString());
        if(session==null) {
            session=new SessionEntity();
            Date creation = CommonUtils.getDate();
            Date newDateTime = CommonUtils.increaseDateTime(creation);
            logger.info("Session Localdatetime = {}", newDateTime);

            UserAuthEntity userAuth = userAuthRepository.findByToken(token);
            AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
            AccountEntity account = accountRepository.findByAccountId(acc.getAccountId());
            UserEntity userEntity = userRepository.findByUserId(userAuth.getUserId());

            Gson gson = new Gson();
//        Map<String,String> userData=new HashMap<>();
//        userData.put("user_id",String.valueOf(userAuth.getUserId()));
//        userData.put("max_active_sessions", userEntity.getSession().get("max_duration").toString());
//        Gson gson=new Gson();
//        String userJson =gson.toJson(userData);
//        logger.info(userJson);
//
//        Map<String,String> accountData=new HashMap<>();
//        accountData.put("account_id",String.valueOf(account.getAccountId()));
//        accountData.put("max_active_sessions", account.getSession().get("max_active_sessions").toString());
//        String accountJson =gson.toJson(accountData);
//        logger.info(accountJson);
            String sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
            String sessionId = account.getAccountId() + "_" + userAuth.getUserId() + "_" + sessionKey;
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
            session.setUserMaxSessions(Integer.valueOf(userEntity.getSession().get("max_duration").toString()));
            session.setAccountMaxSessions(Integer.valueOf(account.getSession().get("max_active_sessions").toString()));
            session.setCreationDate(creation);
            session.setExpDate(newDateTime);
            session.setParticipantName(userEntity.getFname());
            session.setTotalParticipants(Integer.valueOf(account.getSession().get("max_participants").toString()));


            SettingsEntity settingsEntity = new SettingsEntity();

            settingsEntity.setDuration(Integer.valueOf(userEntity.getSession().get("max_duration").toString()));
            for (Integer featureId : userEntity.getFeatures()) {
                if (1 == featureId) {
                    settingsEntity.setRecording(true);
                    settingsEntity.setRecordingDetails(userEntity.getFeaturesMeta().get(featureId.toString()));
                }
                if (2 == featureId) {
                    settingsEntity.setScreenShare(true);
                }
                if (3 == featureId) {
                    settingsEntity.setChat(true);
                }
                if (4 == featureId) {
                    settingsEntity.setPreRecorded(true);
                    String prdJson = gson.toJson(userEntity.getFeaturesMeta().get(featureId.toString()));
                    settingsEntity.setPreRecordedDetails(prdJson);
                }
                if (8 == featureId) {
                    settingsEntity.setFloatingLayout(true);
                }
                if (9 == featureId) {
                    settingsEntity.setSupervisor(true);
                }
                if (11 == featureId) {
                    settingsEntity.setDisplayTimer(true);
                }
                if (14 == featureId) {
                    settingsEntity.setActivitiesButton(true);
                }
                if (15 == featureId) {
                    settingsEntity.setParticipantsButton(true);
                }
            }
            String settingsJson = gson.toJson(settingsEntity);
            session.setSettings(settingsJson);
        }else {
            String sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
            session.setSessionKey(sessionKey);
            if(moderator==false){
                session.setType("Customer");
            }else{
                session.setType("Support");
            }

        }
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

    private Object featureData(Integer userId) throws JsonProcessingException {
        UserEntity user = userRepository.findByUserId(userId);
        Integer[] featuresId = user.getFeatures();
        ObjectMapper objectMapper = new ObjectMapper();
        List<FeatureEntity> featureEntities = new ArrayList<>();

        for (int i = 0; i < featuresId.length; i++) {
            FeatureEntity featureEntity = featureRepository.findByFeatureId(featuresId[i]);
            featureEntities.add(featureEntity);
        }
        return objectMapper.convertValue(featureEntities, JsonNode.class);
    }
    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        logger.info(generatedString);
        System.out.println(generatedString);
        return generatedString;
    }
}
