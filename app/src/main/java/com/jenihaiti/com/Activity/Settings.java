package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.facebook.login.LoginManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.makeramen.roundedimageview.RoundedImageView;

public class Settings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    PrefManager prefManager;

    ShimmerFrameLayout shimmer;
    LayoutInflater inflater;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    SwitchCompat switchSound, switchVibration;

    LinearLayout lyBack, lyToolbar, lyAbout, lyTermCondition, lyLogin;
    TextView txtToolbarTitle, txtBack, txtLogin;

    TemplateView nativeTemplate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(Settings.this, gso);

        init();

        txtToolbarTitle.setText("" + getString(R.string.Settings));
        txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_blue));
        txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_blue));

        if (prefManager.getBool("SOUND")) {
            switchSound.setChecked(true);
        } else {
            switchSound.setChecked(false);
        }

        if (prefManager.getBool("VIBRATION")) {
            switchVibration.setChecked(true);
        } else {
            switchVibration.setChecked(false);
        }

        if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
            txtLogin.setText(getResources().getString(R.string.logout));
        } else {
            txtLogin.setText(getResources().getString(R.string.log_in));
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
            prefManager = new PrefManager(Settings.this);
            shimmer = findViewById(R.id.shimmer);
            switchVibration = findViewById(R.id.switchVibration);
            switchSound = findViewById(R.id.switchSound);
            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            lyBack = findViewById(R.id.lyBack);
            lyAbout = findViewById(R.id.lyAbout);
            lyTermCondition = findViewById(R.id.lyTermCondition);
            lyLogin = findViewById(R.id.lyLogin);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            txtBack = findViewById(R.id.txtBack);
            txtLogin = findViewById(R.id.txtLogin);
            nativeTemplate = findViewById(R.id.nativeTemplate);

            switchSound.setOnCheckedChangeListener(this);
            switchVibration.setOnCheckedChangeListener(this);
            lyBack.setOnClickListener(this);
            lyAbout.setOnClickListener(this);
            lyTermCondition.setOnClickListener(this);
            lyLogin.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchSound:
                Log.e("Sound ==>", "" + isChecked);
                if (isChecked) {
                    prefManager.setBool("SOUND", true);
                } else {
                    prefManager.setBool("SOUND", false);
                }
                break;

            case R.id.switchVibration:
                Log.e("Vibration ==>", "" + isChecked);
                if (isChecked) {
                    prefManager.setBool("VIBRATION", true);
                } else {
                    prefManager.setBool("VIBRATION", false);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack:
                finish();
                break;

            case R.id.lyAbout:
                startActivity(new Intent(Settings.this, AboutUs.class));
                break;

            case R.id.lyTermCondition:
                Intent intentPrivacy = new Intent(Settings.this, Instruction.class);
                intentPrivacy.putExtra("type", "Privacy Policy");
                startActivity(intentPrivacy);
                break;

            case R.id.lyLogin:
                if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
                    logout();
                } else {
                    startActivity(new Intent(Settings.this, Login.class));
                }
                break;
        }
    }

    private void logout() {
        inflater = (LayoutInflater) Settings.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.alert_dialog, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        RoundedImageView rivDialog = popupView.findViewById(R.id.rivDialog);
        rivDialog.setImageResource(R.drawable.app_icon2);

        TextView txtTitle = popupView.findViewById(R.id.txtTitle);
        TextView txtDescription = popupView.findViewById(R.id.txtDescription);
        Button btnNegative = popupView.findViewById(R.id.btnNegative);
        Button btnPositive = popupView.findViewById(R.id.btnPositive);

        txtTitle.setText(getResources().getString(R.string.app_name));
        txtDescription.setText("Are you sure you want to Logout?");

        btnPositive.setText("Logout");
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setLoginId("0");
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(Settings.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                getActivity().finish();
                txtLogin.setText("Login");
                popupWindow.dismiss();
            }
        });

        btnNegative.setText("Cancel");
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void NativeAds() {
        try {
            Log.e("loginID =>", "" + prefManager.getLoginId());
            AdLoader adLoader = new AdLoader.Builder(Settings.this, "" + prefManager.getValue("native_adid"))
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