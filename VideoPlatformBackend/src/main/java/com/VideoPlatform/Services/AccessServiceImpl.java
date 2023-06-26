package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccessEntity;
import com.VideoPlatform.Repository.AccessRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@Service
public class AccessServiceImpl implements AccessService {
    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private AccessRepository accessRepository;

    @Override
    public AccessEntity createAccess(AccessEntity accessEntity){
        return accessRepository.save(accessEntity);
    }
    @Override
    public List<AccessEntity> getAllAccess(){
        return accessRepository.findAll();
    }
    @Override
    public ResponseEntity<?> updateAccess(String params1) {
        logger.info("Params Update : {}",params1);

        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        AccessEntity existing = accessRepository.findByAccessId(params.get("accessId").getAsInt());
        try {
            existing.setName(objectMapper.readValue(params.get("name").toString(),String.class));
            existing.setSystemName(objectMapper.readValue(params.get("systemName").toString(),String.class));
            existing.setpId(objectMapper.readValue(params.get("pId").toString(),Integer.class));
            existing.setSeq(objectMapper.readValue(params.get("seq").toString(),Integer.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        accessRepository.save(existing);
        Map<String, String> result = new HashMap<>();
        result.put("status_code", "200");
        result.put("msg", "Access updated!");
        return ok(result);
    }

    @Override
    public void deleteAccess(Integer accessId){
        accessRepository.deleteAccess(accessId);
    }

}
