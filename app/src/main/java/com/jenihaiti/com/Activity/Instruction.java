package com.jenihaiti.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

public class Instruction extends AppCompatActivity {

    PrefManager prefManager;

    TextView txtPolicyDesc, txtAppname, txtToolbarTitle, txtBack;
    LinearLayout lyBack, lyToolbar, lyAdView;

    TemplateView nativeTemplate;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        Utility.screenCapOff(Instruction.this);

        init();

        txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_blue));
        txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_blue));
        txtAppname.setText("" + Html.fromHtml(prefManager.getValue("app_name")));

        if (type.equalsIgnoreCase("Instruction")) {
            txtPolicyDesc.setText("" + Html.fromHtml(prefManager.getValue("instrucation")));
            txtToolbarTitle.setText("" + getString(R.string.Instruction));
        } else {
            txtPolicyDesc.setText("" + Html.fromHtml(prefManager.getValue("privacy_policy")));
            txtToolbarTitle.setText("" + getString(R.string.Privacy_policy));
        }

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Instruction.this.finish();
            }
        });

        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            lyAdView.setVisibility(View.VISIBLE);
            Admob();
        } else {
            lyAdView.setVisibility(View.GONE);
        }

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
            Intent intent = getIntent();
            if (intent.hasExtra("type")) {
                type = intent.getStringExtra("type");
                Log.e("type ==>", "" + type);
            }

            prefManager = new PrefManager(Instruction.this);

            lyAdView = findViewById(R.id.lyAdView);
            nativeTemplate = findViewById(R.id.nativeTemplate);
            lyBack = findViewById(R.id.lyBack);
            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            txtBack = findViewById(R.id.txtBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            txtAppname = findViewById(R.id.txtAppname);
            txtPolicyDesc = findViewById(R.id.txtPolicyDesc);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    private void Admob() {
        try {
            AdView mAdView = new AdView(Instruction.this);
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

    private void NativeAds() {
        try {
            Log.e("loginID =>", "" + prefManager.getLoginId());
            AdLoader adLoader = new AdLoader.Builder(Instruction.this, "" + prefManager.getValue("native_adid"))
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