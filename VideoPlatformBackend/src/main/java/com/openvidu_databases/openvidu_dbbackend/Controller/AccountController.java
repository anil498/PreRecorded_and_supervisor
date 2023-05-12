package com.openvidu_databases.openvidu_dbbackend.Controller;

//import com.openvidu_databases.openvidu_dbbackend.Entity.AccountAuthEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.openvidu_databases.openvidu_dbbackend.Constant.RequestMappings;
import com.openvidu_databases.openvidu_dbbackend.Entity.AccountAuthEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.AccountEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserAuthEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
//import com.openvidu_databases.openvidu_dbbackend.Repository.AccountAuthRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.AccountAuthRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.AccountRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.UserAuthRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.UserRepository;
import com.openvidu_databases.openvidu_dbbackend.Services.AccountService;
import com.openvidu_databases.openvidu_dbbackend.Services.UserService;
import com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLACCOUNT)
public class AccountController {
    private static final Logger logger= LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private  AccountRepository accountRepository;

//    @Autowired(required = true)
//    private AccountAuthEntity accountAuthEntity;

    @Autowired
    private AccountAuthRepository accountAuthRepository;

    @Autowired
    private UserService userService;

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

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    @GetMapping("/getAll")
    public ResponseEntity<List<AccountEntity>> getAllAccounts(HttpServletRequest request) {
        logger.info(getHeaders(request).toString());
        logger.info(request.getHeader("id"));
        logger.info(request.getHeader("token"));
        int accId = Integer.parseInt(request.getHeader("accId"));
        String authKey = request.getHeader("authKey");
        String ID = request.getHeader("userId");
        String token = request.getHeader("token");
        if (isValidAuthKey(accId,authKey) && isValidToken(ID,token)) {
            return ResponseEntity.ok(accountService.getAllAccounts());
        }
        else {
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/getById/{id}")
    public ResponseEntity<List<AccountEntity>> getAccountById(@PathVariable int id, HttpServletRequest request) {

        int accId = Integer.parseInt(request.getHeader("accId"));
        String authKey = request.getHeader("authKey");
        String ID = request.getHeader("userId");
        String token = request.getHeader("token");
        logger.info("authId" + accId);
        if (isValidAuthKey(accId,authKey) && isValidToken(ID,token)) {
            logger.info(String.valueOf(accountService.getAccountById(id)));
          return ResponseEntity.ok(accountService.getAccountById(id));
        }
        else{
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody String params1, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String ID = request.getHeader("id");
        String token = request.getHeader("token");

        if(isValidToken(ID,token)) {
        logger.info(params1);
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

            String creation = LocalDateTime.now().format(formatter);
            AccountEntity acc = new AccountEntity();
            UserEntity user = new UserEntity();
            logger.info(String.valueOf(params));
            acc.setName(params.get("name").getAsString());
            acc.setAddress(params.get("address").getAsString());
//            acc.setLogo(objectMapper.readValue(params.get("logo").getAsString(),byte[].class));
            acc.setCreationDate(creation);
            acc.setMaxUser(params.get("maxUser").getAsInt());
            acc.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
//            acc.setFeaturesMeta((HashMap<String, String>) params.get("featuresMeta"));
            acc.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            acc.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));

//                   acc.setFeaturesMeta(params.get("featuresMeta").toString());
//
            acc.setExpDate(LocalDateTime.parse(objectMapper.readValue(params.get("expDate").toString(),String.class),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            user.setAccountId(acc.getAccountId());
            user.setLoginId(params.get("loginId").getAsString());
            String pass = params.get("password").getAsString();
            String mypass = passwordEncoder.encode(pass);
            user.setPassword(mypass);
            user.setContact(params.get("contact").getAsString());
            user.setEmail(params.get("email").getAsString());
            user.setCreationDate(creation);
            user.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
//          user.setFeaturesMeta((HashMap<String, String>) params.get("featuresMeta2"));
            user.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            user.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));

        accountService.createAccount(acc);
        user.setAccountId(acc.getAccountId());
        userService.createUser(user);

        logger.info("acc.getMaxUser() : "+acc.getMaxUser());
        if(acc.getMaxUser() == 0){
            String token1 = "User"+generateToken(user.getLoginId())+user.getUserId();
            UserAuthEntity ua = new UserAuthEntity();
            ua.setLoginId(user.getLoginId());
            ua.setUserId(user.getUserId());
            ua.setToken(token1);
            ua.setCreationDate(LocalDateTime.now());
            ua.setExpDate(LocalDateTime.parse(objectMapper.readValue(params.get("expDate").toString(),String.class),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            userAuthRepository.save(ua);

        }

            AccountAuthEntity auth = accountAuthRepository.findById(acc.getAccountId());
            if(auth != null){
                auth.setAuthKey(generatedKey(acc.getAccountId()));
                auth.setCreationDate(LocalDateTime.now());
                auth.setExpDate(LocalDateTime.parse(objectMapper.readValue(params.get("expDate").toString(),String.class),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            else{
                auth = new AccountAuthEntity();
                auth.setAccountId(acc.getAccountId());
                auth.setName(acc.getName());
                auth.setAuthKey(generatedKey(acc.getAccountId()));
                auth.setCreationDate(LocalDateTime.now());
                auth.setExpDate(LocalDateTime.parse(objectMapper.readValue(params.get("expDate").toString(),String.class),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            }

            accountAuthRepository.save(auth);

            return new ResponseEntity<>("Account Created",HttpStatus.CREATED);
        }

        return  new ResponseEntity<>("Unauthorised User",HttpStatus.UNAUTHORIZED);

    }

    private String generatedKey(int accountId){
        String key = "Account"+generateKey(accountId)+accountId;
        logger.info("AccountAuthKey : "+key);
        logger.info("Unique Authorization key generated...!");
        return key;
    }

    private String generateKey(int accountId) {
        return Jwts.builder()
                .setSubject(String.valueOf(accountId))
                .setIssuedAt(new Date())
                .setExpiration(new java.sql.Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, "accountsecret")
                .compact();
    }
    private String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new java.sql.Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean isValidAuthKey(int accountid,String token){
        AccountAuthEntity acc = accountAuthRepository.findById(accountid);
       // logger.info(token);
        //logger.info(user.getToken());
        String key = acc.getAuthKey();
        if(acc == null || acc.getExpDate().isBefore(LocalDateTime.now()) || token == null || !(key.equals(token)))
            return false;
        return true;
    }
    public Boolean isValidToken(String id,String token){
        UserAuthEntity user = userAuthRepository.findById(id);
        //logger.info(token);
        //logger.info(user.getToken());
        String t = (user.getToken());
        if(user == null || user.getExpDate().isBefore(LocalDateTime.now()) || token == null || !(t.equals(token)))
            return false;
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



}
