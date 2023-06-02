package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    @Override
    public SessionEntity createSession(SessionEntity sess,String authKey, String token) {
        //logger.info("User details {}",session.toString());
        SessionEntity session = new SessionEntity();

        String creation = LocalDateTime.now().format(formatter);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newDateTime = now.plus(callAccessTime, ChronoUnit.HOURS);
        logger.info("Session Localdatetime = {}",newDateTime);

        UserAuthEntity userAuth = userAuthRepository.findByToken(token);
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        AccountEntity account = accountRepository.findByAccountId(acc.getAccountId());
        UserEntity userEntity = userRepository.findByUserId(userAuth.getUserId());

        Map<String,String> userData=new HashMap<>();
        userData.put("user_id",String.valueOf(userAuth.getUserId()));
        userData.put("max_active_sessions", userEntity.getSession().get("max_duration").toString());
        Gson gson=new Gson();
        String userJson =gson.toJson(userData);
        logger.info(userJson);

        Map<String,String> accountData=new HashMap<>();
        accountData.put("account_id",String.valueOf(account.getAccountId()));
        accountData.put("max_active_sessions", account.getSession().get("max_active_sessions").toString());
        String accountJson =gson.toJson(accountData);
        logger.info(accountJson);

        String sessionId=account.getAccountId()+"_"+userAuth.getUserId()+"_"+System.currentTimeMillis();
        String sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();

        session.setSessionId(sessionId);
        session.setSessionKey(sessionKey);
        session.setSessionName(account.getName());
        session.setUserI(userJson);
        session.setAccountI(accountJson);
        session.setParticipantName(sess.getParticipantName());
        session.setParticipants(sess.getParticipants());
        session.setCreationDate(creation);
        session.setExpDate(newDateTime);


        SettingsEntity settingsEntity = new SettingsEntity();

        settingsEntity.setDuration(Integer.valueOf(userEntity.getSession().get("max_duration").toString()));
        for (Integer featureId:userEntity.getFeatures()){
            if(1 == featureId){
                settingsEntity.setRecording(true);
                String recordingJson = gson.toJson(userEntity.getFeaturesMeta().get(featureId.toString()));
                settingsEntity.setRecordingDetails(recordingJson);
            }
            if(2 == featureId){
                settingsEntity.setScreenShare(true);
            }
            if(3 == featureId){
                settingsEntity.setChat(true);
            }
            if(4 == featureId) {
                settingsEntity.setPreRecorded(true);
                String prdJson = gson.toJson(userEntity.getFeaturesMeta().get(featureId.toString()));
                settingsEntity.setPreRecordedDetails(prdJson);
            }
            if(6 == featureId) {
            }
            if(7 == featureId) {
            }
            if(8 == featureId) {
                settingsEntity.setFloatingLayout(true);
            }
            if(9 == featureId) {
                settingsEntity.setSupervisor(true);
            }
            if(10 == featureId) {
            }
            if(11 == featureId) {
                settingsEntity.setDisplayTimer(true);
            }
            if(12 == featureId) {
            }
            if(13 == featureId) {
            }

        }

        //settingsEntity.setRecording(userEntity.getFeatures()[0].);
        session.setSettings(String.valueOf(settingsEntity));
        logger.info("Session : {}",session);
        return sessionRepository.save(session);
    }

    @Override
    public List<SessionEntity> getAllSessions() {
        return sessionRepository.findAll();
    }

//    @Override
//    public Map<String,Object> getByKey(String key, UserAuthEntity user) {
//        //logger.info(String.valueOf(userRepository.findById(id)));
//        HashMap<String,Object> response=new HashMap<>();
//
//        SessionEntity session = sessionRepository.findBySessionKey(key);
//        SessionEntity sess = sessionRepository.findBySessionSupportKey(key);
//        int userId=0;
//        if(sess == null) {
//            userId = session.getUserId();
//            response.put("session",sessionRepository.findBySessionKey(key));
//        }
//        else if(session == null) {
//            userId = sess.getUserId();
//            response.put("session",sessionRepository.findBySessionSupportKey(key));
//        }
//        else{
//            logger.info("Invalid session key !");
//            return null;
//        }
//
//        response.put("user",userRepository.findById(userId));
//        try {
//            response.put("feature",featureData(userId));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        return response;
//    }
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
