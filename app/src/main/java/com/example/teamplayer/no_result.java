package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class no_result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_result);
        setTitle("search results");
    }

    public void tryAgain(View view) {
        Intent intent=new Intent(this,search_result.class);
        startActivity(intent);
    }
}
