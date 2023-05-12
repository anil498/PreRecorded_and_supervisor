package com.openvidu_databases.openvidu_dbbackend.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.openvidu_databases.openvidu_dbbackend.Constant.RequestMappings;
import com.openvidu_databases.openvidu_dbbackend.Models.AppNotification;
import com.openvidu_databases.openvidu_dbbackend.Models.SubmitResponse;
import com.openvidu_databases.openvidu_dbbackend.Services.MessagingService;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
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

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.API)
public class MessageApiController {

    @Autowired
    MessagingService messagingService;

    @Value("${firebase.collection}")
    private String firebaseCollection;
    @Autowired
    FirebaseMessaging firebaseMessaging;
    @Autowired
    FirebaseAuth firebaseAuth;
    @Autowired
    Firestore db;

    private static final Logger logger= LoggerFactory.getLogger(MessageApiController.class);
    @PostMapping(value="/sendSms", produces = MediaType.APPLICATION_JSON_VALUE)
    public SubmitResponse sendSMS(@RequestBody(required = false) Map<String, ?> params, HttpServletResponse response, HttpServletRequest request) throws IOException, URISyntaxException {
        String msisdn= (String) params.get("msisdn");
        //logger.info("getHeaders(request) = "+getHeaders(request));
        //logger.info("getHeaders(request).get(\"origin\") = "+getHeaders(request).get("origin"));
        //String callUrl= getHeaders(request).get("origin")+(String) params.get("callUrl");
        String callUrl= "https://demo2.progate.mobi"+(String) params.get("callUrl");
        String sessionId =getHeaders(request).get("sessionid");
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.API, params != null ? params.toString() : "{}",getHeaders(request));
        SubmitResponse responseSms=messagingService.sendSms(request,response,sessionId,msisdn,callUrl);
        responseSms.setCallUrl(callUrl);
        logger.info("Request response {}",responseSms);
        return responseSms;
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

