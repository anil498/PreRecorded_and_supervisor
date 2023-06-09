package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;

import com.VideoPlatform.Utils.TimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.VideoPlatform.Constant.AllConstants.DATE_FORMATTER;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private AccessRepository accessRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getAllChild(Integer id) {
         return userRepository.findAllChild(id);
    }

    @Override
    public UserEntity getUserById(Integer id) {
        return (UserEntity) userRepository.findByUserId(id);
    }

    @Override
    public UserEntity createUser(UserEntity user,String authKey,String token,int accountId) {
        AccountEntity a = accountRepository.findByAccountId(accountId);
        int maxUsers = a.getMaxUser();

        if(maxUsers == 0){
            logger.info("No more users are allowed, max limit exceed..!");
            return null;
        }
        logger.info("User details {}",user.toString());
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        UserAuthEntity u = userAuthRepository.findByToken(token);
        Date creation = TimeUtils.getDate();
        user.setAccountId(acc.getAccountId());
        user.setCreationDate(creation);
        user.setParentId(u.getUserId());
        String myPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(myPass);

        return userRepository.save(user);
    }
    @Override
    public UserEntity createUserZero(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUser(String params1) {
        logger.info("Params Update : {}",params1);
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserEntity existing = userRepository.findByUserId(params.get("userId").getAsInt());
        existing.setFname(params.get("fname").getAsString());
        existing.setLname(params.get("lname").getAsString());
        try {
            existing.setLogo(objectMapper.readValue(params.get("logo").toString(),HashMap.class));
            existing.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
            existing.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
            existing.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            existing.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));
            Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
            existing.setExpDate(expDate);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        existing.setContact(params.get("contact").getAsString());
        existing.setEmail(params.get("email").getAsString());
        logger.info("New Entity {}",existing);
        return userRepository.save(existing);
    }

    @Override
    public String deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
        return "User successfully deleted.";
    }

    @Override
    public ResponseEntity<?> loginService(String loginId,String password,int authId){
        logger.info("loginId : "+loginId);
        logger.info("password : "+password);

        UserEntity user1 = userRepository.findByLoginId(loginId);

        if(user1 == null){
            logger.info("No user present with given login id !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(user1.getStatus()!=1){
            logger.info("User does not exist !");
            return  new ResponseEntity<UserEntity>(HttpStatus.FORBIDDEN);
        }

        logger.info("user "+user1);
        int userId = user1.getUserId();

        if (!(passwordEncoder.matches(password,user1.getPassword()))) {
            logger.info("Inside loginid password check !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(isValidTokenLogin(userId)){
            UserAuthEntity user = userAuthRepository.findByUId(userId);
            AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAccountId(user1.getAccountId());
            HashMap<String,Object> response=new HashMap<>();
            response.put("token",user.getToken());
            response.put("auth_key",accountAuthEntity.getAuthKey());
            response.put("user_data",user1);
            response.put("status_code","200");
            response.put("status_message","Login Successful");
            response.put("Features", featureData(user1.getUserId()));
            response.put("Access", accessData(user1.getUserId()));
            String lastLogin = LocalDateTime.now().format(formatter);
            logger.info("LastLogin1 : {}",lastLogin);
            user1.setLastLogin(lastLogin);
            userRepository.setLogin(lastLogin,userId);
            return  ResponseEntity.ok(response);
        }
        else {

            if (user1 != null && passwordEncoder.matches(password,user1.getPassword())) {
                AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAccountId(user1.getAccountId());
                int auth = accountAuthEntity.getAuthId();
                String token1 = generateToken(userId,"UR");
                Date now = TimeUtils.getDate();
                Date newDateTime = TimeUtils.increaseDateTime(now);
                UserAuthEntity ua = userAuthRepository.findByUId(userId);
                String lastLogin = LocalDateTime.now().format(formatter);
                logger.info("LastLogin2 : {}",lastLogin);
                user1.setLastLogin(lastLogin);
                userRepository.setLogin(lastLogin,userId);

                if(ua != null){
                    ua.setToken(token1);
                    ua.setAuthId(auth);
                    ua.setCreationDate(now);
                    ua.setExpDate(newDateTime);
                }
                else{
                    ua = new UserAuthEntity();
                    ua.setLoginId(user1.getLoginId());
                    ua.setUserId(user1.getUserId());
                    ua.setToken(token1);
                    ua.setAuthId(auth);
                    ua.setCreationDate(now);
                    ua.setExpDate(newDateTime);
                }

                userAuthRepository.save(ua);

                HashMap<String,Object> res = new HashMap<>();
                res.put("token",token1);
                res.put("auth_key",accountAuthEntity.getAuthKey());
                res.put("user_data",user1);
                res.put("status_code","200");
                res.put("status_message","Login Successful");
                res.put("Features", featureData(user1.getUserId()));
                res.put("Access", accessData(user1.getUserId()));
                logger.info(user1.toString());
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }
        logger.info("Last login return invoked !");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
    public Boolean isValidTokenLogin(int id){

        UserAuthEntity user = userAuthRepository.findByUId(id);
        if(user == null || TimeUtils.isExpire(user.getExpDate()) ) {
            return false;
        }
        return true;
    }
    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        logger.info(generatedString);
        System.out.println(generatedString);
        return generatedString;
    }
    private String generateToken(Integer userId,String type) {
        String val = String.format("%03d", userId);
        logger.info("Format val = "+val);
        return type+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
    }

    private Object featureData(Integer userId) {
        UserEntity user = userRepository.findByUserId(userId);
        Integer[] featuresId = user.getFeatures();
        ObjectMapper objectMapper = new ObjectMapper();
        List<FeatureEntity> featureEntities = new ArrayList<>();

        for (int i = 0; i < featuresId.length; i++) {
            FeatureEntity featureEntity = featureRepository.findByFeatureId(featuresId[i]);
            logger.info("Feature Entity : {}",featureEntity);
            if(featureEntity.getStatus() == 1){
                featureEntities.add(featureEntity);
            }
        }
        return objectMapper.convertValue(featureEntities, JsonNode.class);
    }
    private Object accessData(Integer userId) {
        UserEntity user = userRepository.findByUserId(userId);
        Integer[] accessId = user.getAccessId();
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessEntity> accessEntities = new ArrayList<>();

        for (int i = 0; i < accessId.length; i++) {
            AccessEntity accessEntity = accessRepository.findByAccessIds(accessId[i]);
            if(accessEntity.getStatus()==1){
                accessEntities.add(accessEntity);
            }
        }
        return objectMapper.convertValue(accessEntities, JsonNode.class);
    }
}

