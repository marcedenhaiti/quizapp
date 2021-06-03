
package com.jenihaiti.com.Model.LeaderBoardModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("rank")
    @Expose
    private String rank;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("total_score")
    @Expose
    private String totalScore;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("profile_img")
    @Expose
    private String profileImg;
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("user_total_score")
    @Expose
    private Integer userTotalScore;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserTotalScore() {
        return userTotalScore;
    }

    public void setUserTotalScore(Integer userTotalScore) {
        this.userTotalScore = userTotalScore;
    }

}
