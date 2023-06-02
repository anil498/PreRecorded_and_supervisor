package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.SessionEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Entity.UserEntity;

import java.util.List;
import java.util.Map;

public interface SessionService {
    SessionEntity createSession(SessionEntity sess,String authKey,String token);
    List<SessionEntity> getAllSessions();
//    Map<String,Object> getByKey(String key, UserAuthEntity user);
}
