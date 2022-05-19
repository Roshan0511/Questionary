package com.roshan.questionnaire.Models;

public class PostModel {
    private String name;
    private String profilePic;
    private String questionImage;
    private String questionTxt;
    private String userId;
    private String postId;
    private String subject;
    private long time;

    public PostModel() {
    }

    public PostModel(String name, String profilePic, String questionImage, String questionTxt, long time) {
        this.name = name;
        this.profilePic = profilePic;
        this.questionImage = questionImage;
        this.questionTxt = questionTxt;
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getQuestionTxt() {
        return questionTxt;
    }

    public void setQuestionTxt(String questionTxt) {
        this.questionTxt = questionTxt;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
