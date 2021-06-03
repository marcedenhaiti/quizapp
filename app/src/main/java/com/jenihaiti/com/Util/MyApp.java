package com.jenihaiti.com.Util;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;

public class MyApp extends Application {

    private static MyApp mInstance;
    PrefManager prefManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        prefManager = new PrefManager(this);

        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(getApplicationContext());

        //OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        FirebaseApp.initializeApp(this);

    }

    public Context getContext() {
        return mInstance.getContext();
    }

    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}