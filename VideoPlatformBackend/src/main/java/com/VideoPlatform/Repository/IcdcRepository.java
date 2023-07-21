package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface IcdcRepository extends JpaRepository<IcdcEntity, Integer> {
    @Query(nativeQuery=true, value = "SELECT * from icdc where user_id = :userId limit 1")
    IcdcEntity findByUserId(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "SELECT icdc_id,form_name from icdc where user_id = :userId")
    List<Map<String, Object>> findNamesByUserId(@Param("userId") Integer userId);

    @Query(nativeQuery=true, value = "SELECT icdc_id,form_name from icdc where account_id = :accountId")
    List<Map<String, Object>> findNamesByAccountId(@Param("accountId") Integer accountId);
}
