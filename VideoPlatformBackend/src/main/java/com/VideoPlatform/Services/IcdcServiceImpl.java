package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class IcdcServiceImpl implements IcdcService{

    private static final Logger logger= LoggerFactory.getLogger(SessionService.class);

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
