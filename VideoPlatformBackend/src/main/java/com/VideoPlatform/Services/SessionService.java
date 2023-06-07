package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.SessionEntity;

import java.util.List;

public interface SessionService {
    SessionEntity createSession(String authKey,String token,Boolean moderator,String sessionId,String sessionKey,String description,String participantName);
    List<SessionEntity> getAllSessions();
    SessionEntity getByKey(String key);
}
