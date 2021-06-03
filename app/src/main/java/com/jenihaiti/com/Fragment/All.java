package com.jenihaiti.com.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class All extends Fragment {

    PrefManager prefManager;
    ProgressDialog progressDialog;
    Map<String, String> map;

    private View root;

    LinearLayout lyRecycler, lyUserPos, lyNoData;
    ShimmerFrameLayout shimmer;

    RoundedImageView rivFirst, rivSecond, rivThird, rivUser;

    TextView txtPointsFirst, txtPointsSecond, txtPointsThird,
            txtFirstName, txtSecondName, txtThirdName, txtUserName, txtUserRank, txtUserPoints;

    private RecyclerView rvAllContestant;
    private List<Result> topContenstantList;
    private TopContestantAdapter topContestantAdapter;

    TemplateView nativeTemplate;

    public All() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.subfragment_all, container, false);

        init();
        TopContestatnt();

        Log.e("native_ad", "" + prefManager.getValue("native_ad"));
        if (prefManager.getValue("native_ad").equalsIgnoreCase("yes")) {
            nativeTemplate.setVisibility(View.VISIBLE);
            NativeAds();
        } else {
            nativeTemplate.setVisibility(View.GONE);
        }

        return root;
    }

    private void init() {
        try {
            map = new HashMap<>();
            map = Utility.GetMap(getActivity());
            prefManager = new PrefManager(getActivity());
            progressDialog = new ProgressDialog(getActivity(), R.style.AlertDialogDanger);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);

            shimmer = root.findViewById(R.id.shimmer);
            lyNoData = root.findViewById(R.id.lyNoData);
            lyUserPos = root.findViewById(R.id.lyUserPos);
            lyRecycler = root.findViewById(R.id.lyRecycler);
            nativeTemplate = root.findViewById(R.id.nativeTemplate);

            rvAllContestant = root.findViewById(R.id.rvAllContestant);

            rivFirst = root.findViewById(R.id.rivFirst);
            rivSecond = root.findViewById(R.id.rivSecond);
            rivThird = root.findViewById(R.id.rivThird);
            rivUser = root.findViewById(R.id.rivUser);

            txtUserName = root.findViewById(R.id.txtUserName);
            txtUserRank = root.findViewById(R.id.txtUserRank);
            txtUserPoints = root.findViewById(R.id.txtUserPoints);
            txtFirstName = root.findViewById(R.id.txtFirstName);
            txtSecondName = root.findViewById(R.id.txtSecondName);
            txtThirdName = root.findViewById(R.id.txtThirdName);
            txtPointsFirst = root.findViewById(R.id.txtPointsFirst);
            txtPointsSecond = root.findViewById(R.id.txtPointsSecond);
            txtPointsThird = root.findViewById(R.id.txtPointsThird);
        } catch (Exception e) {
            Log.e("ALL init Exception =>", "" + e);
        }
    }

    //getLeaderBoard API call
    private void TopContestatnt() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<LeaderBoardModel> call = bookNPlayAPI.getLeaderBoard("" + prefManager.getLoginId(), "all");
        call.enqueue(new Callback<LeaderBoardModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<LeaderBoardModel> call, @NonNull Response<LeaderBoardModel> response) {
                try {
                    Log.e("ALL API call : status", "" + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        txtUserRank.setText("" + response.body().getUser().getRank());
                        txtUserName.setText("" + response.body().getUser().getFullname());
                        txtUserPoints.setText("" + String.format("%.0f",
                                Double.parseDouble(response.body().getUser().getTotalScore())));
                        if (!TextUtils.isEmpty(response.body().getUser().getProfileImg())) {
                            Picasso.get().load(response.body().getUser().getProfileImg())
                                    .into(rivUser);
                        }

                        if (response.body().getResult().size() > 0) {
                            lyNoData.setVisibility(View.GONE);

                            topContenstantList = new ArrayList<Result>();
                            topContenstantList = response.body().getResult();
                            Log.e("ALL size", "" + topContenstantList.size());

                            if (topContenstantList.size() == 1) {
                                txtFirstName.setText("" + topContenstantList.get(0).getName());
                                txtPointsFirst.setText("" + String.format("%.0f",
                                        Double.parseDouble(topContenstantList.get(0).getScore())));
                                if (!TextUtils.isEmpty(topContenstantList.get(0).getProfileImg())) {
                                    Picasso.get().load(topContenstantList.get(0).getProfileImg())
                                            .into(rivFirst);
                                }

                            } else if (topContenstantList.size() == 2) {
                                txtFirstName.setText("" + topContenstantList.get(0).getName());
                                txtPointsFirst.setText("" + String.format("%.0f",
                                        Double.parseDouble(topContenstantList.get(0).getScore())));
                                if (!TextUtils.isEmpty(topContenstantList.get(0).getProfileImg())) {
                                    Picasso.get().load(topContenstantList.get(0).getProfileImg())
                                            .into(rivFirst);
                                }

                                txtSecondName.setText("" + topContenstantList.get(1).getName());
                                txtPointsSecond.setText("" + String.format("%.0f",
                                        Double.parseDouble(topContenstantList.get(1).getScore())));
                                if (!TextUtils.isEmpty(topContenstantList.get(1).getProfileImg())) {
                                    Picasso.get().load(topContenstantList.get(1).getProfileImg())
                                            .into(rivSecond);
                                }

                            } else {
                                txtFirstName.setText("" + topContenstantList.get(0).getName());
                                txtPointsFirst.setText("" + String.format("%.0f",
                                        Double.parseDouble(topContenstantList.get(0).getScore())));
                                if (!TextUtils.isEmpty(topContenstantList.get(0).getProfileImg())) {
                                    Picasso.get().load(topContenstantList.get(0).getProfileImg())
                                            .into(rivFirst);
                                }

                                txtSecondName.setText("" + topContenstantList.get(1).getName());
                                txtPointsSecond.setText("" + String.format("%.0f",
                                        Double.parseDouble(topContenstantList.get(1).getScore())));
                                if (!TextUtils.isEmpty(topContenstantList.get(1).getProfileImg())) {
                                    Picasso.get().load(topContenstantList.get(1).getProfileImg())
                                            .into(rivSecond);
                                }

                                txtThirdName.setText("" + topContenstantList.get(2).getName());
                                txtPointsThird.setText("" + String.format("%.0f",
                                        Double.parseDouble(topContenstantList.get(2).getScore())));
                                if (!TextUtils.isEmpty(topContenstantList.get(2).getProfileImg())) {
                                    Picasso.get().load(topContenstantList.get(2).getProfileImg())
                                            .into(rivThird);
                                }
                            }

                            if (topContenstantList.size() > 3) {
                                topContestantAdapter = new TopContestantAdapter(getActivity(), topContenstantList);
                                GridLayoutManager gridLayoutManager =
                                        new GridLayoutManager(getActivity(), 1,
                                                LinearLayoutManager.VERTICAL, false);
                                rvAllContestant.setLayoutManager(gridLayoutManager);
                                rvAllContestant.setHasFixedSize(true);
                                rvAllContestant.setAdapter(topContestantAdapter);
                                rvAllContestant.setItemAnimator(new DefaultItemAnimator());
                                topContestantAdapter.notifyDataSetChanged();
                            } else {
                                rvAllContestant.setVisibility(View.GONE);
                            }

                        } else {
                            lyNoData.setVisibility(View.VISIBLE);
                            lyUserPos.setVisibility(View.GONE);
                        }

                    } else {
                        Log.e("ALL massage", "" + response.body().getMessage());
                        lyNoData.setVisibility(View.VISIBLE);
                        lyUserPos.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("ALL API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<LeaderBoardModel> call, @NonNull Throwable t) {
                Log.e("ALL API call : Failure", "That didn't work!!!" + t.getMessage());
                lyUserPos.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
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
            AdLoader adLoader = new AdLoader.Builder(getActivity(), "" + prefManager.getValue("native_adid"))
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
