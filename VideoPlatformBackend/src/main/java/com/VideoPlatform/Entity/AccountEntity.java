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

    @Column(name = "name")
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
    private Integer[] features;

    @Column(name="features_meta",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private HashMap<String, Object> featuresMeta = new HashMap<String, Object>(0);

    @Column(name = "access_id",columnDefinition = "integer[]")
    @Type(type="com.VideoPlatform.Utils.GenericArrayUserType")
    private Integer[] accessId;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public HashMap<String, Object> getLogo() {
        return logo;
    }

    public void setLogo(HashMap<String, Object> logo) {
        this.logo = logo;
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

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }

    public Integer[] getFeatures() {
        return features;
    }

    public void setFeatures(Integer[] features) {
        this.features = features;
    }

    public HashMap<String, Object> getFeaturesMeta() {
        return featuresMeta;
    }

    public void setFeaturesMeta(HashMap<String, Object> featuresMeta) {
        this.featuresMeta = featuresMeta;
    }

    public Integer[] getAccessId() {
        return accessId;
    }

    public void setAccessId(Integer[] accessId) {
        this.accessId = accessId;
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

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
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


