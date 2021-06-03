package com.jenihaiti.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jenihaiti.com.Model.LoginRegiModel.LoginRegiModel;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import java.util.HashMap;
import java.util.Map;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    PrefManager prefManager;
    Map<String, String> map;

    String strUsername, strEmail, strPassword, strMobileNumber,strDeviceToken;
    EditText etEmail, etPassword, etPhoneNumber, etUsername;
    TextView ivPassVisible, txtRegister, txtLogin;
    LinearLayout lyPassVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        Utility.screenCapOff(Registration.this);

        init();
        strDeviceToken = Utility.getToken(Registration.this);

    }

    public void init() {
        try {
            prefManager = new PrefManager(Registration.this);
            map = new HashMap<>();
            map = Utility.GetMap(Registration.this);

            etUsername = findViewById(R.id.etUsername);
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            etPhoneNumber = findViewById(R.id.etPhoneNumber);

            txtLogin = findViewById(R.id.txtLogin);
            txtRegister = findViewById(R.id.txtRegister);

            lyPassVisibility = findViewById(R.id.lyPassVisibility);

            ivPassVisible = findViewById(R.id.ivPassVisible);

            lyPassVisibility.setOnClickListener(this);
            txtRegister.setOnClickListener(this);
            txtLogin.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyPassVisibility:
                if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    Log.e("etPassword ==>", "" + etPassword.getText().toString());
                    Toasty.warning(Registration.this, "Enter Password", Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    Log.e("Visible Password ", "" + etPassword.getText().toString());
                    ivPassVisible.setBackground(getResources().getDrawable(R.drawable.ic_pass_invisible));
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    Log.e("Invisible Password", "" + etPassword.getText().toString());
                    ivPassVisible.setBackground(getResources().getDrawable(R.drawable.ic_pass_visible));
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;

            case R.id.txtRegister:
                strUsername = etUsername.getText().toString().trim();
                strEmail = etEmail.getText().toString().trim();
                strPassword = etPassword.getText().toString().trim();
                strMobileNumber = etPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(strUsername)) {
                    Toasty.warning(Registration.this, "Enter username", Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strEmail)) {
                    Toasty.warning(Registration.this, "Enter email", Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strPassword)) {
                    Toasty.warning(Registration.this, "Enter password", Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strMobileNumber)) {
                    Toasty.warning(Registration.this, "Enter phone number", Toasty.LENGTH_SHORT).show();
                    return;
                }

                SignUp();
                break;

            case R.id.txtLogin:
                startActivity(new Intent(Registration.this, Login.class));
                break;
        }
    }

    //registration API
    public void SignUp() {
        if (!((Activity) Registration.this).isFinishing()) {
            Utility.ProgressBarShow(Registration.this);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<LoginRegiModel> call = bookNPlayAPI.registration(strEmail, strPassword, strUsername,
                strMobileNumber, strDeviceToken);
        call.enqueue(new Callback<LoginRegiModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<LoginRegiModel> call, @NonNull Response<LoginRegiModel> response) {
                try {
                    Log.e("registration Status ==>", "" + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Utility.ProgressbarHide();
                        Log.e("email ==>", "" + response.body().getResult().get(0).getEmail());
                        prefManager.setLoginId("" + response.body().getResult().get(0).getId());
                        prefManager.setValue("Email", "" + response.body().getResult().get(0).getEmail());
                        prefManager.setValue("Phone", "" + response.body().getResult().get(0).getMobileNumber());
                        prefManager.setValue("username", "" + response.body().getResult().get(0).getFirstName());
                        Log.e("LoginId ==>", "" + prefManager.getLoginId());
                        startActivity(new Intent(Registration.this, MainActivity.class));
                        Registration.this.finish();
                        Toasty.success(Registration.this, "" + response.body().getMessage()
                                , Toasty.LENGTH_SHORT).show();
                    } else {
                        new AlertDialog.Builder(Registration.this, R.style.AlertDialogDanger)
                                .setTitle("" + getResources().getString(R.string.app_name))
                                .setMessage("" + response.body().getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                    }
                                }).show();
                    }
                } catch (Exception e) {
                    Log.e("registration Exception ==>", "" + e);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<LoginRegiModel> call, @NonNull Throwable t) {
                Log.e("registration onFailre Throwable ==>", "" + t.getMessage());
                Utility.ProgressbarHide();
            }
        });
    }

}