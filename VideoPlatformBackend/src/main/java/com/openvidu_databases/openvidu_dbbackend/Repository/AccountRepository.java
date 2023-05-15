package com.openvidu_databases.openvidu_dbbackend.Repository;

import com.openvidu_databases.openvidu_dbbackend.Entity.AccountEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserAuthEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

    @Query(nativeQuery=true, value = "select * from account_data")
    List<Object> findAlll();

    @Query(nativeQuery = true,value = "SELECT * FROM account_data WHERE account_id=:accountId")
    List<AccountEntity> findById(@Param("accountId") int accountId);
}
