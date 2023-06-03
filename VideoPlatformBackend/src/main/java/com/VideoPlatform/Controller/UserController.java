package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.UserServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

import static org.springframework.http.ResponseEntity.ok;
import static com.VideoPlatform.Constant.AllConstants.DATE_FORMATTER;
import static com.VideoPlatform.Constant.AllConstants.formatter;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLUSER)
public class UserController {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AccountAuthRepository accountAuthRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Value("${secret.key}")
    private String secret;

    @Value("${access.time}")
    private int accessTime;

//    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    @GetMapping("/GetAll")
    public ResponseEntity<List<UserEntity>> getAllUsers(HttpServletRequest request) throws JsonProcessingException {
        logger.info(getHeaders(request).toString());
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!(byAccess(2000,token))){
            logger.info("for 2000 : "+byAccess(2000,token));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ok(userService.getAllUsers());
    }

    @GetMapping("/Child")
    public ResponseEntity<List<UserEntity>> getAllChild(HttpServletRequest request) throws JsonProcessingException {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!(byAccess(2000,token))){
            logger.info("for 2000 : "+byAccess(2000,token));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }
        UserAuthEntity user = userAuthRepository.findByToken(token);
        return ResponseEntity.ok(userService.getAllChild(user.getUserId()));
    }

    @GetMapping("/GetById/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Integer id, HttpServletRequest request) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(2000,token))){
            logger.info("for 2000 : "+byAccess(2000,token));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.FORBIDDEN);
        }

            return ok(userService.getUserById(id));

    }

    @PostMapping("/Create")
    public ResponseEntity<?> createUser(@RequestBody UserEntity user, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int accountId = isValidAuthKey(authKey);
        if(accountId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(2001,token))){
            logger.info("for 2001 : "+byAccess(2001,token));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        AccountEntity a = accountRepository.findByAccountId(accountId);
        int maxUsers = a.getMaxUser();

        if(maxUsers == 0){
            logger.info("No more users are allowed, max limit exceed..!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        userService.createUser(user,authKey,token);

        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "User created!");
        return ok(result);

    }

    @PutMapping("/Update")
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserEntity user, HttpServletRequest request) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(2002,token))){
            logger.info("for 2002 : "+byAccess(2002,token));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        return ok(userService.updateUser(user));
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, @RequestBody UserEntity user, HttpServletRequest request) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(2003,token))){
            logger.info("for 2003 : "+byAccess(2003,token));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        userService.deleteUser(id);

        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "User deleted!");

        return ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> params,HttpServletRequest request) throws JsonProcessingException {
        String authKey = request.getHeader("Authorization");
        int authId = isValidAuthKey(authKey);
        logger.info("Auth Id .. "+authId);
        if(authId == 0){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        String loginId = params.get("loginId");
        String password = params.get("password");

        logger.info("loginId : "+loginId);
        logger.info("password : "+password);

        UserEntity user1 = userRepository.findByLoginId(loginId);

        if(user1 == null){
            logger.info("No user present with given login id !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("user "+user1);
        int userId = user1.getUserId();

        logger.info("userId "+userId);
        UserAuthEntity user = userAuthRepository.findByUId(userId);
        logger.info("userId "+userId);

        logger.info("user1 {}",user1);
        logger.info("user1.getLoginId() {}",user1.getLoginId());
        logger.info("user1.getPassword() {}",user1.getPassword());
        if (!(passwordEncoder.matches(password,user1.getPassword()))) {
            logger.info("Inside loginid password check !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

            if(isValidTokenLogin(userId)){
                logger.info("Inside second if ...");
                ObjectMapper obj = new ObjectMapper();
                HashMap<String,Object> response=new HashMap<>();
                response.put("token",user.getToken());
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
                    String token1 = generateToken(userId,"UR");
                    Date now = CommonUtils.getDate();
                    Date newDateTime = CommonUtils.increaseDateTime(now);
                    UserAuthEntity ua = userAuthRepository.findByUId(userId);
                    String lastLogin = LocalDateTime.now().format(formatter);
                    logger.info("LastLogin2 : {}",lastLogin);
                    user1.setLastLogin(lastLogin);
                    userRepository.setLogin(lastLogin,userId);

                    if(ua != null){
                        ua.setToken(token1);
                        ua.setAuthId(authId);
                        ua.setCreationDate(now);
                        ua.setExpDate(newDateTime);
                    }
                    else{
                        ua = new UserAuthEntity();
                        ua.setLoginId(user1.getLoginId());
                        ua.setUserId(user1.getUserId());
                        ua.setToken(token1);
                        ua.setAuthId(authId);
                        ua.setCreationDate(now);
                        ua.setExpDate(newDateTime);
                    }

                    userAuthRepository.save(ua);

                    Map<String,Object> res = new HashMap<>();
                    res.put("token",token1);
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


    private String generateToken(Integer userId,String type) {
        String val = String.format("%03d", userId);
        logger.info("Format val = "+val);
        return type+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
    }

    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        logger.info(generatedString);
        System.out.println(generatedString);
        return generatedString;
    }

    public int isValidAuthKey(String authKey){
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        if(acc == null) return 0;
        String key = (acc.getAuthKey());
        if(CommonUtils.isExpire(acc.getExpDate())){
            return 0;
        }
        int authId = acc.getAuthId();
        return authId;
    }
    public Boolean isValidToken(String token){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        if(user == null)return false;
        logger.info("Data by authId.."+user);

        if(CommonUtils.isExpire(user.getExpDate()))
            return false;
//        if(user.getExpDate().isBefore(LocalDateTime.now()))
//            return false;
        return true;
    }
    public Boolean isValidTokenLogin(int id){

        UserAuthEntity user = userAuthRepository.findByUId(id);
        if(user == null || CommonUtils.isExpire(user.getExpDate()) ) {
            return false;
        }
        return true;
    }

   private Map<String, String> getHeaders(HttpServletRequest request) {
       Enumeration<String> headers = request.getHeaderNames();
       Map<String, String> headerMap = new HashMap<>();
       while (headers.hasMoreElements()) {
           String header = headers.nextElement();
           headerMap.put(header, request.getHeader(header));
       }
       return headerMap;
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

    private Object accessData(Integer userId) throws JsonProcessingException {
        UserEntity user = userRepository.findByUserId(userId);
        Integer[] accessId = user.getAccessId();
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessEntity> accessEntities = new ArrayList<>();

        for (int i = 0; i < accessId.length; i++) {
            AccessEntity accessEntity = accessRepository.findByAccessIds(accessId[i]);
            accessEntities.add(accessEntity);
        }
        return objectMapper.convertValue(accessEntities, JsonNode.class);
    }
    private boolean byAccess(int toCheckValue,String token) throws JsonProcessingException {

        UserAuthEntity user = userAuthRepository.findByToken(token);
        int userId = user.getUserId();
        UserEntity u = userRepository.findByUserId(userId);
        if(user == null) return false;
        ObjectMapper obj = new ObjectMapper();
        String str = obj.writeValueAsString(u.getAccessId());
        logger.info("Val : "+str);
        String[] string = str.replaceAll("\\[", "")
                .replaceAll("]", "")
                .split(",");
        int[] arr = new int[string.length];
        int[] apiArr = new int[string.length];
        for (int i = 0; i < string.length; i++) {
            arr[i] = Integer.valueOf(string[i]);
        }
        int size = arr.length;
        for (int i = 0; i < arr.length; i++) {
            AccessEntity access = accessRepository.findByAccessId(arr[i]);
            int apiId = access.getApiId();
            apiArr[i] = apiId;
        }
        boolean apiPresent  = ifExistInApiArray(apiArr,toCheckValue);
        return apiPresent;

    }

    private boolean ifExistInApiArray(int[] arr,int toCheckValue){
        logger.info("ToCheck Array is : "+arr);
        logger.info("TocheckVal is : "+toCheckValue);
        return IntStream.of(arr).anyMatch(x -> x == toCheckValue);
    }
    private boolean ifExistInAccessArray(int[] arr,int toCheckValue){
        boolean test
                = Arrays.asList(arr)
                .contains(toCheckValue);
        return test;
    }


}

