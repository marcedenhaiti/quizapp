package com.jenihaiti.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.squareup.picasso.Picasso;

public class AboutUs extends AppCompatActivity {

    private PrefManager prefManager;

    LinearLayout lyBack, lyToolbar, lyAdView;

    private ImageView ivAppicon;
    private TextView txtBack, txtToolbarTitle, txtAppname, txtCompanyname, txtEmail,
            txtWebsite, txtContactNo, txtAboutus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Utility.screenCapOff(AboutUs.this);

        init();
        setDetails();

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUs.this.finish();
            }
        });

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
            prefManager = new PrefManager(AboutUs.this);

            lyAdView = findViewById(R.id.lyAdView);
            lyBack = findViewById(R.id.lyBack);
            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);

            ivAppicon = findViewById(R.id.ivAppicon);
            txtBack = findViewById(R.id.txtBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            txtAppname = findViewById(R.id.txtAppname);
            txtCompanyname = findViewById(R.id.txtCompanyname);
            txtEmail = findViewById(R.id.txtEmail);
            txtWebsite = findViewById(R.id.txtWebsite);
            txtContactNo = findViewById(R.id.txtContactNo);
            txtAboutus = findViewById(R.id.txtAboutus);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDetails() {
        try {
            txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_blue));
            txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_blue));
            txtToolbarTitle.setText("" + getResources().getString(R.string.about_app));
            Picasso.get().load("" + prefManager.getValue("app_logo"))
                    .placeholder(R.drawable.app_icon)
                    .into(ivAppicon);
            txtAppname.setText("" + prefManager.getValue("app_name"));
            txtCompanyname.setText("" + prefManager.getValue("Author"));
            txtEmail.setText("" + prefManager.getValue("host_email"));
            txtWebsite.setText("" + prefManager.getValue("website"));
            txtContactNo.setText("" + prefManager.getValue("contact"));
            txtAboutus.setText("" + Html.fromHtml(prefManager.getValue("app_desripation")));
        } catch (Exception e) {
            Log.e("set_details error", "" + e);
        }
    }

    private void Admob() {
        try {
            AdView mAdView = new AdView(AboutUs.this);
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