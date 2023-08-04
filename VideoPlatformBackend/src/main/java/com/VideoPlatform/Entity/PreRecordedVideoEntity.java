package com.VideoPlatform.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityScan
@Data
@Table(name = "prerecorded_video")
public class PreRecordedVideoEntity {

    @Id
    @Column(name = "video_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_data_generator")
    @SequenceGenerator(name="video_data_generator", sequenceName = "video_data_seq",allocationSize = 1)
    private int videoId;

    @Column(name = "video_name")
    private String videoName;

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "video_path")
    private String videoPath;

    @Column(name = "creation_date",columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    @Column(name = "status")
    private int status = 1;

    public int getVideoId() { return videoId; }

    public void setVideoId(int videoId) { this.videoId = videoId; }

    public String getVideoName() { return videoName; }

    public void setVideoName(String videoName) { this.videoName = videoName; }

    public int getAccountId() { return accountId; }

    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getVideoPath() { return videoPath; }

    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }

    public Date getCreationDate() { return creationDate; }

    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }

    @Override
    public String toString() {
        return "PreRecordedVideoEntity{" +
                "videoId=" + videoId +
                ", videoName='" + videoName + '\'' +
                ", accountId=" + accountId +
                ", userId=" + userId +
                ", videoPath='" + videoPath + '\'' +
                ", creationDate=" + creationDate +
                ", status=" + status +
                '}';
    }
}
