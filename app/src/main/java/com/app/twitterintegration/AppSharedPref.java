package com.app.twitterintegration;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPref {

    private static AppSharedPref instance = null;
    private SharedPreferences sh;

    private AppSharedPref(Context mContext) {
        sh = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    /**
     * @param mContext
     * @return {@link AppSharedPref}
     */
    public synchronized static AppSharedPref getInstance(Context mContext) {

        if (instance == null) {
            instance = new AppSharedPref(mContext);
        }
        return instance;
    }

    public String getUserId() {
        return sh.getString("uid", "");
    }

    public void setUserId(String uid) {
        sh.edit().putString("uid", uid).apply();
    }

    public boolean getSaveLogedIn() {
        return sh.getBoolean("saveLogIn", false);
    }

    public void setSaveLogedIn(Boolean IsComplete) {
        sh.edit().putBoolean("saveLogIn", IsComplete).apply();
    }

    public void clear() {
        sh.edit().clear().apply();
    }

    public String getDataString(String key) {
        return sh.getString(key, "");
    }

    public void setDataString(String key, String value) {
        sh.edit().putString(key, value).apply();
    }

    public void setSocialMediaLogedIn(Integer IsSocialMedia) {
        //1 - Facebook , 2- Twitter
        sh.edit().putInt("SocialMediaLogedIn", IsSocialMedia).apply();
    }

    public int getSocialMediaLogedIn() {
        return sh.getInt("SocialMediaLogedIn", 0);
    }
}
