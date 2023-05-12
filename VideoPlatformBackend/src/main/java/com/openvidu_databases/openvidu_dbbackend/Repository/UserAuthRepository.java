package com.openvidu_databases.openvidu_dbbackend.Repository;

import com.openvidu_databases.openvidu_dbbackend.Entity.UserAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Integer> {

    @Query(nativeQuery = true,value = "SELECT * FROM user_auth WHERE login_id=:loginId")
    UserAuthEntity findById(@Param("loginId") String loginId);
}
