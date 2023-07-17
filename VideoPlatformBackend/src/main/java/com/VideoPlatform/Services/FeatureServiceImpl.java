package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@Service
public class FeatureServiceImpl implements FeatureService{
    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public String createFeature(FeatureEntity featureEntity,String authKey, String token){
        if(featureRepository.findByFeatureId(featureEntity.getFeatureId())!=null) return null;
        featureRepository.save(featureEntity);
        UserAuthEntity userAuthEntity = userAuthRepository.findByTokenAndAuthKey(token,authKey);
        if(userAuthEntity == null) return null;
        UserEntity userEntity = userRepository.findByUserId(userAuthEntity.getUserId());
        if(userEntity==null) return null;
        AccountEntity accountEntity = accountRepository.findByAccountId(userEntity.getAccountId());
        if(accountEntity==null) return null;
        List<Integer> features = new ArrayList<>(Arrays.asList(userEntity.getFeatures()));
        logger.info("Features : {}",features);
        Integer f = featureEntity.getFeatureId();
        logger.info("featureEntity.getFeatureId() :  {} ",featureEntity.getFeatureId());
        features.add(f);
        Integer[] featureId = features.toArray(new Integer[0]);
        logger.info("Updated features  : {},{}",features,featureId);
        accountEntity.setFeatures(featureId);
        accountRepository.save(accountEntity);
        userEntity.setFeatures(featureId);
        userRepository.save(userEntity);
        return "";
    }

    @Override
    public List<FeatureEntity> getAllFeatures(){
        return featureRepository.findAll();
    }

    @Override
    public ResponseEntity<?> updateFeature(String params1) {
        logger.info("Params Update : {}",params1);

        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        FeatureEntity existing = featureRepository.findByFeatureId(params.get("featureId").getAsInt());
        if(existing == null){
            logger.info("Feature Id doesn't exist !");
            return new ResponseEntity<>("Feature Id doesn't exist !",HttpStatus.UNAUTHORIZED);
        }
        try {
            if(!params.get("name").isJsonNull())
                existing.setName(objectMapper.readValue(params.get("name").toString(),String.class));
            logger.info("MetaList val {}",params.get("metaList").toString());
            if(!params.get("metaList").isJsonNull())
                existing.setMetaList(objectMapper.readValue(params.get("metaList").toString(), JsonNode.class));
            if(!params.get("status").isJsonNull())
                existing.setStatus(params.get("status").getAsInt());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        featureRepository.save(existing);
        Map<String, String> result = new HashMap<>();
        result.put("status_code", "200");
        result.put("msg", "Feature updated!");
        return ok(result);
    }

    @Override
    public void deleteFeature(Integer featureId){
        featureRepository.deleteAccess(featureId);
    }

}
