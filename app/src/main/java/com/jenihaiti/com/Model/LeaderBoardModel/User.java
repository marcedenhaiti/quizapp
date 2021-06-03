
package com.jenihaiti.com.Model.LeaderBoardModel;

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
    private String totalScore;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("profile_img")
    @Expose
    private String profileImg;

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

}
