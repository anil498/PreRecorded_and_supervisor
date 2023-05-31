package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.SessionEntity;
import com.VideoPlatform.Repository.SessionRepository;
import com.VideoPlatform.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionServiceImpl implements SessionService{

    private static final Logger logger= LoggerFactory.getLogger(SessionService.class);

    @Autowired
    UserRepository userRepository;
    @Autowired
    SessionRepository sessionRepository;

    @Override
    public SessionEntity createSession(SessionEntity session) {
        logger.info("User details {}",session.toString());
        return sessionRepository.save(session);
    }

    @Override
    public List<SessionEntity> getAllSessions() {
        return sessionRepository.findAll();
    }
}
