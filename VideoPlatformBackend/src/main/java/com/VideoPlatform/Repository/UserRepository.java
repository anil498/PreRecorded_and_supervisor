package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.UserEntity;
import com.google.gson.JsonObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query(nativeQuery=true, value = "SELECT * from user_data")
    List<UserEntity> findAll();

    @Query(nativeQuery=true, value = "SELECT * from user_data where login_id = :loginId ")
    UserEntity findByLoginId(@Param("loginId") String loginId);

    @Query(nativeQuery=true, value = "SELECT * from user_data where user_id = :userId ")
    UserEntity findByUserId(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "SELECT * from user_data where account_id = :accountId ")
    UserEntity findByAccountId(@Param("accountId") Integer accountId);

    @Query(nativeQuery=true, value = "SELECT * from user_data where parent_id = :userId ")
    List<UserEntity> findAllChild(@Param("userId") int userId);

    @Query(nativeQuery=true, value = "select features from user_data where user_id = :userId ")
    Integer[] findFeatures(@Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_data SET last_login = :lastlogin WHERE user_id = :userId")
    void setLogin(@Param("lastlogin") String lastlogin, @Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE user_data SET status = 2 where user_id = :userId ")
    void deleteUser(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "SELECT session from user_data where user_id = :userId ")
    JsonObject getSession(@Param("userId") Integer userId);

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM user_data")
    Integer totalUsers();

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM user_data WHERE status=1")
    Integer activeUsers();

    @Query(nativeQuery = true,value = "SELECT EXTRACT('day' FROM d.day) AS day,COUNT(a.creation_date) AS count FROM (SELECT generate_series(:startDate,:endDate, interval '1 day') AS day) AS d LEFT JOIN user_data a ON DATE_TRUNC('day', a.creation_date\\:\\:date) = d.day GROUP BY d.day ORDER BY d.day;")
    List<Object[]> dailyUserCreation(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(nativeQuery = true, value = "SELECT EXTRACT('month' FROM m.month) AS month,COUNT(a.creation_date) AS count FROM (SELECT generate_series(DATE '2023-01-01',DATE '2023-12-01', interval '1 month') AS month) AS m LEFT JOIN user_data a ON DATE_TRUNC('month', a.creation_date\\:\\:date) = m.month GROUP BY m.month ORDER BY m.month;")
    List<Object[]> monthlyUserCreation();

    @Query(nativeQuery = true, value = "SELECT EXTRACT('year' FROM y.year) AS year,COUNT(a.creation_date) AS count FROM (SELECT generate_series(DATE '2023-01-01',DATE '2025-12-01', interval '1 year') AS year) AS y LEFT JOIN user_data a ON DATE_TRUNC('year', a.creation_date\\:\\:date) = y.year GROUP BY y.year ORDER BY y.year;")
    List<Object[]> yearlyUserCreation();

    @Query(nativeQuery = true, value = "SELECT COUNT(*) from user_data where account_id = :accountId")
    Integer usersAccount(@Param("accountId") Integer accountId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_data SET features = array_remove(features, :feature) WHERE account_id = :accountId")
    Integer deleteFeatureValues(@Param("feature") Integer feature,@Param("accountId") Integer accountId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_data SET access_id = array_remove(access_id, :access) WHERE account_id = :accountId")
    Integer deleteAccessValues(@Param("access") Integer access,@Param("accountId") Integer accountId);

    @Query(nativeQuery = true, value = "SELECT * from user_data where account_id=:accountId")
    List<UserEntity> findUsersByAccountId(@Param("accountId") Integer accountId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_data SET session = :session WHERE user_id = :userId")
    void updateSessionValue(@Param("session") HashMap<String,Object> session,@Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_data SET features_meta = :featuresMeta WHERE login_id = :loginId")
    void updateFeaturesMeta(@Param("loginId") String loginId,@Param("featuresMeta") String featuresMeta);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_data SET session = :session WHERE login_id = :loginId")
    void updateSession(@Param("loginId") String loginId,@Param("session") String session);

}
