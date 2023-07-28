package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;

@Entity
@EntityScan
@Data
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @Column(name = "sessionKey") private String sessionKey;

    @Column(name = "session_id" ) private String sessionId;

    @Column(name = "session_name") private String sessionName;

    @Column(name = "user_id" ) private Integer userId;

    @Column(name = "account_id" ) private Integer accountId;

    @Column(name = "max_sessions_u") Integer userMaxSessions;

    @Column(name = "max_sessions_a") Integer accountMaxSessions;

    @Column(name = "participant_name") private String participantName;

    @Column(name = "total_participants") private Integer totalParticipants;

    @Column(name = "settings",columnDefinition = "text") private String settings;

    @Column(name = "type") private String type;

    @Column(name = "hold") private Boolean hold = false;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "creation_date") private Date creationDate;

    @Column(name = "exp_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expDate;

    @Column(name = "status") private Integer status = 1;

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

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

    public Integer getUserMaxSessions() {
        return userMaxSessions;
    }

    public void setUserMaxSessions(Integer userMaxSessions) {
        this.userMaxSessions = userMaxSessions;
    }

    public Integer getAccountMaxSessions() {
        return accountMaxSessions;
    }

    public void setAccountMaxSessions(Integer accountMaxSessions) {
        this.accountMaxSessions = accountMaxSessions;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public Integer getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Integer totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public JsonNode getSettings(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(settings);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Boolean getHold() {
        return hold;
    }

    public void setHold(Boolean hold) {
        this.hold = hold;
    }

    @Override
    public String toString() {
        return "SessionEntity{" +
                "sessionKey='" + sessionKey + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", userMaxSessions=" + userMaxSessions +
                ", accountMaxSessions=" + accountMaxSessions +
                ", participantName='" + participantName + '\'' +
                ", totalParticipants=" + totalParticipants +
                ", settings='" + settings + '\'' +
                ", type='" + type + '\'' +
                ", hold=" + hold +
                ", creationDate=" + creationDate +
                ", expDate=" + expDate +
                ", status=" + status +
                '}';
    }
}
