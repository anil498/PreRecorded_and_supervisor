package com.VideoPlatform.Repository;

import com.VideoPlatform.Entity.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<FeatureEntity, Integer> {
    @Query(nativeQuery=true, value = "select * from platform_features where feature_id = :featureId ")
    Object findById(@Param("featureId") int featureId);

    @Query(nativeQuery=true, value = "select * from platform_features")
    List<FeatureEntity> findAll();

    @Query(nativeQuery=true, value = "select feature_id, meta_list from platform_features")
    List<Object> findList();

    @Query(nativeQuery=true, value = "select * from platform_features where feature_id = :featureId ")
    FeatureEntity findByFeatureId(@Param("featureId") int featureId);

}
