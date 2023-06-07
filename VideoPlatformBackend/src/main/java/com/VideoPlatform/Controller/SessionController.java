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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Value(("${call.access.time}"))
    private int callAccessTime;

    @PostMapping("/Create")
    public ResponseEntity<?> createSession(@RequestBody(required = false) Map<String, ?> params,HttpServletRequest request, HttpServletResponse response) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if (authId == 0) {
            logger.info("Unauthorised user, wrong authorization key !");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!(commonService.checkAccess("my_sessions", token))) {
            logger.info("Permission Denied. Don't have access for this service!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

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

        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "Session created!");
        return ok(result);
    }

    @GetMapping("/GetAll")
    public ResponseEntity<?> getAllSessions(HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<SessionEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<SessionEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("session_details",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<SessionEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ok(sessionService.getAllSessions());
    }

    @PostMapping("/GetByKey")
    public ResponseEntity<SessionEntity> getSessionByKey(@RequestBody Map<String, String> params, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("session_details",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
        }
        String sessionKey = params.get("sessionKey");
            return new ResponseEntity<>(sessionService.getByKey(sessionKey), HttpStatus.OK);
    }
}
