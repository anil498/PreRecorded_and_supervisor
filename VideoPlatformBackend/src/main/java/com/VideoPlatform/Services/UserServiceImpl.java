package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;

import com.VideoPlatform.Utils.CommonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.VideoPlatform.Constant.AllConstants.DATE_FORMATTER;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private AccessRepository accessRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getAllChild(Integer id) {
         return userRepository.findAllChild(id);
    }

    @Override
    public UserEntity getUserById(Integer id) {
        return (UserEntity) userRepository.findByUserId(id);
    }

    @Override
    public UserEntity createUser(UserEntity user,String authKey,String token) {
        logger.info("User details {}",user.toString());
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        UserAuthEntity u = userAuthRepository.findByToken(token);
        Date creation = CommonUtils.getDate();
        user.setAccountId(acc.getAccountId());
        user.setCreationDate(creation);
        user.setParentId(u.getUserId());
        String myPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(myPass);

        return userRepository.save(user);
    }
    @Override
    public UserEntity createUserZero(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        UserEntity existing = (UserEntity) userRepository.findByUserId(user.getUserId());
        existing.setFname(user.getFname());
        existing.setLname(user.getLname());
        existing.setSession(user.getSession());
        existing.setFeatures(user.getFeatures());
        existing.setFeaturesMeta(user.getFeaturesMeta());
        logger.info("AccessId val : {}",user.getAccessId());
        existing.setAccessId(user.getAccessId());
        existing.setExpDate(user.getExpDate());
        existing.setContact(user.getContact());
        existing.setEmail(user.getEmail());

        return userRepository.save(existing);
    }

    @Override
    public String deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
        return "User successfully deleted.";
    }

    @Override
    public ResponseEntity<?> loginService(String loginId,String password,int authId){
        logger.info("loginId : "+loginId);
        logger.info("password : "+password);

        UserEntity user1 = userRepository.findByLoginId(loginId);

        if(user1 == null){
            logger.info("No user present with given login id !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("user "+user1);
        int userId = user1.getUserId();

        logger.info("userId "+userId);
        UserAuthEntity user = userAuthRepository.findByUId(userId);
        logger.info("userId "+userId);

        logger.info("user1 {}",user1);
        logger.info("user1.getLoginId() {}",user1.getLoginId());
        logger.info("user1.getPassword() {}",user1.getPassword());
        if (!(passwordEncoder.matches(password,user1.getPassword()))) {
            logger.info("Inside loginid password check !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        if(isValidTokenLogin(userId)){
            logger.info("Inside second if ...");
            ObjectMapper obj = new ObjectMapper();
            HashMap<String,Object> response=new HashMap<>();
            response.put("token",user.getToken());
            response.put("user_data",user1);
            response.put("status_code","200");
            response.put("status_message","Login Successful");
            response.put("Features", featureData(user1.getUserId()));
            response.put("Access", accessData(user1.getUserId()));
            String lastLogin = LocalDateTime.now().format(formatter);
            logger.info("LastLogin1 : {}",lastLogin);
            user1.setLastLogin(lastLogin);
            userRepository.setLogin(lastLogin,userId);
            return  ResponseEntity.ok(response);
        }
        else {

            if (user1 != null && passwordEncoder.matches(password,user1.getPassword())) {
                String token1 = generateToken(userId,"UR");
                Date now = CommonUtils.getDate();
                Date newDateTime = CommonUtils.increaseDateTime(now);
                UserAuthEntity ua = userAuthRepository.findByUId(userId);
                String lastLogin = LocalDateTime.now().format(formatter);
                logger.info("LastLogin2 : {}",lastLogin);
                user1.setLastLogin(lastLogin);
                userRepository.setLogin(lastLogin,userId);

                if(ua != null){
                    ua.setToken(token1);
                    ua.setAuthId(authId);
                    ua.setCreationDate(now);
                    ua.setExpDate(newDateTime);
                }
                else{
                    ua = new UserAuthEntity();
                    ua.setLoginId(user1.getLoginId());
                    ua.setUserId(user1.getUserId());
                    ua.setToken(token1);
                    ua.setAuthId(authId);
                    ua.setCreationDate(now);
                    ua.setExpDate(newDateTime);
                }

                userAuthRepository.save(ua);

                Map<String,Object> res = new HashMap<>();
                res.put("token",token1);
                res.put("user_data",user1);
                res.put("status_code","200");
                res.put("status_message","Login Successful");
                res.put("Features", featureData(user1.getUserId()));
                res.put("Access", accessData(user1.getUserId()));
                logger.info(user1.toString());
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }
        logger.info("Last login return invoked !");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
    public Boolean isValidTokenLogin(int id){

        UserAuthEntity user = userAuthRepository.findByUId(id);
        if(user == null || CommonUtils.isExpire(user.getExpDate()) ) {
            return false;
        }
        return true;
    }
    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        logger.info(generatedString);
        System.out.println(generatedString);
        return generatedString;
    }
    private String generateToken(Integer userId,String type) {
        String val = String.format("%03d", userId);
        logger.info("Format val = "+val);
        return type+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
    }

    private Object featureData(Integer userId) {
        UserEntity user = userRepository.findByUserId(userId);
        Integer[] featuresId = user.getFeatures();
        ObjectMapper objectMapper = new ObjectMapper();
        List<FeatureEntity> featureEntities = new ArrayList<>();

        for (int i = 0; i < featuresId.length; i++) {
            FeatureEntity featureEntity = featureRepository.findByFeatureId(featuresId[i]);
            logger.info("Feature Entity : {}",featureEntity);
            if(featureEntity.getStatus() == 1){
                featureEntities.add(featureEntity);
            }
        }
        return objectMapper.convertValue(featureEntities, JsonNode.class);
    }
    private Object accessData(Integer userId) {
        UserEntity user = userRepository.findByUserId(userId);
        Integer[] accessId = user.getAccessId();
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessEntity> accessEntities = new ArrayList<>();

        for (int i = 0; i < accessId.length; i++) {
            AccessEntity accessEntity = accessRepository.findByAccessIds(accessId[i]);
            accessEntities.add(accessEntity);
        }
        return objectMapper.convertValue(accessEntities, JsonNode.class);
    }
}

