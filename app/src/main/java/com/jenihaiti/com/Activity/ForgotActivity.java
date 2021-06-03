package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jenihaiti.com.Model.ForgotPassModel.ForgotPassModel;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {

    PrefManager prefManager;
    Map<String, String> map;
    LayoutInflater inflater;
    EditText etEmail;
    TextView txtSend;
    String strEmail;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot);

        Utility.screenCapOff(ForgotActivity.this);

        init();

        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = etEmail.getText().toString().trim();
                if (TextUtils.isEmpty(strEmail)) {
                    Toasty.warning(ForgotActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                ForgotPassword();
            }
        });

    }

    public void init() {
        try {
            prefManager = new PrefManager(ForgotActivity.this);
            map = new HashMap<>();
            map = Utility.GetMap(ForgotActivity.this);

            etEmail = findViewById(R.id.etEmail);
            txtSend = findViewById(R.id.txtSend);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    public void ForgotPassword() {
        Utility.ProgressBarShow(ForgotActivity.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<ForgotPassModel> call = bookNPlayAPI.forgotpassword(map, strEmail);
        call.enqueue(new Callback<ForgotPassModel>() {
            @Override
            public void onResponse(@NonNull Call<ForgotPassModel> call, @NonNull Response<ForgotPassModel> response) {
                Utility.ProgressbarHide();
                if (response.code() == 200 && response.body().getStatus() == 200) {
                    flag = true;
                } else {
                    flag = false;
                }
                AlertMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(@NonNull Call<ForgotPassModel> call, @NonNull Throwable t) {
                Utility.ProgressbarHide();
                flag = false;
                AlertMessage("" + t.getMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void AlertMessage(String message) {
        Log.e("message ==>", "" + message);
        inflater = (LayoutInflater) ForgotActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.alert_dialog, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        RoundedImageView rivDialog = popupView.findViewById(R.id.rivDialog);
        if (flag) {
            rivDialog.setImageResource(R.drawable.ic_success);
        } else {
            rivDialog.setImageResource(R.drawable.ic_warn);
        }

        TextView txtTitle = popupView.findViewById(R.id.txtTitle);
        TextView txtDescription = popupView.findViewById(R.id.txtDescription);
        Button btnNegative = popupView.findViewById(R.id.btnNegative);
        Button btnPositive = popupView.findViewById(R.id.btnPositive);

        txtTitle.setText(getResources().getString(R.string.app_name));
        txtDescription.setText(message);

        btnPositive.setText("OK");
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ForgotActivity.this.finish();
            }
        });
        btnNegative.setVisibility(View.GONE);
    }

}