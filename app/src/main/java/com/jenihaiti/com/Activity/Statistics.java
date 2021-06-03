package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jenihaiti.com.Model.ProfileModel.ProfileModel;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Statistics extends AppCompatActivity {

    PrefManager prefManager;
    Map<String, String> map;

    ShimmerFrameLayout shimmer;
    LinearLayout lyBack;

    RoundedImageView rivUser;

    TextView txtUserName, txtRank, txtScore, txtTotalQue, txtCorrectAns, txtIncorrectAns;

    TemplateView nativeTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Utility.screenCapOff(Statistics.this);

        init();
        Get_Profile();

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Log.e("native_ad", "" + prefManager.getValue("native_ad"));
        if (prefManager.getValue("native_ad").equalsIgnoreCase("yes")) {
            nativeTemplate.setVisibility(View.VISIBLE);
            NativeAds();
        } else {
            nativeTemplate.setVisibility(View.GONE);
        }

    }

    public void init() {
        try {
            prefManager = new PrefManager(Statistics.this);
            map = new HashMap<>();
            map = Utility.GetMap(Statistics.this);

            shimmer = findViewById(R.id.shimmer);
            lyBack = findViewById(R.id.lyBack);
            nativeTemplate = findViewById(R.id.nativeTemplate);

            rivUser = findViewById(R.id.rivUser);
            txtUserName = findViewById(R.id.txtUserName);
            txtRank = findViewById(R.id.txtRank);
            txtScore = findViewById(R.id.txtScore);
            txtTotalQue = findViewById(R.id.txtTotalQue);
            txtCorrectAns = findViewById(R.id.txtCorrectAns);
            txtIncorrectAns = findViewById(R.id.txtIncorrectAns);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    //profile API call
    private void Get_Profile() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<ProfileModel> call = bookNPlayAPI.profile("" + prefManager.getLoginId());
        call.enqueue(new Callback<ProfileModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                try {
                    if (response.body().getStatus() == 200) {
                        Log.e("profile API call : status", "" + response.body().getStatus());

                        if (response.body().getResult().size() > 0) {
                            txtUserName.setText("Hello, " + response.body().getResult().get(0).getFirstName());
                            txtRank.setText("" + response.body().getResult().get(0).getRank());
                            txtScore.setText("" + String.format("%.0f",
                                    Double.parseDouble(response.body().getResult().get(0).getTotalScore())));

                            txtTotalQue.setText("" + response.body().getResult().get(0).getQuestionsAttended());
                            txtCorrectAns.setText("" + response.body().getResult().get(0).getCorrectAnswers());
                            txtIncorrectAns.setText("" + (Integer.parseInt(response.body().getResult().get(0).getQuestionsAttended())
                                    - Integer.parseInt(response.body().getResult().get(0).getCorrectAnswers())));

                            if (!response.body().getResult().get(0).getProfileImg().equalsIgnoreCase("")) {
                                Picasso.get().load(response.body().getResult().get(0).getProfileImg())
                                        .into(rivUser);
                            }
                        }

                    } else {
                        Log.e("profile massage", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("profile API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e("profile API call : Failure", "That didn't work!!!" + t.getMessage());
            }
        });
    }

    @Override
    public void onPause() {
        Utility.shimmerHide(shimmer);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (shimmer != null) {
            Utility.shimmerHide(shimmer);
        }
    }

    private void NativeAds() {
        try {
            AdLoader adLoader = new AdLoader.Builder(Statistics.this, "" + prefManager.getValue("native_adid"))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        private ColorDrawable background;

                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            Log.e("Advertiser =>", "" + nativeAd.getAdvertiser());
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                            nativeTemplate.setStyles(styles);
                            nativeTemplate.setNativeAd(nativeAd);
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            // Handle the failure by logging, altering the UI, and so on.
                            Log.e("NativeAd adError=>", "" + adError);
                        }

                        @Override
                        public void onAdClicked() {
                            // Log the click event or other custom behavior.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            Log.e("NativeAd Exception=>", "" + e);
        }
    }

}