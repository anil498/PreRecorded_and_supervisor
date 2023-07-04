package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

    @Query(nativeQuery = true,value = "SELECT SUM(total_participants) FROM sessions")
    Integer participants();

    @Query(nativeQuery = true,value = "SELECT EXTRACT('day' FROM d.day) AS day,COUNT(a.creation_date) AS count FROM (SELECT generate_series(:startDate,:endDate, interval '1 day') AS day) AS d LEFT JOIN sessions a ON DATE_TRUNC('day', a.creation_date\\:\\:date) = d.day GROUP BY d.day ORDER BY d.day;")
    List<Object[]> dailySessionCreation(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(nativeQuery = true, value = "SELECT EXTRACT('month' FROM m.month) AS month,COUNT(a.creation_date) AS count FROM (SELECT generate_series(DATE '2023-01-01',DATE '2023-12-01', interval '1 month') AS month) AS m LEFT JOIN sessions a ON DATE_TRUNC('month', a.creation_date\\:\\:date) = m.month GROUP BY m.month ORDER BY m.month;")
    List<Object[]> monthlySessionCreation();

    @Query(nativeQuery = true, value = "SELECT EXTRACT('year' FROM y.year) AS year,COUNT(a.creation_date) AS count FROM (SELECT generate_series(DATE '2023-01-01',DATE '2025-12-01', interval '1 year') AS year) AS y LEFT JOIN sessions a ON DATE_TRUNC('year', a.creation_date\\:\\:date) = y.year GROUP BY y.year ORDER BY y.year;")
    List<Object[]> yearlySessionCreation();

    @Query(nativeQuery = true,value = "select * from sessions where session_id=:sessionId limit 1")
    SessionEntity findBySessionId(@Param("sessionId") String sessionId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE sessions SET hold = :hold where session_key = :sessionKey")
    void updateHold(@Param("sessionKey") String sessionKey,@Param("hold") Boolean hold);

}
