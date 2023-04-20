package com.sandhya.youtube.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sandhya.youtube.R;
import com.sandhya.youtube.activities.VideoViewActivity;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("Refresh",token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification()!=null){
            pushnotification(message.getNotification().getTitle(),message.getNotification().getBody());
        }

    }

    private void pushnotification(String title, String body) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        final String CHANNEL_ID = "push_notification";
        Intent intent = new Intent(this, VideoViewActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        CharSequence name = "Channel Name";
        String description = "Channel push notification";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,NotificationManager.IMPORTANCE_NONE);
            channel.setDescription(description);
            if (manager!=null) {
                manager.createNotificationChannel(channel);
            }
            notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .setSubText(body)
                    .setSmallIcon(R.drawable.ic_account)
                    .build();
        }else {
            notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSubText(body)
                    .setSmallIcon(R.drawable.ic_account)
                    .build();
        }
        if (manager!=null){
            manager.notify(1,notification);
        }
    }


}
