package com.VideoPlatform.Services;

import com.VideoPlatform.Controller.UserController;
import com.VideoPlatform.Entity.SessionEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.SessionRepository;
import com.VideoPlatform.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SessionServiceImpl implements SessionService{
    private static final Logger logger= LoggerFactory.getLogger(SessionService.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    SessionRepository sessionRepository;

    @Override
    public SessionEntity createSession(SessionEntity session) {
        logger.info("User details {}",session.toString());
        // user.setUserPassword(user.getUserPassword());

        return sessionRepository.save(session);
    }
}
