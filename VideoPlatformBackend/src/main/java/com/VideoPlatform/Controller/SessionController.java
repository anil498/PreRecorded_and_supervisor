package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.SessionService;
import com.VideoPlatform.Utils.CommonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
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

    @Value(("${call.access.time}"))
    private int callAccessTime;

    @PostMapping("/Create")
    public ResponseEntity<?> createSession(HttpServletRequest request, HttpServletResponse response) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int accountId = isValidAuthKey(authKey);
        if (accountId == 0) {
            logger.info("Unauthorised user, wrong authorization key !");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!isValidToken(token)) {
            logger.info("Invalid Token !");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!(checkAccess("my_sessions", token))) {
            logger.info("Permission Denied. Don't have access for this service!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SessionEntity sessionEntitySSuppot= sessionService.createSession(null,authKey,token,true);
        sessionService.createSession(sessionEntitySSuppot,authKey,token,false);
        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "Session created!");
        return ok(result);
    }

    @GetMapping("/GetAll")
    public ResponseEntity<List<SessionEntity>> getAllSessions(HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<SessionEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<SessionEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(checkAccess("session_details",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<SessionEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ok(sessionService.getAllSessions());
    }

//    @GetMapping("/GetByKey/{key}")
//    public ResponseEntity<?> getSessionByKey(@PathVariable String key, HttpServletRequest request) {
//
//        String authKey = request.getHeader("Authorization");
//        String token = request.getHeader("Token");
//
//        int authId = isValidAuthKey(authKey);
//        if(authId == 0){
//            logger.info("Unauthorised user, wrong authorization key !");
//            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
//        }
//        if(!isValidToken(token)) {
//            logger.info("Invalid Token !");
//            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
//        }
//        if(!(checkAccess("session_details",token))){
//            logger.info("Permission Denied. Don't have access for this service!");
//            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
//        }
//        UserAuthEntity user=userAuthRepository.findByToken(token);
//        Map<String,Object> s = sessionService.getByKey(key,user);
//        if(s == null){
//            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
//        }
//        return ok(s);
//    }

    @GetMapping("/GetByKey/{key}")
    public ResponseEntity<SessionEntity> getSessionByKey(@PathVariable String key, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(checkAccess("session_details",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
        }

            return  new ResponseEntity<SessionEntity>(sessionRepository.findBySessionKey(key),HttpStatus.OK);

    }

    public int isValidAuthKey(String authKey){
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        if(acc == null)return 0;
        String key = (acc.getAuthKey());
        if(CommonUtils.isExpire(acc.getExpDate())){
            return 0;
        }
        int authId = acc.getAuthId();
        return authId;
    }

    public Boolean isValidToken(String token){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        if(user == null)return false;
        logger.info("Data by authId.."+user);
        //logger.info(user.getToken());
        String t = (user.getToken());
        if(CommonUtils.isExpire(user.getExpDate()) || !(t.equals(token)))
            return false;
        return true;
    }
    private boolean checkAccess(String systemName,String token){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        int userId = user.getUserId();
        UserEntity u = userRepository.findByUserId(userId);
        Integer[] accessId = u.getAccessId();
        List<String> accessEntities = new ArrayList<>();
        for (int i = 0; i < accessId.length; i++) {
            AccessEntity accessEntity = accessRepository.findByAccessIds(accessId[i]);
            accessEntities.add(accessEntity.getSystemName());
        }
        if(accessEntities.contains(systemName))
            return true;
        return false;
    }
}
