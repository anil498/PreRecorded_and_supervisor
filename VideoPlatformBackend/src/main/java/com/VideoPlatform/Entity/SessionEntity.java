package com.VideoPlatform.Entity;

import com.google.gson.JsonObject;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    @Column(name = "settings") private String settings;

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

    @Override
    public String toString() {
        return "SessionEntity{" +
                "sessionId='" + sessionId + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", userI='" + userI + '\'' +
                ", accountI='" + accountI + '\'' +
                ", participantName='" + participantName + '\'' +
                ", participants='" + participants + '\'' +
                ", settings=" + settings +
                '}';
    }
}
