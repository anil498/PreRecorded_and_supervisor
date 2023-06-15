package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    @Query(nativeQuery = true,value = "select EXTRACT('day' from date_trunc('day',creation_date)) as day,count(*) from account_data group by date_trunc('day',creation_date)")
    List<Map<String,String>> dailyAccountCreation();

    @Query(nativeQuery = true,value = "select EXTRACT('month' from date_trunc('month',creation_date)) as month ,count(*) from account_data group by date_trunc('month',creation_date)")
    List<Map<String,String>> monthlyAccountCreation();

    @Query(nativeQuery = true,value = "select EXTRACT('year' from date_trunc('year',creation_date)) as year,count(*) from account_data group by date_trunc('year',creation_date)")
    List<Map<String,String>> yearlyAccountCreation();

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE account_data SET status = 2 where account_id = :accountId ")
    void deleteAccount(@Param("accountId") Integer accountId);

}
