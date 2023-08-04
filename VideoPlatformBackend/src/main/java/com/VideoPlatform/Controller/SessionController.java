package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Services.SessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLSESSION)
public class SessionController {

    private static final Logger logger= LoggerFactory.getLogger(SessionController.class);
    @Autowired
    SessionService sessionService;
    @Autowired
    CommonService commonService;
    @Value("${file.path}")
    private String FILE_DIRECTORY;
    @Value("${call.prefix}")
    private String callPrefix;

    @PostMapping("/Create")
    public ResponseEntity<?> createSession(@RequestBody(required = false) Map<String, ?> params,HttpServletRequest request, HttpServletResponse response) {
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.APICALLSESSION, params != null ? params : "{}",commonService.getHeaders(request));
        if(params==null)
            return new ResponseEntity<>(commonService.responseData("500","Request must contain parameter values!"),HttpStatus.INTERNAL_SERVER_ERROR);

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"session_create")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String description=null;
        if(params.containsKey("description")){
            description= String.valueOf(params.get("description"));
        }
        String participantName="Participant";
        if(params.containsKey("participantName")){
            participantName= String.valueOf(params.get("participantName"));
        }
        SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,false,"","",description,participantName,"Customer");
        SessionEntity sessionEntitySupport = sessionService.createSession(authKey,token,true,sessionEntityCustomer.getSessionId(),sessionEntityCustomer.getSessionKey(),description,participantName,"Support");

        return ok(commonService.responseData("200","Session Created!"));
    }

    @GetMapping("/GetAll")
    public ResponseEntity<?> getAllSessions(HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(commonService.authorizationCheck(authKey,token,"get_all")){
            return ok(sessionService.getAllSupportSessions());
        }
        if(commonService.authorizationCheck(authKey,token,"my_sessions")){
            return ok(sessionService.getAllSupportSessionsUser(token));
        }
        return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/GetByKey")
    public ResponseEntity<?> getSessionByKey(@RequestBody Map<String, String> params, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"my_sessions")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String sessionKey = params.get("sessionKey");
        if(sessionService.getByKey(sessionKey) == null)
            return new ResponseEntity<>("Session Key Expired or does not exist!",HttpStatus.FORBIDDEN);
        long timeTaken = System.currentTimeMillis() - startTime;
        logger.info("TIme taken = {}",timeTaken);
        return ok(sessionService.getByKey(sessionKey));

    }

    @PostMapping("/GetSessions")
    public ResponseEntity<?> getSessionByCount(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        Integer count = params.get("count");
        if(!commonService.authorizationCheck(authKey,token,"my_sessions")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<String> sessionKeys = new ArrayList<>();
        while(count!=0){
            SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,false,"","","","","Customer");
            sessionKeys.add(sessionEntityCustomer.getSessionKey());
            count--;
        }
        return ResponseEntity.ok(sessionKeys);
    }

    @DeleteMapping("/Delete/{key}")
    public ResponseEntity<?> deleteSession(@PathVariable String key, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"my_sessions")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return sessionService.deleteSession(key);
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateHold(@RequestBody Map<String, Object> params, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"session_update")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        sessionService.updateHold(params);
        return ok(commonService.responseData("200","Session updated!"));
    }

    @PostMapping("/sendLink")
    public ResponseEntity<?> sendLink(@RequestBody(required = false)  Map<String,?> params, HttpServletRequest request,HttpServletResponse response) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"dynamic_links")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return sessionService.sendLink(params,request,response);
    }

    @PostMapping("/sessionPlugin")
    public ResponseEntity<?> sessionPlugin(@RequestBody(required = false) Map<String, ?> params,HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if (!commonService.authorizationCheck(authKey, token, "session_create")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String description = null;
        if (params.containsKey("description")) {
            description = String.valueOf(params.get("description"));
        }
        String participantName = "Participant";
        if (params.containsKey("participantName")) {
            participantName = String.valueOf(params.get("participantName"));
        }
        SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,false,"","",description,participantName,"Customer");
        String callUrl= callPrefix+sessionEntityCustomer.getSessionKey();
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg","Session created!");
        result.put("callUrl",callUrl);
        return ok(result);
    }

    @GetMapping("/Download/{filename}")
    public ResponseEntity<byte[]> handleFileDownload(@PathVariable("filename") String filename) throws IOException {
        Path file = Paths.get(FILE_DIRECTORY, filename);
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
