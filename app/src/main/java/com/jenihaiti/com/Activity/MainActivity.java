package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.jenihaiti.com.PushNotification.Config;
import com.jenihaiti.com.PushNotification.NotificationUtils;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.Objects;



public class MainActivity<callbackManager> extends AppCompatActivity implements View.OnClickListener {

    PrefManager prefManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private InterstitialAd mInterstitialAd;

    LayoutInflater inflater;

    public Context context;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    LinearLayout lyLeaderboard, lyInstruction, lyUserProfile, lySettings, lyLogout;
    TextView txtPlayquiz;

    String TYPE = "";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utility.screenCapOff(MainActivity.this);

        prefManager = new PrefManager(MainActivity.this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        Init();
        PushInit();
    }

    public void Init() {
        try {
            lyLeaderboard = findViewById(R.id.lyLeaderboard);
            lyInstruction = findViewById(R.id.lyInstruction);
            lyUserProfile = findViewById(R.id.lyUserProfile);
            lySettings = findViewById(R.id.lySettings);
            lyLogout = findViewById(R.id.lyLogout);
            txtPlayquiz = findViewById(R.id.txt_play_quiz);

            lyLeaderboard.setOnClickListener(this);
            lyInstruction.setOnClickListener(this);
            lyUserProfile.setOnClickListener(this);
            lySettings.setOnClickListener(this);
            lyLogout.setOnClickListener(this);
            txtPlayquiz.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("Init Exception ==>", "" + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyLeaderboard:
                if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
                    if (mInterstitialAd != null) {
                        TYPE = "LeaderBoard";
                        mInterstitialAd.show(MainActivity.this);
                    } else {
                        Log.e("TAG", "The interstitial ad wasn't ready yet.");
                        startActivity(new Intent(MainActivity.this, UserLeaderBoard.class));
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
                break;

            case R.id.lyInstruction:
                if (mInterstitialAd != null) {
                    TYPE = "Instruction";
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.e("TAG", "The interstitial ad wasn't ready yet.");
                    Intent intentInstruction = new Intent(MainActivity.this, Instruction.class);
                    intentInstruction.putExtra("type", "Instruction");
                    startActivity(intentInstruction);
                }
                break;

            case R.id.lyUserProfile:
                if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
                    if (mInterstitialAd != null) {
                        TYPE = "UserProfile";
                        mInterstitialAd.show(MainActivity.this);
                    } else {
                        Log.e("TAG", "The interstitial ad wasn't ready yet.");
                        startActivity(new Intent(MainActivity.this, UserProfile.class));
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
                break;

            case R.id.lySettings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                break;

            case R.id.lyLogout:
                if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
                    logout();
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
                break;

            case R.id.txt_play_quiz:
                if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
                    startActivity(new Intent(MainActivity.this, Category.class));
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    public void onBackPressed() {
        inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        txtDescription.setText("Do you want to exit?");

        btnPositive.setText("Yes");
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                finishAffinity();
            }
        });

        btnNegative.setText("No");
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void logout() {
        inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                popupWindow.dismiss();
                prefManager.setLoginId("0");
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                MainActivity.this.finish();
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

    public void PushInit() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (Objects.equals(intent.getAction(), Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("message ==>", "" + message);
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        displayFirebaseRegId();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);
        if (!TextUtils.isEmpty(regId)) {
            Log.e(TAG, "Firebase reg id: " + regId);
        } else {
            Log.e(TAG, "Firebase Reg Id is not received yet!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());

        Log.e("interstital_ad", "" + prefManager.getValue("interstital_ad"));
        if (prefManager.getValue("interstital_ad").equalsIgnoreCase("yes")) {
            TYPE = "";
            mInterstitialAd = null;
            InterstitialAd();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void InterstitialAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            Log.e("Ad failed to show.", "" + adError.toString());
                            if (TYPE.equalsIgnoreCase("LeaderBoard")) {
                                startActivity(new Intent(MainActivity.this, LeaderBoard.class));
                            } else if (TYPE.equalsIgnoreCase("Instruction")) {
                                Intent intentInstruction = new Intent(MainActivity.this, Instruction.class);
                                intentInstruction.putExtra("type", "Instruction");
                                startActivity(intentInstruction);
                            } else if (TYPE.equalsIgnoreCase("UserProfile")) {
                                startActivity(new Intent(MainActivity.this, UserProfile.class));
                            }
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            Log.e("TAG", "Ad was shown.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            if (TYPE.equalsIgnoreCase("LeaderBoard")) {
                                startActivity(new Intent(MainActivity.this, LeaderBoard.class));
                            } else if (TYPE.equalsIgnoreCase("Instruction")) {
                                Intent intentInstruction = new Intent(MainActivity.this, Instruction.class);
                                intentInstruction.putExtra("type", "Instruction");
                                startActivity(intentInstruction);
                            } else if (TYPE.equalsIgnoreCase("UserProfile")) {
                                startActivity(new Intent(MainActivity.this, UserProfile.class));
                            }
                            Log.e("TAG", "Ad was dismissed.");
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                            Log.e("TAG", "onAdImpression.");
                        }
                    };

            mInterstitialAd.load(this, "" + prefManager.getValue("interstital_adid"),
                    adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            Log.e(TAG, "onAdLoaded");
                            mInterstitialAd = interstitialAd;
                            mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(TAG, "" + loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });

        } catch (Exception e) {
            Log.e("Interstial Exception =>", "" + e);
        }
    }

}