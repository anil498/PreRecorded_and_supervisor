package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.UserEntity;
import com.google.gson.JsonObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query(nativeQuery=true, value = "select * from user_data")
    List<UserEntity> findAll();

    @Query(nativeQuery=true, value = "select * from user_data where login_id = :loginId ")
    UserEntity findByLoginId(@Param("loginId") String loginId);

    @Query(nativeQuery=true, value = "select * from user_data where user_id = :userId ")
    UserEntity findByUserId(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "select * from user_data where account_id = :accountId ")
    UserEntity findByAccountId(@Param("accountId") Integer accountId);

    @Query(nativeQuery=true, value = "select * from user_data where parent_id = :userId ")
    List<UserEntity> findAllChild(@Param("userId") int userId);

    @Query(nativeQuery=true, value = "select features from user_data where user_id = :userId ")
    Integer[] findFeatures(@Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_data SET last_login = :lastlogin WHERE user_id = :userId")
    void setLogin(@Param("lastlogin") String lastlogin, @Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE user_data SET sataus = 0 where user_id = :userId ")
    void deleteUser(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "select session from user_data where user_id = :userId ")
    JsonObject getSession(@Param("userId") Integer userId);


}
