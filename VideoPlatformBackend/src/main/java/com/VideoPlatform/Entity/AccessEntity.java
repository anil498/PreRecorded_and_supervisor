package com.VideoPlatform.Entity;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@EntityScan
@Data
@Table(name = "platform_access")
public class AccessEntity {
    @Id
    @Column(name = "access_id")
    private int accessId;

    @Column(name = "name")
    private String name;

    @Column(name = "seq")
    private int seq;

    @Column(name = "p_id")
    private int pId;

    @Column(name = "api_id")
    private int apiId;

    @Column(name = "status")
    private int status;

    public int getAccessId() {
        return accessId;
    }

    public void setAccessId(int accessId) {
        this.accessId = accessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccessEntity{" +
                "accessId=" + accessId +
                ", name='" + name + '\'' +
                ", seq=" + seq +
                ", pId=" + pId +
                ", apiId=" + apiId +
                ", status=" + status +
                '}';
    }
}
