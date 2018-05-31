package com.boomware.sample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.boomware.sdk.Boomware;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyNotificationService extends FirebaseMessagingService {

    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String CHANNEL_ID = "default";

    public void onMessageReceived(RemoteMessage remoteMessage) {

        //instance should be initialized in App.onCreate
        if (Boomware.getInstance().onPushMessageReceived(remoteMessage)) {

            //implement your notification
            showNotification(remoteMessage.getData().get(TITLE), remoteMessage.getData().get(BODY));
        }
    }


    public void showNotification(String title, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, createChannel())
                .setContentTitle(TextUtils.isEmpty(title) ? getString(R.string.app_name) : title)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(createOpenIntent())
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(message.hashCode(), mBuilder.build());
    }

    private String createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
        return CHANNEL_ID;
    }

    private PendingIntent createOpenIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

}
