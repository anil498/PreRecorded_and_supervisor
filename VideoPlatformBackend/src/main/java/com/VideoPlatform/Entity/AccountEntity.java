package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Entity
@EntityScan
@Data
@Table(name = "account_data")
public class AccountEntity {

    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_data_generator")
    @SequenceGenerator(name="account_data_generator", sequenceName = "account_data_seq",allocationSize = 1)
    private int accountId;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name="logo",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private HashMap<String, Object> logo = new HashMap<String, Object>(0);

    @Column(name = "creation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date creationDate;

    @Column(name = "max_user")
    private int maxUser;

    @Column(name="session",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private HashMap<String, Object> session = new HashMap<String,Object>(0);

    @Column(name = "features",columnDefinition = "integer[]")
    @Type(type="com.VideoPlatform.Utils.GenericArrayUserType")
    private Integer[] features = {};

    @Column(name="features_meta",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private HashMap<String, Object> featuresMeta = new HashMap<String, Object>(0);

    @Column(name = "access_id",columnDefinition = "integer[]")
    @Type(type="com.VideoPlatform.Utils.GenericArrayUserType")
    private Integer[] accessId = {};

    @Column(name = "status")
    private int status=1;

    @Column(name = "exp_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date expDate;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public Boolean setName(String name) {
        if(name != null){
            this.name = name;
            return true;
        }
        return false;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if(address != null)
            this.address = address;
    }

    public HashMap<String, Object> getLogo() {
        return logo;
    }

    public Boolean setLogo(HashMap<String, Object> logo) {
        if(logo != null) {
            this.logo = logo;
            return true;
        }
        return false;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getMaxUser() {
        return maxUser;
    }

    public void setMaxUser(int maxUser) {
        this.maxUser = maxUser;
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public Boolean setSession(HashMap<String, Object> session) {
        if(session != null) {
            this.session = session;
            return true;
        }
        return false;
    }

    public Integer[] getFeatures() {
        return features;
    }

    public Boolean setFeatures(Integer[] features) {
        if(features != null) {
            this.features = features;
            return true;
        }
        return false;
    }

    public HashMap<String, Object> getFeaturesMeta() {
        return featuresMeta;
    }

    public Boolean setFeaturesMeta(HashMap<String, Object> featuresMeta) {
        if(featuresMeta != null) {
            this.featuresMeta = featuresMeta;
            return true;
        }
        return false;
    }

    public Integer[] getAccessId() {
        return accessId;
    }

    public Boolean setAccessId(Integer[] accessId) {
        if(accessId != null) {
            this.accessId = accessId;
            return false;
        }
        return false;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpDate() {
        return expDate;
    }

    public Boolean setExpDate(Date expDate) {
        if(expDate != null) {
            this.expDate = expDate;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "accountId=" + accountId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", logo=" + logo +
                ", creationDate=" + creationDate +
                ", maxUser=" + maxUser +
                ", session=" + session +
                ", features=" + Arrays.toString(features) +
                ", featuresMeta=" + featuresMeta +
                ", accessId=" + Arrays.toString(accessId) +
                ", status=" + status +
                ", expDate=" + expDate +
                '}';
    }
}


