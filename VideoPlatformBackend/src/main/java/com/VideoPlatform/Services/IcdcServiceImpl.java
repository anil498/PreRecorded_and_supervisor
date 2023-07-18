package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.IcdcResponseEntity;
import com.VideoPlatform.Entity.SessionEntity;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
}
