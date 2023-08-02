package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountAuthEntity;
import com.VideoPlatform.Entity.AccountEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.TimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.*;

@Service
public class CommonService {

    private static final Logger logger= LoggerFactory.getLogger(SessionService.class);

    @Autowired
    AccountAuthRepository accountAuthRepository;
    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccessRepository accessRepository;
    @Autowired
    AccountRepository accountRepository;

    public int isValidAuthKey(String authKey){
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        if(acc == null) return 0;
        if(TimeUtils.isExpire(acc.getExpDate())){
            return 0;
        }
        int authId = acc.getAuthId();
        return authId;
    }
    public int isValidAuthKeyU(String authKey){
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        if(acc == null) return 0;
        String key = (acc.getAuthKey());
        if(TimeUtils.isExpire(acc.getExpDate())){
            return 0;
        }
        int accountId = acc.getAccountId();
        return accountId;
    }
    public Boolean isValidRequest(String token,String authKey,String systemName){
        UserAuthEntity userAuthEntity = userAuthRepository.findByTokenAndAuthKey(token,authKey);
        if(userAuthEntity == null)return false;
        String systemNames = userAuthEntity.getSystemNames();
        logger.info("SystemNames : {}",systemNames);

        if(TimeUtils.isExpire(userAuthEntity.getExpDate()))
            return false;
        if(!systemNames.contains(systemName)){
            logger.info("Permission Denied. Don't have access for this service!");
            return false;
        }
        return true;
    }
    public Boolean isValidRequestUserUpdate(String authKey,String token,String systemName1,String systemName2){
        UserAuthEntity userAuthEntity = userAuthRepository.findByTokenAndAuthKey(token,authKey);
        logger.info("UserAuthEntity : {} ",userAuthEntity);
        if(userAuthEntity == null)return false;
        String systemNames = userAuthEntity.getSystemNames();
        logger.info("SystemNames : {}",systemNames);

        if(TimeUtils.isExpire(userAuthEntity.getExpDate()))
            return false;
        if(!(systemNames.contains(systemName1) || systemNames.contains(systemName2))){
            logger.info("Permission Denied. Don't have access for this service!");
            return false;
        }
        return true;
    }
    public Boolean isAccessAllowed(String token,String systemName){
        UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
        String systemNames = userAuthEntity.getSystemNames();
        if(!systemNames.contains(systemName)){
            logger.info("Permission Denied. Don't have access for this service!");
            return false;
        }
        return true;
    }

    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        return generatedString;
    }
    public String generateToken(Integer userId,String type) {
        String val = String.format("%03d", userId);
        return type+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
    }
    public String generatedKey(int accountId){
        String val = String.format("%03d", accountId);
        String key = "AC"+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        return key;
    }
    public Boolean authorizationCheck(String authKey, String token, String systemName){

        if(!isValidRequest(token,authKey,systemName)) {
            logger.info("Unauthorised user, wrong authorization key or Invalid Token !");
            return false;
        }
        return true;
    }

    public Map<String, String> getHeaders(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        Map<String, String> headerMap = new HashMap<>();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            headerMap.put(header, request.getHeader(header));
        }
        return headerMap;
    }

    public Boolean checkMandatory(String params1){
        int f=0;
        logger.info("Params get : {}",params1);
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        logger.info("Params jsonObject : {}",params);

        if(params.has("address")){
            if(params.get("address").isJsonNull()) f=1;
        }
        if(params.has("maxUser")){
            logger.info("params.get {}",params.get("maxUser"));
            if(params.get("maxUser").isJsonNull()) {f=1;logger.info("Null from featureMeta!");}
        }
        if(params.has("expDate")){
            if(params.get("expDate").isJsonNull()) f=1;
        }
        if(params.has("name")){
            if(params.get("name").isJsonNull()) f=1;
        }

        if(f==0) return true;
        return false;
    }

    public void compareAndChange(JsonObject params, AccountEntity storedExisting,Integer accountId) {

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Integer[] existingFeatureId = storedExisting.getFeatures();
        Integer[] existingAccessId = storedExisting.getAccessId();
        HashMap<String, Object> existingSession = storedExisting.getSession();

        try {
            Integer[] newFeatureId = objectMapper.readValue(params.get("features").toString(), Integer[].class);
            Integer[] newAccessId = objectMapper.readValue(params.get("accessId").toString(), Integer[].class);
            HashMap<String, Object> newSession = objectMapper.readValue(params.get("session").toString(), HashMap.class);

            Integer[] deletedFeatureValues = findDeletedValues(existingFeatureId,newFeatureId);
            Integer[] deletedAccessValues = findDeletedValues(existingAccessId,newAccessId);

            for (int value : deletedFeatureValues) {
                userRepository.deleteFeatureValues(value,accountId);
            }
            for (int value : deletedAccessValues) {
                userRepository.deleteAccessValues(value,accountId);
            }
            removeFromFeatureMeta(deletedFeatureValues,accountId);
            changeUserSession(existingSession,newSession,accountId);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    private void removeFromFeatureMeta(Integer[] featureId, Integer accountId){
        List<UserEntity> list = userRepository.findUsersByAccountId(accountId);
        logger.info("Users : {}",list);
        for(UserEntity userEntity : list) {
            HashMap<String, Object> map = userEntity.getFeaturesMeta();
            for(int i=0;i<featureId.length;i++){
                if(map.containsKey(featureId[i].toString())){
                    logger.info("Removed : {} ",featureId[i]);
                    map.remove(featureId[i].toString());
                }
            }
            Gson gson=new Gson();
            String jsonMeta=gson.toJson(map);
            userRepository.updateFeaturesMeta(userEntity.getLoginId(),jsonMeta);
        }
    }

    private void changeUserSession(HashMap<String, Object> existingSession, HashMap<String, Object> newSession, Integer accountId) {
        List<UserEntity> list = userRepository.findUsersByAccountId(accountId);
        logger.info("Users : {}",list);
        for(UserEntity userEntity : list){
            HashMap<String,Object> map = new HashMap<>();
            logger.info("existingSession.get(\"max_duration\") : {} ",existingSession.get("max_duration"));
            logger.info("newSession.get(\"max_duration\") : {} ",newSession.get("max_duration"));
            if((Integer)existingSession.get("max_duration") > (Integer)newSession.get("max_duration")){
                logger.info("existing  > new => true");
                if((Integer)userEntity.getSession().get("max_duration") > (Integer)newSession.get("max_duration")){
                    logger.info("user > new  => true");
                    map.put("max_duration",newSession.get("max_duration"));
                }
                else map.put("max_duration",userEntity.getSession().get("max_duration"));
            }
            else map.put("max_duration",userEntity.getSession().get("max_duration"));
            if((Integer)existingSession.get("max_participants") > (Integer)newSession.get("max_participants")){
                if((Integer)userEntity.getSession().get("max_participants") > (Integer)newSession.get("max_participants")){
                    map.put("max_participants",newSession.get("max_participants"));
                }
                else map.put("max_participants",userEntity.getSession().get("max_participants"));
            }
            else map.put("max_participants",userEntity.getSession().get("max_participants"));
            if((Integer)existingSession.get("max_active_sessions") > (Integer)newSession.get("max_active_sessions")){
                if((Integer)userEntity.getSession().get("max_active_sessions") > (Integer)newSession.get("max_active_sessions")){
                    map.put("max_active_sessions",newSession.get("max_active_sessions"));
                }
                else map.put("max_active_sessions",userEntity.getSession().get("max_active_sessions"));
            }
            else map.put("max_active_sessions",userEntity.getSession().get("max_active_sessions"));

            Gson gson=new Gson();
            String jsonSession=gson.toJson(map);
            userRepository.updateSession(userEntity.getLoginId(),jsonSession);
        }
    }

    private Integer[] findDeletedValues(Integer[] existingFeatureId, Integer[] newFeatureId) {
        HashSet<Integer> set = new HashSet<>();
        for (int value : newFeatureId) {
            set.add(value);
        }
        HashSet<Integer> deletedValuesSet = new HashSet<>();
        for (int value : existingFeatureId) {
            if (!set.contains(value)) {
                deletedValuesSet.add(value);
            }
        }
        Integer[] deletedValues = new Integer[deletedValuesSet.size()];
        int index = 0;
        for (int value : deletedValuesSet) {
            deletedValues[index++] = value;
        }
        return deletedValues;
    }

    public HashMap<String,Object> getMapOfLogo(String params1){

        Gson gson=new Gson();
        JsonObject params=gson.fromJson(String.valueOf(params1),JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        HashMap<String,Object> map = new HashMap<>();
        if(params.has("byte"))
            map.put("byte",params.get("byte").getAsString());
        if(params.has("name"))
            map.put("name",params.get("name").getAsString());
        return map;
    }

    public void changeUserLogo(Integer accountId, String oldPath, String newPath){
        List<UserEntity> userEntities = userRepository.findByAccountId(accountId);
        if(userEntities.isEmpty() || userEntities==null){
            return;
        }
        for(UserEntity userEntity : userEntities){
            if(userEntity.getLogo().equals(oldPath)){
                userRepository.updateLogoPath(userEntity.getUserId(),newPath);
            }
        }
    }

    public Map<String,String> getImage(String path) throws IOException {
        Map<String,String> map = new HashMap<>();
        File file = new File(path);
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String fileName = file.getName();
        String mimeType = "image/" + fileName.substring(fileName.lastIndexOf('.') + 1);
        String imageUrl = "data:" + mimeType + ";base64," + encodedString;

        map.put("name",fileName);
        map.put("byte",imageUrl);
        return map;
    }
    public Map<String,Object> getDefaultImageToStore(byte[] fileContent,String imgName) throws IOException {
        Map<String,Object> map = new HashMap<>();
//        File file = new File(path);
//        fileContent = FileUtils.readFileToByteArray(file);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
//        String fileName = file.getName();
        map.put("name",imgName);
        map.put("byte",encodedString);
        logger.info(map.toString());
        return map;
    }

    public void decodeToImage(String base64,String path){
        byte[] data = DatatypeConverter.parseBase64Binary(base64);
        File file = new File(path);
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))){
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Map<String,String> responseData(String statusCode,String message){
        Map<String ,String> response = new HashMap<>();
        response.put("status_code",statusCode);
        response.put("msg",message);
        return response;
    }
}
