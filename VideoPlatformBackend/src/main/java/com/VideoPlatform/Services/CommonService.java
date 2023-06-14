package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountAuthEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Repository.AccessRepository;
import com.VideoPlatform.Repository.AccountAuthRepository;
import com.VideoPlatform.Repository.UserAuthRepository;
import com.VideoPlatform.Repository.UserRepository;
import com.VideoPlatform.Utils.TimeUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class CommonService {

    private static final Logger logger= LoggerFactory.getLogger(SessionService.class);

    @Autowired
    AccountAuthRepository accountAuthRepository;
    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccessRepository accessRepository;

    public int isValidAuthKey(String authKey){
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        if(acc == null) return 0;
        if(TimeUtils.isExpire(acc.getExpDate())){
            return 0;
        }
        int authId = acc.getAuthId();
        return authId;
    }
    public int isValidAuthKeyU(String authKey){
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        if(acc == null) return 0;
        String key = (acc.getAuthKey());
        if(TimeUtils.isExpire(acc.getExpDate())){
            return 0;
        }
        int accountId = acc.getAccountId();
        return accountId;
    }
    public Boolean isValidTokenAndAccess(String token,Integer authId,String systemName){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        if(user == null)return false;

        int userId = user.getUserId();
        String systemNames = user.getSystemNames();
        logger.info("SystemNames : {}",systemNames);

        logger.info("authId : {}",authId);
        logger.info("authId1 : {}",user.getAuthId());
        if(TimeUtils.isExpire(user.getExpDate()) || !(authId == user.getAuthId()))
            return false;
        if(!systemNames.contains(systemName)){
            logger.info("Permission Denied. Don't have access for this service!");
            return false;
        }
        return true;
    }

    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        return generatedString;
    }
    public String generateToken(Integer userId,String type) {
        String val = String.format("%03d", userId);
        return type+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
    }
    public String generatedKey(int accountId){
        String val = String.format("%03d", accountId);
        String key = "AC"+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
        return key;
    }
    public Boolean authorizationCheck(String authKey, String token, String systemName){
        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return false;
        }
        if(!isValidTokenAndAccess(token,authId,systemName)) {
            logger.info("Invalid Token !");
            return false;
        }
        return true;
    }
    public Map<String, String> getHeaders(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        Map<String, String> headerMap = new HashMap<>();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            headerMap.put(header, request.getHeader(header));
        }
        return headerMap;
    }

}
