package com.jenihaiti.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jenihaiti.com.Adapter.WithdrawHistoryAdapter;
import com.jenihaiti.com.Model.ProfileModel.ProfileModel;
import com.jenihaiti.com.Model.WithdrawalModel.Result;
import com.jenihaiti.com.Model.WithdrawalModel.WithdrawalModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawHistory extends AppCompatActivity {

    PrefManager prefManager;
    Map<String, String> map;

    ShimmerFrameLayout shimmer;

    TextView txtPoints, txtAmount, txtCurrency, txtMinPoints, txtBack, txtToolbarTitle;
    ImageView ivNoRecord;

    LinearLayout lyWithdraw, lyToolbar, lyBack;

    RecyclerView rvHistory;
    WithdrawHistoryAdapter withdrawHistoryAdapter;
    List<Result> historyList;

    TemplateView nativeTemplate;

    String totalPoints = "0";
    double amountEarn = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_history);

        init();
        Profile();
        WithdrawList();

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lyWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("=>min_earning_point", "" + prefManager.getValue("min_earning_point"));
                if (Integer.parseInt(totalPoints) >=
                        Integer.parseInt("" + prefManager.getValue("min_earning_point"))) {
                    startActivity(new Intent(WithdrawHistory.this, WithdrawRequest.class));

                } else {
                    Toasty.info(WithdrawHistory.this,
                            "" + getResources().getString(R.string.withdraw_request_warning),
                            Toasty.LENGTH_LONG).show();
                }
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
            prefManager = new PrefManager(WithdrawHistory.this);
            map = new HashMap<>();
            map = Utility.GetMap(WithdrawHistory.this);

            shimmer = findViewById(R.id.shimmer);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            lyBack = findViewById(R.id.lyBack);
            txtBack = findViewById(R.id.txtBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            txtToolbarTitle.setText("" + getResources().getString(R.string.withdrawal_history));
            txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_gray));
            txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_gray));

            lyWithdraw = findViewById(R.id.lyWithdraw);
            nativeTemplate = findViewById(R.id.nativeTemplate);

            txtPoints = findViewById(R.id.txtPoints);
            txtAmount = findViewById(R.id.txtAmount);
            txtCurrency = findViewById(R.id.txtCurrency);
            txtMinPoints = findViewById(R.id.txtMinPoints);

            rvHistory = findViewById(R.id.rvHistory);
            ivNoRecord = findViewById(R.id.ivNoRecord);

            txtMinPoints.setText(getResources().getString(R.string.minimum)
                    + " " + prefManager.getValue("min_earning_point")
                    + " " + getResources().getString(R.string.minimum_points_required));
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
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

                        txtPoints.setText("" + response.body().getResult().get(0).getTotalPoints());
                        totalPoints = response.body().getResult().get(0).getTotalPoints();

                        PonitsToAmount();

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

    //withdrawal_list API call
    private void WithdrawList() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<WithdrawalModel> call = bookNPlayAPI.withdrawal_list("" + prefManager.getLoginId());
        call.enqueue(new Callback<WithdrawalModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<WithdrawalModel> call, @NonNull Response<WithdrawalModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("withdrawal_list API call : status", "" + response.body().getStatus());

                        if (response.body().getResult().size() > 0) {
                            historyList = new ArrayList<Result>();
                            historyList = response.body().getResult();

                            withdrawHistoryAdapter = new WithdrawHistoryAdapter(WithdrawHistory.this,
                                    historyList);
                            rvHistory.setLayoutManager(new GridLayoutManager(WithdrawHistory.this, 1,
                                    LinearLayoutManager.VERTICAL, false));
                            rvHistory.setItemAnimator(new DefaultItemAnimator());
                            rvHistory.setAdapter(withdrawHistoryAdapter);
                            withdrawHistoryAdapter.notifyDataSetChanged();

                            ivNoRecord.setVisibility(View.GONE);
                            rvHistory.setVisibility(View.VISIBLE);
                        } else {
                            ivNoRecord.setVisibility(View.VISIBLE);
                            rvHistory.setVisibility(View.GONE);
                        }

                    } else {
                        Log.e("withdrawal_list massage", "" + response.body().getMessage());
                        ivNoRecord.setVisibility(View.VISIBLE);
                        rvHistory.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("withdrawal_list API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<WithdrawalModel> call, @NonNull Throwable t) {
                Utility.shimmerHide(shimmer);
                ivNoRecord.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
                Log.e("withdrawal_list API call : Failure", "That didn't work!!!" + t.getMessage());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.shimmerHide(shimmer);
    }

    private void NativeAds() {
        try {
            Log.e("loginID =>", "" + prefManager.getLoginId());
            AdLoader adLoader = new AdLoader.Builder(WithdrawHistory.this, "" + prefManager.getValue("native_adid"))
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