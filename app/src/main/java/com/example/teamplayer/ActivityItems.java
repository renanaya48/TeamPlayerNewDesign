package com.example.teamplayer;

public class ActivityItems {
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
}
