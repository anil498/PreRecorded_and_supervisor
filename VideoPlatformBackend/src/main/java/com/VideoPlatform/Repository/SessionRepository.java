package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity,String> {
    @Query(nativeQuery = true,value = "select * from sessions where session_key=:sessionKey")
    SessionEntity findBySessionKey(@Param("sessionKey") String sessionKey);

    @Query(nativeQuery = true,value = "select * from sessions where user_id=:userId and type='Support'")
    List<SessionEntity> findSupportSessions(@Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE sessions SET status = 2 where session_key = :sessionKey ")
    void deleteSession(@Param("sessionKey") String sessionKey);

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM sessions")
    Integer totalSessions();

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM sessions where exp_date NOT BETWEEN creation_date AND NOW()")
    Integer activeSessions();

    @Query(nativeQuery = true,value = "select EXTRACT('day' from date_trunc('day',creation_date)) as day,count(*) from sessions group by date_trunc('day',creation_date)")
    List<Map<String,Object>> dailySessionCreation();

    @Query(nativeQuery = true,value = "select EXTRACT('month' from date_trunc('month',creation_date)) as month,count(*) from sessions group by date_trunc('month',creation_date)")
    List<Map<String,Object>> monthlySessionCreation();

    @Query(nativeQuery = true,value = " select EXTRACT('year' from date_trunc('year',creation_date)) as year,count(*) from sessions group by date_trunc('year',creation_date)")
    List<Map<String,Object>> yearlySessionCreation();
}
