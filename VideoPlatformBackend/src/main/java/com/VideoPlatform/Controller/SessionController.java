package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.SessionService;
import com.VideoPlatform.Utils.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
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
    public ResponseEntity<?> createSession(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

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
        if (!(byAccess(4000, token))) {
            logger.info("for 4000 : " + byAccess(4000, token));
            logger.info("Permission Denied. Don't have access for this service!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        sessionService.createSession(authKey,token,null);
        sessionService.createSession(authKey,token,"Support");
        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "Session created!");
        return ok(result);
    }

    @GetMapping("/GetAll")
    public ResponseEntity<List<SessionEntity>> getAllSessions(HttpServletRequest request) throws JsonProcessingException {

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
        if(!(byAccess(4000,token))){
            logger.info("for 4000 : "+byAccess(4000,token));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<SessionEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ok(sessionService.getAllSessions());
    }

    @GetMapping("/GetByKey/{key}")
    public ResponseEntity<?> getSessionByKey(@PathVariable String key, HttpServletRequest request) throws JsonProcessingException {

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
        if(!(byAccess(4000,token))){
            logger.info("for 1001 : "+byAccess(4000,token));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
        }
        UserAuthEntity user=userAuthRepository.findByToken(token);
        Map<String,Object> s = sessionService.getByKey(key,user);
        if(s == null){
            return  new ResponseEntity<SessionEntity>(HttpStatus.UNAUTHORIZED);
        }
        return ok(s);
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
//    public Boolean isValidSessionKey(String sessionKey){
//        SessionEntity session = sessionRepository.findBySessionKey(sessionKey);
//        SessionEntity sess = sessionRepository.findBySessionSupportKey(sessionKey);
//        if(session == null && sess == null) return false;
//        if(sess == null) {
//            if (session.getExpDate().isBefore(LocalDateTime.now()))
//                return false;
//        }
//        else if(session == null) {
//            if (sess.getExpDate().isBefore(LocalDateTime.now()))
//                return false;
//        }
//        return true;
//    }
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



}
