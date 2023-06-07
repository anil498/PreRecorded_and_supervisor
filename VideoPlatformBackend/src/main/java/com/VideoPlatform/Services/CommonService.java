package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccessEntity;
import com.VideoPlatform.Entity.AccountAuthEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.AccessRepository;
import com.VideoPlatform.Repository.AccountAuthRepository;
import com.VideoPlatform.Repository.UserAuthRepository;
import com.VideoPlatform.Repository.UserRepository;
import com.VideoPlatform.Utils.TimeUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class CommonService {
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
        String key = (acc.getAuthKey());
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
    public Boolean isValidToken(String token,Integer authId){
        UserAuthEntity user = userAuthRepository.findByToken(token);
        if(user == null)return false;
        if(TimeUtils.isExpire(user.getExpDate()) && authId == user.getAuthId())
            return false;
        return true;
    }
    public boolean checkAccess(String systemName,String token){
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
