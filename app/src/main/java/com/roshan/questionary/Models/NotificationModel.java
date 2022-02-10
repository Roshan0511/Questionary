package com.roshan.questionary.Models;

public class NotificationModel {
    private String notificationBy;
    private long NotificationAt;
    private String type;
    private String postID;
    private String postedBY;
    private boolean checkOpen;
    private String notificationId;

    public NotificationModel() {
    }

    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }

    public long getNotificationAt() {
        return NotificationAt;
    }

    public void setNotificationAt(long notificationAt) {
        NotificationAt = notificationAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostedBY() {
        return postedBY;
    }

    public void setPostedBY(String postedBY) {
        this.postedBY = postedBY;
    }

    public Boolean getCheckOpen() {
        return checkOpen;
    }

    public void setCheckOpen(Boolean checkOpen) {
        this.checkOpen = checkOpen;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
