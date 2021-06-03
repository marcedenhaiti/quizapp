package com.jenihaiti.com.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.jenihaiti.com.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class Utility {

    Context context;
    public static ProgressDialog pDialog;

    public Utility(Context context) {
        this.context = context;
    }

    //DateFormation :
    @SuppressLint("LongLogTag")
    public static String DateFormat(String date) {
        String finaldate = "";

        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormate Exception ==>", "" + e);
        }

        return finaldate;
    }

    //DateFormation :
    @SuppressLint("LongLogTag")
    public static String DateFormat2(String date) {
        String finaldate = "";

        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormate2 Exception ==>", "" + e);
        }

        return finaldate;
    }

    public static String covertTimeToText(String dataDate) {
        String convTime = null;
        String prefix = "";
        String suffix = "ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            Log.e("==>pastTime", "" + pasTime.getTime());
            Log.e("==>nowTime", "" + nowTime.getTime());

            long dateDiff = nowTime.getTime() - pasTime.getTime();
            Log.e("==>dateDiff", "" + (dateDiff / 1000));

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

            Log.e("==>second", "" + (second));
            Log.e("==>minute", "" + (minute));
            Log.e("==>hour", "" + (hour));
            Log.e("==>day", "" + (day));

            if (second < 60 && second > 0) {
                if (second == 1) {
                    convTime = second + " second " + suffix;
                } else {
                    convTime = second + " seconds " + suffix;
                }
            } else if (minute < 60 && minute > 0) {
                if (minute == 1) {
                    convTime = minute + " minute " + suffix;
                } else {
                    convTime = minute + " minutes " + suffix;
                }
            } else if (hour < 24 && hour > 0) {
                if (hour == 1) {
                    convTime = hour + " hour " + suffix;
                } else {
                    convTime = hour + " hours " + suffix;
                }
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " months " + suffix;
                } else {
                    convTime = (day / 7) + " week " + suffix;
                }
            } else if (day < 7) {
                if (day == 1) {
                    convTime = day + " day " + suffix;
                } else {
                    convTime = day + " days " + suffix;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", "" + e.getMessage());
        }

        return convTime;
    }


    public static void shimmerShow(ShimmerFrameLayout shimmer) {
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();
    }

    public static void shimmerHide(ShimmerFrameLayout shimmer) {
        shimmer.stopShimmer();
        shimmer.hideShimmer();
        shimmer.setVisibility(View.GONE);
    }


    public static void ProgressBarShow(Context mContext) {
        if (pDialog == null) {
            Log.e("showProgressDialog()", "");
            pDialog = new ProgressDialog(mContext, R.style.AlertDialogDanger);
            pDialog.setMessage("Please wait...");
            pDialog.setCanceledOnTouchOutside(false);
        }
        pDialog.show();
    }

    public static void ProgressbarHide() {
        if (pDialog != null && pDialog.isShowing()) {
            Log.e("dismissProgressDialog()", "");
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public static Map<String, String> GetMap(Context context) {
        PrefManager prefManager = new PrefManager(context);

        Map<String, String> map = new HashMap<>();
        map.put("general_token", prefManager.getValue("general_token"));
        map.put("key", prefManager.getValue("key"));
        map.put("device_token", prefManager.getValue("device_token"));
        map.put("auth_token", prefManager.getValue_return("auth_token"));

        return map;
    }

    //make fullscreen
    public static void fullScreen(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    //Screen capture ON/OFF
    public static void screenCapOff(Activity activity) {
//        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fcm_token", "empty");
    }

}
