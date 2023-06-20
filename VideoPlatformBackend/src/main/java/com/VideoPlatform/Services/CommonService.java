package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountAuthEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Entity.UserEntity;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@Service
public class CommonService {

    private static final Logger logger= LoggerFactory.getLogger(SessionService.class);
//    String FILEPATH = "D:\\TestVideoWrite\\";
//    File file = new File(FILEPATH+"abc.png");

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
    public Boolean isValidRequest(String token,String authKey,String systemName){
        UserAuthEntity user = userAuthRepository.findByTokenAndAuthKey(token,authKey);
        if(user == null)return false;
        String systemNames = user.getSystemNames();
        logger.info("SystemNames : {}",systemNames);

        if(TimeUtils.isExpire(user.getExpDate()))
            return false;
        if(!systemNames.contains(systemName)){
            logger.info("Permission Denied. Don't have access for this service!");
            return false;
        }
        return true;
    }
    public Boolean isAccessAllowed(String token,String systemName){
        UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
        String systemNames = userAuthEntity.getSystemNames();
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

        if(!isValidRequest(token,authKey,systemName)) {
            logger.info("Unauthorised user, wrong authorization key !");
            logger.info("Invalid Token !");
            return false;
        }
        return true;
    }

//    public String writeByteToFile(String loginId){
//        try{
//            logger.info("loginId : {}",loginId);
//
//            UserEntity userEntity = userRepository.findByLoginId(loginId);
//            logger.info("UserEntity : {}",userEntity);
//            logger.info("UserEntity userId : {}",userEntity.getUserId());
//            logger.info("UserEntity logo : {}",userEntity.getLogo());
//            Object logoByte = userEntity.getLogo().get("byte");
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            ObjectOutputStream obj;
//            try {
//                obj = new ObjectOutputStream(output);
//                obj.writeObject(logoByte);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            byte[] bytes = output.toByteArray();
//            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
//            BufferedImage bImage2 = ImageIO.read(bis);
//            ImageIO.write(bImage2, "jpg", new File("output.jpg") );
////            logger.info("logoByte : {}", bytes);
////            try {
////                OutputStream os = new FileOutputStream(file);
////                os.write(bytes);
////                logger.info("Bytes written successfully !");
////                os.close();
////            }
////            catch (Exception e) {
////                logger.info("Exception: " + e);
////            }
//        }
//        catch (Exception e){
//            logger.info("Exception: {} ",e);
//        }
//        return "Written !!!";
//    }
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
