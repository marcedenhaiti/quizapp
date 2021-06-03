package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
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

import com.jenihaiti.com.Adapter.TopContestantAdapter;
import com.jenihaiti.com.Model.LeaderBoardModel.LeaderBoardModel;
import com.jenihaiti.com.Model.LeaderBoardModel.Result;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderBoard extends AppCompatActivity {

    private PrefManager prefManager;
    Map<String, String> map;

    private ShimmerFrameLayout shimmer;

    LinearLayout lyBack, lyToolbar;

    RoundedImageView rivFirst, rivSecond, rivThird;

    TextView txtToolbarTitle, txtPointsFirst, txtPointsSecond, txtPointsThird,
            txtFirstName, txtSecondName, txtThirdName;

    private RecyclerView rvTopContestant;
    private List<Result> topContenstantList;
    private TopContestantAdapter topContestantAdapter;

    TemplateView nativeTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        init();
        Top_Contestatnt();

        txtToolbarTitle.setText("" + getResources().getString(R.string.leaderboard));

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeaderBoard.this.finish();
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

    private void init() {
        try {
            prefManager = new PrefManager(LeaderBoard.this);
            map = new HashMap<>();
            map = Utility.GetMap(LeaderBoard.this);

            shimmer = findViewById(R.id.shimmer);

            nativeTemplate = findViewById(R.id.nativeTemplate);
            lyBack = findViewById(R.id.lyBack);
            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            rvTopContestant = findViewById(R.id.rvTopContestant);

            rivFirst = findViewById(R.id.rivFirst);
            rivSecond = findViewById(R.id.rivSecond);
            rivThird = findViewById(R.id.rivThird);
            txtFirstName = findViewById(R.id.txtFirstName);
            txtSecondName = findViewById(R.id.txtSecondName);
            txtThirdName = findViewById(R.id.txtThirdName);
            txtPointsFirst = findViewById(R.id.txtPointsFirst);
            txtPointsSecond = findViewById(R.id.txtPointsSecond);
            txtPointsThird = findViewById(R.id.txtPointsThird);
        } catch (Exception e) {
            Log.e("init Exception =>", "" + e);
        }
    }

    //getLeaderBoard API call
    private void Top_Contestatnt() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<LeaderBoardModel> call = bookNPlayAPI.getLeaderBoard("" + prefManager.getLoginId(), "all");
        call.enqueue(new Callback<LeaderBoardModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<LeaderBoardModel> call, @NonNull Response<LeaderBoardModel> response) {
                try {
                    Log.e("getLeaderBoard API call : status", "" + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            topContenstantList = new ArrayList<Result>();
                            topContenstantList = response.body().getResult();
                            Log.e("topContenstantList size", "" + topContenstantList.size());

                            if (topContenstantList.size() > 3) {
                                topContestantAdapter = new TopContestantAdapter(LeaderBoard.this, topContenstantList);
                                GridLayoutManager gridLayoutManager =
                                        new GridLayoutManager(LeaderBoard.this, 1,
                                                LinearLayoutManager.VERTICAL, false);
                                rvTopContestant.setLayoutManager(gridLayoutManager);
                                rvTopContestant.setHasFixedSize(true);
                                rvTopContestant.setAdapter(topContestantAdapter);
                                rvTopContestant.setItemAnimator(new DefaultItemAnimator());
                                topContestantAdapter.notifyDataSetChanged();
                            } else {
                                rvTopContestant.setVisibility(View.GONE);
                            }

                            txtFirstName.setText("" + topContenstantList.get(0).getName());
                            txtPointsFirst.setText("" + String.format("%.0f",
                                    Double.parseDouble("" + topContenstantList.get(0).getUserTotalScore())));
                            if (!TextUtils.isEmpty(topContenstantList.get(0).getProfileImg())) {
                                Log.e("1st image ==>", "" + topContenstantList.get(0).getProfileImg());
                                Picasso.get().load(topContenstantList.get(0).getProfileImg())
                                        .into(rivFirst);
                            }


                            txtSecondName.setText("" + topContenstantList.get(1).getName());
                            txtPointsSecond.setText("" + String.format("%.0f",
                                    Double.parseDouble("" + topContenstantList.get(1).getUserTotalScore())));
                            if (!TextUtils.isEmpty(topContenstantList.get(1).getProfileImg())) {
                                Log.e("2nd image ==>", "" + topContenstantList.get(1).getProfileImg());
                                Picasso.get().load(topContenstantList.get(1).getProfileImg())
                                        .into(rivSecond);
                            }

                            txtThirdName.setText("" + topContenstantList.get(2).getName());
                            txtPointsThird.setText("" + String.format("%.0f",
                                    Double.parseDouble("" + topContenstantList.get(2).getUserTotalScore())));
                            if (!TextUtils.isEmpty(topContenstantList.get(2).getProfileImg())) {
                                Log.e("3rd image ==>", "" + topContenstantList.get(2).getProfileImg());
                                Picasso.get().load(topContenstantList.get(2).getProfileImg())
                                        .into(rivThird);
                            }
                        }

                    } else {
                        Log.e("getLeaderBoard massage", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("getLeaderBoard API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<LeaderBoardModel> call, @NonNull Throwable t) {
                Log.e("getLeaderBoard API call : Failure", "That didn't work!!!" + t.getMessage());
                Utility.shimmerHide(shimmer);
            }
        });
    }

    @Override
    public void onPause() {
        Utility.shimmerHide(shimmer);
        super.onPause();
    }

    private void NativeAds() {
        try {
            Log.e("loginID =>", "" + prefManager.getLoginId());
            AdLoader adLoader = new AdLoader.Builder(LeaderBoard.this, "" + prefManager.getValue("native_adid"))
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.shimmerHide(shimmer);
    }

}