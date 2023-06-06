package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

    @Query(nativeQuery=true, value = "select * from account_data")
    List<Object> findAlll();

    @Query(nativeQuery = true,value = "SELECT * FROM account_data WHERE account_id=:accountId")
    List<AccountEntity> findById(@Param("accountId") int accountId);

    @Query(nativeQuery = true,value = "SELECT * FROM account_data WHERE account_id=:accountId")
    AccountEntity findByAccountId(@Param("accountId") int accountId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE account_data SET status = 2 where account_id = :accountId ")
    void deleteAccount(@Param("accountId") Integer accountId);

}
