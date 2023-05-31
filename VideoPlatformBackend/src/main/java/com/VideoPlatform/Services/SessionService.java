package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.SessionEntity;

import java.util.List;

public interface SessionService {
    SessionEntity createSession(SessionEntity session);
    List<SessionEntity> getAllSessions();
}
