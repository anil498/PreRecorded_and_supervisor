package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IcdcRepository extends JpaRepository<IcdcEntity, Integer> {
    @Query(nativeQuery=true, value = "SELECT * from icdc where user_id = :userId limit 1")
    IcdcEntity findByUserId(@Param("userId") Integer userId);


}
