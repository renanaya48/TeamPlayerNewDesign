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

/**
 * Notification recieved handler
 */
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
            //Set Notification to show at the Top
            int importance = NotificationManager.IMPORTANCE_HIGH;
            //Create new notification channel
            NotificationChannel channel = new NotificationChannel("8", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            //Create notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationClass.getAppContext(), "8")
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setContentTitle("Team Player")
                    .setContentText(data)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationManager.notify(5, builder.build());
        }
    }
}
