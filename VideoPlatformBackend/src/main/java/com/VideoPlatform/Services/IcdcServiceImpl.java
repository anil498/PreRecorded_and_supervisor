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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IcdcServiceImpl implements IcdcService{

    private static final Logger logger= LoggerFactory.getLogger(IcdcServiceImpl.class);

    @Autowired
    private CommonService commonService;
    @Autowired
    private IcdcRepository icdcRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private IcdcResponseRepository icdcResponseRepository;
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public IcdcEntity createIcdc(IcdcEntity icdcEntity, String authKey, String token){
        AccountAuthEntity accountAuthEntity = accountAuthRepository.findByAuthKey(authKey);
        UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
        icdcEntity.setAccountId(accountAuthEntity.getAccountId());
        Date creation = TimeUtils.getDate();
        icdcEntity.setCreationDate(creation);
        icdcEntity.setUserId(userAuthEntity.getUserId());
        return icdcRepository.save(icdcEntity);
    }
    @Override
    public IcdcResponseEntity saveIcdcResponse(IcdcResponseEntity icdcResponseEntity){
        if(icdcResponseEntity==null) {
            logger.info("ICDC entity null");
            return null;
        }
        Date creation = TimeUtils.getDate();
        icdcResponseEntity.setCreationDate(creation);
        String sessionId = icdcResponseEntity.getSessionId();
        SessionEntity sessionEntity = sessionRepository.findBySessionId(sessionId,"Customer");
        if(sessionEntity==null) {
            logger.info("Session entity null");
            return null;}
        icdcResponseEntity.setAccountId(sessionEntity.getAccountId());
        icdcResponseEntity.setUserId(sessionEntity.getUserId());
        return icdcResponseRepository.save(icdcResponseEntity);
    }
    @Override
    public List<IcdcEntity> getAllIcdc() {
        return icdcRepository.findAll();
    }
    @Override
    public List<IcdcEntity> getAllUserIcdc(String token) {
        UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
        return icdcRepository.findAllByUserId(userAuthEntity.getUserId());
    }
    @Override
    public ResponseEntity<?> updateIcdc(String params1) throws JsonProcessingException {
        logger.info("Params Update : {}", params1);
        Gson gson = new Gson();
        JsonObject params = gson.fromJson(params1, JsonObject.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        IcdcEntity icdcEntity = icdcRepository.findByIcdcId(params.get("icdcId").getAsInt());
        icdcEntity.setFormName(params.get("formName").getAsString());
        icdcEntity.setIcdcData(objectMapper.readValue(params.get("icdcData").toString(),List.class));
        icdcRepository.save(icdcEntity);
        return new ResponseEntity<>(commonService.responseData("200","ICDC Updated!"),HttpStatus.OK);
    }
    @Override
    public String deleteIcdc(Integer icdcId) {
        icdcRepository.deleteIcdc(icdcId);
        return "ICDC Deleted!";
    }
//    @Override
//    public List<Map<String,Object>> getNames(Map<String,Object> params){
//        List<Map<String, Object>> list = new ArrayList<>();
//        if(params.containsKey("userId") && params.containsKey("accountId")) {
//            Integer userId = (Integer) params.get("userId");
//            Integer accountId = (Integer) params.get("accountId");
//            list = icdcRepository.findNamesByUserId(userId);
//            if (list.isEmpty() || list == null) {
//                list = icdcRepository.findNamesByAccountId(accountId);
//                if (list.isEmpty() || list == null) {
//                    logger.info("Both user and account doesn't contain any form");
//                }
//            }
//        }
//        else {
//            Integer accountId = (Integer) params.get("accountId");
//            list = icdcRepository.findNamesByAccountId(accountId);
//            if (list.isEmpty() || list == null) {
//                logger.info("Account doesn't contain any form");
//            }
//        }
//        return list;
//    }
}
