package com.example.teamplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.util.Log;

import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationManagerCompat;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class NotificationsReceiveHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        String data = notification.payload.body;
        System.out.println("dataaaaaaaaaaaaaaa");
        System.out.println(data);
        String customKey;
        Notification.Builder builder = new Notification.Builder(ApplicationClass.getAppContext());
        builder.setContentTitle("Team Player").setContentText(data).setSmallIcon(R.drawable.ic_stat_notification).setPriority(Notification.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ApplicationClass.getAppContext());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}
