package com.openvidu_databases.openvidu_dbbackend.Entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;

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
    private LocalDateTime creationDate;

    @Column(nullable = false,name="exp_date")
    private LocalDateTime expDate;

    @Column(name = "access_id",columnDefinition = "integer[]")
    @Type(type="com.openvidu_databases.openvidu_dbbackend.Utils.GenericArrayUserType")
    private Integer[] accessId;

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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getExpDate() {
        return expDate;
    }

    public void setExpDate(LocalDateTime expDate) {
        this.expDate = expDate;
    }

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public Integer[] getAccessId() { return accessId; }

    public void setAccessId(Integer[] accessId) { this.accessId = accessId; }

    @Override
    public String toString() {
        return "UserAuthEntity{" +
                "userId=" + userId +
                ", loginId='" + loginId + '\'' +
                ", authId=" + authId +
                ", token='" + token + '\'' +
                ", creationDate=" + creationDate +
                ", expDate=" + expDate +
                ", accessId='" + accessId + '\'' +
                '}';
    }
}
