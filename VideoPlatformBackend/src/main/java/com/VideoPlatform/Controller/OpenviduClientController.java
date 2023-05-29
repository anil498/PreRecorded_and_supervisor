package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICLIENT)
public class OpenviduClientController {

    private static Logger logger= LoggerFactory.getLogger(OpenviduClientController.class);
    @Autowired
    FeatureRepository featureRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountAuthRepository accountAuthRepository;
    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    SessionRepository sessionRepository;

    @PostMapping("/get")
    public ResponseEntity<?> featureList(@RequestBody Map<String,String> params, HttpServletRequest request) throws JsonProcessingException {
        logger.info("API Request {} {}",request,params);
        String authKey = request.getHeader("Authorization");
        int check = isValidAuthKey(authKey);
        logger.info("Check val .. " + check);
        if (check == 0) {
            logger.info("Unauthorised user, wrong authorization key !");
            return new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        String token = request.getHeader("Token");
        Boolean isValid= isValidToken(token);
        if (!isValid) {
            logger.info("Invalid Token !");
            return new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        String sessionKey=null;
        if(params.containsKey("sessionKey")){
            sessionKey=params.get("sessionKey");
        }


        HashMap<String,Object> response=new HashMap<>();
        UserAuthEntity userAuthEntity=userAuthRepository.findByToken(token);
        SessionEntity session = sessionRepository.findBySessionKey(sessionKey);
        SessionEntity sess = sessionRepository.findBySessionSupportKey(sessionKey);
        int userId=0;
        logger.info(String.valueOf(session));
        if(session==null){
            userId=sess.getUserId();
            response.put("session",sessionRepository.findBySessionSupportKey(sessionKey));
        }
        else{
            userId = session.getUserId();
            response.put("session",sessionRepository.findBySessionKey(sessionKey));
        }

        response.put("user",userRepository.findById(userId));
        response.put("feature",featureData(userId));


        return new ResponseEntity<>(response,HttpStatus.OK);
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
    public int isValidAuthKey(String authKey){
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        if(acc == null)return 0;
        if(acc.getExpDate().isBefore(LocalDateTime.now())){
            return 0;
        }
        return 1;
    }
    public Boolean isValidToken(String token){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        if(user == null)return false;

        if(user.getExpDate().isBefore(LocalDateTime.now()))
            return false;
        return true;
    }

}

