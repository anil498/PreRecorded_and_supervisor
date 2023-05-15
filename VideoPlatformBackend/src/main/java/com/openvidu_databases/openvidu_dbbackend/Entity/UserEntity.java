package com.openvidu_databases.openvidu_dbbackend.Entity;
import java.io.Serializable;
import java.sql.Array;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.api.client.util.DateTime;
import lombok.Data;

import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

@Entity
@EntityScan
@Data
@Table(name = "user_data")
public class UserEntity implements Serializable {

    //    private static final long serialVersionUID=810972626450090362960L;
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_data_generator")
    @SequenceGenerator(name="user_data_generator", sequenceName = "user_data_seq")
    private int userId;

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "login_id", unique = true)
    private String loginId;

   // @Lob
    @Column(name = "password")
    private String password;

    @Column(name = "fname",columnDefinition = "varchar(255) default 'NULL'")
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
  //  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
  //  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastLogin;

    @Column(name = "exp_date")
    private String expDate;

    @Column(name="session",columnDefinition="text")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.MapType")
    private HashMap<String, String> session = new HashMap<String, String>(0);

    @Column(name = "features",columnDefinition = "integer[]")
   // @Type(type = "packageofclass.GenericArrayUserType")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType")
    private Integer[] features;
    @Column(name = "access_id",columnDefinition = "integer[]")
   // @Type(type = "packageofclass.GenericArrayUserType")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType")
    private Integer[] accessId;
//    @Column(name="features",columnDefinition="text")
//    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.MapType")
//    private HashMap<String, String> features = new HashMap<String, String>(0);
//
    @Column(name="features_meta",columnDefinition="text")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.MapType")
    private HashMap<String, String> featuresMeta = new HashMap<String, String>(0);
//
//    @Column(name="access_id",columnDefinition="text")
//    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.MapType")
//    private HashMap<String, String> accessId = new HashMap<String, String>(0);

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

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
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

//    public String getFeaturesMeta() {
//        return featuresMeta;
//    }
//
//    public void setFeaturesMeta(String featuresMeta) {
//        this.featuresMeta = featuresMeta;
//    }

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

//    public HashMap<String, String> getFeatures() {
//        return features;
//    }

//    public void setFeatures(HashMap<String, String> features) {
//        this.features = features;
//    }
//
//    public HashMap<String, String> getAccessId() {
//        return accessId;
//    }
//
//    public void setAccessId(HashMap<String, String> accessId) {
//        this.accessId = accessId;
//    }

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