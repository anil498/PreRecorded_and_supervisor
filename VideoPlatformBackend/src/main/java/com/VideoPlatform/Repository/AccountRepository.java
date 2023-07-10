package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.AccountEntity;
import com.google.gson.JsonObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

    @Query(nativeQuery=true, value = "select * from account_data")
    List<Object> findAlll();

    @Query(nativeQuery = true,value = "SELECT * FROM account_data WHERE account_id=:accountId")
    List<AccountEntity> findById(@Param("accountId") int accountId);

    @Query(nativeQuery = true,value = "SELECT * FROM account_data WHERE account_id=:accountId")
    AccountEntity findByAccountId(@Param("accountId") int accountId);

    @Query(nativeQuery = true,value = "SELECT * FROM account_data WHERE name=:name")
    AccountEntity findByAccountName(@Param("name") String name);

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM account_data")
    Integer totalAccounts();

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM account_data WHERE status=1")
    Integer activeAccounts();

    @Query(nativeQuery = true,value = "SELECT EXTRACT('day' FROM d.day) AS day,COUNT(a.creation_date) AS count FROM (SELECT generate_series(:startDate,:endDate, interval '1 day') AS day) AS d LEFT JOIN account_data a ON DATE_TRUNC('day', a.creation_date\\:\\:date) = d.day GROUP BY d.day ORDER BY d.day;")
    List<Object[]> dailyAccountCreation(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

//    @Query(nativeQuery = true,value = "select EXTRACT('month' from date_trunc('month',creation_date)) as month ,count(*) from account_data group by date_trunc('month',creation_date)")
    @Query(nativeQuery = true, value = "SELECT EXTRACT('month' FROM m.month) AS month,COUNT(a.creation_date) AS count FROM (SELECT generate_series(DATE '2023-01-01',DATE '2023-12-01', interval '1 month') AS month) AS m LEFT JOIN account_data a ON DATE_TRUNC('month', a.creation_date\\:\\:date) = m.month GROUP BY m.month ORDER BY m.month;")
    List<Object[]> monthlyAccountCreation();

    @Query(nativeQuery = true, value = "SELECT EXTRACT('year' FROM y.year) AS year,COUNT(a.creation_date) AS count FROM (SELECT generate_series(DATE '2023-01-01',DATE '2025-12-01', interval '1 year') AS year) AS y LEFT JOIN account_data a ON DATE_TRUNC('year', a.creation_date\\:\\:date) = y.year GROUP BY y.year ORDER BY y.year;")
    List<Object[]> yearlyAccountCreation();

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE account_data SET status = 2 where account_id = :accountId ")
    void deleteAccount(@Param("accountId") Integer accountId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE account_data SET features_meta=:featuresMeta where name = :name ")
    void updateFeatureMeta(@Param("name") String name, @Param("featuresMeta") String featuresMeta);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE account_data SET features = :features WHERE account_id = :accountId")
    void updateFeatures(@Param("accountId") Integer accountId,@Param("features") Integer[] features);


}
