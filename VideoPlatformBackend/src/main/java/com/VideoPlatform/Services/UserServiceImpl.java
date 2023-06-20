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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.VideoPlatform.Constant.AllConstants.DATE_FORMATTER;
import static org.springframework.http.ResponseEntity.ok;

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
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private CommonService commonService;

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
        if(userRepository.findByLoginId(user.getLoginId()) != null){
            logger.info("Login Id already exists !");
            return null;
        }
        int maxUsers = a.getMaxUser();
        if(maxUsers<=(userRepository.usersAccount(accountId)-1)){
            logger.info("No more users are allowed, max limit exceed..!");
            return null;
        }
        Boolean bExp = user.setExpDate(user.getExpDate());
        Boolean bLogo = user.setLogo(user.getLogo());
        Boolean bSession = user.setSession(user.getSession());
        Boolean bMeta = user.setFeaturesMeta(user.getFeaturesMeta());
        Boolean bAccess = user.setAccessId(user.getAccessId());
        Boolean bFeatures = user.setFeatures(user.getFeatures());
        logger.info("ExP : {} ,Features : {} ",user.getExpDate(),user.getFeatures());
        logger.info("ExP : {} ,Features : {} ",bExp,bFeatures);
        if(bExp == false || bLogo == false || bSession == false || bMeta == false || bAccess == false || bFeatures == false) {
            logger.info("Please enter valid values, null values not accepted !!!");
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
    public ResponseEntity<?> updateUser(String params1,String authKey) {
        logger.info("Params Update : {}",params1);


        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAuthKey(authKey);
        AccountEntity accountEntity = accountRepository.findByAccountId(accountAuthEntity.getAccountId());
        UserEntity existing = userRepository.findByUserId(params.get("userId").getAsInt());
        Integer[] featuresId = accountEntity.getFeatures();
        Integer[] accessId = accountEntity.getAccessId();
        HashMap<String, Object> sessionA = accountEntity.getSession();

        if(existing.getAccountId() == accountEntity.getAccountId() || existing.getParentId() == 0) {
            existing.setFname(params.get("fname").getAsString());
            existing.setLname(params.get("lname").getAsString());
            try {
                Boolean bFeatures, bAccess, bSession;
                if (checkIfAllowedFeature(featuresId, objectMapper.readValue(params.get("features").toString(), Integer[].class))) {
                    bFeatures = existing.setFeatures(objectMapper.readValue(params.get("features").toString(), Integer[].class));
                } else {
                    logger.info("Feature values not present in Account Entity. Not allowed to update !");
                    return new ResponseEntity<>("Invalid feature values !", HttpStatus.NOT_ACCEPTABLE);
                }
                if (checkIfAllowedAccess(accessId, objectMapper.readValue(params.get("accessId").toString(), Integer[].class))) {
                    bAccess = existing.setAccessId(objectMapper.readValue(params.get("accessId").toString(), Integer[].class));
                } else {
                    logger.info("Access Id values not present in Account Entity. Not allowed to update !");
                    return new ResponseEntity<>("Invalid access id values !", HttpStatus.NOT_ACCEPTABLE);
                }
                if (checkIfAllowedSession(sessionA, objectMapper.readValue(params.get("session").toString(), HashMap.class))) {
                    bSession = existing.setSession(objectMapper.readValue(params.get("session").toString(), HashMap.class));
                } else {
                    logger.info("Invalid session values. Not allowed to update !");
                    return new ResponseEntity<>("Invalid session values !", HttpStatus.NOT_ACCEPTABLE);
                }
                Boolean bLogo = existing.setLogo(objectMapper.readValue(params.get("logo").toString(), HashMap.class));
                Boolean bMeta = existing.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(), HashMap.class));
                Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(), String.class));
                Boolean bExp = existing.setExpDate(expDate);
                if(bExp == false || bLogo == false || bSession == false || bMeta == false || bAccess == false || bFeatures == false) {
                    logger.info("Please enter valid values, null values not accepted !!!");
                    return new ResponseEntity<>("Invalid or null credentials. Try again !",HttpStatus.NOT_ACCEPTABLE);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            existing.setContact(params.get("contact").getAsString());
            existing.setEmail(params.get("email").getAsString());
            logger.info("New Entity {}", existing);

            userRepository.save(existing);

            UserAuthEntity userAuthEntity = userAuthRepository.findByUId(params.get("userId").getAsInt());
            userAuthEntity.setSystemNames(accessCheck(params.get("userId").getAsInt()));
            userAuthRepository.save(userAuthEntity);
            Map<String, String> result = new HashMap<>();
            result.put("status_code", "200");
            result.put("msg", "User updated!");
            return ok(result);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public String deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
        userAuthRepository.deleteById(userId);
        return "User successfully deleted.";
    }

    @Override
    public ResponseEntity<?> loginService(String loginId,String password,int authId){
        logger.info("loginId : "+loginId);
        logger.info("password : "+password);

        UserEntity userEntity = userRepository.findByLoginId(loginId);

        if(userEntity == null){
            logger.info("No user present with given login id !");
            Map<String,String> result = new HashMap<>();
            result.put("status_code ","401");
            result.put("msg", "Invalid username or password !");
            return  new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
        if(userEntity.getStatus()!=1){
            logger.info("User does not exist !");
            Map<String,String> result = new HashMap<>();
            result.put("status_code ","401");
            result.put("msg", "User does not exist !");
            return  new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }

        logger.info("user "+userEntity);
        int userId = userEntity.getUserId();

        if (!(passwordEncoder.matches(password,userEntity.getPassword()))) {
            logger.info("Inside loginid password check !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(isValidTokenLogin(userId)){
            UserAuthEntity userAuthEntity = userAuthRepository.findByUId(userId);
            AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAccountId(userEntity.getAccountId());
            if(TimeUtils.isExpire(accountAuthEntity.getExpDate())){
                return new ResponseEntity<>("Account Expired !",HttpStatus.UNAUTHORIZED);
            }
            userAuthEntity.setSystemNames(accessCheck(userId));

            HashMap<String,Object> response=new HashMap<>();
            response.put("token",userAuthEntity.getToken());
            response.put("auth_key",accountAuthEntity.getAuthKey());
            response.put("user_data",userEntity);
            response.put("status_code","200");
            response.put("status_message","Login Successful");
            response.put("Features", featureData(userEntity.getUserId()));
            response.put("Access", accessData(userEntity.getUserId()));
            response.put("Dashboard",dashboardService.dashboardData(loginId,userAuthEntity.getToken()));
            String lastLogin = LocalDateTime.now().format(formatter);
            logger.info("LastLogin1 : {}",lastLogin);
            userEntity.setLastLogin(lastLogin);
            userRepository.setLogin(lastLogin,userId);
            return  ResponseEntity.ok(response);
        }
        else {

            if (passwordEncoder.matches(password,userEntity.getPassword())) {
                AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAccountId(userEntity.getAccountId());
                if(TimeUtils.isExpire(accountAuthEntity.getExpDate())){
                    return new ResponseEntity<>("Account Expired !",HttpStatus.UNAUTHORIZED);
                }
                int auth = accountAuthEntity.getAuthId();
                String token1 = commonService.generateToken(userId,"UR");
                Date now = TimeUtils.getDate();
                Date newDateTime = TimeUtils.increaseDateTime(now);
                UserAuthEntity ua = userAuthRepository.findByUId(userId);
                String lastLogin = LocalDateTime.now().format(formatter);
                logger.info("LastLogin2 : {}",lastLogin);
                userEntity.setLastLogin(lastLogin);
                userRepository.setLogin(lastLogin,userId);

                if(ua != null){
                    ua.setToken(token1);
                    ua.setAuthId(auth);
                    ua.setCreationDate(now);
                    ua.setExpDate(newDateTime);
                    ua.setSystemNames(accessCheck(userId));
                    ua.setAuthKey(accountAuthEntity.getAuthKey());
                }
                else{
                    ua = new UserAuthEntity();
                    ua.setLoginId(userEntity.getLoginId());
                    ua.setUserId(userEntity.getUserId());
                    ua.setToken(token1);
                    ua.setAuthId(auth);
                    ua.setCreationDate(now);
                    ua.setExpDate(newDateTime);
                    ua.setSystemNames(accessCheck(userId));
                    ua.setAuthKey(accountAuthEntity.getAuthKey());
                }

                userAuthRepository.save(ua);

                HashMap<String,Object> res = new HashMap<>();
                res.put("token",token1);
                res.put("auth_key",accountAuthEntity.getAuthKey());
                res.put("user_data",userEntity);
                res.put("status_code","200");
                res.put("status_message","Login Successful");
                res.put("Features", featureData(userEntity.getUserId()));
                res.put("Access", accessData(userEntity.getUserId()));
                res.put("Dashboard",dashboardService.dashboardData(loginId,token1));
                logger.info(userEntity.toString());
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }
        logger.info("Last login return invoked !");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @Override
    public Boolean checkLoginId(String loginId){
        if(userRepository.findByLoginId(loginId) == null){
            return true;
        }
        return false;
    }
//    public String resetPassword(String newPassword, String loginId, Integer userId){
//        UserEntity userEntity = userRepository.findByUserId(userId);
//        if(userEntity==null) return null;
//        userEntity.setPassword(passwordEncoder.encode(newPassword));
//        return "Password reset Successfully !";
//    }

    public Boolean isValidTokenLogin(int id){

        UserAuthEntity user = userAuthRepository.findByUId(id);
        if(user == null || TimeUtils.isExpire(user.getExpDate()) ) {
            return false;
        }
        return true;
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
    public Boolean checkIfAllowedFeature(Integer[] featureA, Integer[] featureU){
        int f=0;
        List<Integer> intList = new ArrayList<>(Arrays.asList(featureA));
        for(int i=0;i<featureU.length;i++) {
            if (!intList.contains(featureU[i])) {

                return false;
            }
        }
        return true;
    }
    public Boolean checkIfAllowedAccess(Integer[] accessA, Integer[] accessU){
        int f=0;
        List<Integer> intList = new ArrayList<>(Arrays.asList(accessA));
        for(int i=0;i<accessU.length;i++) {
            if (!intList.contains(accessU[i])) {
                return false;
            }
        }
        return true;
    }
    public Boolean checkIfAllowedSession(HashMap<String,Object> sessionA, HashMap<String,Object> sessionU){
        if(Integer.valueOf(String.valueOf(sessionA.get("max_duration"))) < Integer.valueOf(String.valueOf(sessionU.get("max_duration"))) || Integer.valueOf(String.valueOf(sessionA.get("max_participants"))) < Integer.valueOf(String.valueOf(sessionU.get("max_participants"))) || Integer.valueOf(String.valueOf(sessionA.get("max_active_sessions"))) < Integer.valueOf(String.valueOf(sessionU.get("max_active_sessions")))){
            return false;
        }
        return true;
    }
    public String accessCheck(Integer userId){
        UserEntity userEntity = userRepository.findByUserId(userId);
        Integer[] accessId = userEntity.getAccessId();
        List<String> accessEntities = new ArrayList<>();
        for (int i = 0; i < accessId.length; i++) {
            AccessEntity accessEntity = accessRepository.findByAccessIds(accessId[i]);
            accessEntities.add(accessEntity.getSystemName());
        }
        logger.info("system_name array is : {}",accessEntities);

       return String.valueOf(accessEntities);
    }

}

