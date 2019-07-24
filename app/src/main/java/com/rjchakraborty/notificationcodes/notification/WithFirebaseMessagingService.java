package com.rjchakraborty.notificationcodes.notification;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rjchakraborty.notificationcodes.listeners.AppConstants;

import java.util.Map;


/**
 * Created by RJ Chakraborty on 15-12-2017.
 * Code Snippet from my WithU Application. It requires others classes and methods to work completely
 * I put here as code snippet only.
 */

public class WithFirebaseMessagingService extends FirebaseMessagingService {

    private String dataTitle = null, dataMessage = null;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        //String notificationTitle = null, notificationBody = null;
        // Check if message contains a data payload.
        // Check if message contains a notification payload.
        if (ConnectionUtil.isLoggedIn()) {
            if (remoteMessage.getNotification() != null) {
                dataTitle = remoteMessage.getNotification().getTitle();
                dataMessage = remoteMessage.getNotification().getBody();

                if (dataMessage != null && dataTitle != null) {
                    parseNotificationData(dataTitle, dataMessage);
                }
            } else if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
                parsePayloadData(remoteMessage.getData());
            }

        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //sendRegistrationToServer(s);
    }


    private void parsePayloadData(Map<String, String> nMap) {
        if (nMap != null) {
            String messageJSON = JsonHandler.convertStringMapToJson(nMap);
            PersistableBundleCompat pBundle = new PersistableBundleCompat();
            pBundle.putString(AppConstants.SUBCONTENT, messageJSON);

            new JobRequest.Builder(NotificationParserJob.TAG)
                    .setBackoffCriteria(2000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                    .addExtras(pBundle)
                    .startNow()
                    .build()
                    .schedule();
        }
    }

    private void parseNotificationData(String title, String message) {
        PersistableBundleCompat pBundle = new PersistableBundleCompat();
        pBundle.putString(AppConstants.NOTE_TITLE, title);
        pBundle.putString(AppConstants.NOTE_CONTENT, message);

        new JobRequest.Builder(NotificationParserJob.TAG)
                .setBackoffCriteria(2000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .addExtras(pBundle)
                .startNow()
                .build()
                .schedule();
    }

}
