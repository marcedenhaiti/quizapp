package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jenihaiti.com.Adapter.LevelResultAdapter;
import com.jenihaiti.com.BuildConfig;
import com.jenihaiti.com.Model.TodayLeaderBoardModel.Result;
import com.jenihaiti.com.Model.TodayLeaderBoardModel.TodayLeaderBoardModel;
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
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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

public class LevelResult extends AppCompatActivity implements View.OnClickListener {

    PrefManager prefManager;
    Map<String, String> map;

    ShimmerFrameLayout shimmer;

    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;

    RecyclerView rvContestantResult;
    List<Result> todayList;
    LevelResultAdapter levelResultAdapter;

    RoundedImageView rivContestant;
    LinearLayout lyHome, lyAdView, lyShareResult, lyPlayNextLevel;

    TextView txtPlayNextLevel, txtPointEarn, txtTotalLevel, txtLevelNumber, txtTotalScore, txtUsername,
            txtCurrentRank, txtShareResult, txtGreetings;

    String levelID, currentLevel, totalLevel, rewardTYPE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_result);

        init();
        GetTodayLeaderBoard();

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
            Intent intent = getIntent();
            if (intent.hasExtra("levelID")) {
                levelID = intent.getStringExtra("levelID");
                currentLevel = intent.getStringExtra("currentLevel");
                totalLevel = intent.getStringExtra("TotalLevel");
                Log.e("Result levelID =>", "" + levelID);
                Log.e("Result totalLevel =>", "" + totalLevel);
                Log.e("Result currentLevel =>", "" + currentLevel);
            }

            prefManager = new PrefManager(LevelResult.this);
            map = new HashMap<>();
            map = Utility.GetMap(LevelResult.this);

            shimmer = findViewById(R.id.shimmer);
            lyHome = findViewById(R.id.lyHome);
            lyAdView = findViewById(R.id.lyAdView);
            lyShareResult = findViewById(R.id.lyShareResult);
            lyPlayNextLevel = findViewById(R.id.lyPlayNextLevel);

            rvContestantResult = findViewById(R.id.rvContestantResult);
            rivContestant = findViewById(R.id.rivContestant);

            txtGreetings = findViewById(R.id.txtGreetings);
            txtUsername = findViewById(R.id.txtUserName);
            txtPlayNextLevel = findViewById(R.id.txtPlayNextLevel);
            txtTotalLevel = findViewById(R.id.txtTotalLevel);
            txtLevelNumber = findViewById(R.id.txtLevelNumber);
            txtTotalScore = findViewById(R.id.txtTotalScore);
            txtPointEarn = findViewById(R.id.txtPointEarn);
            txtCurrentRank = findViewById(R.id.txtCurrentRank);
            txtShareResult = findViewById(R.id.txtShareResult);

            lyHome.setOnClickListener(this);
            txtPlayNextLevel.setOnClickListener(this);
            txtShareResult.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPlayNextLevel:
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(LevelResult.this);
                } else {
                    Log.e("TAG", "The interstitial ad wasn't ready yet.");
                    startActivity(new Intent(LevelResult.this, LevelSelection.class));
                    finish();
                }
                break;

            case R.id.txtShareResult:
                if (mRewardedAd != null) {
                    rewardTYPE = "Share";
                    mRewardedAd.show(LevelResult.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                        }
                    });
                } else {
                    ShareOnShare();
                }
                break;

            case R.id.lyHome:
                if (mRewardedAd != null) {
                    rewardTYPE = "Home";
                    mRewardedAd.show(LevelResult.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                        }
                    });
                } else {
                    Log.e("TAG", "The interstitial ad wasn't ready yet.");
                    startActivity(new Intent(LevelResult.this, MainActivity.class));
                    finish();
                }
                break;
        }
    }

    //getTodayLeaderBoard API
    private void GetTodayLeaderBoard() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<TodayLeaderBoardModel> call = bookNPlayAPI.getTodayLeaderBoard(levelID, "" + prefManager.getLoginId());
        call.enqueue(new Callback<TodayLeaderBoardModel>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<TodayLeaderBoardModel> call, @NonNull Response<TodayLeaderBoardModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("getTodayLeaderBoard : status", "" + response.body().getStatus());

                        todayList = new ArrayList<Result>();
                        todayList = response.body().getResult();
                        Log.e("todayList size ==>", "" + todayList.size());

                        if (response.body().getUser().getIsUnlock().equalsIgnoreCase("1")) {
                            txtGreetings.setText("Félicitations! \n Vous avez terminé le niveau");
                        } else {
                            txtGreetings.setText("Oops!!! \n Vous n\'avez pas terminé le niveau");
                        }
                        txtLevelNumber.setText("" + currentLevel + " / ");
                        txtTotalLevel.setText("" + totalLevel);
                        txtCurrentRank.setText("" + response.body().getUser().getRank());
                        txtTotalScore.setText("" + String.format("%.0f",
                                Double.parseDouble("" + response.body().getUser().getTotalScore())));
                        txtPointEarn.setText("" + String.format("%.0f",
                                Double.parseDouble("" + response.body().getUser().getScore())) + "+ Point Earned");

                        if (!TextUtils.isEmpty(response.body().getUser().getProfileImg())) {
                            Picasso.get().load(response.body().getUser().getProfileImg())
                                    .into(rivContestant);
                        }

                        Result();

                    }
                } catch (Exception e) {
                    Log.e("getTodayLeaderBoard API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<TodayLeaderBoardModel> call, @NonNull Throwable t) {
                Log.e("getTodayLeaderBoard : Failure", "That didn't work!!!" + t.getMessage());
                Utility.shimmerHide(shimmer);
            }
        });
    }

    private void Result() {
        levelResultAdapter = new LevelResultAdapter(LevelResult.this, todayList,
                "" + prefManager.getLoginId());
        rvContestantResult.setLayoutManager(new GridLayoutManager(LevelResult.this, 1,
                LinearLayoutManager.VERTICAL, false));
        rvContestantResult.setHasFixedSize(true);
        rvContestantResult.setAdapter(levelResultAdapter);
        rvContestantResult.setItemAnimator(new DefaultItemAnimator());
        levelResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("reward_ad", "" + prefManager.getValue("reward_ad"));
        if (prefManager.getValue("reward_ad").equalsIgnoreCase("yes")) {
            rewardTYPE = "";
            mRewardedAd = null;
            RewardedVideoAd();
        }
        Log.e("interstital_ad", "" + prefManager.getValue("interstital_ad"));
        if (prefManager.getValue("interstital_ad").equalsIgnoreCase("yes")) {
            mInterstitialAd = null;
            InterstitialAd();
        }
    }

    @Override
    public void onPause() {
        Utility.shimmerHide(shimmer);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Log.e("interstital_ad", "" + prefManager.getValue("interstital_ad"));
        if (prefManager.getValue("interstital_ad").equalsIgnoreCase("yes")) {
            mInterstitialAd.show(LevelResult.this);
        } else {
            startActivity(new Intent(LevelResult.this, LevelSelection.class));
            finish();
        }
    }

    public void ShareOnShare() {
        String shareMessage = "\n\nLet me recommend you this application : \n"
                + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "";

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My scorecard");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "\nName : " + todayList.get(0).getName() +
                "\nRank : " + todayList.get(0).getRank() + "\nLevel : " + currentLevel +
                "\nLevel Score : " + todayList.get(0).getScore() + shareMessage);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        try {
            startActivity(Intent.createChooser(shareIntent, "Share with"));
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("ActivityNotF", "" + ex.getMessage());
        }
    }

    private void Admob() {
        try {
            AdView mAdView = new AdView(LevelResult.this);
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

    private void InterstitialAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            Log.e("TAG", "InterstitialAd failed to show. " + adError.toString());
                            startActivity(new Intent(LevelResult.this, LevelSelection.class));
                            finish();
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            Log.e("TAG", "InterstitialAd was shown. ");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            Log.e("TAG", "InterstitialAd was dismissed. ");
                            startActivity(new Intent(LevelResult.this, LevelSelection.class));
                            finish();
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                            Log.e("TAG", "InterstitialAd onAdImpression. ");
                        }
                    };

            mInterstitialAd.load(this, "" + prefManager.getValue("interstital_adid"),
                    adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                            Log.e("onAdLoaded", "");
                            mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.e("loadAdError", "" + loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        } catch (Exception e) {
            Log.e("", "InterstitialAd Exception => " + e);
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
                            if (rewardTYPE.equalsIgnoreCase("Share")) {
                                ShareOnShare();
                            } else if (rewardTYPE.equalsIgnoreCase("Home")) {
                                startActivity(new Intent(LevelResult.this, MainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            Log.e("TAG", "Ad was shown.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mRewardedAd = null;
                            Log.e("TAG", "Ad was dismissed.");
                            if (rewardTYPE.equalsIgnoreCase("Share")) {
                                ShareOnShare();
                            } else if (rewardTYPE.equalsIgnoreCase("Home")) {
                                startActivity(new Intent(LevelResult.this, MainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                            Log.e("TAG", "onAdImpression.");
                        }
                    };

            mRewardedAd.load(LevelResult.this, "" + prefManager.getValue("reward_adid"),
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