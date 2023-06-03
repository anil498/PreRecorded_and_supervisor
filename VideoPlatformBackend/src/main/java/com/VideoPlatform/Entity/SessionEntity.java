package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityScan
@Data
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @Column(name = "session_id" )
    private String sessionId;

    @Column(name = "session_name") private String sessionName;

    @Column(name = "user_id" ) private Integer userId;

    @Column(name = "account_id" ) private Integer accountId;

    @Column(name = "max_active_sessions_u") Integer userMaxActiveSessions;

    @Column(name = "max_active_sessions_a") Integer accountMaxActiveSessions;

    @Column(name = "participant_name") private String participantName;

    @Column(name = "participants") private String participants;

    @Column(name = "settings",columnDefinition = "text") private String settings;

    @Column(name = "sessionKey") private String sessionKey;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    @Column(name = "creation_date") private Date creationDate;

    @Column(name = "exp_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date expDate;

    @Column(name = "status") private Integer status = 1;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getUserMaxActiveSessions() {
        return userMaxActiveSessions;
    }

    public void setUserMaxActiveSessions(Integer userMaxActiveSessions) {
        this.userMaxActiveSessions = userMaxActiveSessions;
    }

    public Integer getAccountMaxActiveSessions() {
        return accountMaxActiveSessions;
    }

    public void setAccountMaxActiveSessions(Integer accountMaxActiveSessions) {
        this.accountMaxActiveSessions = accountMaxActiveSessions;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SessionEntity{" +
                "sessionId='" + sessionId + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", userMaxActiveSessions=" + userMaxActiveSessions +
                ", accountMaxActiveSessions=" + accountMaxActiveSessions +
                ", participantName='" + participantName + '\'' +
                ", participants='" + participants + '\'' +
                ", settings='" + settings + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                ", creationDate=" + creationDate +
                ", expDate=" + expDate +
                ", status=" + status +
                '}';
    }
}
