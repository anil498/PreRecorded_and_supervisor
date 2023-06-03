package com.VideoPlatform.Entity;

import com.VideoPlatform.Utils.UnixTimestampConverter;
import com.google.gson.JsonObject;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

@Entity
@EntityScan
@Data
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @Column(name = "session_id" )
    private String sessionId;
    @Column(name = "session_name") private String sessionName;
    @Column(name = "users" ) private String userI;
    @Column(name = "account" ) private String accountI;
    @Column(name = "participant_name") private String participantName;
    @Column(name = "participants") private String participants;
    @Column(name = "settings",columnDefinition = "text") private String settings;
    @Column(name = "sessionKey") private String sessionKey;
 //   @Convert(converter = UnixTimestampConverter.class)
    @Column(name = "creation_date") private Date creationDate;
    @Column(name = "exp_date")
 //   @Convert(converter = UnixTimestampConverter.class)
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

    public String getUserI() {
        return userI;
    }

    public void setUserI(String user) {
        this.userI = user;
    }

    public String getAccountI() {
        return accountI;
    }

    public void setAccountI(String account) {
        this.accountI = account;
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
                ", userI='" + userI + '\'' +
                ", accountI='" + accountI + '\'' +
                ", participantName='" + participantName + '\'' +
                ", participants='" + participants + '\'' +
                ", settings='" + settings + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", expDate='" + expDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
