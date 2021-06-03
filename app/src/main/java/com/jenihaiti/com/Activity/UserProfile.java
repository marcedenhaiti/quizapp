package com.jenihaiti.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jenihaiti.com.Adapter.RecentQuizAdapter;
import com.jenihaiti.com.Model.ProfileModel.ProfileModel;
import com.jenihaiti.com.Model.RecentQuizModel.RecentQuizModel;
import com.jenihaiti.com.Model.RecentQuizModel.Result;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    PrefManager prefManager;
    Map<String, String> map;

    ShimmerFrameLayout shimmer;

    LinearLayout lyBack, lyNoData, lyAdView;

    RewardedAd mRewardedAd;

    TextView txtUserName, txtAmount, txtCurrency, txtPoints, txtCityName, txtLeaderboard, txtStatistics,
            txtWithdrawal;

    RoundedImageView rivUser;

    RecyclerView rvRecentmatch;
    RecentQuizAdapter recentQuizAdapter;
    List<Result> recentQuizList;

    String totalPoints = "0";
    double amountEarn = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Utility.screenCapOff(UserProfile.this);

        init();
        Profile();

        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            lyAdView.setVisibility(View.VISIBLE);
            Admob();
        } else {
            lyAdView.setVisibility(View.GONE);
        }

    }

    private void init() {
        try {
            prefManager = new PrefManager(UserProfile.this);
            map = new HashMap<>();
            map = Utility.GetMap(UserProfile.this);

            shimmer = findViewById(R.id.shimmer);

            lyBack = findViewById(R.id.lyBack);
            lyNoData = findViewById(R.id.lyNoData);
            lyAdView = findViewById(R.id.lyAdView);

            rivUser = findViewById(R.id.rivUser);
            txtUserName = findViewById(R.id.txtUserName);
            txtCityName = findViewById(R.id.txtCityName);
            txtLeaderboard = findViewById(R.id.txtLeaderboard);
            txtStatistics = findViewById(R.id.txtStatistics);
            txtWithdrawal = findViewById(R.id.txtWithdrawal);
            txtPoints = findViewById(R.id.txtPoints);
            txtAmount = findViewById(R.id.txtAmount);
            txtCurrency = findViewById(R.id.txtCurrency);

            rvRecentmatch = findViewById(R.id.rvRecentmatch);

            lyBack.setOnClickListener(this);
            txtStatistics.setOnClickListener(this);
            txtWithdrawal.setOnClickListener(this);
            txtLeaderboard.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack:
                finish();
                break;

            case R.id.txtLeaderboard:
                startActivity(new Intent(UserProfile.this, UserLeaderBoard.class));
                break;

            case R.id.txtStatistics:
                startActivity(new Intent(UserProfile.this, Statistics.class));
                break;

            case R.id.txtWithdrawal:
                if (mRewardedAd != null) {
                    mRewardedAd.show(UserProfile.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                        }
                    });
                } else {
                    startActivity(new Intent(UserProfile.this, WithdrawHistory.class));
                }
                break;
        }
    }

    //profile API call
    private void Profile() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<ProfileModel> call = bookNPlayAPI.profile("" + prefManager.getLoginId());
        call.enqueue(new Callback<ProfileModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("profile API call : status", "" + response.body().getStatus());

                        if (response.body().getResult().size() > 0) {
                            txtUserName.setText("" + response.body().getResult().get(0).getFirstName());

                            if (!response.body().getResult().get(0).getBiodata().equalsIgnoreCase("")) {
                                txtCityName.setText("" + response.body().getResult().get(0).getBiodata());
                            } else {
                                txtCityName.setVisibility(View.GONE);
                            }

                            if (!response.body().getResult().get(0).getProfileImg().equalsIgnoreCase("")) {
                                Picasso.get().load(response.body().getResult().get(0).getProfileImg())
                                        .into(rivUser);
                            }

                            txtPoints.setText("" + response.body().getResult().get(0).getTotalPoints());
                            totalPoints = response.body().getResult().get(0).getTotalPoints();
                            PonitsToAmount();

                            recentQuiz();
                        }

                    } else {
                        Log.e("profile massage", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("profile API error==>", "" + e);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e("profile API call : Failure", "That didn't work!!!" + t.getMessage());
            }
        });
    }

    private void PonitsToAmount() {
        Log.e("totalPoints", "" + totalPoints);
        Log.e("earning_point", "" + prefManager.getValue("earning_point"));

        if (Integer.parseInt(totalPoints) < Integer.parseInt(prefManager.getValue("earning_point"))) {
            txtAmount.setText("0");
        } else {
            amountEarn = (((Double.parseDouble(totalPoints)) * (Double.parseDouble(prefManager.getValue("earning_amount"))))
                    / (Double.parseDouble(prefManager.getValue("earning_point"))));
            txtAmount.setText("" + amountEarn);
        }
        txtCurrency.setText("(" + prefManager.getValue("currency") + ")");
    }

    private void recentQuiz() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<RecentQuizModel> call = bookNPlayAPI.RecentQuizByUser("" + prefManager.getLoginId());
        call.enqueue(new Callback<RecentQuizModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<RecentQuizModel> call, @NonNull Response<RecentQuizModel> response) {
                try {
                    if (response.body().getStatus() == 200) {
                        Log.e("RecentQuizByUser API call : status", "" + response.body().getStatus());

                        if (response.body().getResult().size() > 0) {
                            recentQuizList = new ArrayList<Result>();
                            recentQuizList = response.body().getResult();
                            Log.e("recentQuizList size ==>", "" + recentQuizList.size());

                            recentQuizAdapter = new RecentQuizAdapter(UserProfile.this, recentQuizList);
                            rvRecentmatch.setLayoutManager(new GridLayoutManager(UserProfile.this, 1,
                                    LinearLayoutManager.VERTICAL, false));
                            rvRecentmatch.setHasFixedSize(true);
                            rvRecentmatch.setAdapter(recentQuizAdapter);
                            rvRecentmatch.setItemAnimator(new DefaultItemAnimator());
                            recentQuizAdapter.notifyDataSetChanged();

                            lyNoData.setVisibility(View.GONE);
                            rvRecentmatch.setVisibility(View.VISIBLE);
                        } else {
                            lyNoData.setVisibility(View.VISIBLE);
                            rvRecentmatch.setVisibility(View.GONE);
                        }

                    } else {
                        Log.e("RecentQuizByUser massage", "" + response.body().getMessage());
                        lyNoData.setVisibility(View.VISIBLE);
                        rvRecentmatch.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("RecentQuizByUser API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<RecentQuizModel> call, @NonNull Throwable t) {
                Utility.shimmerHide(shimmer);
                lyNoData.setVisibility(View.VISIBLE);
                rvRecentmatch.setVisibility(View.GONE);
                Log.e("RecentQuizByUser API call : Failure", "That didn't work!!!" + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        Utility.shimmerHide(shimmer);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Utility.shimmerHide(shimmer);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("reward_ad", "" + prefManager.getValue("reward_ad"));
        if (prefManager.getValue("reward_ad").equalsIgnoreCase("yes")) {
            mRewardedAd = null;
            RewardedVideoAd();
        }
    }

    private void Admob() {
        try {
            AdView mAdView = new AdView(UserProfile.this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            mAdView.setAdUnitId("" + prefManager.getValue("banner_adid"));
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("onAdFailedToLoad =>", "" + loadAdError.toString());
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
            mAdView.loadAd(adRequest);
            lyAdView.addView(mAdView);
        } catch (Exception e) {
            Log.e("AdView Exception=>", "" + e.getMessage());
        }
    }

    private void RewardedVideoAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            Log.e("Ad failed to show.", "" + adError.toString());
                            mRewardedAd = null;
                            startActivity(new Intent(UserProfile.this, WithdrawHistory.class));
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            Log.e("TAG", "Ad was shown.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            startActivity(new Intent(UserProfile.this, WithdrawHistory.class));
                            mRewardedAd = null;
                            Log.e("TAG", "Ad was dismissed.");
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }
                    };

            mRewardedAd.load(UserProfile.this, "" + prefManager.getValue("reward_adid"),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            super.onAdLoaded(rewardedAd);
                            mRewardedAd = rewardedAd;
                            mRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                        }
                    });

        } catch (Exception e) {
            Log.e("RewardAd Exception =>", "" + e);
        }
    }

}