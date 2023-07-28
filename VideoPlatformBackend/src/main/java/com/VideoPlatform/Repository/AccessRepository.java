package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.AccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AccessRepository extends JpaRepository<AccessEntity, Integer> {

    @Query(nativeQuery=true, value = "select * from platform_access where access_id = :accessId ")
    Object findById(@Param("accessId") int accessId);

    @Query(nativeQuery=true, value = "select * from platform_access where access_id = :accessId ")
    AccessEntity findByAccessId(@Param("accessId") int accessId);

    @Query(nativeQuery=true, value = "select * from platform_access where access_id = :accessId ")
    AccessEntity findByAccessIds(@Param("accessId") int accessId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "UPDATE platform_access SET status = 2 where access_id = :accessId ")
    void deleteAccess(@Param("accessId") Integer accessId);

    @Query(nativeQuery=true, value = "select * from platform_access where status = 1")
    List<AccessEntity> findAllAccess();
}
