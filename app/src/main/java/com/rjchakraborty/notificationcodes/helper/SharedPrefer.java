package com.rjchakraborty.notificationcodes.helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.rjchakraborty.notificationcodes.application.Notification;

import java.util.Random;

import androidx.core.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by RJ Chakraborty on 9/5/2017.
 */

public class SharedPrefer {
    private static final String MY_PREFS_NAME = "Notification_SharedPrefs";
    private static final String DYNAMIC_PREFS = "Dynamic_Prefs";

    public static final String FCM_TOKEN = "_FCM_TOKEN";

 


    public static void removeAll() {
        SharedPreferences settings = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    public static void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        SharedPreferences prefs = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static void saveInt(String key, int value) {
        SharedPreferences.Editor editor = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        SharedPreferences prefs = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(key, -1);
    }

    public static void saveFloat(String key, float value) {
        SharedPreferences.Editor editor = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(String key) {
        SharedPreferences prefs = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getFloat(key, -1f);
    }

    public static void saveLong(String key, long value) {
        SharedPreferences.Editor editor = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key) {
        SharedPreferences prefs = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getLong(key, -1);
    }



    public static void saveString(String key, String value) {
        SharedPreferences.Editor editor = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        SharedPreferences prefs = Notification.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(key, null);
    }


}

