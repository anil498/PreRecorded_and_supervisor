package com.openvidu_databases.openvidu_dbbackend.Repository;

import com.openvidu_databases.openvidu_dbbackend.Entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity,Integer> {
    @Query(nativeQuery = true,value = "select * from sessions where session_key=:sessionKey")
    SessionEntity findBySessionKey(@Param("sessionKey") String sessionKey);
    @Query(nativeQuery = true,value = "select * from sessions where session_support_key=:sessionKey")
    SessionEntity findBySessionSupportKey(@Param("sessionKey") String sessionKey);

}
