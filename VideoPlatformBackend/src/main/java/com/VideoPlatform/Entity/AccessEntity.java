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
    private Integer accessId;

    @Column(name = "name")
    private String name;

    @Column(name = "system_name")
    private String systemName;

    @Column(name = "seq")
    private Integer seq;

    @Column(name = "p_id")
    private Integer pId;

    @Column(name = "status")
    private Integer status=1;

    public Integer getAccessId() {
        return accessId;
    }

    public void setAccessId(Integer accessId) {
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

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSystemName() { return systemName; }

    public void setSystemName(String systemName) { this.systemName = systemName; }

    @Override
    public String toString() {
        return "AccessEntity{" +
                "accessId=" + accessId +
                ", name='" + name + '\'' +
                ", systemName='" + systemName + '\'' +
                ", seq=" + seq +
                ", pId=" + pId +
                ", status=" + status +
                '}';
    }
}
