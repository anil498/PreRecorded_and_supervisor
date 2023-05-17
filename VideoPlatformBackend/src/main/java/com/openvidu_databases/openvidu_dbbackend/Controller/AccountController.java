package com.openvidu_databases.openvidu_dbbackend.Controller;

//import com.openvidu_databases.openvidu_dbbackend.Entity.AccountAuthEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.openvidu_databases.openvidu_dbbackend.Constant.RequestMappings;
import com.openvidu_databases.openvidu_dbbackend.Entity.*;
//import com.openvidu_databases.openvidu_dbbackend.Repository.AccountAuthRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.*;
import com.openvidu_databases.openvidu_dbbackend.Services.AccountService;
import com.openvidu_databases.openvidu_dbbackend.Services.UserService;
import com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.util.*;
import java.util.stream.IntStream;

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

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    @GetMapping("/getAll")
    public ResponseEntity<List<AccountEntity>> getAllAccounts(HttpServletRequest request) throws JsonProcessingException {
        logger.info(getHeaders(request).toString());

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(authId,1000))){
            logger.info("for 1000 : "+byAccess(authId,1000));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(authId,token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(accountService.getAllAccounts());
    }
/*    @GetMapping("/getAll")
    public ResponseEntity<List<AccountEntity>> getAllUsersFromAccount(HttpServletRequest request) throws JsonProcessingException {
        logger.info(getHeaders(request).toString());

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(authId,1000))){
            logger.info("for 1000 : "+byAccess(authId,1000));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(authId,token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(accountService.getAllAccounts());
    }

 */
    @GetMapping("/getById/{id}")
    public ResponseEntity<List<AccountEntity>> getAccountById(@PathVariable int id, HttpServletRequest request) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(authId,1000))){
            logger.info("for 1000 : "+byAccess(authId,1000));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(authId,token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody String params1, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        logger.info("Auth Id : "+authId);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(authId,1001))){
            logger.info("for 1001 : "+byAccess(authId,1001));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(authId,token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
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
            acc.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
            acc.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            acc.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));
            acc.setExpDate(LocalDateTime.parse(objectMapper.readValue(params.get("expDate").toString(),String.class),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            UserAuthEntity u = userAuthRepository.findByAuthId(authId);
            logger.info("User Data : "+u);
            user.setFname(params.get("fname").getAsString());
            user.setLname(params.get("lname").getAsString());
            user.setExpDate(params.get("expDate").getAsString());
            user.setLoginId(params.get("loginId").getAsString());
            String pass = params.get("password").getAsString();
            String mypass = passwordEncoder.encode(pass);
            user.setPassword(mypass);
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
            userService.createUser(user);

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

            int authId1 = auth.getAuthId();
            logger.info("acc.getMaxUser() : "+acc.getMaxUser());
            if(acc.getMaxUser() == 0){
                logger.info("Checking userId : "+user.getUserId());
                String token1 = generateToken(user.getUserId(),"UR");
                UserAuthEntity ua = new UserAuthEntity();
                ua.setLoginId(user.getLoginId());
                ua.setUserId(user.getUserId());
                ua.setToken(token1);
 //               ua.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
                ua.setAuthId(authId1);
                ua.setCreationDate(LocalDateTime.now());
                ua.setExpDate(LocalDateTime.parse(objectMapper.readValue(params.get("expDate").toString(),String.class),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                userAuthRepository.save(ua);
            }

            Map<String,String> result = new HashMap<>();
            result.put("status_code ","200");
            result.put("message", "Account Created");

            return new ResponseEntity<>(result,HttpStatus.CREATED);
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
            // logger.info(token);
            //logger.info(user.getToken());
            String key = (acc.getAuthKey());
            if(acc == null || acc.getExpDate().isBefore(LocalDateTime.now()) || authKey == null || !(key.equals(authKey))){
                return 0;
            }
            int authId = acc.getAuthId();
            return authId;
        }
        public Boolean isValidToken(int authId,String token){
            UserAuthEntity user = userAuthRepository.findByAuthId(authId);
            if(user == null)return false;
            logger.info("DAta by authId.."+user);
            //logger.info(user.getToken());
            String t = (user.getToken());
            if(user == null || user.getExpDate().isBefore(LocalDateTime.now()) || token == null || !(t.equals(token)))
                return false;
            return true;
        }

    private boolean byAccess(Integer authId,int toCheckValue) throws JsonProcessingException {

        UserAuthEntity user = userAuthRepository.findByAuthId(authId);
        logger.info("User auth data by Auth Id :"+user);
        Integer userId = user.getUserId();
        logger.info("User ID is : "+userId);
        UserEntity u = userRepository.findByUserId(userId);
        logger.info("User data by User Id :"+u);
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
        logger.info("IfAPIPresent.... : "+apiPresent);
        logger.info("String : " + str);
        logger.info("Integer array : " + Arrays.toString(arr));
        logger.info("API array : " + Arrays.toString(apiArr));
        return apiPresent;

    }

    private boolean ifExistInApiArray(int[] arr,int toCheckValue){
        logger.info("ToCheck Array is : "+arr);
        logger.info("TocheckVal is : "+toCheckValue);
        return IntStream.of(arr).anyMatch(x -> x == toCheckValue);
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
