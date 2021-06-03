package com.jenihaiti.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jenihaiti.com.Adapter.CategoryAdapter;
import com.jenihaiti.com.Model.CategoryModel.CategoryModel;
import com.jenihaiti.com.Model.CategoryModel.Result;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Category extends AppCompatActivity implements View.OnClickListener {

    PrefManager prefManager;
    Map<String, String> map;

    ShimmerFrameLayout shimmer;

    LinearLayout lyBack, lyToolbar, lyCategory, lyAdView;

    TextView txtToolbarTitle;

    RecyclerView rvCategory;
    List<Result> categoryList;
    CategoryAdapter categoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Utility.screenCapOff(Category.this);

        init();
        Category();

        txtToolbarTitle.setText("Category");

        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            lyAdView.setVisibility(View.VISIBLE);
            Admob();
        } else {
            lyAdView.setVisibility(View.GONE);
        }

    }

    public void init() {
        try {
            prefManager = new PrefManager(Category.this);
            map = new HashMap<>();
            map = Utility.GetMap(Category.this);

            shimmer = findViewById(R.id.shimmer);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            lyBack = findViewById(R.id.lyBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            lyAdView = findViewById(R.id.lyAdView);
            lyCategory = findViewById(R.id.lyCategory);
            rvCategory = findViewById(R.id.rvCategory);

            lyBack.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack:
                Category.this.finish();
                break;
        }
    }

    //get_category API call
    private void Category() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<CategoryModel> call = bookNPlayAPI.get_category();
        call.enqueue(new Callback<CategoryModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<CategoryModel> call, @NonNull Response<CategoryModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("status", "" + response.body().getStatus());

                        if (response.body().getResult().size() > 0) {
                            categoryList = new ArrayList<>();
                            categoryList = response.body().getResult();
                            Log.e("categoryList size", "" + categoryList.size());
                            categoryAdapter = new CategoryAdapter(Category.this, categoryList);
                            rvCategory.setLayoutManager(new GridLayoutManager(Category.this,
                                    2, LinearLayoutManager.VERTICAL, false));
                            rvCategory.setHasFixedSize(true);
                            rvCategory.setAdapter(categoryAdapter);
                            rvCategory.setItemAnimator(new DefaultItemAnimator());
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            rvCategory.setVisibility(View.GONE);
                        }

                    } else {
                        Log.e("massage", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("get_category API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(@NonNull Call<CategoryModel> call, @NonNull Throwable t) {
                Log.e("get_category Failure", "That didn't work!!!" + t.getMessage());
                Utility.shimmerHide(shimmer);
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

    private void Admob() {
        try {
            AdView mAdView = new AdView(Category.this);
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

}