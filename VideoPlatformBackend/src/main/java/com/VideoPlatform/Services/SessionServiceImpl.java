package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public SessionEntity createSession(SessionEntity session, String authKey, String token) {
        logger.info("User details {}",session.toString());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newDateTime = now.plus(callAccessTime, ChronoUnit.HOURS);
        logger.info("Session Localdatetime = {}",newDateTime);
        UserAuthEntity userAuth = userAuthRepository.findByToken(token);
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        AccountEntity account = accountRepository.findByAccountId(acc.getAccountId());
        UserEntity userEntity = userRepository.findByUserId(userAuth.getUserId());
        session.setSessionName(account.getName());
        session.setUserId(userAuth.getUserId());
        session.setMobile(userEntity.getContact());
        session.setAccountId(acc.getAccountId());
        session.setStatus("1");
        String sessionId=acc.getAccountId()+"_"+userAuth.getUserId()+"_"+System.currentTimeMillis();
        session.setSessionId(sessionId);
        String sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        session.setSessionKey(sessionKey);
        String supportKey = sessionKey+"_1";
        session.setSessionSupportKey(supportKey);
        session.setUserInfo(session.getUserInfo());
        session.setExpDate(newDateTime);
        String creation = LocalDateTime.now().format(formatter);
        session.setCreation(creation);

        return sessionRepository.save(session);
    }

    @Override
    public List<SessionEntity> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public Map<String,Object> getByKey(String key, UserAuthEntity user) {
        //logger.info(String.valueOf(userRepository.findById(id)));
        HashMap<String,Object> response=new HashMap<>();

        SessionEntity session = sessionRepository.findBySessionKey(key);
        SessionEntity sess = sessionRepository.findBySessionSupportKey(key);
        int userId=0;
        if(sess == null) {
            userId = session.getUserId();
            response.put("session",sessionRepository.findBySessionKey(key));
        }
        else if(session == null) {
            userId = sess.getUserId();
            response.put("session",sessionRepository.findBySessionSupportKey(key));
        }
        else{
            logger.info("Invalid session key !");
            return null;
        }

        response.put("user",userRepository.findById(userId));
        try {
            response.put("feature",featureData(userId));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return response;
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
