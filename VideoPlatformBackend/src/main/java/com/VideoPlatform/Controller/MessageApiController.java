package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Models.AppNotification;
import com.VideoPlatform.Models.SubmitResponse;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.MessagingService;
import com.VideoPlatform.Services.SessionService;
import com.VideoPlatform.Utils.CommonUtils;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLSESSION)
public class MessageApiController {

    @Value("${firebase.collection}")
    private String firebaseCollection;
    @Value("${call.prefix}")
    private String callPrefix;
    @Value(("${call.access.time}"))
    private int callAccessTime;
    @Value(("${support.suffix}"))
    private String supportSuffix;


    @Autowired
    MessagingService messagingService;
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
    @Autowired
    SessionService sessionService;

    private static final Logger logger= LoggerFactory.getLogger(MessageApiController.class);
    @PostMapping(value="/Send/SMS", produces = MediaType.APPLICATION_JSON_VALUE)
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
//        logger.info("getHeaders(request) = "+getHeaders(request));
//        logger.info("getHeaders(request).get(\"origin\") = "+getHeaders(request).get("origin"));

//        String callUrl= "https://demo2.progate.mobi"+(String) params.get("callUrl");
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.APICALLSESSION, params != null ? params.toString() : "{}",getHeaders(request));

        SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,true);
        SessionEntity sessionEntitySupport = sessionService.createSession(authKey,token,false);
        String callUrl= callPrefix+sessionEntityCustomer.getSessionId();
        logger.info("callUrlCustomer : {}",callUrl);

        SubmitResponse responseSms=messagingService.sendSms(request,response,msisdn,callUrl);
        responseSms.setCallUrl(callUrl);

        HashMap<String,String> res=new HashMap<>();
        String returnUrl = callUrl+supportSuffix;
        if(params.containsKey("getLink")){
            String getLink = String.valueOf(params.get("getLink"));
            if(getLink.equals("0")){
                res.put("status_code","200");
                res.put("msg","Video link send successfully!");
            }
            else if(getLink.equals("1")){
                res.put("status_code","200");
                res.put("msg","Video link send successfully!");
                res.put("link",returnUrl);
            }
            else if(getLink.equals("2")){
                res.put("status_code","200");
                res.put("msg","Video link send successfully!");
                res.put("link",returnUrl);
                res.put("customer_link",callUrl);
            }
        }

        logger.info(String.valueOf(responseSms));
        return ResponseEntity.ok(res);
    }

    @PostMapping ("/Send/WhatsApp")
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
//        String userInfo=null;
//        if(params.containsKey("userInfo")){
//            userInfo= String.valueOf(params.get("userInfo"));
//        }

        SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,true);
        SessionEntity sessionEntitySupport = sessionService.createSession(authKey,token,false);
        String placeHolder= callPrefix+sessionEntityCustomer.getSessionId();
        logger.info("callUrlCustomer : {}",placeHolder);

        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.APICALLSESSION, params != null ? params.toString() : "{}",getHeaders(request));
        SubmitResponse responseSms=messagingService.sendWA(request,response,to,placeHolder,from,type,templateid);
        responseSms.setCallUrl(placeHolder);
        HashMap<String,String> res=new HashMap<>();
        String returnUrl = placeHolder+supportSuffix;
        if(params.containsKey("getLink")){
            String getLink = String.valueOf(params.get("getLink"));
            if(getLink.equals("0")){
                res.put("status_code","200");
                res.put("msg","Video link send successfully!");
            }
            else if(getLink.equals("1")){
                res.put("status_code","200");
                res.put("msg","Video link send successfully!");
                res.put("link",returnUrl);
            }
            else if(getLink.equals("2")){
                res.put("status_code","200");
                res.put("msg","Video link send successfully!");
                res.put("link",returnUrl);
                res.put("customer_link",placeHolder);
            }
        }
        logger.info("Request response {}",responseSms);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/Send/AppNotification")
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
            SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,true);
            SessionEntity sessionEntitySupport = sessionService.createSession(authKey,token,false);
            String callUrl= callPrefix+sessionEntityCustomer.getSessionId();
            logger.info("callUrl : {}",callUrl);

//            SessionEntity sessionEntity = storeSessions(token,authKey,phoneNumber,userInfo);

            HashMap<String, String>map= new HashMap<>();
            map.put("TITLE",title);
            map.put("SESSION_ID",sessionEntityCustomer.getSessionId());
            map.put("BODY",body);

            Message message = Message.builder()
                    .setToken(appNotification.getUsertoken())
                    .putAllData(map)
                    .build();

            // Send notification message
            HashMap<String,String> res=new HashMap<>();
            String returnUrl = callUrl+supportSuffix;
            if(params.containsKey("getLink")){
                String getLink = String.valueOf(params.get("getLink"));
                if(getLink.equals("0")){
                    res.put("status_code","200");
                    res.put("msg","Video link send successfully!");
                }
                else if(getLink.equals("1")){
                    res.put("status_code","200");
                    res.put("msg","Video link send successfully!");
                    res.put("link",returnUrl);
                }
                else if(getLink.equals("2")){
                    res.put("status_code","200");
                    res.put("msg","Video link send successfully!");
                    res.put("link",returnUrl);
                    res.put("customer_link",callUrl);
                }
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        if(sessionRepository.findBySessionKey(generatedString) == null)
            return generatedString;
        else givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        logger.info(generatedString);
        return null;
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

