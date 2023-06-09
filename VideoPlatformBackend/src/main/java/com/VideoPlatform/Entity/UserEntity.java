package com.VideoPlatform.Entity;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Column(name = "creation_date",columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date creationDate;

    @Column(name = "last_login")
    private String lastLogin;

    @Column(name = "exp_date",columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date expDate;

    @Column(name = "parent_id")
    private int parentId;

    @Column(name="session",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private HashMap<String, Object> session = new HashMap<String, Object>(0);

    @Column(name = "features",columnDefinition = "integer[]")
    @Type(type="com.VideoPlatform.Utils.GenericArrayUserType")
    private Integer[] features;

    @Column(name = "access_id",columnDefinition = "integer[]")
    @Type(type="com.VideoPlatform.Utils.GenericArrayUserType")
    private Integer[] accessId;

    @Column(name="features_meta",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private HashMap<String, Object> featuresMeta = new HashMap<String, Object>(0);

    @Column(name = "status")
    private int status = 1;

    @Column(name="logo",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private HashMap<String, Object> logo = new HashMap<String, Object>(0);

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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
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

    public HashMap<String, Object> getFeaturesMeta() {
        return featuresMeta;
    }

    public void setFeaturesMeta(HashMap<String, Object> featuresMeta) {
        this.featuresMeta = featuresMeta;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HashMap<String, Object> getLogo() {
        return logo;
    }

    public void setLogo(HashMap<String, Object> logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId=" + userId +
                ", accountId=" + accountId +
                ", loginId='" + loginId + '\'' +
                ", password='" + password + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                ", creationDate=" + creationDate +
                ", lastLogin='" + lastLogin + '\'' +
                ", expDate=" + expDate +
                ", parentId=" + parentId +
                ", session=" + session +
                ", features=" + Arrays.toString(features) +
                ", accessId=" + Arrays.toString(accessId) +
                ", featuresMeta=" + featuresMeta +
                ", status=" + status +
                ", logo=" + logo +
                '}';
    }
}