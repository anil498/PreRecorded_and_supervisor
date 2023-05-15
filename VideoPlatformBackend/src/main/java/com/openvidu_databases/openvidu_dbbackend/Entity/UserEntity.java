package com.openvidu_databases.openvidu_dbbackend.Entity;

import java.io.Serializable;
import java.util.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import javax.persistence.*;

@Entity
@EntityScan
@Data
@Table(name = "user_data")
public class UserEntity implements Serializable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_data_generator")
    @SequenceGenerator(name="user_data_generator", sequenceName = "user_data_seq",allocationSize = 1)
    private int userId;

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "login_id", unique = true)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "fname")
    private String fname;

    @Column(name = "lname")
    private String lname;

    @Column(name = "contact")
    private String contact;

    @Column(name = "email")
    private String email;

    @Column(name = "creation_date")
    private String creationDate;

    @Column(name = "last_login")
    private String lastLogin;

    @Column(name = "exp_date")
    private String expDate;

    @Column(name="session",columnDefinition="text")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.MapType")
    private HashMap<String, String> session = new HashMap<String, String>(0);

    @Column(name = "features",columnDefinition = "integer[]")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType")
    private Integer[] features;

    @Column(name = "access_id",columnDefinition = "integer[]")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType")
    private Integer[] accessId;

    @Column(name="features_meta",columnDefinition="text")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.MapType")
    private HashMap<String, String> featuresMeta = new HashMap<String, String>(0);

    @Column(name = "status")
    private int status = 1;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public HashMap<String, String> getSession() {
        return session;
    }

    public void setSession(HashMap<String, String> session) {
        this.session = session;
    }
//

    public Integer[] getFeatures() {
        return features;
    }

    public void setFeatures(Integer[] features) {
        this.features = features;
    }

    public Integer[] getAccessId() {
        return accessId;
    }

    public void setAccessId(Integer[] accessId) {
        this.accessId = accessId;
    }

    public HashMap<String, String> getFeaturesMeta() {
        return featuresMeta;
    }

    public void setFeaturesMeta(HashMap<String, String> featuresMeta) {
        this.featuresMeta = featuresMeta;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId=" + userId +
                ", accountId='" + accountId + '\'' +
                ", loginId='" + loginId + '\'' +
                ", password='" + password + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", mobile='" + contact + '\'' +
                ", email='" + email + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", expDate='" + expDate + '\'' +
                ", session=" + session +
                ", features='" + features + '\'' +
                ", featuresMeta=" + featuresMeta +
                ", accessId='" + accessId + '\'' +
                ", status=" + status +
                '}';
    }
}