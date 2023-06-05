package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.io.IOException;
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

//    @Column(name="meta_list",columnDefinition="text")
//    @Type(type="com.VideoPlatform.Utils.MapType")
//    private HashMap<String, Object> metaList = new HashMap<String, Object>(0);

//    @Column(name="meta_list",columnDefinition="text")
//    private String metaList;

//    @Column(name = "meta_list", columnDefinition = "jsonb")
//
//    @Type(type = "jsonb")
//
//    private JsonNode metaList;

//    @Column(name = "meta_list", columnDefinition = "jsonb")
//    private String metaList;

    @Column(name = "meta_list", columnDefinition = "text")
    @JsonRawValue
    private String metaList;

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
    public JsonNode getMetaList() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(metaList);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setMetaList(JsonNode metaList) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.metaList = mapper.writeValueAsString(metaList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FeatureEntity{" +
                "featureId=" + featureId +
                ", name='" + name + '\'' +
                ", metaList='" + metaList + '\'' +
                ", status=" + status +
                '}';
    }
}
