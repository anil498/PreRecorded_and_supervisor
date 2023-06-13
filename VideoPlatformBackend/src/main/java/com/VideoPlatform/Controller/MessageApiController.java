package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Models.AppNotification;
import com.VideoPlatform.Models.SubmitResponse;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Services.MessagingService;
import com.VideoPlatform.Services.SessionService;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
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
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLSESSION)
public class MessageApiController {

    @Value("${firebase.collection}")
    private String firebaseCollection;
    @Value("${call.prefix}")
    private String callPrefix;
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
    @Autowired
    CommonService commonService;

    private static final Logger logger= LoggerFactory.getLogger(MessageApiController.class);
    @PostMapping(value="/CreateAndSendLink/SMS", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendSMS(@RequestBody(required = false) Map<String, ?> params, HttpServletResponse response, HttpServletRequest request) throws IOException, URISyntaxException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token)){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("sms",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        String msisdn= (String) params.get("msisdn");
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.APICALLSESSION, params != null ? params.toString() : "{}",commonService.getHeaders(request));
        String description=null;
        if(params.containsKey("description")){
            description= String.valueOf(params.get("description"));
        }
        String participantName=null;
        if(params.containsKey("participantName")){
            participantName= String.valueOf(params.get("participantName"));
        }
        SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,false,"","",description,participantName);
        SessionEntity sessionEntitySupport = sessionService.createSession(authKey,token,true,sessionEntityCustomer.getSessionId(),sessionEntityCustomer.getSessionKey(),description,participantName);
        String callUrl= callPrefix+sessionEntityCustomer.getSessionKey();
        logger.info("callUrlCustomer : {}",callUrl);

        SubmitResponse responseSms=messagingService.sendSms(request,response,msisdn,callUrl);
        responseSms.setCallUrl(callUrl);

        HashMap<String,String> res=new HashMap<>();
        String returnUrl = callPrefix+sessionEntitySupport.getSessionKey();
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

    @PostMapping ("/CreateAndSendLink/WhatsApp")
    public ResponseEntity<?> sendWA(@RequestBody(required = false) Map<String, ?> params, HttpServletResponse response, HttpServletRequest request) throws IOException, URISyntaxException, OpenViduJavaClientException, OpenViduHttpException {
        logger.info("Rest API {} request {}",params,request);
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token)){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("whatsapp",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        String from= (String) params.get("from");
        String to= (String) params.get("msisdn");
        String type= (String) params.get("type");
        String templateId= (String) params.get("templateId");
        String description=null;
        if(params.containsKey("description")){
            description= String.valueOf(params.get("description"));
        }
        String participantName=null;
        if(params.containsKey("participantName")){
            participantName= String.valueOf(params.get("participantName"));
        }
        SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,false,"","",description,participantName);
        SessionEntity sessionEntitySupport = sessionService.createSession(authKey,token,true,sessionEntityCustomer.getSessionId(),sessionEntityCustomer.getSessionKey(),description,participantName);
        String placeHolder= callPrefix+sessionEntityCustomer.getSessionKey();
        logger.info("callUrlCustomer : {}",placeHolder);

        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.APICALLSESSION, params != null ? params.toString() : "{}",commonService.getHeaders(request));
        SubmitResponse responseSms=messagingService.sendWA(request,response,to,placeHolder,from,type,templateId);
        responseSms.setCallUrl(placeHolder);
        HashMap<String,String> res=new HashMap<>();
        String returnUrl = callPrefix+sessionEntitySupport.getSessionKey();
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

    @PostMapping("/CreateAndSendLink/AppNotification")
    public ResponseEntity<?> sendNotification(@RequestBody(required = false) Map<String, ?> params, HttpServletResponse response, HttpServletRequest request) throws IOException {
        logger.info("Rest API {}",params);
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token)){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("send_notification",token))){
            logger.info("Permission Denied. Don't have access for this service!");
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
            String description=null;
            if(params.containsKey("description")){
                description= String.valueOf(params.get("description"));
            }String participantName=null;
            if(params.containsKey("participantName")){
                participantName= String.valueOf(params.get("participantName"));
            }

            SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,false,"","",description,participantName);
            SessionEntity sessionEntitySupport = sessionService.createSession(authKey,token,true,sessionEntityCustomer.getSessionId(),sessionEntityCustomer.getSessionKey(),description,participantName);
            String callUrl= callPrefix+sessionEntityCustomer.getSessionKey();
            logger.info("callUrl : {}",callUrl);

            HashMap<String, String>map= new HashMap<>();
            map.put("TITLE",title);
            map.put("SESSION_ID",sessionEntityCustomer.getSessionId());
            map.put("BODY",body);

            Message message = Message.builder()
                    .setToken(appNotification.getUsertoken())
                    .putAllData(map)
                    .build();

            HashMap<String,String> res=new HashMap<>();
            String returnUrl = callPrefix+sessionEntitySupport.getSessionKey();
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



}

