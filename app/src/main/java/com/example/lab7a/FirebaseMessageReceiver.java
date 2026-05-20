package com.example.lab7a;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService {

    private static final String TAG =
            "FirebaseMessageReceiver";

    private static final String CHANNEL_ID =
            "alarm_notification_channel_final";

    @Override
    public void onNewToken(String token) {

        Log.d(TAG, "FCM Token: " + token);
    }

    @Override
    public void onMessageReceived(
            RemoteMessage remoteMessage) {

        String title = "Notification";

        String message = "You have a new message!";

        if (remoteMessage.getNotification() != null) {

            if (remoteMessage.getNotification().getTitle()
                    != null)

                title = remoteMessage
                        .getNotification()
                        .getTitle();

            if (remoteMessage.getNotification().getBody()
                    != null)

                message = remoteMessage
                        .getNotification()
                        .getBody();
        }

        showNotification(title, message);
    }

    private void showNotification(
            String title,
            String message) {

        Context context = getApplicationContext();

        NotificationManager notificationManager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Alarm Notification Channel",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Channel for FCM notifications");

            channel.enableLights(true);

            channel.setLightColor(Color.RED);

            channel.enableVibration(true);

            channel.setVibrationPattern(
                    new long[]{0, 500, 250, 500});

            channel.setSound(
                    android.provider.Settings.System
                            .DEFAULT_NOTIFICATION_URI,

                    new AudioAttributes.Builder()
                            .setUsage(
                                    AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(
                                    AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );

            notificationManager
                    .createNotificationChannel(channel);
        }

        Vibrator vibrator =
                (Vibrator) context.getSystemService(
                        Context.VIBRATOR_SERVICE);

        if (vibrator != null) {

            vibrator.vibrate(500);
        }

        Intent intent =
                new Intent(context, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID)

                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info)

                        .setContentTitle(title)

                        .setContentText(message)

                        .setAutoCancel(true)

                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH)

                        .setDefaults(
                                NotificationCompat.DEFAULT_ALL)

                        .setContentIntent(pendingIntent)

                        .setVisibility(
                                NotificationCompat.VISIBILITY_PUBLIC);

        notificationManager.notify(
                (int) System.currentTimeMillis(),
                builder.build());
    }
}