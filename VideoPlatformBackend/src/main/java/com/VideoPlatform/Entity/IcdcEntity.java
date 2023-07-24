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
@Table(name = "icdc")
public class IcdcEntity {
    @Id
    @Column(name = "icdc_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "icdc_data_generator")
    @SequenceGenerator(name="icdc_data_generator", sequenceName = "icdc_data_seq",allocationSize = 1)
    private int icdcId;

    @Column(name = "form_name",unique = true)
    private String formName;

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "user_id")
    private int userId;

    @Column(name="icdc_data",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private List<Map<String, Object>> icdcData;

    @Column(name = "status")
    private int status = 1;

    public int getIcdcId() {
        return icdcId;
    }

    public void setIcdcId(int icdcId) {
        this.icdcId = icdcId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFormName() { return formName; }

    public void setFormName(String formName) { this.formName = formName; }

    public List<Map<String, Object>> getIcdcData() {
        return icdcData;
    }

    public void setIcdcData(List<Map<String, Object>> icdcData) {
        this.icdcData = icdcData;
    }

    @Override
    public String toString() {
        return "IcdcEntity{" +
                "icdcId=" + icdcId +
                ", formName='" + formName + '\'' +
                ", accountId=" + accountId +
                ", userId=" + userId +
                ", icdcData=" + icdcData +
                ", status=" + status +
                '}';
    }


}
