package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
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
    AccountAuthRepository accountAuthRepository;
    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccessRepository accessRepository;
    @Autowired
    SessionService sessionService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    CommonService commonService;
    @Value("${file.path}")
    private String FILE_DIRECTORY;
    @Value("${call.prefix}")
    private String callPrefix;

    @PostMapping("/Create")
    public ResponseEntity<?> createSession(@RequestBody(required = false) Map<String, ?> params,HttpServletRequest request, HttpServletResponse response) {

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

        SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,false,"","",description,participantName);
        SessionEntity sessionEntitySupport = sessionService.createSession(authKey,token,true,sessionEntityCustomer.getSessionId(),sessionEntityCustomer.getSessionKey(),description,participantName);


        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Session created!");
        return ok(result);
    }

    @GetMapping("/GetAll")
    public ResponseEntity<?> getAllSessions(HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"my_sessions")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ok(sessionService.getAllSupportSessions(authKey,token));
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
    @DeleteMapping("/Delete/{key}")
    public ResponseEntity<?> deleteSession(@PathVariable String key, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"my_sessions")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        sessionService.deleteSession(key);

        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Session deleted!");

        return ok(result);
    }

    @PutMapping("/hold")
    public ResponseEntity<?> updateHold(@RequestBody Map<String, Object> params, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"session_update")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        sessionService.updateHold(params);
        return ok(commonService.responseData("200","Hold updated!"));
    }

    @PostMapping("/sendLink")
    public ResponseEntity<?> sendLink(@RequestBody(required = false)  Map<String,?> params, HttpServletRequest request,HttpServletResponse response) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"dynamic_links")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        sessionService.sendLink(params,request,response);

        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Link sent successfully !");

        return ok(result);
    }
    @PostMapping("/sessionPlugin")
    public ResponseEntity<?> sessionPlugin(@RequestBody(required = false) Map<String, ?> params,HttpServletRequest request, HttpServletResponse response) {

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
        SessionEntity sessionEntityCustomer = sessionService.createSession(authKey,token,false,"","",description,participantName);
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
