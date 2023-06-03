package com.VideoPlatform.Entity;

import com.VideoPlatform.Utils.UnixTimestampConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

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

    @Column(nullable = false,name="creation_date",columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date creationDate;

    @Column(nullable = false,name="exp_date",columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date expDate;

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
