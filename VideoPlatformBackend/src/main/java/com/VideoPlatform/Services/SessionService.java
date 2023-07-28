package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.SessionEntity;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface SessionService {
    SessionEntity createSession(String authKey,String token,Boolean moderator,String sessionId,String sessionKey,String description,String participantName,String type);
    List<SessionEntity> getAllSupportSessionsUser(String token);
    List<SessionEntity> getAllSupportSessions();
    SessionEntity getByKey(String key);
    String deleteSession(String sessionKey);
    ResponseEntity<?> sendLink(Map<String,?> params, HttpServletRequest request, HttpServletResponse response);

    void updateHold(Map<String, Object> params);
}
