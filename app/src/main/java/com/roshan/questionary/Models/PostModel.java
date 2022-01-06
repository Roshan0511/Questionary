package com.roshan.questionary.Models;

public class PostModel {
    private String name;
    private String profilePic;
    private String questionImage;
    private String questionTxt;
    private int likeCount;
    private int commentCount;
    private String userId;
    private String postId;
    private long time;

    public PostModel() {
    }

    public PostModel(String name, String profilePic, String questionImage, String questionTxt, int likeCount, int commentCount, long time) {
        this.name = name;
        this.profilePic = profilePic;
        this.questionImage = questionImage;
        this.questionTxt = questionTxt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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
}
