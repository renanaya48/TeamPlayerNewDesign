package com.example.teamplayer;

import android.util.Log;
import android.view.View;

public class ActivityItems {
    //members
    private static final String TAG = "details";
    private int mImageResource;
    private String mActivityName;
    private String mDescription;
    private boolean isManager;

    /**
     * constructor
     * @param imageResource the image that be shown on the screen
     * @param activityName the activity name
     * @param desc the activity description
     * @param isManager true if the user is the manager of the activity
     */
    public ActivityItems(int imageResource, String activityName, String desc, boolean isManager) {
        mImageResource = imageResource;
        mActivityName = activityName;
        mDescription = desc;
        this.isManager=isManager;
    }

    /**
     *
     * @return the picture of the activity
     */
    public int getImageResource() {
        return mImageResource;
    }

    /**
     *
     * @return the activity name
     */
    public String getActivityName() {
        return mActivityName;
    }

    /**
     *
     * @return the activity description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     *
     * @return true if the user is manger and false if not
     */
    public boolean isManager(){
        return this.isManager;
    }

    public void show(View view){
        Log.d(TAG, " hi it works!!");
    }
}
