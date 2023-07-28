package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface IcdcRepository extends JpaRepository<IcdcEntity, Integer> {
    @Query(nativeQuery=true, value = "SELECT * from icdc where user_id = :userId limit 1")
    IcdcEntity findByUserId(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "SELECT * from icdc where user_id = :userId")
    List<IcdcEntity> findAllByUserId(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "SELECT * from icdc where icdc_id = :icdcId")
    IcdcEntity findByIcdcId(@Param("icdcId") Integer icdcId);

    @Query(nativeQuery=true, value = "SELECT icdc_id,form_name,icdc_data from icdc where user_id = :userId")
    List<Map<String, Object>> findNamesByUserId(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "SELECT icdc_id,form_name from icdc where account_id = :accountId")
    List<Map<String, Object>> findNamesByAccountId(@Param("accountId") Integer accountId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE icdc SET status = 2 where icdc_id = :icdcId ")
    void deleteIcdc(@Param("icdcId") Integer icdcId);

}
