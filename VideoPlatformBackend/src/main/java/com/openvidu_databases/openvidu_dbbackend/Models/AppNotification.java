package com.openvidu_databases.openvidu_dbbackend.Models;

public class AppNotification {
    private String userphone;
    private String usertoken;


    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }
}