package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;



import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class select_action extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.sendTag("User_ID",email );

    }

    public void joinActivity(View view) {
        Intent intent=new Intent(this,activity_Search.class);
        startActivity(intent);
    }

    public void createActivity(View view) {
        Intent intent=new Intent(this,activity_create_new.class);
        startActivity(intent);
    }

    public void myActivity(View view) {
        Intent intent=new Intent(this,user_activities.class);
        startActivity(intent);
    }

    public void managerButton(View view) {
        Intent intent = new Intent(this, manager.class);
        startActivity(intent);
    }

    public void detailsButton(View view) {
        Intent intent = new Intent(this, activity_details.class);
        startActivity(intent);
    }


}
