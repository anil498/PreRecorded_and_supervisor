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

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM account_data")
    Integer totalAccounts();

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM account_data WHERE status=1")
    Integer activeAccounts();

    @Query(nativeQuery = true,value = "select date_trunc('day',creation_date) as creation_date,count(*) from account_data group by date_trunc('day',creation_date)")
    List<Object> dailyAccountCreation();

    @Query(nativeQuery = true,value = "select date_trunc('month',creation_date) as creation_date,count(*) from account_data group by date_trunc('month',creation_date)")
    List<Object> monthlyAccountCreation();

    @Query(nativeQuery = true,value = "select date_trunc('year',creation_date) as creation_date,count(*) from account_data group by date_trunc('year',creation_date)")
    List<Object> yearlyAccountCreation();

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE account_data SET status = 2 where account_id = :accountId ")
    void deleteAccount(@Param("accountId") Integer accountId);

}
