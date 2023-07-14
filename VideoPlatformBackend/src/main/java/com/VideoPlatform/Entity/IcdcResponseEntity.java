package com.VideoPlatform.Entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.HashMap;

@Entity
@EntityScan
@Data
@Table(name = "icdc_response")
public class IcdcResponseEntity {

        @Id
        @Column(name = "icdc_id")
        private int icdcId;

        @Column(name = "icdc_result",columnDefinition = "text")
        @Type(type="com.VideoPlatform.Utils.MapType")
        private HashMap<String, Object> icdcResult = new HashMap<String, Object>(0);


        public int getIcdcId() {
            return icdcId;
        }

        public void setIcdcId(int icdcId) {
            this.icdcId = icdcId;
        }

        public HashMap<String, Object> getIcdcResult() {
            return icdcResult;
        }

        public void setIcdcResult(HashMap<String, Object> icdcResult) {
            this.icdcResult = icdcResult;
        }

        @Override
        public String toString() {
            return "IcdcEntity{" +
                    "icdcId=" + icdcId +
                    ", icdcData=" + icdcResult +
                    '}';
        }
    }

