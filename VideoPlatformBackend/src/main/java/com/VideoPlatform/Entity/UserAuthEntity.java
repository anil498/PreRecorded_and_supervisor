package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

@Entity
@Table(name = "user_auth")
public class UserAuthEntity {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "login_id",nullable = false,unique = true)
    private String loginId;

    @Column(name = "auth_id")
    private int authId;

    @Column(nullable = false, name="token")
    private String token;

    @Column(nullable = false, name="auth_key")
    private String authKey;

    @Column(name="system_name",columnDefinition = "text")
    private String systemNames;

    @Column(nullable = false,name="creation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    @Column(nullable = false,name="exp_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expDate;

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getLoginId() { return loginId; }

    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public String getSystemNames() {
        return systemNames;
    }

    public void setSystemNames(String systemNames) {
        this.systemNames = systemNames;
    }

    public String getAuthKey() { return authKey; }

    public void setAuthKey(String authKey) { this.authKey = authKey; }

    @Override
    public String toString() {
        return "UserAuthEntity{" +
                "userId=" + userId +
                ", loginId='" + loginId + '\'' +
                ", authId=" + authId +
                ", token='" + token + '\'' +
                ", authKey='" + authKey + '\'' +
                ", systemNames='" + systemNames + '\'' +
                ", creationDate=" + creationDate +
                ", expDate=" + expDate +
                '}';
    }
}
