package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.FeatureEntity;
import com.VideoPlatform.Repository.FeatureRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@Service
public class FeatureServiceImpl implements FeatureService{
    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private FeatureRepository featureRepository;

    @Override
    public FeatureEntity createFeature(FeatureEntity featureEntity){
        if(featureRepository.findByFeatureId(featureEntity.getFeatureId())!=null) return null;
        return featureRepository.save(featureEntity);
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
        try {
            existing.setName(objectMapper.readValue(params.get("name").toString(),String.class));
            logger.info("MetaList val {}",params.get("metaList").toString());
            existing.setMetaList(objectMapper.readValue(params.get("metaList").toString(), JsonNode.class));
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
