package com.jenihaiti.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jenihaiti.com.Model.SuccessModel.SuccessModel;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawRequest extends AppCompatActivity {

    PrefManager prefManager;
    Map<String, String> map;

    TextView txtBack, txtToolbarTitle, txtSubmit;
    EditText etPaymentDetails;

    RadioGroup radioGroup;

    LinearLayout lyToolbar, lyBack;
    TemplateView nativeTemplate;

    String paymentType = "", paymentDetails = "";
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_request);

        init();

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                paymentType = rb.getText().toString();
                paymentDetails = etPaymentDetails.getText().toString();

                if (TextUtils.isEmpty(paymentType)) {
                    Toasty.warning(WithdrawRequest.this, "Please select payment method",
                            Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(paymentDetails)) {
                    Toasty.warning(WithdrawRequest.this, "Please enter payment details",
                            Toasty.LENGTH_SHORT).show();
                    return;
                }

                WithdrawalRequest();
            }
        });

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
            prefManager = new PrefManager(WithdrawRequest.this);
            map = new HashMap<>();
            map = Utility.GetMap(WithdrawRequest.this);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            lyBack = findViewById(R.id.lyBack);
            txtBack = findViewById(R.id.txtBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            txtToolbarTitle.setText("" + getResources().getString(R.string.withdraw_request));
            txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_gray));
            txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_gray));

            nativeTemplate = findViewById(R.id.nativeTemplate);
            radioGroup = findViewById(R.id.radioGroup);
            txtSubmit = findViewById(R.id.txtSubmit);
            etPaymentDetails = findViewById(R.id.etPaymentDetails);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    private void WithdrawalRequest() {
        Utility.ProgressBarShow(WithdrawRequest.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.withdrawal_request("" + prefManager.getLoginId(),
                "" + paymentDetails, "" + paymentType);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                Log.e("Withdrawal_request", "" + response.body().getMessage());
                Utility.ProgressbarHide();
                if (response.code() == 200 && response.body().getStatus() == 200) {
                    flag = true;
                } else {
                    flag = false;
                }

                AlertDialog("" + response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Utility.ProgressbarHide();
                flag = false;
                AlertDialog("" + t.getMessage());
            }
        });
    }

    private void AlertDialog(String message) {
        LayoutInflater inflater = (LayoutInflater) WithdrawRequest.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        txtDescription.setText("" + message);

        btnPositive.setText("Okay");
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                finish();
            }
        });

        btnNegative.setVisibility(View.GONE);
    }

    private void NativeAds() {
        try {
            Log.e("loginID =>", "" + prefManager.getLoginId());
            AdLoader adLoader = new AdLoader.Builder(WithdrawRequest.this, "" + prefManager.getValue("native_adid"))
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
                            Log.e("NativeAd adError=>", "" + adError.getCause());
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