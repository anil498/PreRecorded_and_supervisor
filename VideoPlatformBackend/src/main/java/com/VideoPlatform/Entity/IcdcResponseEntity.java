package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@EntityScan
@Data
@Table(name = "icdc_response")
public class IcdcResponseEntity {

    @Id
    @Column(name = "response_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "response_data_generator")
    @SequenceGenerator(name="response_data_generator", sequenceName = "response_data_seq",allocationSize = 1)
    private int responseId;

    @Column(name = "icdc_id")
    private int icdcId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name="icdc_result",columnDefinition="text")
    @Type(type="com.VideoPlatform.Utils.MapType")
    private List<Map<String, Object>> icdcResult;

    @Column(name = "creation_date",columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    public int getIcdcId() { return icdcId; }

    public void setIcdcId(int icdcId) { this.icdcId = icdcId; }

    public List<Map<String, Object>> getIcdcResult() { return icdcResult; }

    public void setIcdcResult(List<Map<String, Object>> icdcResult) { this.icdcResult = icdcResult; }

    public int getResponseId() { return responseId; }

    public void setResponseId(int responseId) { this.responseId = responseId; }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "IcdcResponseEntity{" +
                "responseId=" + responseId +
                ", icdcId=" + icdcId +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", sessionId=" + sessionId +
                ", icdcResult=" + icdcResult +
                ", creationDate=" + creationDate +
                '}';
    }
}

