package com.rjchakraborty.notificationcodes.notification;

import android.os.Handler;
import android.os.SharedMemory;
import android.text.TextUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

import com.rjchakraborty.notificationcodes.helper.SharedPrefer;
import com.rjchakraborty.notificationcodes.listeners.AppConstants;
import com.rjchakraborty.notificationcodes.application.Notification;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by RJ Chakraborty on 10/6/2017.
 */

public class SendNotification {

    private int retryCount = 0;

    public SendNotification() {
        this.retryCount = 0;
    }


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    public void execute(String data) throws IOException {
        URL url = new URL("https://fcm.googleapis.com/fcm/send");
        if (data != null) {
            String token = SharedPrefer.getString(SharedPrefer.FCM_TOKEN);
            if (token == null) {
                retryCount++;
                //getFCMToken(data);
            } else {
                JSONObject root = null;
                JSONObject dataObj = null;

                try {
                    JSONObject androidObj = new JSONObject();
                    androidObj.put("priority", "high");

                    root = new JSONObject();
                    dataObj = new JSONObject(data);
                    root.put("to", token);
                    root.put("data", dataObj);
                    root.put("android", androidObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(JSON, Objects.requireNonNull(root).toString());
                Request request = new Request.Builder()
                        .url(url)
                        .header("Content-Type", "application/json; UTF-8")
                        .header("Authorization", AppConstants.LIVE_NOTIFICATION_KEY)
                        .post(body)
                        .build();
                final String finalData = data;
                client.newCall(request).enqueue(new Callback() {
                    Handler mainHandler = new Handler(Notification.getAppContext().getMainLooper());

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        try {
                            final boolean success = response.isSuccessful();
                            final String responseStr;
                            if (response.body() != null) {
                                responseStr = response.body().string();
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (success) {
                                            handleResponse(responseStr, finalData);
                                        }
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }
    }


    private void handleResponse(String responseStr, String data) {
        try {
            //{"multicast_id":5238413187681300328,"success":0,"failure":1,"canonical_ids":0,"results":[{"error":"NotRegistered"}]}
            JSONObject responseObject = new JSONObject(responseStr);
            if (responseObject.optInt("success") == 0 && responseObject.optInt("failure") == 1) {
                retryCount++;
                //getFCMToken(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

