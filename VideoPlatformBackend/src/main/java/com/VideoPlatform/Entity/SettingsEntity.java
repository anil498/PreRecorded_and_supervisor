package com.VideoPlatform.Entity;


import com.VideoPlatform.Services.UserServiceImpl;
import com.VideoPlatform.Utils.CustomMultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;

public class SettingsEntity {
    private static final Logger logger= LoggerFactory.getLogger(SettingsEntity.class);
//    private static final String FILE_DIRECTORY = "/home/Vatsala.Vats/vid-content/";

    private Integer duration=0;
    private Object logo;
    private Object description;
    private Boolean displayTicker;
    private Boolean displayTimer;
    private Boolean recording;
    private Object recordingDetails;
    private Boolean screenShare;
    private Boolean waitForModerator;
    private Boolean chat;
    private Boolean activitiesButton;
    private Boolean participantsButton;
    private Boolean floatingLayout;
    private Object layoutType;
    private Boolean supervisor;
    private Boolean preRecorded;
    private Boolean sharePreRecordedVideo;
    private Object landingPage;
    private Object preRecordedDetails;


    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        if(duration!=0) this.duration = duration;
    }

    public Object getLogo() {
        return logo;
    }

    public void setLogo(Object logo) {
        if(logo != null) this.logo = logo;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        if(description != null) this.description = description;
    }

    public Boolean getDisplayTicker() {
        return displayTicker;
    }

    public void setDisplayTicker(Boolean displayTicker) {
        this.displayTicker = displayTicker;
    }

    public Boolean getDisplayTimer() {
        return displayTimer;
    }

    public void setDisplayTimer(Boolean displayTimer) {
        this.displayTimer = displayTimer;
    }

    public Boolean getRecording() {
        return recording;
    }

    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    public Object getRecordingDetails() {
        return recordingDetails;
    }

    public void setRecordingDetails(Object recordingDetails) {
        if(recordingDetails != null) this.recordingDetails = recordingDetails;
    }

    public Boolean getScreenShare() {
        return screenShare;
    }

    public void setScreenShare(Boolean screenShare) {
        this.screenShare = screenShare;
    }

    public Boolean getWaitForModerator() {
        return waitForModerator;
    }

    public void setWaitForModerator(Boolean waitForModerator) {
        this.waitForModerator = waitForModerator;
    }

    public Boolean getChat() {
        return chat;
    }

    public void setChat(Boolean chat) {
        this.chat = chat;
    }

    public Boolean getActivitiesButton() {
        return activitiesButton;
    }

    public void setActivitiesButton(Boolean activitiesButton) {
        this.activitiesButton = activitiesButton;
    }

    public Boolean getParticipantsButton() {
        return participantsButton;
    }

    public void setParticipantsButton(Boolean participantsButton) {
        this.participantsButton = participantsButton;
    }

    public Boolean getFloatingLayout() {
        return floatingLayout;
    }

    public void setFloatingLayout(Boolean floatingLayout) {
        this.floatingLayout = floatingLayout;
    }

    public Boolean getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Boolean supervisor) {
        this.supervisor = supervisor;
    }

    public Boolean getPreRecorded() {
        return preRecorded;
    }

    public void setPreRecorded(Boolean preRecorded) {
        this.preRecorded = preRecorded;
    }

    public Object getPreRecordedDetails() {
        return preRecordedDetails;
    }

    public Object getLayoutType() { return layoutType; }

    public void setLayoutType(Object layoutType) { this.layoutType = layoutType; }

    public void setPreRecordedDetails(Object preRecordedDetails) {
        if(preRecordedDetails != null) this.preRecordedDetails = preRecordedDetails;
//        try {
////            Gson gson=new Gson();
////            JsonObject params=gson.fromJson(String.valueOf(preRecordedDetails),JsonObject.class);
////            ObjectMapper objectMapper=new ObjectMapper();
////            objectMapper.registerModule(new JavaTimeModule());
//            byte[] input = Base64.decodeBase64(preRecordedDetails.toString());
//            CustomMultipartFile file = new CustomMultipartFile(input);
//            Files.copy(file.getInputStream(), Paths.get(FILE_DIRECTORY,file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            logger.info("Exception occurred while uploading file to server !");
//            e.printStackTrace();
//        }
    }

    public Object getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(Object landingPage) {
        if(landingPage != null) this.landingPage = landingPage;
    }

    public Boolean getSharePreRecordedVideo() {
        return sharePreRecordedVideo;
    }

    public void setSharePreRecordedVideo(Boolean sharePreRecordedVideo) {
        this.sharePreRecordedVideo = sharePreRecordedVideo;
    }

    @Override
    public String toString() {
        return "SettingsEntity{" +
                "duration=" + duration +
                ", logo=" + logo +
                ", description=" + description +
                ", displayTicker=" + displayTicker +
                ", displayTimer=" + displayTimer +
                ", recording=" + recording +
                ", recordingDetails=" + recordingDetails +
                ", screenShare=" + screenShare +
                ", waitForModerator=" + waitForModerator +
                ", chat=" + chat +
                ", activitiesButton=" + activitiesButton +
                ", participantsButton=" + participantsButton +
                ", floatingLayout=" + floatingLayout +
                ", layoutType=" + layoutType +
                ", supervisor=" + supervisor +
                ", preRecorded=" + preRecorded +
                ", sharePreRecordedVideo=" + sharePreRecordedVideo +
                ", landingPage=" + landingPage +
                ", preRecordedDetails=" + preRecordedDetails +
                '}';
    }
}
