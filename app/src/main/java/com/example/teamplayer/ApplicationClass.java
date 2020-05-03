package com.example.teamplayer;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;

import static com.onesignal.OneSignal.OSInFocusDisplayOption.None;

public class ApplicationClass extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(None).setNotificationReceivedHandler(new NotificationsReceiveHandler())
                .init();
        ApplicationClass.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationClass.context;
    }
}
