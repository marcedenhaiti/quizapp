
package com.jenihaiti.com.Model.RecentQuizModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("level_id")
    @Expose
    private String levelId;
    @SerializedName("total_questions")
    @Expose
    private String totalQuestions;
    @SerializedName("questions_attended")
    @Expose
    private String questionsAttended;
    @SerializedName("correct_answers")
    @Expose
    private String correctAnswers;
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("is_unlock")
    @Expose
    private String isUnlock;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("level_name")
    @Expose
    private String levelName;
    @SerializedName("win_question_count")
    @Expose
    private String winQuestionCount;
    @SerializedName("profile_img")
    @Expose
    private String profileImg;
    @SerializedName("win_status")
    @Expose
    private String winStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getQuestionsAttended() {
        return questionsAttended;
    }

    public void setQuestionsAttended(String questionsAttended) {
        this.questionsAttended = questionsAttended;
    }

    public String getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsUnlock() {
        return isUnlock;
    }

    public void setIsUnlock(String isUnlock) {
        this.isUnlock = isUnlock;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getWinQuestionCount() {
        return winQuestionCount;
    }

    public void setWinQuestionCount(String winQuestionCount) {
        this.winQuestionCount = winQuestionCount;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getWinStatus() {
        return winStatus;
    }

    public void setWinStatus(String winStatus) {
        this.winStatus = winStatus;
    }

}
