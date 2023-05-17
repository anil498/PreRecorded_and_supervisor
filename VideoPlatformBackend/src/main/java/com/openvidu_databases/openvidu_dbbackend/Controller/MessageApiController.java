package com.openvidu_databases.openvidu_dbbackend.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.openvidu_databases.openvidu_dbbackend.Constant.RequestMappings;
import com.openvidu_databases.openvidu_dbbackend.Entity.AccountAuthEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.SessionEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserAuthEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
import com.openvidu_databases.openvidu_dbbackend.Models.AppNotification;
import com.openvidu_databases.openvidu_dbbackend.Models.SubmitResponse;
import com.openvidu_databases.openvidu_dbbackend.Repository.AccountAuthRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.SessionRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.UserAuthRepository;
import com.openvidu_databases.openvidu_dbbackend.Services.MessagingService;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    FirebaseMessaging firebaseMessaging;
    @Autowired
    FirebaseAuth firebaseAuth;
    @Autowired
    Firestore db;
    @Autowired
    AccountAuthRepository accountAuthRepository;
    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    SessionRepository sessionRepository;

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
        if(!isValidToken(authId,token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }


        String msisdn= (String) params.get("msisdn");
        //logger.info("getHeaders(request) = "+getHeaders(request));
        //logger.info("getHeaders(request).get(\"origin\") = "+getHeaders(request).get("origin"));

//        String callUrl= "https://demo2.progate.mobi"+(String) params.get("callUrl");
//        String sessionId =getHeaders(request).get("sessionid");
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.API, params != null ? params.toString() : "{}",getHeaders(request));

//        logger.info("Request response {}",responseSms);
        String sessionKey = storeSessions(authId,msisdn);
        String callUrl= getHeaders(request).get("origin")+callPrefix+sessionKey;
        logger.info(callUrl);
        SubmitResponse responseSms=messagingService.sendSms(request,response,msisdn);
        responseSms.setCallUrl(callUrl);
        return ResponseEntity.ok(responseSms);
 //       return responseSms;
    }
    @PostMapping ("/sendWhatsapp")
    public SubmitResponse sendWA(@RequestBody(required = false) Map<String, ?> params, HttpServletResponse response, HttpServletRequest request) throws IOException, URISyntaxException, OpenViduJavaClientException, OpenViduHttpException {
        String from= (String) params.get("from");
        String to= (String) params.get("msisdn");
        String type= (String) params.get("type");
        String templateid= (String) params.get("templateId");
        //String placeHolder= getHeaders(request).get("origin")+(String) params.get("callUrl");
        String placeHolder= "https://demo2.progate.mobi"+(String) params.get("callUrl");
        String sessionId =getHeaders(request).get("sessionid");
        //String callUrl= getHeaders(request).get("origin")+(String) params.get("callUrl");
        String callUrl= "https://demo2.progate.mobi"+(String) params.get("callUrl");
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.API, params != null ? params.toString() : "{}",getHeaders(request));
        SubmitResponse responseSms=messagingService.sendWA(request,response,sessionId,to,placeHolder,from,type,templateid);
        responseSms.setCallUrl(callUrl);

        logger.info("Request response {}",responseSms);
        return responseSms;
    }

    @PostMapping("/notification")
    public ResponseEntity<?> sendNotification(@RequestBody(required = false) Map<String, ?> params) throws IOException {

        String phoneNumber= (String) params.get("msisdn");
        String sessionId = (String) params.get("sessionId");
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

            HashMap<String,String> response=new HashMap<>();
            HashMap<String, String>map= new HashMap<>();
            map.put("TITLE",title);
            map.put("SESSION_ID",sessionId);
            map.put("BODY",body);


            Message message = Message.builder()
                    .setToken(appNotification.getUsertoken())
                    .putAllData(map)
                    .build();

            // Send notification message
            String id=firebaseMessaging.send(message);
            response.put("id",id);
            response.put("sessionId",sessionId);
            return new ResponseEntity<>(response, HttpStatus.OK);
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
    private String storeSessions(Integer authId, String msisdn){
        SessionEntity session = new SessionEntity();
        UserAuthEntity userAuth = userAuthRepository.findByAuthId(authId);
        AccountAuthEntity acc = accountAuthRepository.findByAuthId(authId);
        session.setUserId(userAuth.getUserId());
        session.setMobile(msisdn);
        session.setAccountId(acc.getAccountId());
        session.setStatus("1");
        String sessionId=acc.getAccountId()+"_"+userAuth.getUserId()+"_"+System.currentTimeMillis();
        session.setSessionId(sessionId);
        session.setSessionKey(givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect());
        sessionRepository.save(session);
        return session.getSessionKey();
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

