package com.openvidu_databases.openvidu_dbbackend.Repository;

import com.openvidu_databases.openvidu_dbbackend.Entity.AccountAuthEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountAuthRepository extends JpaRepository<AccountAuthEntity, Integer> {

    @Query(nativeQuery = true,value = "SELECT * FROM acc_auth WHERE account_id=:accountId")
    AccountAuthEntity findById(@Param("accountId") int accountId);
}
