package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;

public class participants_Items {
    private static final String TAG = "participants";
    private int mImageResource;
    private String mParticipantName;
    private String mAge;
    //private String mDescription;

    public participants_Items(int imageResource, String participantName, String age) {
        mImageResource = imageResource;
        mParticipantName = participantName;
        mAge = age;
        //mDescription = desc;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getParticipantName() {
        return mParticipantName;
    }

    public String getAge(){return mAge;}

    /*
    public String getDescription() {
        return mDescription;
    }
    */

    public void show(View view){
        Log.d(TAG, " hi its work!!");
    }
}

