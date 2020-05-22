package com.example.teamplayer;

import android.util.Log;
import android.view.View;

public class ActivityItems {
    private static final String TAG = "details";
    private int mImageResource;
    private String mActivityName;
    private String mDescription;
    private boolean isManager;

    public ActivityItems(int imageResource, String activityName, String desc, boolean isManager) {
        mImageResource = imageResource;
        mActivityName = activityName;
        mDescription = desc;
        this.isManager=isManager;
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

    public boolean isManager(){
        return this.isManager;
    }

    public void show(View view){
        Log.d(TAG, " hi it works!!");
    }
}
