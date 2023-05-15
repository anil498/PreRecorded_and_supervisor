package com.openvidu_databases.openvidu_dbbackend.Repository;

import com.google.gson.JsonObject;
import com.openvidu_databases.openvidu_dbbackend.Entity.FeatureEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<FeatureEntity, Integer> {
    @Query(nativeQuery=true, value = "select * from platform_features where feature_id = :featureId ")
    FeatureEntity findById(@Param("featureId") int featureId);

    @Query(nativeQuery=true, value = "select * from platform_features")
    List<FeatureEntity> findAll();

    @Query(nativeQuery=true, value = "select feature_id, meta_list from platform_features")
    List<Object> findList();
    //List<FeatureEntity> findBy
}
