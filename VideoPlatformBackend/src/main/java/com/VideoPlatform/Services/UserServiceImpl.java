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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Value("${file.path}")
    private String FILE_DIRECTORY;
    @Value("${defaultPath}")
    private String defaultExtractPath;
    @Autowired
    ResourceLoader resourceLoader;


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getAllChild(String token) {

         UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
         return userRepository.findAllChild(userAuthEntity.getUserId());
    }

    @Override
    public UserEntity getUserById(Integer id) {
        return (UserEntity) userRepository.findByUserId(id);
    }

    @Override
    public ResponseEntity<?> createUser(String params1,String authKey,String token) throws JsonProcessingException {
        logger.info(params1);
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAuthKey(authKey);
        Integer accountId = accountAuthEntity.getAccountId();
        AccountEntity accountEntity = accountRepository.findByAccountId(accountId);

        if(userRepository.findByLoginId(params.get("loginId").getAsString()) != null){
            logger.info("Login Id {} already exists !",params.get("loginId").getAsString());
            return new ResponseEntity<>(commonService.responseData("406","Login Id already exist !"),HttpStatus.NOT_ACCEPTABLE);
        }
        int maxUsers = accountEntity.getMaxUser();
        if(maxUsers<=(userRepository.usersAccount(accountId)-1)){
            logger.info("No more users are allowed for account {} having login id {} , max limit exceed..!",accountId,params.get("loginId").getAsString());
            return new ResponseEntity<>(commonService.responseData("406","Max limit exceed, No more users are allowed."),HttpStatus.NOT_ACCEPTABLE);
        }

        Integer[] featuresId = accountEntity.getFeatures();
        Integer[] accessId = accountEntity.getAccessId();
        HashMap<String, Object> sessionA = accountEntity.getSession();
        UserEntity user = new UserEntity();

            if (checkIfAllowedFeature(featuresId, objectMapper.readValue(params.get("features").toString(),Integer[].class),accountId)) {
                user.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));
            } else {
                return new ResponseEntity<>(commonService.responseData("406","Feature not allowed, Try again."), HttpStatus.NOT_ACCEPTABLE);
            }
            if (checkIfAllowedAccess(accessId,objectMapper.readValue(params.get("accessId").toString(),Integer[].class),accountId)) {
                user.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            } else {
                return new ResponseEntity<>(commonService.responseData("406","Access not allowed, Try again."), HttpStatus.NOT_ACCEPTABLE);
            }
            if (checkIfAllowedSession(sessionA, objectMapper.readValue(params.get("session").toString(),HashMap.class))) {
                user.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
            } else {
                logger.info("Invalid session values. Not allowed to create !");
                return new ResponseEntity<>(commonService.responseData("406","Invalid session values."), HttpStatus.NOT_ACCEPTABLE);
            }
        user.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
        user.setLoginId(params.get("loginId").getAsString());
        user.setFname(params.get("fname").getAsString());
        user.setLname(params.get("lname").getAsString());
        user.setContact(params.get("contact").getAsString());
        user.setEmail(params.get("email").getAsString());
        user.setLastLogin("");
        Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
        user.setExpDate(expDate);
        UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
        Date creation = TimeUtils.getDate();
        user.setAccountId(accountId);
        user.setCreationDate(creation);
        user.setParentId(userAuthEntity.getUserId());
        String myPass = passwordEncoder.encode(params.get("password").getAsString());
        user.setPassword(myPass);
        userRepository.save(user);
        try{

            if(params.get("logo").isJsonNull() || params.get("logo").toString()==null){
                userRepository.updateLogoPath(user.getUserId(), String.valueOf(accountEntity.getLogo()));
            }else{
                HashMap<String, Object> logo = commonService.getMapOfLogo(params.get("logo").toString());
                Path path = Paths.get(FILE_DIRECTORY +"media/"+accountId+"/"+user.getUserId()+"/image");
                logger.info(path.toString());
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String newPath = path.toString()+"/"+logo.get("name");
                logger.info("New Path : "+newPath);
                commonService.decodeToImage(logo.get("byte").toString(),newPath);
                userRepository.updateLogoPath(user.getUserId(),newPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(commonService.responseData("200","User Created!"));
    }
    @Override
    public ResponseEntity<?> createUserZero(UserEntity userEntity) {

        userRepository.save(userEntity);
        return ok(commonService.responseData("200","User Created!"));
    }

    @Override
    public ResponseEntity<?> updateUser(String params1,String authKey) {
        logger.info("Params to Update : {}",params1);

        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        UserEntity existing = userRepository.findByUserId(params.get("userId").getAsInt());
        AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAccountId(params.get("accountId").getAsInt());
        AccountEntity accountEntity = accountRepository.findByAccountId(params.get("accountId").getAsInt());
        if(existing == null || accountEntity == null || accountAuthEntity == null){
            logger.info("Getting null or invalid value from parameters !");
            return new ResponseEntity<>(commonService.responseData("400","Getting null or invalid value from parameters."),HttpStatus.BAD_REQUEST);
        }
        Integer[] featuresId = accountEntity.getFeatures();
        Integer[] accessId = accountEntity.getAccessId();
        HashMap<String, Object> sessionA = accountEntity.getSession();

        if(existing.getAccountId() == accountEntity.getAccountId()) {
            existing.setFname(params.get("fname").getAsString());
            existing.setLname(params.get("lname").getAsString());
            try {
                if(!params.get("features").isJsonNull()) {
                    if (checkIfAllowedFeature(featuresId, objectMapper.readValue(params.get("features").toString(), Integer[].class), accountEntity.getAccountId())) {
                        existing.setFeatures(objectMapper.readValue(params.get("features").toString(), Integer[].class));
                    } else {
                        logger.info("Feature values not present in Account Entity. Not allowed to update !");
                        return new ResponseEntity<>(commonService.responseData("406","Invalid Feature values!"), HttpStatus.NOT_ACCEPTABLE);
                    }
                }
                if(!params.get("accessId").isJsonNull()){
                    if (checkIfAllowedAccess(accessId, objectMapper.readValue(params.get("accessId").toString(), Integer[].class),accountEntity.getAccountId())) {
                        existing.setAccessId(objectMapper.readValue(params.get("accessId").toString(), Integer[].class));
                    } else {
                        logger.info("Access Id values not present in Account Entity. Not allowed to update !");
                        return new ResponseEntity<>(commonService.responseData("406","Invalid Access values!"), HttpStatus.NOT_ACCEPTABLE);
                    }
                }
                if(!params.get("session").isJsonNull()){
                    if (checkIfAllowedSession(sessionA, objectMapper.readValue(params.get("session").toString(), HashMap.class))) {
                        existing.setSession(objectMapper.readValue(params.get("session").toString(), HashMap.class));
                    } else {
                        logger.info("Invalid session values. Not allowed to update !");
                        return new ResponseEntity<>(commonService.responseData("406","Invalid Session values!"), HttpStatus.NOT_ACCEPTABLE);
                    }
                }
                if(params.get("logo").isJsonNull() ||  params.get("logo").toString()==null){

                    userRepository.updateLogoPath(existing.getUserId(), String.valueOf(accountEntity.getLogo()));
                }
                else {
                    HashMap<String, Object> logo = commonService.getMapOfLogo(params.get("logo").toString());
                    Path path = Paths.get(FILE_DIRECTORY +"media/"+accountEntity.getAccountId()+"/"+existing.getUserId()+"/image");
                    logger.info(path.toString());
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }
                    String newPath = path.toString()+"/"+logo.get("name");
                    logger.info("New Path : "+newPath);
                    commonService.decodeToImage(logo.get("byte").toString(),newPath);
                    existing.setLogo(newPath);
                }

                if(!params.get("featuresMeta").isJsonNull())
                    existing.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(), HashMap.class));
                if(!params.get("expDate").isJsonNull()) {
                    Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(), String.class));
                    existing.setExpDate(expDate);
                }
                if(!params.get("contact").isJsonNull())
                    existing.setContact(params.get("contact").getAsString());
                if(!params.get("email").isJsonNull())
                    existing.setEmail(params.get("email").getAsString());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            userRepository.save(existing);

            UserAuthEntity userAuthEntity = userAuthRepository.findByUId(params.get("userId").getAsInt());
            if(userAuthEntity != null) {
                userAuthEntity.setSystemNames(accessCheck(params.get("userId").getAsInt()));
                userAuthRepository.save(userAuthEntity);
            }

            return ok(commonService.responseData("200","User Updated!"));
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

            return  new ResponseEntity<>(commonService.responseData("401","Invalid username or password!"),HttpStatus.UNAUTHORIZED);
        }
        if(userEntity.getStatus()!=1){
            logger.info("User does not exist !");
            return  new ResponseEntity<>(commonService.responseData("401","User does not exist!"),HttpStatus.UNAUTHORIZED);
        }

        logger.info("user "+userEntity);
        int userId = userEntity.getUserId();

        if (!(passwordEncoder.matches(password,userEntity.getPassword()))) {
            logger.info("Inside loginId password check !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(isValidTokenLogin(userId)){
            UserAuthEntity userAuthEntity = userAuthRepository.findByUId(userId);
            AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAccountId(userEntity.getAccountId());
            if(TimeUtils.isExpire(accountAuthEntity.getExpDate())){
                return new ResponseEntity<>(commonService.responseData("401","Account Expired!"),HttpStatus.UNAUTHORIZED);
            }
            userAuthEntity.setSystemNames(accessCheck(userId));

            HashMap<String,Object> response=new HashMap<>();
            response.put("token",userAuthEntity.getToken());
            response.put("auth_key",accountAuthEntity.getAuthKey());
            response.put("user_data",userEntity);
            response.put("status_code","200");
            response.put("msg","Login Successful!");
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
                    return new ResponseEntity<>(commonService.responseData("401","Account Expired!"),HttpStatus.UNAUTHORIZED);
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
                res.put("msg","Login Successful!");
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
    public void saveFilePathToFeature(String filePath, String loginId, String name){

        UserEntity userEntity = userRepository.findByLoginId(loginId);
        HashMap<String,Object> featuresMeta=userEntity.getFeaturesMeta();
        HashMap<String,Object> map= (HashMap<String, Object>) featuresMeta.get("4");
        map.replace("pre_recorded_video_file",filePath);
        featuresMeta.replace("4",map);
        logger.info("Features Meta {}",featuresMeta);
        userEntity.setFeaturesMeta(featuresMeta);
        logger.info("Pre recorded val : {}",map.get("pre_recorded_video_file"));
        logger.info("getFeatureMeta1 {} ", userEntity.getFeaturesMeta());
        Gson gson=new Gson();
        String json=gson.toJson(featuresMeta);
        logger.info("Json {}",json);
        userRepository.updateFeaturesMeta(loginId,json);
        logger.info("getFeatureMeta2 {} ", userEntity.getFeaturesMeta());
    }

    @Override
    public Boolean checkLoginId(String loginId){
        if(userRepository.findByLoginId(loginId) == null){
            return true;
        }
        return false;
    }

    @Override
    public Map<String,String> getImage(Map<String,Object> params) throws IOException {
        Integer userId = (Integer) params.get("userId");
        Integer accountId = (Integer) params.get("accountId");
        String path = "";
        Map<String,String> map = new HashMap<>();
        if(userId==null && accountId!=null) {
            AccountEntity accountEntity = accountRepository.findByAccountId(accountId);
            if (accountEntity != null) {
                path = accountEntity.getLogo();
                return commonService.getImage(path);
            }
        }
        else if(accountId==null && userId!=null){
            UserEntity userEntity = userRepository.findByUserId(userId);
            if(userEntity!=null){
                path=userEntity.getLogo();
                return commonService.getImage(path);
            }
        }
        return null;
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
    public Boolean checkIfAllowedFeature(Integer[] featureA, Integer[] featureU,Integer accountId){
        if(featureA==null || featureU==null) return false;
        int f=0;
        List<Integer> intList = new ArrayList<>(Arrays.asList(featureA));
        for(int i=0;i<featureU.length;i++) {
            if (!intList.contains(featureU[i])) {
                logger.info("Feature value {} not present in Account Entity {}. Not allowed to create !",featureU[i],accountId);
                return false;
            }
        }
        return true;
    }
    public Boolean checkIfAllowedAccess(Integer[] accessA, Integer[] accessU, Integer accountId){
        if(accessA==null || accessU==null) return false;
        int f=0;
        List<Integer> intList = new ArrayList<>(Arrays.asList(accessA));
        for(int i=0;i<accessU.length;i++) {
            if (!intList.contains(accessU[i])) {
                logger.info("Access Id value {} not present in Account Entity {}. Not allowed to create !",accessU[i],accountId);
                return false;
            }
        }
        return true;
    }
    public Boolean checkIfAllowedSession(HashMap<String,Object> sessionA, HashMap<String,Object> sessionU){
        if(sessionA == null || sessionU == null) return false;
        if((Integer) sessionA.get("max_duration") < (Integer) sessionU.get("max_duration") || (Integer)sessionA.get("max_participants") < (Integer) sessionU.get("max_participants") || (Integer)sessionA.get("max_active_sessions") < (Integer) sessionU.get("max_active_sessions")){
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

