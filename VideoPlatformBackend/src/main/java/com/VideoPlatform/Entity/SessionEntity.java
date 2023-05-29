package com.VideoPlatform.Entity;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityScan
@Data
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @Column(name = "session_id" ) String sessionId;
    @Column(name = "account_id" ) Integer accountId;
    @Column(name = "user_id" ) Integer userId;
    @Column(name = "session_name") String sessionName;
    @Column(name = "session_key" ) String sessionKey;
    @Column(name = "session_support_key" ) String sessionSupportKey;
    @Column(name = "user_info" ) String userInfo;
    @Column(name = "mobile") String mobile;
    @Column(name = "creation_date") String creation;
    @Column(name = "exp_date") String expDate;
    @Column(name = "status") String status;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSessionSupportKey() {
        return sessionSupportKey;
    }

    public void setSessionSupportKey(String sessionSupportKey) {
        this.sessionSupportKey = sessionSupportKey;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "SessionEntity{" +
                "sessionId='" + sessionId + '\'' +
                ", accountId=" + accountId +
                ", userId=" + userId +
                ", sessionName='" + sessionName + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                ", sessionSupportKey='" + sessionSupportKey + '\'' +
                ", userInfo='" + userInfo + '\'' +
                ", mobile='" + mobile + '\'' +
                ", creation='" + creation + '\'' +
                ", expDate=" + expDate +
                ", status='" + status + '\'' +
                '}';
    }
}
