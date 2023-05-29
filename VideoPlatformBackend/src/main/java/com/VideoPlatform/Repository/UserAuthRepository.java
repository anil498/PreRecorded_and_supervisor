package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.UserAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Integer> {

    @Query(nativeQuery = true,value = "SELECT * FROM user_auth WHERE user_id=:userId")
    UserAuthEntity findByUId(@Param("userId") Integer userId);

    @Query(nativeQuery = true,value = "SELECT * FROM user_auth WHERE auth_id=:authId")
    UserAuthEntity findByAuthId(@Param("authId") Integer authId);

    @Query(nativeQuery = true,value = "SELECT * FROM user_auth WHERE token=:token")
    UserAuthEntity findByToken(@Param("token") String token);
}
