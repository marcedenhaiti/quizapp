package com.jenihaiti.com.Webservice;

import com.jenihaiti.com.Model.CategoryModel.CategoryModel;
import com.jenihaiti.com.Model.ForgotPassModel.ForgotPassModel;
import com.jenihaiti.com.Model.GeneralSettingsModel.GeneralSettingsModel;
import com.jenihaiti.com.Model.LeaderBoardModel.LeaderBoardModel;
import com.jenihaiti.com.Model.LevelModel.LevelModel;
import com.jenihaiti.com.Model.LoginRegiModel.LoginRegiModel;
import com.jenihaiti.com.Model.ProfileModel.ProfileModel;
import com.jenihaiti.com.Model.QuestionModel.QuestionModel;
import com.jenihaiti.com.Model.RecentQuizModel.RecentQuizModel;
import com.jenihaiti.com.Model.SuccessModel.SuccessModel;
import com.jenihaiti.com.Model.TodayLeaderBoardModel.TodayLeaderBoardModel;
import com.jenihaiti.com.Model.WithdrawalModel.WithdrawalModel;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppAPI {

    @GET("genaral_setting")
    Call<GeneralSettingsModel> genaral_setting();

    @FormUrlEncoded
    @POST("checkStatus")
    Call<SuccessModel> checkStatus(@Field("purchase_code") String purchase_code,
                                   @Field("package_name") String package_name);

    @FormUrlEncoded
    @POST("login")
    Call<LoginRegiModel> login(@Field("email") String email,
                               @Field("password") String password,
                               @Field("type") String type,
                               @Field("device_token") String device_token);

    @Multipart
    @POST("login")
    Call<LoginRegiModel> login(@Part("first_name") RequestBody first_name,
                               @Part("email") RequestBody email,
                               @Part("password") RequestBody password,
                               @Part("type") RequestBody type,
                               @Part("device_token") RequestBody device_token,
                               @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("registration")
    Call<LoginRegiModel> registration(@Field("email") String email,
                                      @Field("password") String password,
                                      @Field("first_name") String first_name,
                                      @Field("mobile_number") String mobile_number,
                                      @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ForgotPassModel> forgotpassword(@HeaderMap Map<String, String> headers,
                                         @Field("email") String email);

    @FormUrlEncoded
    @POST("profile")
    Call<ProfileModel> profile(@Field("user_id") String user_id);


    @GET("get_category")
    Call<CategoryModel> get_category();

    @FormUrlEncoded
    @POST("get_lavel")
    Call<LevelModel> get_lavel(@Field("category_id") String category_id,
                               @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_question_by_lavel")
    Call<QuestionModel> get_question_by_lavel(@Field("category_id") String category_id,
                                              @Field("level_id") String level_id);

    @FormUrlEncoded
    @POST("save_question_report")
    Call<SuccessModel> save_question_report(@Field("category_id") String category_id,
                                            @Field("level_id") String level_id,
                                            @Field("questions_attended") String questions_attended,
                                            @Field("total_questions") String total_questions,
                                            @Field("correct_answers") String correct_answers,
                                            @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("getLeaderBoard")
    Call<LeaderBoardModel> getLeaderBoard(@Field("user_id") String user_id,
                                          @Field("type") String type);

    @FormUrlEncoded
    @POST("getTodayLeaderBoard")
    Call<TodayLeaderBoardModel> getTodayLeaderBoard(@Field("level_id") String level_id,
                                                    @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("RecentQuizByUser")
    Call<RecentQuizModel> RecentQuizByUser(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("withdrawal_list")
    Call<WithdrawalModel> withdrawal_list(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("withdrawal_request")
    Call<SuccessModel> withdrawal_request(@Field("user_id") String user_id,
                                          @Field("payment_detail") String payment_detail,
                                          @Field("payment_type") String payment_type);

}
