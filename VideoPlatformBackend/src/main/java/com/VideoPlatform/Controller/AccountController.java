package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.AccountServiceImpl;
import com.VideoPlatform.Utils.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.VideoPlatform.Constant.RequestMappings;
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

import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLACCOUNT)
public class AccountController {
    private static final Logger logger= LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountAuthRepository accountAuthRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${secret.key}")
    private String secret;

    @Value("${access.time}")
    private int accessTime;

    @GetMapping("/GetAll")
    public ResponseEntity<List<AccountEntity>> getAllAccounts(HttpServletRequest request) {
        logger.info(getHeaders(request).toString());

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int check = isValidAuthKey(authKey);
        if(check == 0) {
            logger.info("Unauthorised user, invalid authorization key !");
            return new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(checkAccess("customer_management",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<List<AccountEntity>> getAccountById(@PathVariable int id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int check = isValidAuthKey(authKey);
        if(check == 0){
            logger.info("Unauthorised user, invalid authorization key !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(checkAccess("customer_management",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping("/Create")
    public ResponseEntity<?> create(@RequestBody String params1, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int check = isValidAuthKey(authKey);
        logger.info("Auth Id : "+check);
        if(check == 0){
            logger.info("Unauthorised user, Invalid authorization key !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(checkAccess("customer_creation",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        logger.info(params1);
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

            Date creation = CommonUtils.getDate();
            AccountEntity acc = new AccountEntity();
            UserEntity user = new UserEntity();
            logger.info(String.valueOf(params));
            acc.setName(params.get("name").getAsString());
            acc.setAddress(params.get("address").getAsString());
//            acc.setLogo(objectMapper.readValue(params.get("logo").getAsString(),byte[].class));
            acc.setCreationDate(creation);
            acc.setMaxUser(params.get("maxUser").getAsInt());
            acc.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
            acc.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
            acc.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            acc.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));

            Date expDate = CommonUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
            acc.setExpDate(expDate);

            UserAuthEntity u = userAuthRepository.findByToken(token);
            logger.info("User Data : "+u);
            user.setFname(params.get("fname").getAsString());
            user.setLname(params.get("lname").getAsString());
            user.setExpDate(expDate);
            user.setLoginId(params.get("loginId").getAsString());
            String pass = params.get("password").getAsString();
            logger.info("PAssword Check !!! {}",pass);
            String myPass = passwordEncoder.encode(pass);
            logger.info("Encoded Pass : {}",myPass);
            user.setPassword(myPass);
            user.setContact(params.get("contact").getAsString());
            user.setEmail(params.get("email").getAsString());
            user.setCreationDate(creation);
            user.setParentId(u.getUserId());
            user.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
            user.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
            user.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            user.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));

            accountService.createAccount(acc);
            user.setAccountId(acc.getAccountId());
            userService.createUserZero(user);

            AccountAuthEntity auth = accountAuthRepository.findById(acc.getAccountId());
            if(auth != null){
                auth.setAuthKey(generatedKey(acc.getAccountId()));
                auth.setCreationDate(creation);
                auth.setExpDate(expDate);
            }
            else{
                auth = new AccountAuthEntity();
                auth.setAccountId(acc.getAccountId());
                auth.setName(acc.getName());
                auth.setAuthKey(generatedKey(acc.getAccountId()));
                auth.setCreationDate(creation);
                auth.setExpDate(expDate);
            }

            accountAuthRepository.save(auth);

            int authId1 = auth.getAuthId();
            logger.info("acc.getMaxUser() : "+acc.getMaxUser());
            if(acc.getMaxUser() == 0){
                logger.info("Checking userId : "+user.getUserId());
                String token1 = generateToken(user.getUserId(),"UR");
                UserAuthEntity ua = new UserAuthEntity();
                ua.setLoginId(user.getLoginId());
                ua.setUserId(user.getUserId());
                ua.setToken(token1);
                ua.setAuthId(authId1);
                ua.setCreationDate(creation);
                ua.setExpDate(expDate);
                userAuthRepository.save(ua);
            }

            Map<String,String> result = new HashMap<>();
            result.put("status_code ","200");
            result.put("msg", "Account created!");

            return new ResponseEntity<>(result,HttpStatus.CREATED);
    }

    @PutMapping("/Update")
    public ResponseEntity<AccountEntity> updateAccount(@RequestBody AccountEntity account, HttpServletRequest request){

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(checkAccess("customer_update",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        return ok(accountService.updateAccount(account));
    }


    private String generatedKey(int accountId){
        String key = generateKey(accountId,"AC");
        logger.info("AccountAuthKey : "+key);
        logger.info("Unique Authorization key generated...!");
        return key;
    }

    private String generateKey(int accountId,String type) {
        String val = String.format("%03d", accountId);
        logger.info("Format val = "+val);
        return type+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
    }
    private String generateToken(int userId,String type) {
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

        if(acc == null)return 0;
        if(CommonUtils.isExpire(acc.getExpDate())){
            return 0;
        }
        return 1;
    }
    public Boolean isValidToken(String token){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        if(user == null)return false;
        if(CommonUtils.isExpire(user.getExpDate()))
            return false;
        return true;
    }
    private boolean checkAccess(String systemName,String token){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        int userId = user.getUserId();
        UserEntity u = userRepository.findByUserId(userId);
        Integer[] accessId = u.getAccessId();
        List<String> accessEntities = new ArrayList<>();
        for (int i = 0; i < accessId.length; i++) {
            AccessEntity accessEntity = accessRepository.findByAccessIds(accessId[i]);
            accessEntities.add(accessEntity.getSystemName());
        }
        if(accessEntities.contains(systemName))
            return true;
        return false;
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

}
