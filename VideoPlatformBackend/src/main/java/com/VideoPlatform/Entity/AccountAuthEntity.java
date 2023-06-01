package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityScan
@Data
@Table(name = "account_auth")
public class AccountAuthEntity {
    @Id
    @Column(name = "auth_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_auth_generator")
    @SequenceGenerator(name="account_auth_generator", sequenceName = "account_auth_seq",allocationSize = 1)
    private int authId;

    @Column(nullable = false, name="account_id")
    private int accountId;

    @Column(nullable = false, name="name")
    private String name;

    @Column(nullable = false, name="auth_key")
    private String authKey;

    @Column(nullable = false,name="creation_date")
    private LocalDateTime creationDate;

    @Column(nullable = false,name="exp_date")
    private LocalDateTime expDate;

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
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
        return "AccountAuthEntity{" +
                "authId=" + authId +
                ", accountId=" + accountId +
                ", name='" + name + '\'' +
                ", authKey='" + authKey + '\'' +
                ", creationDate=" + creationDate +
                ", expDate=" + expDate +
                '}';
    }
}
