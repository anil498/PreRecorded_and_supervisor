package com.VideoPlatform.Entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@EntityScan
@Data
@Table(name = "icdc_response")
public class IcdcResponseEntity {

        @Id
        @Column(name = "icdc_id")
        private int icdcId;

//        @Column(name = "icdc_result",columnDefinition = "text")
//        private String icdcResult;

    @Column(name="icdc_result",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private List<Map<String, Object>> icdcResult;


    public int getIcdcId() {
            return icdcId;
        }

        public void setIcdcId(int icdcId) {
            this.icdcId = icdcId;
        }

        public List<Map<String, Object>> getIcdcResult() {
            return icdcResult;
        }

        public void setIcdcResult(List<Map<String, Object>> icdcResult) {
            this.icdcResult = icdcResult;
        }

        @Override
        public String toString() {
            return "IcdcEntity{" +
                    "icdcId=" + icdcId +
                    ", icdcResult=" + icdcResult +
                    '}';
        }
    }

