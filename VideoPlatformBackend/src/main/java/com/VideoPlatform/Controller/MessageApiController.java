package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Models.AppNotification;
import com.VideoPlatform.Models.SubmitResponse;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.MessagingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.API)
public class MessageApiController {

    @Autowired
    MessagingService messagingService;

    @Value("${firebase.collection}")
    private String firebaseCollection;
    @Value("${call.prefix}")
    private String callPrefix;

    @Value(("${call.access.time}"))
    private int callAccessTime;
    @Autowired
    FirebaseMessaging firebaseMessaging;
    @Autowired
    FirebaseAuth firebaseAuth;
    @Autowired
    Firestore db;
    @Autowired
    AccountAuthRepository accountAuthRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    AccessRepository accessRepository;
    @Autowired
    UserRepository userRepository;

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    private static final Logger logger= LoggerFactory.getLogger(MessageApiController.class);
    @PostMapping(value="/sendSms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendSMS(@RequestBody(required = false) Map<String, ?> params, HttpServletResponse response, HttpServletRequest request) throws IOException, URISyntaxException {

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
        if(!(byAccess(5001,token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        String msisdn= (String) params.get("msisdn");
        //logger.info("getHeaders(request) = "+getHeaders(request));
        //logger.info("getHeaders(request).get(\"origin\") = "+getHeaders(request).get("origin"));

//        String callUrl= "https://demo2.progate.mobi"+(String) params.get("callUrl");
//        String sessionId =getHeaders(request).get("sessionid");
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.API, params != null ? params.toString() : "{}",getHeaders(request));

//        logger.info("Request response {}",responseSms);
        String userInfo=null;
        if(params.containsKey("userInfo")){
            userInfo= String.valueOf(params.get("userInfo"));
        }
        SessionEntity sessionEntity = storeSessions(token,authKey,msisdn,userInfo);
        String callUrl= callPrefix+sessionEntity.getSessionKey();
//        String callUrl = callPrefix+sessionKey;
        String callUrlSupport = callPrefix+sessionEntity.getSessionSupportKey();
        logger.info(callUrl);
        SubmitResponse responseSms=messagingService.sendSms(request,response,msisdn,callUrl);
        responseSms.setCallUrl(callUrl);
        HashMap<String,String> res=new HashMap<>();
        res.put("callurl",callUrlSupport);
        logger.info(String.valueOf(responseSms));
        return ResponseEntity.ok(res);
 //       return responseSms;
    }
    @PostMapping ("/sendWhatsapp")
    public ResponseEntity<?> sendWA(@RequestBody(required = false) Map<String, ?> params, HttpServletResponse response, HttpServletRequest request) throws IOException, URISyntaxException, OpenViduJavaClientException, OpenViduHttpException {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int check = isValidAuthKey(authKey);
        if(check == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(5002,token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        String from= (String) params.get("from");
        String to= (String) params.get("msisdn");
        String type= (String) params.get("type");
        String templateid= (String) params.get("templateId");
        String userInfo=null;
        if(params.containsKey("userInfo")){
            userInfo= String.valueOf(params.get("userInfo"));
        }
        SessionEntity sessionEntity = storeSessions(token,authKey,to,userInfo);
        String placeHolder= callPrefix+sessionEntity.getSessionKey();
        String callUrlSupport = callPrefix+sessionEntity.getSessionSupportKey();
//        String placeHolder= "https://demo2.progate.mobi"+(String) params.get("callUrl");
//        String sessionId =getHeaders(request).get("sessionid");

        String callUrl= callPrefix+sessionEntity.getSessionKey();
//        String callUrl= "https://demo2.progate.mobi"+(String) params.get("callUrl");
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.API, params != null ? params.toString() : "{}",getHeaders(request));
        SubmitResponse responseSms=messagingService.sendWA(request,response,to,placeHolder,from,type,templateid);
        responseSms.setCallUrl(callUrl);
        HashMap<String,String> res=new HashMap<>();
        res.put("callurl",callUrlSupport);
        logger.info("Request response {}",responseSms);
        return ResponseEntity.ok(res);
//        return responseSms;
    }

    @PostMapping("/notification")
    public ResponseEntity<?> sendNotification(@RequestBody(required = false) Map<String, ?> params, HttpServletResponse response, HttpServletRequest request) throws IOException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(5003,token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        String phoneNumber= (String) params.get("msisdn");
//        String sessionId = (String) params.get("sessionId");
        String title = (String) params.get("title");
        String body = (String) params.get("body");

        try {
            DocumentReference docRef = db.collection(firebaseCollection).document(phoneNumber);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            AppNotification appNotification=new AppNotification();
            if (document.exists()) {
                appNotification=document.toObject(AppNotification.class);
            } else {
                System.out.println("No such document!");
            }
            String userInfo=null;
            if(params.containsKey("userInfo")){
                userInfo= String.valueOf(params.get("userInfo"));
            }
            SessionEntity sessionEntity = storeSessions(token,authKey,phoneNumber,userInfo);
            HashMap<String,String> res=new HashMap<>();
            HashMap<String, String>map= new HashMap<>();
            map.put("TITLE",title);
            map.put("SESSION_ID",sessionEntity.getSessionId());
            map.put("BODY",body);


            Message message = Message.builder()
                    .setToken(appNotification.getUsertoken())
                    .putAllData(map)
                    .build();

            String callUrl = callPrefix+sessionEntity.getSessionKey();
            String callUrlSupport = callPrefix+sessionEntity.getSessionSupportKey();
            // Send notification message
            String id=firebaseMessaging.send(message);
            res.put("id",id);

            res.put("callurl",callUrlSupport);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            //   looger.error("Getting Exception While Submitting the message {}",e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        if(acc.getExpDate().isBefore(LocalDateTime.now())){
            return 0;
        }

        return 1;
    }
    public Boolean isValidToken(String token){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        if(user == null)return false;
        if(user.getExpDate().isBefore(LocalDateTime.now()))
            return false;
        return true;
    }
    private SessionEntity storeSessions(String token,String authKey,String msisdn,String userInfo){
        String creation = LocalDateTime.now().format(formatter);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newDateTime = now.plus(callAccessTime, ChronoUnit.HOURS);
        SessionEntity session = new SessionEntity();

        UserAuthEntity userAuth = userAuthRepository.findByToken(token);
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        AccountEntity account = accountRepository.findByAccountId(acc.getAccountId());
        session.setSessionName(account.getName());
        session.setUserId(userAuth.getUserId());
        session.setMobile(msisdn);
        session.setAccountId(acc.getAccountId());
        session.setStatus("1");
        String sessionId=acc.getAccountId()+"_"+userAuth.getUserId()+"_"+System.currentTimeMillis();
        session.setSessionId(sessionId);
        String sessionKey = givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        session.setSessionKey(sessionKey);
        String supportKey = sessionKey+"_1";
        session.setSessionSupportKey(supportKey);
        session.setCreation(creation);
        session.setUserInfo(userInfo);
        session.setExpDate(String.valueOf(newDateTime));

        sessionRepository.save(session);
        return session;
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

