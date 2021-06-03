
package com.jenihaiti.com.Model.TodayLeaderBoardModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("rank")
    @Expose
    private String rank;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("total_score")
    @Expose
    private Integer totalScore;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("profile_img")
    @Expose
    private String profileImg;
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("is_unlock")
    @Expose
    private String isUnlock;

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

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
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

    public String getIsUnlock() {
        return isUnlock;
    }

    public void setIsUnlock(String isUnlock) {
        this.isUnlock = isUnlock;
    }

}
