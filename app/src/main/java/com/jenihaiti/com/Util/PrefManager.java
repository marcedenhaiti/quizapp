package com.jenihaiti.com.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;
    public static String pushRID = "0";

    private static final String PREF_NAME = "DivineTechs-Welcome";

    private static final String LOGIN_ID = "LOGIN";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";


    @SuppressLint({"CommitPrefEdits", "LongLogTag"})
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        Log.e("setFirstTimeLaunch", "" + isFirstTime);
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


    public void setValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getValue(String key) {
        return pref.getString(key, "");
    }


    public void setLoginId(String id) {
        editor.putString(LOGIN_ID, id);
        editor.commit();
    }

    public String getLoginId() {
        return pref.getString(LOGIN_ID, "0");
    }


    public void setIntValue(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getIntValue(String key) {
        return pref.getInt(key, 0);
    }

    public String getValue_return(String key) {
        Log.e("getValue_return()", "");
        return pref.getString(key, "");
    }

    public void setBool(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBool(String key) {
        return pref.getBoolean(key, true);
    }

    public static void forceRTLIfSupported(Window window, Activity activity) {
        /*if (activity.getResources().getString(R.string.isRTL).equals("true")){}*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.e("local_data", "" + LocaleUtils.getSelectedLanguageId());
            if ("ar".equals(LocaleUtils.getSelectedLanguageId())) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        } else {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        }
    }

}
