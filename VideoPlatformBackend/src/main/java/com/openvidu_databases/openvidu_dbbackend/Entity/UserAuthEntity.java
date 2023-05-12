package com.openvidu_databases.openvidu_dbbackend.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_auth")
public class UserAuthEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_auth_generator")
    @SequenceGenerator(name="user_auth_generator", sequenceName = "user_auth_seq")
    private int userId;

    @Column(name = "login_id",nullable = false,unique = true)
    private String loginId;

    @Column(nullable = false, name="token")
    private String token;

    @Column(nullable = false,name="creation_date")
    private LocalDateTime creationDate;

    @Column(nullable = false,name="exp_date")
    private LocalDateTime expDate;

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

    @Override
    public String toString() {
        return "UserAuthEntity{" +
                "userId=" + userId +
                ", loginId='" + loginId + '\'' +
                ", token='" + token + '\'' +
                ", creationDate=" + creationDate +
                ", expDate=" + expDate +
                '}';
    }
}
