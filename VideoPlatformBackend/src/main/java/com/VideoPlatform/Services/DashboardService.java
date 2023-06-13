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

import java.util.HashMap;

@Service
public class DashboardService {
    private static final Logger logger= LoggerFactory.getLogger(SessionController.class);

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

    public Object dashboardData(){
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> dashData = new HashMap<>();
        dashData.put("accounts",accountDetails());
        dashData.put("users",userDetails());
        dashData.put("sessions",sessionDetails());
        return objectMapper.convertValue(dashData, JsonNode.class);
    }
    public Object accountDetails(){
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> accInfo = new HashMap<>();
        accInfo.put("isDisplay",true);
        accInfo.put("totalAccounts",accountRepository.totalAccounts());
        accInfo.put("activeAccounts",accountRepository.activeAccounts());
        logger.info("dailyAccountCreation {} ",accountRepository.dailyAccountCreation());
//        accInfo.put("dailyAccountCreation",accountRepository.dailyAccountCreation());
//        accInfo.put("monthlyAccountCreation",accountRepository.monthlyAccountCreation());
//        accInfo.put("yearlyAccountCreation",accountRepository.yearlyAccountCreation());
        return objectMapper.convertValue(accInfo, JsonNode.class);
    }
    public Object userDetails(){
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> userInfo = new HashMap<>();
        userInfo.put("isDisplay",true);
        userInfo.put("totalUsers",userRepository.totalUsers());
        userInfo.put("activeUsers",userRepository.activeUsers());
//        userInfo.put("dailyUserCreation",userRepository.dailyUserCreation());
//        userInfo.put("monthlyUserCreation",userRepository.monthlyUserCreation());
//        userInfo.put("yearlyUserCreation",userRepository.yearlyUserCreation());
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
//        sessionInfo.put("dailySessionCreation",sessionRepository.dailySessionCreation());
//        sessionInfo.put("monthlySessionCreation",sessionRepository.monthlySessionCreation());
//        sessionInfo.put("yearlySessionCreation",sessionRepository.yearlySessionCreation());
        return objectMapper.convertValue(sessionInfo,JsonNode.class);
    }
}

