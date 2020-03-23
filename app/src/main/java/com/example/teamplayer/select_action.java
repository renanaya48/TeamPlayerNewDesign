package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class select_action extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
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
}
