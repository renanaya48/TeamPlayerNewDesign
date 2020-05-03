package com.example.teamplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class NotificationsReceiveHandler implements OneSignal.NotificationReceivedHandler {
    private Context mContext;

    @Override
    public void notificationReceived(OSNotification notification) {
        mContext= ApplicationClass.getAppContext();
        String data = notification.payload.body;
        String customKey;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ll";
            String description = "88";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("8", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationClass.getAppContext(), "8")
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setContentTitle("Team Player")
                    .setContentText(data)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);


// notificationId is a unique int for each notification that you must define
            notificationManager.notify(5, builder.build());
        }
    }
}
