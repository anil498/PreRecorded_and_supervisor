package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.FeatureEntity;
import com.VideoPlatform.Entity.SessionEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.FeatureRepository;
import com.VideoPlatform.Repository.SessionRepository;
import com.VideoPlatform.Repository.UserAuthRepository;
import com.VideoPlatform.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SessionServiceImpl implements SessionService{

    private static final Logger logger= LoggerFactory.getLogger(SessionService.class);

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    FeatureRepository featureRepository;

    @Override
    public SessionEntity createSession(SessionEntity session) {
        logger.info("User details {}",session.toString());
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
}
