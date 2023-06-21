package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.TimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountAuthRepository accountAuthRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CommonService commonService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    AccessRepository accessRepository;

    private static final Logger logger= LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    public List<AccountEntity> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<AccountEntity> getAccountById(int id) {
        return accountRepository.findById(id);
    }

    @Override
    public String accountCreation(String params1,String authKey,String token) throws JsonProcessingException {
        logger.info(params1);
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String loginId = params.get("loginId").getAsString();
        if(userRepository.findByLoginId(loginId) != null){
            logger.info("Login Id already exist !");
            return null;
        }
        String accountName = params.get("name").getAsString();
        if(accountRepository.findByAccountName(accountName) != null){
            logger.info("Account name already exist !");
            return null;
        }
        Date creation = TimeUtils.getDate();
        AccountEntity acc = new AccountEntity();
        UserEntity user = new UserEntity();
        logger.info(String.valueOf(params));
        acc.setName(params.get("name").getAsString());
        acc.setAddress(params.get("address").getAsString());
        acc.setCreationDate(creation);
        acc.setMaxUser(params.get("maxUser").getAsInt());
        acc.setLogo(objectMapper.readValue(params.get("logo").toString(),HashMap.class));
        acc.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
        acc.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
        acc.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
        acc.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));
        Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
        acc.setExpDate(expDate);

        UserAuthEntity u = userAuthRepository.findByToken(token);
        logger.info("User Data : "+u);
        user.setFname(params.get("fname").getAsString());
        user.setLname(params.get("lname").getAsString());
        user.setLoginId(params.get("loginId").getAsString());
        String pass = params.get("password").getAsString();
        logger.info("Password Check !!! {}",pass);
        String myPass = passwordEncoder.encode(pass);
        logger.info("Encoded Pass : {}",myPass);
        user.setPassword(myPass);
        user.setContact(params.get("contact").getAsString());
        user.setEmail(params.get("email").getAsString());
        user.setCreationDate(creation);
        user.setParentId(u.getUserId());
        user.setExpDate(expDate);
        user.setLogo(objectMapper.readValue(params.get("logo").toString(),HashMap.class));
        user.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
        user.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
        user.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
        user.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));

        createAccount(acc);
        user.setAccountId(acc.getAccountId());
        userService.createUserZero(user);

        AccountAuthEntity auth = accountAuthRepository.findById(acc.getAccountId());
        if(auth != null){
            auth.setAuthKey(commonService.generatedKey(acc.getAccountId()));
            auth.setCreationDate(creation);
            auth.setExpDate(expDate);
        }
        else{
            auth = new AccountAuthEntity();
            auth.setAccountId(acc.getAccountId());
            auth.setName(acc.getName());
            auth.setAuthKey(commonService.generatedKey(acc.getAccountId()));
            auth.setCreationDate(creation);
            auth.setExpDate(expDate);
        }

        accountAuthRepository.save(auth);

        int authId1 = auth.getAuthId();
        logger.info("acc.getMaxUser() : "+acc.getMaxUser());

        AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAccountId(user.getAccountId());
        if(acc.getMaxUser() == 0){
            logger.info("Checking userId : "+user.getUserId());
            String token1 = commonService.generateToken(user.getUserId(),"UR");
            UserAuthEntity ua = new UserAuthEntity();
            ua.setLoginId(user.getLoginId());
            ua.setUserId(user.getUserId());
            ua.setToken(token1);
            ua.setAuthId(authId1);
            ua.setCreationDate(creation);
            ua.setExpDate(expDate);
            ua.setSystemNames(accessCheck(user.getUserId()));
            ua.setAuthKey(accountAuthEntity.getAuthKey());
            userAuthRepository.save(ua);
        }
        return "Account created !";
    }
    @Override
    public AccountEntity createAccount(AccountEntity account) {

        return accountRepository.save(account);
    }

    @Override
    public AccountEntity updateAccount(String params1) {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AccountEntity existing = accountRepository.findByAccountId(params.get("accountId").getAsInt());
        try {
            existing.setLogo(objectMapper.readValue(params.get("logo").toString(), HashMap.class));
            existing.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
            existing.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
            existing.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            existing.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));
            Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
            existing.setExpDate(expDate);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        existing.setMaxUser(params.get("maxUser").getAsInt());
        existing.setName(params.get("name").getAsString());
        existing.setAddress(params.get("address").getAsString());
        logger.info("New Entity {}",existing);
        return accountRepository.save(existing);
    }
    @Override
    public String deleteAccount(Integer accountId) {
        accountRepository.deleteAccount(accountId);
        accountAuthRepository.deleteById(accountId);
        return "Account successfully deleted.";
    }
    public String accessCheck(Integer userId){
        UserEntity userEntity = userRepository.findByUserId(userId);
        Integer[] accessId = userEntity.getAccessId();
        List<String> accessEntities = new ArrayList<>();
        for (int i = 0; i < accessId.length; i++) {
            AccessEntity accessEntity = accessRepository.findByAccessIds(accessId[i]);
            accessEntities.add(accessEntity.getSystemName());
        }
        logger.info("system_name array is : {}",accessEntities);

        return String.valueOf(accessEntities);
    }
}
