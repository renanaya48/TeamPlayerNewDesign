package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class no_activities1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_activities1);
    }

    public void goToSearch(View view){
        Intent intent = new Intent(this, activity_Search.class);
        startActivity(intent);
    }
}
