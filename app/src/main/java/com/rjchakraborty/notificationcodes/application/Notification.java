package com.rjchakraborty.notificationcodes.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.N_MR1;


public class Notification extends Application {


    private static Notification instance;

    static {
        instance = null;
    }


    public Notification() {
        instance = this;
    }

    public static Notification getAppContext() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }



}
