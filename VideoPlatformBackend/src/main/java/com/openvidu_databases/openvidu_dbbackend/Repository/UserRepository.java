package com.openvidu_databases.openvidu_dbbackend.Repository;

import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query(nativeQuery=true, value = "select * from user_data")
    List<UserEntity> findAll();

    @Query(nativeQuery=true, value = "select * from user_data where login_id = :loginId ")
    List<UserEntity> findById(@Param("loginId") String loginId);

    @Query(nativeQuery=true, value = "select * from user_data where login_id = :loginId ")
    UserEntity findByUserId(@Param("loginId") String loginId);

    @Query(nativeQuery=true, value = "select * from user_data where account_id = :accountId ")
    List<UserEntity> findAllChild(@Param("accountId") String accountId);

    @Query(nativeQuery=true, value = "select features from user_data where login_id = :loginId ")
    Integer[] findFeatures(@Param("loginId") String accountId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user_data set last_login = CURRENT_TIMESTAMP where login_id = :loginId")
    void setLastLogin(@Param("loginId") String userId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "delete from user_data where login_id = :loginId ")
    void deleteById(@Param("loginId") String loginId);

}
