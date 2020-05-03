package com.example.teamplayer;

import android.util.Log;
import android.view.View;

public class ActivityItems {
    private static final String TAG = "details";
    private int mImageResource;
    private String mActivityName;
    private String mDescription;

    public ActivityItems(int imageResource, String activityName, String desc) {
        mImageResource = imageResource;
        mActivityName = activityName;
        mDescription = desc;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getActivityName() {
        return mActivityName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void show(View view){
        Log.d(TAG, " hi it works!!");
    }
}
