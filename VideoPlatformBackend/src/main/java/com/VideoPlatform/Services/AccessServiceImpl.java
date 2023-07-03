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
        if(accessRepository.findByAccessId(accessEntity.getAccessId())!=null) return null;
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
        if(existing == null){
            logger.info("Access Id doesn't exist !");
            return new ResponseEntity<>("Access Id doesn't exist !",HttpStatus.UNAUTHORIZED);
        }
        try {
            if(!params.get("name").isJsonNull())
                existing.setName(objectMapper.readValue(params.get("name").toString(),String.class));
            if(!params.get("systemName").isJsonNull())
                existing.setSystemName(objectMapper.readValue(params.get("systemName").toString(),String.class));
            if(!params.get("pId").isJsonNull())
                existing.setpId(objectMapper.readValue(params.get("pId").toString(),Integer.class));
            if(!params.get("seq").isJsonNull())
                existing.setSeq(objectMapper.readValue(params.get("seq").toString(),Integer.class));
            if(!params.get("status").isJsonNull())
                existing.setStatus(params.get("status").getAsInt());
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
