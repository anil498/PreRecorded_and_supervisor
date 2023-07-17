package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccessEntity;
import com.VideoPlatform.Entity.AccountEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.AccessRepository;
import com.VideoPlatform.Repository.AccountRepository;
import com.VideoPlatform.Repository.UserAuthRepository;
import com.VideoPlatform.Repository.UserRepository;
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

import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@Service
public class AccessServiceImpl implements AccessService {
    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private CommonService commonService;

    @Override
    public String createAccess(AccessEntity accessEntity,String authKey,String token){

        if(accessRepository.findByAccessId(accessEntity.getAccessId())!=null) return null;
        accessRepository.save(accessEntity);

        UserAuthEntity userAuthEntity = userAuthRepository.findByTokenAndAuthKey(token,authKey);
        if(userAuthEntity == null) return null;

        UserEntity userEntity = userRepository.findByUserId(userAuthEntity.getUserId());
        if(userEntity==null) return null;

        AccountEntity accountEntity = accountRepository.findByAccountId(userEntity.getAccountId());
        if(accountEntity==null) return null;

        List<Integer> access = new ArrayList<>(Arrays.asList(userEntity.getAccessId()));
        logger.info("Access : {}",access);
        Integer a = accessEntity.getAccessId();
        access.add(a);
        Integer[] accessId = access.toArray(new Integer[0]);
        accountEntity.setAccessId(accessId);
        accountRepository.save(accountEntity);
        userEntity.setAccessId(accessId);
        userRepository.save(userEntity);

        String value = userAuthEntity.getSystemNames();
        value = value.substring(1, value.length() - 1);
        String[] valueArray = value.split(",");
        List<String> valueList = new ArrayList<>();
        for (String element : valueArray) {
            valueList.add(element.trim());
        }
        System.out.println(valueList);
        valueList.add(accessEntity.getSystemName());
        userAuthEntity.setSystemNames(valueList.toString());
        userAuthRepository.save(userAuthEntity);
        return "";
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
