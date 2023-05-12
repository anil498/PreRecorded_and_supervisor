/*package com.openvidu_databases.openvidu_dbbackend.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.gson.Gson;
import com.openvidu_databases.openvidu_dbbackend.Models.AppNotification;
import com.openvidu_databases.openvidu_dbbackend.Models.ShortenRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/notify")
public class NotificationService {
    @Autowired
    RestTemplate restTemplate;
    @Value("${sms.url:-}")
    String url;
    @Value("${sms.text:-}")
    String text;
    //  @Autowired
    //  URLController urlController;
    @Autowired
    ShortenRequest shortenRequest;
    @Value("${tiny.url:-}")
    private String baseString;

    @Value("${whatsapp.url:https://api.pinbot.ai/v1/wamessage/send}")
    String whatsAppUrl;

    @Value("${whatsapp.text:}")
    String whatsappText;

    @Value("${firebase.collection}")
    private String firebaseCollection;
    @Autowired
    FirebaseMessaging firebaseMessaging;

    @Autowired
    FirebaseAuth firebaseAuth;
    @Autowired
    Firestore db;

/*
    @PostMapping("/sms")
    public ResponseEntity<?> sendSms(@RequestBody(required = false) Map<String, Object> params, HttpServletRequest request,
                                     HttpServletResponse res) throws Exception {
        String msisdn = params.get("msisdn").toString();
        String sessionId = params.get("sessionId").toString();
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();
        shortenRequest.setUrl(sessionId);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity <String> entity = new HttpEntity<String>(headers);
        String finalUR=baseString+sessionId;
        System.out.println(finalUR+baseString+sessionId);
        String finalText=createURL(text,finalUR);
        finalText= URLEncoder.encode(finalText, "UTF-8");
        String finalUrl=createURL(url,msisdn,finalText);
        URI uri =new URI(finalUrl);

        String responseBody= restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();

        System.out.println(responseBody);
        Map<Object,Object> attributes = gson.fromJson(responseBody,Map.class);
        Map<String, Object> response = new HashMap<String, Object>();
        System.out.println("Response url:- "+baseString+sessionId);
        response.put("state",attributes.get("state"));
        response.put("description",attributes.get("description"));
        response.put("url",baseString+sessionId);
        response.put("sessionId",sessionId);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public String createURL (String url, Object ... params) {
        return new MessageFormat(url).format(params);
    }

   // public static final Logger logger= LoggerFactory.getLogger(NotificationService.class);

*/
    /*
@PostMapping("/sendWhatsapp")
public String sendMessage(String recipient, String templateId, String url, String salutation, String salutation1) {
    String endpoint = whatsAppUrl;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // Build the request body
    String requestBody = "{ \"from\": \"919811026184\", " +
            "\"to\": \"" + recipient + "\", " +
            "\"type\": \"template\", " +
            "\"message\": { " +
            "\"templateid\": \"" + templateId + "\", " +
            "\"url\": \"" + url + "\", " +
            "\"placeholders\": [ \"" + salutation + "\", \"" + salutation1 + "\"] " +
            "} " +
            "}";

    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);
    return response.getBody();
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
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
         //   looger.error("Getting Exception While Submitting the message {}",e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

     */
