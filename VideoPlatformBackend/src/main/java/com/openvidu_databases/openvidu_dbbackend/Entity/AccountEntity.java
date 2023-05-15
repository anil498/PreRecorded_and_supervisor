package com.openvidu_databases.openvidu_dbbackend.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "creation_date")
    private String creationDate;

    @Column(name = "max_admin")
    private int maxAdmin;

    @Column(name = "max_user")
    private int maxUser;

    @Column(name="session",columnDefinition="text")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.MapType")
    private HashMap<String, String> session = new HashMap<String, String>(0);

    @Column(name = "features",columnDefinition = "integer[]")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType")
    private Integer[] features;

    @Column(name="features_meta",columnDefinition="text")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.MapType")
    private HashMap<String, String> featuresMeta = new HashMap<String, String>(0);

    @Column(name = "access_id",columnDefinition = "integer[]")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType")
    private Integer[] accessId;

    @Column(name = "status")
    private int status=1;

    @Column(name = "exp_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expDate;

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

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getMaxAdmin() {
        return maxAdmin;
    }

    public void setMaxAdmin(int maxAdmin) {
        this.maxAdmin = maxAdmin;
    }

    public int getMaxUser() {
        return maxUser;
    }

    public void setMaxUser(int maxUser) {
        this.maxUser = maxUser;
    }

    public HashMap<String, String> getSession() {
        return session;
    }

    public void setSession(HashMap<String, String> session) {
        this.session = session;
    }

    public Integer[] getFeatures() {
        return features;
    }

    public void setFeatures(Integer[] features) {
        this.features = features;
    }

    public HashMap<String, String> getFeaturesMeta() {
        return featuresMeta;
    }

    public void setFeaturesMeta(HashMap<String, String> featuresMeta) {
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

    public LocalDateTime getExpDate() {
        return expDate;
    }

    public void setExpDate(LocalDateTime expDate) {
        this.expDate = expDate;
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "accountId=" + accountId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", logo=" + Arrays.toString(logo) +
                ", creationDate='" + creationDate + '\'' +
                ", maxAdmin='" + maxAdmin + '\'' +
                ", maxUser='" + maxUser + '\'' +
                ", session=" + session +
                ", features=" + features +
                ", featuresMeta=" + featuresMeta +
                ", accessId=" + accessId +
                ", status=" + status +
                ", expDate='" + expDate + '\'' +
                '}';
    }
}


