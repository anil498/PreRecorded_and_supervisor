package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.SessionEntity;

import java.util.List;

public interface SessionService {
    SessionEntity createSession(SessionEntity sessionEntity,String authKey,String token,Boolean moderator);
    List<SessionEntity> getAllSessions();
    SessionEntity getByKey(String key);
}
