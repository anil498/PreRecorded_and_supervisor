package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity,String> {
    @Query(nativeQuery = true,value = "select * from sessions where session_key=:sessionKey")
    SessionEntity findBySessionKey(@Param("sessionKey") String sessionKey);
    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE sessions SET status = 2 where session_key = :sessionKey ")
    void deleteSession(@Param("sessionKey") String sessionKey);
}
