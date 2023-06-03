package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
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

    @Column(nullable = false,name="creation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date creationDate;

    @Column(nullable = false,name="exp_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
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

    @Override
    public String toString() {
        return "UserAuthEntity{" +
                "userId=" + userId +
                ", loginId='" + loginId + '\'' +
                ", authId=" + authId +
                ", token='" + token + '\'' +
                ", creationDate=" + creationDate +
                ", expDate=" + expDate +
                '}';
    }
}
