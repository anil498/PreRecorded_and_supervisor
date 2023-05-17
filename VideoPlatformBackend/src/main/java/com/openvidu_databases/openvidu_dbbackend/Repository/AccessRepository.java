package com.openvidu_databases.openvidu_dbbackend.Repository;

import com.openvidu_databases.openvidu_dbbackend.Entity.AccessEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRepository extends JpaRepository<AccessEntity, Integer> {
    @Query(nativeQuery=true, value = "select * from platform_access where access_id = :accessId ")
    Object findById(@Param("accessId") int accessId);
    @Query(nativeQuery=true, value = "select * from platform_access where access_id = :accessId ")
    AccessEntity findByAccessId(@Param("accessId") int accessId);
}
