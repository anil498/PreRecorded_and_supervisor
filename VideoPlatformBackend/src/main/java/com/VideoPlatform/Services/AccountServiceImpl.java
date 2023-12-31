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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    @Value("${file.path:-}")
    private String FILE_DIRECTORY;
    @Value("${defaultImgName:default.png}")
    private String imgName;
    @Value("${defaultPath:classpath:default/default.png}")
    private String defaultExtractPath;
    @Autowired
    ResourceLoader resourceLoader;

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
    public ResponseEntity<?> accountCreation(String params1, String authKey, String token) throws JsonProcessingException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String loginId = params.get("loginId").getAsString();
        if(userRepository.findByLoginId(loginId) != null){
            logger.info("Login Id {} already exist !",loginId);
            return new ResponseEntity<>(commonService.responseData("400","Login Id already exist!"), HttpStatus.CONFLICT);
        }
        String accountName = params.get("name").getAsString();
        if(accountRepository.findByAccountName(accountName) != null){
            logger.info("Account name {} already exist !",accountName);
            return new ResponseEntity<>(commonService.responseData("400","Account Name already exist!"), HttpStatus.CONFLICT);
        }
        Date creation = TimeUtils.getDate();
        AccountEntity acc = new AccountEntity();
        UserEntity user = new UserEntity();
        acc.setName(params.get("name").getAsString());
        acc.setAddress(params.get("address").getAsString());
        acc.setCreationDate(creation);
        acc.setMaxUser(params.get("maxUser").getAsInt());
        acc.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
        acc.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
        acc.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
        acc.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));
        Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
        acc.setExpDate(expDate);

        UserAuthEntity u = userAuthRepository.findByToken(token);
        user.setFname(params.get("fname").getAsString());
        user.setLname(params.get("lname").getAsString());
        user.setLoginId(params.get("loginId").getAsString());
        String pass = params.get("password").getAsString();
        String myPass = passwordEncoder.encode(pass);
        user.setPassword(myPass);
        user.setContact(params.get("contact").getAsString());
        user.setEmail(params.get("email").getAsString());
        user.setCreationDate(creation);
        user.setParentId(u.getUserId());
        user.setExpDate(expDate);
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
        try{
            if(params.get("logo").isJsonNull() || params.get("logo").toString().equals("") || params.get("logo").toString()==null){
                Path defaultPath = Paths.get(FILE_DIRECTORY+"media/default/image");

                if (!Files.exists(defaultPath)) {
                    Files.createDirectories(defaultPath);
                    String newPath = defaultPath+"/"+imgName;
                    logger.info("Default new path : {}",newPath);
                    Resource resource = resourceLoader.getResource(defaultExtractPath);
                    logger.info("URL: {}",resource.getURL());
                    InputStream inputStream = resource.getInputStream();
                    byte[] dataAsBytes = FileCopyUtils.copyToByteArray(inputStream);
                    Map<String, Object> logo = commonService.getDefaultImageToStore(dataAsBytes,imgName);
                    logger.info("Logo Img Byte : {}",logo.get("byte"));
                    logger.info("Logo Img Name : {}",imgName);
                    commonService.decodeToImage(logo.get("byte").toString(), newPath);
                    accountRepository.updateLogoPath(acc.getAccountId(), String.valueOf(newPath));
                    userRepository.updateLogoPath(user.getUserId(), String.valueOf(newPath));
                }
                else {
                    accountRepository.updateLogoPath(acc.getAccountId(), String.valueOf(defaultPath+"/"+imgName));
                    userRepository.updateLogoPath(user.getUserId(), String.valueOf(defaultPath+"/"+imgName));
                }


            }else {
                HashMap<String, Object> logoA = commonService.getMapOfLogo(params.get("logo").toString());
                HashMap<String, Object> logoU = commonService.getMapOfLogo(params.get("logo").toString());
                Path pathU = Paths.get(FILE_DIRECTORY + "media/" + acc.getAccountId() + "/" + user.getUserId() + "/image");
                Path pathA = Paths.get(FILE_DIRECTORY + "media/" + acc.getAccountId() + "/image");
                if (!Files.exists(pathU)) {
                    Files.createDirectories(pathU);
                }
                if (!Files.exists(pathA)) {
                    Files.createDirectories(pathA);
                }
                String newPathA = pathA.toString() + "/" + logoA.get("name");
                String newPathU = pathU.toString() + "/" + logoU.get("name");
                logger.info("Account Path : {} , User Path : {}", newPathA, newPathU);
                commonService.decodeToImage(logoA.get("byte").toString(), newPathA);
                commonService.decodeToImage(logoU.get("byte").toString(), newPathU);
                userRepository.updateLogoPath(user.getUserId(), newPathU);
                accountRepository.updateLogoPath(acc.getAccountId(), newPathA);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(commonService.responseData("200","Account created!"),HttpStatus.OK);
    }
    @Override
    public AccountEntity createAccount(AccountEntity account) {

        return accountRepository.save(account);
    }

    @Override
    public ResponseEntity<?> updateAccount(String params1) {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AccountEntity existing = accountRepository.findByAccountId(params.get("accountId").getAsInt());

        if(existing==null){
            return new ResponseEntity<>(commonService.responseData("400","Invalid or missing value!"),HttpStatus.BAD_REQUEST);
        }
        logger.info("Old Data : {}",existing);
        logger.info("New Data : {}",params);
        commonService.compareAndChange(params,existing,params.get("accountId").getAsInt());

        try {
            if(params.get("logo").isJsonNull() ||  params.get("logo").toString()==null){
                logger.info("Entered Account logo update!");
                Path defaultPath = Paths.get(FILE_DIRECTORY+"media/default/image");
                if (!Files.exists(defaultPath)) {
                    logger.info("Dir doesn't exist..creating...");
                    Files.createDirectories(defaultPath);
                    String newPath = defaultPath+"/"+imgName;
                    logger.info("Default new path : {}",newPath);
                    Resource resource = resourceLoader.getResource(defaultExtractPath);
                    logger.info("URL: {}",resource.getURL());
                    InputStream inputStream = resource.getInputStream();
                    byte[] dataAsBytes = FileCopyUtils.copyToByteArray(inputStream);
                    Map<String, Object> logo = commonService.getDefaultImageToStore(dataAsBytes,imgName);
                    logger.info("Logo Img Byte : {}",logo.get("byte"));
                    logger.info("Logo Img Name : {}",imgName);
                    commonService.decodeToImage(logo.get("byte").toString(), newPath);
                    existing.setLogo(newPath);
//                    accountRepository.updateLogoPath(existing.getAccountId(), String.valueOf(newPath));
                }
                else {
                    logger.info("Dir exist, updating path..");
//                    accountRepository.updateLogoPath(existing.getAccountId(), String.valueOf(defaultPath+"/"+imgName));
                    existing.setLogo(String.valueOf(defaultPath+"/"+imgName));
                    commonService.changeUserLogo(existing.getAccountId(),"",String.valueOf(defaultPath+"/"+imgName));
                }
            }
            else{
                String oldPath = existing.getLogo();
                HashMap<String, Object> logo = commonService.getMapOfLogo(params.get("logo").toString());
                Path path = Paths.get(FILE_DIRECTORY +"/media/"+existing.getAccountId()+"/image");
                logger.info(path.toString());
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String newPath = path.toString()+"/"+logo.get("name");
                logger.info("New Path : "+newPath);
                commonService.decodeToImage(logo.get("byte").toString(),newPath);
                existing.setLogo(newPath);
                commonService.changeUserLogo(existing.getAccountId(), oldPath, newPath);
            }
            if(!params.get("session").isJsonNull())
                existing.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
            if(!params.get("featuresMeta").isJsonNull())
                existing.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
            if(!params.get("accessId").isJsonNull())
                existing.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            if(!params.get("features").isJsonNull())
                existing.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));
            if(!params.get("expDate").isJsonNull()){
                Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
                existing.setExpDate(expDate);
            }
            if(!params.get("maxUser").isJsonNull())
                existing.setMaxUser(params.get("maxUser").getAsInt());
            if(!params.get("name").isJsonNull())
                existing.setName(params.get("name").getAsString());
            if(!params.get("address").isJsonNull())
                existing.setAddress(params.get("address").getAsString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        accountRepository.save(existing);
        logger.info("New Entity {}",existing);
        return new ResponseEntity<>(commonService.responseData("200","Account updated!"),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteAccount(Integer accountId) {
        accountRepository.deleteAccount(accountId);
        accountAuthRepository.deleteById(accountId);
//        userRepository.deleteUserOfAccount(accountId);
//        List<Integer> userIdList = userRepository.findDeletedUser();
//        for(Integer deletedUser : userIdList){
//            UserAuthEntity userAuthEntity = userAuthRepository.findByUId(deletedUser);
//            if(userAuthEntity!=null)
//                userAuthRepository.deleteById(deletedUser);
//        }
        return new ResponseEntity<>(commonService.responseData("200","Account Deleted!"),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> checkAccountName(String accountName){
        if(accountRepository.findByAccountName(accountName) == null){
            return new ResponseEntity<>(commonService.responseData("200","Valid Name value!"),HttpStatus.OK);
        }
        return new ResponseEntity<>(commonService.responseData("409","Name already exist!"),HttpStatus.CONFLICT);
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
