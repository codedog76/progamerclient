package singletons;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsSingleton {
    private static SettingsSingleton sInstance;
    private SharedPreferences mSharedPreferences;
    private final static String PREFERENCES = "progamer_preferences";

    public static SettingsSingleton getInstance(Context mContext) {
        if(sInstance==null)
            sInstance = new SettingsSingleton(mContext);
        return sInstance;
    }

    private SettingsSingleton(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public boolean getLaunchScreenActive() {
        return getSharedPreferences().getBoolean("launch_screen_active", true);
    }

    public void setLaunchScreenActive(boolean state) {
        getSharedPreferences().edit().putBoolean("launch_screen_active", state).apply();
    }

    public void setFirstTimeLaunch(boolean state) {
        getSharedPreferences().edit().putBoolean("first_time_launch", state).apply();
    }

    public boolean getFirstTimeLaunch() {
        return getSharedPreferences().getBoolean("first_time_launch", true);
    }

    public void setLaunchScreenTime(int time) {
        getSharedPreferences().edit().putInt("launch_screen_time", time).apply();
    }

    public int getLaunchScreenTime() {
        return getSharedPreferences().getInt("launch_screen_time", 2000);
    }

    public void setSyncWifiOnly(boolean state) {
        getSharedPreferences().edit().putBoolean("sync_wifi_only", state).apply();
    }

    public boolean getSyncWifiOnly() {
        return getSharedPreferences().getBoolean("sync_wifi_only", false);
    }

    private SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
}
