package com.VideoPlatform.Entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.HashMap;

@Entity
@EntityScan
@Data
@Table(name = "platform_features")
public class FeatureEntity {

    @Id
    @Column(name = "feature_id")
    private int featureId;

    @Column(name = "name")
    private String name;

    @Column(name="meta_list",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private HashMap<String, String> metaList = new HashMap<String, String>(0);

    @Column(name = "status")
    private int status = 1;

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getMetaList() {
        return metaList;
    }

    public void setMetaList(HashMap<String, String> metaList) {
        this.metaList = metaList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PlatformFeatures{" +
                "featureId=" + featureId +
                ", name='" + name + '\'' +
                ", metaList=" + metaList +
                ", status=" + status +
                '}';
    }
}
