package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.AccountServiceImpl;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Utils.TimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Services.UserServiceImpl;

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

    @Autowired
    private CommonService commonService;

    @Value("${secret.key}")
    private String secret;

    @Value("${access.time}")
    private int accessTime;

    @GetMapping("/GetAll")
    public ResponseEntity<List<AccountEntity>> getAllAccounts(HttpServletRequest request) {
        logger.info(commonService.getHeaders(request).toString());

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0) {
            logger.info("Unauthorised user, invalid authorization key !");
            return new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("customer_management",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<List<AccountEntity>> getAccountById(@PathVariable int id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, invalid authorization key !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("customer_management",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping("/Create")
    public ResponseEntity<?> create(@RequestBody String params1, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        logger.info("Auth Id : "+authId);
        if(authId == 0){
            logger.info("Unauthorised user, Invalid authorization key !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("customer_creation",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        logger.info(params1);
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

            Date creation = TimeUtils.getDate();
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

            Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
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
                auth.setAuthKey(commonService.generatedKey(acc.getAccountId()));
                auth.setCreationDate(creation);
                auth.setExpDate(expDate);
            }
            else{
                auth = new AccountAuthEntity();
                auth.setAccountId(acc.getAccountId());
                auth.setName(acc.getName());
                auth.setAuthKey(commonService.generatedKey(acc.getAccountId()));
                auth.setCreationDate(creation);
                auth.setExpDate(expDate);
            }

            accountAuthRepository.save(auth);

            int authId1 = auth.getAuthId();
            logger.info("acc.getMaxUser() : "+acc.getMaxUser());
            if(acc.getMaxUser() == 0){
                logger.info("Checking userId : "+user.getUserId());
                String token1 = commonService.generateToken(user.getUserId(),"UR");
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
    public ResponseEntity<?> updateAccount(@RequestBody AccountEntity account, HttpServletRequest request){

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("customer_update",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        accountService.updateAccount(account);
        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "Account updated!");
        return ok(result);
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if (authId == 0) {
            logger.info("Unauthorised user, wrong authorization key !");
            return new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if (!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if (!(commonService.checkAccess("user_delete", token))) {
            logger.info("Permission Denied. Don't have access for this service!");
            return new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        return ok(accountService.deleteAccount(id));

    }
}
