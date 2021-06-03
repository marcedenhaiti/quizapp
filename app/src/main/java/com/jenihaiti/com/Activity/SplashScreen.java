package com.jenihaiti.com.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.jenihaiti.com.Model.GeneralSettingsModel.GeneralSettingsModel;
import com.jenihaiti.com.Model.SuccessModel.SuccessModel;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.ConnectivityReceiver;
import com.jenihaiti.com.Util.Constant;
import com.jenihaiti.com.Util.MyApp;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import com.google.android.material.snackbar.Snackbar;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    PrefManager prefManager;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.fullScreen(SplashScreen.this);
        setContentView(R.layout.splash_screen);
        Utility.screenCapOff(SplashScreen.this);

        init();
    }

    public void init() {
        prefManager = new PrefManager(SplashScreen.this);
        checkConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApp.getInstance().setConnectivityListener(this);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                boolean isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {

                    checkStatus();
                }
            } else {
                Permission();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SplashScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void Permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                Intent intent_status = new Intent(getApplicationContext(), PermissionActivity.class);
                startActivityForResult(intent_status, PERMISSION_REQUEST_CODE);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        } else {
            boolean isConnected = ConnectivityReceiver.isConnected();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    boolean isConnected = ConnectivityReceiver.isConnected();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void generalSettings() {

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<GeneralSettingsModel> call = bookNPlayAPI.genaral_setting();
        call.enqueue(new Callback<GeneralSettingsModel>() {
            @Override
            public void onResponse(@NonNull Call<GeneralSettingsModel> call, @NonNull Response<GeneralSettingsModel> response) {

                Log.e("response", "" + response);
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getStatus() == 200) {
                        for (int i = 0; i < response.body().getResult().size(); i++) {
                            prefManager.setValue(response.body().getResult().get(i).getKey(), response.body().getResult().get(i).getValue());
                            Log.e(response.body().getResult().get(i).getKey() + "",
                                    "==> " + response.body().getResult().get(i).getValue());
                        }
                        if (!prefManager.isFirstTimeLaunch()) {
                            Log.e("FirstTimeLaunch", "" + prefManager.isFirstTimeLaunch());
                            Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            prefManager.setFirstTimeLaunch(false);
                            Intent mainIntent = new Intent(SplashScreen.this, WelcomeActivity.class);
                            startActivity(mainIntent);
                            finish();
                            Log.e("FirstTimeLaunch", "" + prefManager.isFirstTimeLaunch());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralSettingsModel> call, @NonNull Throwable t) {
                Log.e("Failure", "" + t.getMessage());
                Toasty.error(SplashScreen.this, "" + t.getMessage(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    //checkStatus API call
    private void checkStatus() {

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.checkStatus("" + Constant.PURCHASED_CODE,
                "" + getApplication().getPackageName());
        call.enqueue(new Callback<SuccessModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                try {
                    if (response.code() == 200 &&
                            response.body().getStatus() == 200) {
                        Log.e("checkStatus API call : status", "" + response.body().getStatus());
                        generalSettings();
                    } else {
                        Toasty.warning(SplashScreen.this,""+response.body().getMessage(),Toasty.LENGTH_LONG).show();
                        Log.e("checkStatus massage", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("checkStatus API error==>", "" + e);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                Log.e("checkStatus API call : Failure", "That didn't work!!!" + t.getMessage());
            }
        });
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("called", "onResume");
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

}