package com.rjchakraborty.notificationcodes.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.IconCompat;


import com.rjchakraborty.notificationcodes.R;
import com.rjchakraborty.notificationcodes.helper.SharedPrefer;
import com.rjchakraborty.notificationcodes.listeners.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by RJ Chakraborty on 15-12-2017.
 * Code Snippet from my WithU Application. It requires others classes and methods to work completely
 * I put here as code snippet only.
 */

public class ShowNotification {

    Context mContext;
    private NotificationManager mNotifyManager;
    private Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private boolean vibrate = true;
    private long[] vibratePattern = new long[]{0, 1000};
    public static int value = 1;
    public static List<String> msgBuilder = new ArrayList<>();
    private IconCompat meIcon, youIcon;
    private Person mePerson = null, youPerson = null, moodPerson = null;

    @NotificationCompat.NotificationVisibility
    private int notificationVisibility = NotificationCompat.VISIBILITY_PUBLIC;

    private int notificationPriority = NotificationCompat.PRIORITY_MAX;

    //NotificationStyle
    private NotificationCompat.MessagingStyle messagingStyle = null;
    private NotificationCompat.InboxStyle inboxStyle = null;

    public ShowNotification() {
        this.mContext = com.rjchakraborty.notificationcodes.application.Notification.getAppContext();
        if (mContext != null) {
            mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            String ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();

            if (ring != null && !TextUtils.isEmpty(ring)) {
                if (ring.equalsIgnoreCase("silent")) {
                    ringtoneUri = null;
                } else if (ring.startsWith("file")) {
                    File ringFile = new File(ring);
                    if (ringFile != null && ringFile.exists())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            ringtoneUri = FileProvider.getUriForFile(mContext, "com.rjchakraborty.withu.fileprovider", ringFile);
                        } else {
                            ringtoneUri = Uri.fromFile(ringFile);
                        }
                } else {
                    ringtoneUri = Uri.parse(ring);
                    mContext.grantUriPermission("com.android.systemui", ringtoneUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            if (vibrate) {
                vibratePattern = new long[]{0L, 1000L};
            } else {
                vibratePattern = new long[]{0L};
            }
            if (ConnectionUtil.isConnected()) {
                initNotificationStyle();
            }
        }
    }

   private void initNotificationStyle() {
        //Messaging Conversation Style
        String partnerEmail = SharedPrefer.getString(SharedPrefer.PARTNER_EMAIL_ID);
        if (partnerEmail != null) {
            User you = dbHelper.getUserDetails(partnerEmail);
            if (you != null) {
                youIcon = IconCompat.createWithResource(WITHU.getAppContext(), R.drawable.avatar_boy);
                if (you.getGender() != null && you.getGender().equalsIgnoreCase(Gender.FEMALE.nameStr)) {
                    youIcon = IconCompat.createWithResource(WITHU.getAppContext(), R.drawable.avatar_girl);
                }
                Content object = new Content();
                object.setContent(you.getImageUrl());
                object.setCType(AppConstants.LEFT_PHOTO);
                final String you_image_name = FileUtil.getFireStorageMediaName(object);
                if (you_image_name != null) {
                    final File you_image_file = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE, you_image_name);
                    if (you_image_file != null && you_image_file.exists()) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(you_image_file.getAbsolutePath(), bmOptions);
                        if (bitmap != null) {
                            youIcon = IconCompat.createWithBitmap(ImageUtil.getCircularBitmapWithWhiteBorder(bitmap, 0));
                        }
                    }
                }
                youPerson = new Person.Builder()
                        .setIcon(youIcon)
                        .setName(you.getLoveName() != null ? you.getLoveName() : mContext.getString(R.string.you))
                        .build();
                moodPerson = new Person.Builder()
                        .setIcon(youIcon)
                        .setName(AppConstants.MOOD_TITLE)
                        .build();
            }
        }


        String mEmail = SharedPrefer.getString(SharedPrefer.M_EMAIL);
        if (mEmail != null) {
            User me = dbHelper.getUserDetails(mEmail);
            if (me != null) {
                meIcon = IconCompat.createWithResource(WITHU.getAppContext(), R.drawable.avatar_boy);
                if (me.getGender() != null && me.getGender().equalsIgnoreCase(Gender.FEMALE.nameStr)) {
                    meIcon = IconCompat.createWithResource(WITHU.getAppContext(), R.drawable.avatar_girl);
                }
                Content object = new Content();
                object.setContent(me.getImageUrl());
                object.setCType(AppConstants.RIGHT_PHOTO);
                final String me_image_name = FileUtil.getFireStorageMediaName(object);
                if (me_image_name != null) {
                    final File me_image_file = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE_SENT, me_image_name);
                    if (me_image_file != null && me_image_file.exists()) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(me_image_file.getAbsolutePath(), bmOptions);
                        if (bitmap != null) {
                            meIcon = IconCompat.createWithBitmap(ImageUtil.getCircularBitmapWithWhiteBorder(bitmap, 0));
                        }
                    }
                }
                mePerson = new Person.Builder()
                        .setIcon(meIcon)
                        .setName(me.getLoveName() != null ? me.getLoveName() : mContext.getString(R.string.me))
                        .build();
            }
        }

        if (mePerson != null && youPerson != null) {
            messagingStyle = new NotificationCompat.MessagingStyle(mePerson);
        } else {
            //InBox Notification Style
            inboxStyle = new NotificationCompat.InboxStyle();
        }
    }


    public void cancelNotif(boolean all) {
        value = 1;
        msgBuilder = new ArrayList<>();
        mNotifyManager.cancel(AppConstants.NOTIFY_ID);
        if (all) {
            mNotifyManager.cancelAll();
        }
    }


    public void showNotificationWithoutReply(String notificationTitle, String notificationBody, Intent intent) {
        String id = mContext.getString(R.string.default_notification_channel_id);
        PendingIntent lowIntent = PendingIntent.getActivity(mContext, 100, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, id);
        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                    .setAutoCancel(true)
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);
        } else {
            notificationBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSound(ringtoneUri)
                    .setVibrate(vibratePattern)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                    .setAutoCancel(true)
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);
        }
        if (value == 1) {
            notificationBuilder.setContentText(notificationBody);
        } else {
            notificationBuilder.setContentText((value++) + mContext.getString(R.string.messages));
        }
        if (mNotifyManager != null) {
            mNotifyManager.notify(AppConstants.NOTIFY_ID, notificationBuilder.build());
        }
    }

    public void showNotificationWithQuickReply(long mChatTime, String notificationTitle, String notificationBody, Intent intent, boolean showReply) {
        String id = mContext.getString(R.string.default_notification_channel_id);
        if (mePerson != null && youPerson != null) {
            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(notificationBody, mChatTime, youPerson);
            messagingStyle.addMessage(message);
        } else {
            //InBox Notification Style
            if (value <= 4) {
                msgBuilder.add(notificationBody);
            }
            if (value == 1) {
                inboxStyle.setBigContentTitle(notificationTitle + " (" + (value++) + " " + mContext.getResources().getQuantityString(R.plurals.message, 1, 1) + ")");
                inboxStyle.addLine(notificationBody);
            } else {
                inboxStyle.setBigContentTitle(notificationTitle + " (" + (value++) + " " + mContext.getResources().getQuantityString(R.plurals.message, 1, 1) + ")");
                for (String msg : msgBuilder) {
                    inboxStyle.addLine(msg);
                }
                if (value < 4) {
                    inboxStyle.setSummaryText("+" + (value - 4) + mContext.getString(R.string.more));
                }
            }

        }

        PendingIntent lowIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);

        ReplyMessageService replyMessageService = new ReplyMessageService();
        Intent mReplyMessageServiceIntent = new Intent(mContext, replyMessageService.getClass());
        mReplyMessageServiceIntent.setAction(REPLY_ACTION);

        PendingIntent replyIntent = PendingIntent.getService(mContext, 100, mReplyMessageServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String replyLabel = mContext.getString(R.string.reply);

        RemoteInput replyInput = new RemoteInput.Builder(KEY_REPLY_ACTION)
                .setLabel(replyLabel)
                .build();

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, replyLabel, replyIntent)
                .addRemoteInput(replyInput)
                .setAllowGeneratedReplies(true)
                .build();

        MarkAsReadMessageService markAsReadService = new MarkAsReadMessageService();
        Intent markAsReadServiceIntent = new Intent(mContext, markAsReadService.getClass());
        markAsReadServiceIntent.setAction(MARK_AS_READ_ACTION);

        PendingIntent markAsReadIntent = PendingIntent.getService(mContext, 100, markAsReadServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String markAsReadLabel = mContext.getString(R.string.mark_as_read);

        NotificationCompat.Action markAsReadAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, markAsReadLabel, markAsReadIntent)
                .build();


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, id);
        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //This only needs to be run on Devices on Android O and above
            CharSequence name = mContext.getString(R.string.default_notification_channel_name);
            String description = mContext.getString(R.string.default_notification_channel_description); //user visible
            int importance = NotificationManager.IMPORTANCE_HIGH;

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.enableVibration(vibrate);
            mChannel.setVibrationPattern(vibratePattern);
            mChannel.setLightColor(Color.RED);
            mChannel.setSound(ringtoneUri, att);
            mChannel.setBypassDnd(true);
            mChannel.setLockscreenVisibility(notificationVisibility);
            mChannel.setShowBadge(true);

            if (mNotifyManager != null) {
                mNotifyManager.createNotificationChannel(mChannel);
            }

            notificationBuilder
                    .setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);

            if (mePerson != null && youPerson != null && messagingStyle != null) {
                notificationBuilder.setStyle(messagingStyle);
            } else if (inboxStyle != null) {
                notificationBuilder.setStyle(inboxStyle);
            }

            if (showReply) {
                notificationBuilder.addAction(replyAction);
                notificationBuilder.addAction(markAsReadAction);
            }
        } else {
            notificationBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);

            if (mePerson != null && youPerson != null && messagingStyle != null) {
                notificationBuilder.setStyle(messagingStyle);
            } else if (inboxStyle != null) {
                notificationBuilder.setStyle(inboxStyle);
            }

            if (showReply) {
                notificationBuilder.addAction(replyAction);
                notificationBuilder.addAction(markAsReadAction);
            }

        }
        if (mNotifyManager != null) {
            mNotifyManager.notify(AppConstants.NOTIFY_ID, notificationBuilder.build());
        }
    }

    public void showMoodNotification(String notificationTitle, String notificationBody, Intent intent) {
        String id = mContext.getString(R.string.default_notification_channel_id);
        if (moodPerson != null) {
            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(notificationBody, System.currentTimeMillis(), moodPerson);
            messagingStyle.addMessage(message);
        }

        PendingIntent lowIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, id);
        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //This only needs to be run on Devices on Android O and above
            CharSequence name = mContext.getString(R.string.default_notification_channel_name);
            String description = mContext.getString(R.string.default_notification_channel_description); //user visible
            int importance = NotificationManager.IMPORTANCE_HIGH;

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.enableVibration(vibrate);
            mChannel.setVibrationPattern(vibratePattern);
            mChannel.setLightColor(Color.RED);
            mChannel.setSound(ringtoneUri, att);
            mChannel.setBypassDnd(true);
            mChannel.setLockscreenVisibility(notificationVisibility);
            mChannel.setShowBadge(true);

            if (mNotifyManager != null) {
                mNotifyManager.createNotificationChannel(mChannel);
            }

            notificationBuilder
                    .setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);

            if (moodPerson != null && messagingStyle != null) {
                notificationBuilder.setStyle(messagingStyle);
            } else if (inboxStyle != null) {
                notificationBuilder.setStyle(inboxStyle);
            }

        } else {
            notificationBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);

            if (moodPerson != null && messagingStyle != null) {
                notificationBuilder.setStyle(messagingStyle);
            } else if (inboxStyle != null) {
                notificationBuilder.setStyle(inboxStyle);
            }

        }
        if (mNotifyManager != null) {
            mNotifyManager.notify(AppConstants.NOTIFY_ID, notificationBuilder.build());
        }
    }


    public void showConnectionNotification(String notificationTitle, String notificationBody, String pEmail, Intent intent) {
        PendingIntent lowIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);
        String id = mContext.getString(R.string.default_notification_channel_id);
        ConnectionService connectionService = new ConnectionService();

        //ConfirmConnection
        Intent confirmServiceIntent = new Intent(mContext, connectionService.getClass());
        confirmServiceIntent.setAction(CONFIRM_CONNECTION_ACTION);
        confirmServiceIntent.putExtra(AppConstants.EMAIL, pEmail);

        PendingIntent confirmIntent = PendingIntent.getService(mContext, 100, confirmServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String confirmLabel = mContext.getString(R.string.confirm);

        NotificationCompat.Action confirmAction = new NotificationCompat
                .Action.Builder(R.mipmap.ic_launcher, confirmLabel, confirmIntent)
                .build();

        //DeclineConnection
        Intent declineServiceIntent = new Intent(mContext, connectionService.getClass());
        declineServiceIntent.setAction(DECLINE_CONNECTION_ACTION);
        declineServiceIntent.putExtra(AppConstants.EMAIL, pEmail);

        PendingIntent declineIntent = PendingIntent.getService(mContext, 100, declineServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String declineLabel = mContext.getString(R.string.decline);

        NotificationCompat.Action declineAction = new NotificationCompat
                .Action.Builder(R.mipmap.ic_launcher, declineLabel, declineIntent)
                .build();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, id);
        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = mContext.getString(R.string.default_notification_channel_name);
            String description = mContext.getString(R.string.default_notification_channel_description); //user visible
            int importance = NotificationManager.IMPORTANCE_HIGH;

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.enableVibration(vibrate);
            mChannel.setVibrationPattern(vibratePattern);
            mChannel.setLightColor(Color.RED);
            mChannel.setSound(ringtoneUri, att);
            mChannel.setBypassDnd(true);
            mChannel.setLockscreenVisibility(notificationVisibility);
            mChannel.setShowBadge(true);

            if (mNotifyManager != null) {
                mNotifyManager.createNotificationChannel(mChannel);
            }

            notificationBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setContentTitle(notificationTitle)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(notificationBody))
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);
            notificationBuilder.addAction(confirmAction);
            notificationBuilder.addAction(declineAction);
        } else {
            notificationBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setContentTitle(notificationTitle)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(notificationBody))
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);

            notificationBuilder.addAction(confirmAction);
            notificationBuilder.addAction(declineAction);


        }
        if (mNotifyManager != null) {
            mNotifyManager.notify(AppConstants.NOTIFY_ID, notificationBuilder.build());
        }
    }


    //for normal notifications with message only like update, reminder, event
    public void showNotificationNormal(String notificationTitle, String notificationBody, Intent intent) {
        String id = mContext.getString(R.string.default_notification_channel_id);
        PendingIntent lowIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, id);
        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.default_notification_channel_name);
            String description = mContext.getString(R.string.default_notification_channel_description); //user visible
            int importance = NotificationManager.IMPORTANCE_HIGH;

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.enableVibration(vibrate);
            mChannel.setVibrationPattern(vibratePattern);
            mChannel.setLightColor(Color.RED);
            mChannel.setSound(ringtoneUri, att);
            mChannel.setBypassDnd(true);
            mChannel.setLockscreenVisibility(notificationVisibility);
            mChannel.setShowBadge(true);


            if (mNotifyManager != null) {
                mNotifyManager.createNotificationChannel(mChannel);
            }

            notificationBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setContentTitle(notificationTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                    .setAutoCancel(true)
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);

        } else {
            notificationBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(notificationPriority)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setVisibility(notificationVisibility)
                    .setContentIntent(lowIntent);

        }
        if (notificationBody != null && !TextUtils.isEmpty(notificationBody)) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody));
        }

        if (notificationTitle != null && !TextUtils.isEmpty(notificationTitle)) {
            notificationBuilder.setContentTitle(notificationTitle);
        }

        if (mNotifyManager != null) {
            mNotifyManager.notify(AppConstants.NOTIFY_ID, notificationBuilder.build());
        }

    }

    //for foreground service notification
    public Notification showForegroundNotification(String notificationTitle, String notificationBody, Intent intent, ServiceName serviceName, int progress, boolean isIntermediate) {
        String id = mContext.getString(R.string.upload_notification_channel_id);
        PendingIntent lowIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, id);
        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //Stop Service
        StopNotificationService notificationService = new StopNotificationService();
        Intent declineServiceIntent = new Intent(mContext, notificationService.getClass());
        declineServiceIntent.setAction(STOP_SERVICE_ACTION);
        declineServiceIntent.putExtra(AppConstants.SERVICE, serviceName.nameStr);

        PendingIntent stopServiceIntent = PendingIntent.getService(mContext, 100, declineServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String stopLabel = mContext.getString(R.string.cancel);

        NotificationCompat.Action stopServiceAction = new NotificationCompat
                .Action.Builder(R.mipmap.ic_launcher, stopLabel, stopServiceIntent)
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.upload_notification_channel_name);
            String description = mContext.getString(R.string.upload_notification_channel_description); //user visible
            int importance = NotificationManager.IMPORTANCE_LOW;

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(false);
            mChannel.enableVibration(false);
            mChannel.setVibrationPattern(new long[]{0L});
            mChannel.setSound(null, att);


            if (mNotifyManager != null) {
                mNotifyManager.createNotificationChannel(mChannel);
            }

            notificationBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setVibrate(new long[]{0L})
                    .setSound(null)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setContentTitle(notificationTitle)
                    .setAutoCancel(true)
                    .addAction(stopServiceAction)
                    .setContentIntent(lowIntent);

        } else {
            notificationBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setVibrate(new long[]{0L})
                    .setSound(null)
                    .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .addAction(stopServiceAction)
                    .setContentIntent(lowIntent);
        }

        if (notificationBody != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody));
        }
        if (progress != -1) {
            notificationBuilder.setProgress(100, progress, isIntermediate);
        }

        notificationBuilder.setContentText(notificationBody);

        return notificationBuilder.build();

    }

}
