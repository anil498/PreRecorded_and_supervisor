package com.VideoPlatform.Services;

import com.VideoPlatform.Controller.SessionController;
import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardService {
    private static final Logger logger= LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private CommonService commonService;

    public Object dashboardData(String loginId,String token){
        UserEntity userEntity = userRepository.findByLoginId(loginId);
        if (userEntity == null) return null;
        Integer[] accessId = userEntity.getAccessId();
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> dashData = new HashMap<>();
        if(commonService.isAccessAllowed(token,"customer_management")){
            dashData.put("accounts",accountDetails());
        }
        if(commonService.isAccessAllowed(token,"my_users")){
            dashData.put("users", userDetails());
        }
        if(commonService.isAccessAllowed(token,"my_sessions")){
            dashData.put("sessions", sessionDetails());
        }
//        if(checkAccess("customer_management",accessId)){
//            dashData.put("accounts",accountDetails());
//        }
//        if(checkAccess("my_users",accessId)) {
//            dashData.put("users", userDetails());
//        }
//        if(checkAccess("my_sessions",accessId)) {
//            dashData.put("sessions", sessionDetails());
//        }
        dashData.put("participants",sessionRepository.participants());
        return objectMapper.convertValue(dashData, JsonNode.class);
    }
    public Object accountDetails(){
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> accInfo = new HashMap<>();
        accInfo.put("isDisplay",true);
        accInfo.put("totalAccounts",accountRepository.totalAccounts());
        accInfo.put("activeAccounts",accountRepository.activeAccounts());

        YearMonth yearMonth = YearMonth.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        accInfo.put("dailyAccountCreation",setDateCount(accountRepository.dailyAccountCreation(startDate,endDate)));
        accInfo.put("monthlyAccountCreation",setDateCount(accountRepository.monthlyAccountCreation()));
        accInfo.put("yearlyAccountCreation",setDateCount(accountRepository.yearlyAccountCreation()));
        return objectMapper.convertValue(accInfo, JsonNode.class);
    }
    public Object userDetails(){
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> userInfo = new HashMap<>();
        userInfo.put("isDisplay",true);
        userInfo.put("totalUsers",userRepository.totalUsers());
        userInfo.put("activeUsers",userRepository.activeUsers());

        YearMonth yearMonth = YearMonth.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        userInfo.put("dailyUserCreation",setDateCount(userRepository.dailyUserCreation(startDate,endDate)));
        userInfo.put("monthlyUserCreation",setDateCount(userRepository.monthlyUserCreation()));
        userInfo.put("yearlyUserCreation",setDateCount(userRepository.yearlyUserCreation()));
        return objectMapper.convertValue(userInfo,JsonNode.class);
    }
    public Object sessionDetails(){
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> sessionInfo = new HashMap<>();
        Integer totalSessions = sessionRepository.totalSessions();
        Integer activeSessions = sessionRepository.activeSessions();
        sessionInfo.put("isDisplay",true);
        sessionInfo.put("totalSessions",totalSessions);
        sessionInfo.put("activeSessions",activeSessions);
        sessionInfo.put("expiredSessions",totalSessions-activeSessions);

        YearMonth yearMonth = YearMonth.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        sessionInfo.put("dailySessionCreation",setDateCount(sessionRepository.dailySessionCreation(startDate,endDate)));
        sessionInfo.put("monthlySessionCreation",setDateCount(sessionRepository.monthlySessionCreation()));
        sessionInfo.put("yearlySessionCreation",setDateCount(sessionRepository.yearlySessionCreation()));
        return objectMapper.convertValue(sessionInfo,JsonNode.class);
    }

    public boolean checkAccess(String systemName, Integer[] accessId){

        List<String> accessEntities = new ArrayList<>();
        for (int i = 0; i < accessId.length; i++) {
            AccessEntity accessEntity = accessRepository.findByAccessIds(accessId[i]);
            accessEntities.add(accessEntity.getSystemName());
        }
        if(accessEntities.contains(systemName))
            return true;
        return false;
    }

    public Map<String,String> setDateCount(List<Object[]> list){
        Map<String,String> map = new HashMap<>();
        for(Object[] obj : list){
            String key = String.valueOf(obj[0]);
            String value = String.valueOf(obj[1]);
            map.put(key,value);
        }
        return map;
    }
}

