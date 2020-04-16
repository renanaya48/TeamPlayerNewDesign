package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class manager extends AppCompatActivity {
    private String activityName;
    String activityCollection;
    String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCollection = getIntent().getStringExtra("ACTIVITY");
        activityName = getIntent().getStringExtra("ACTIVITY_NAME");
        description = getIntent().getStringExtra("DESCRIPTION");

        setContentView(R.layout.activity_manager);
        TextView activity_name = (TextView) findViewById(R.id.activity_name);
        activity_name.setText(activityName);
        TextView descriptionText = (TextView) findViewById(R.id.activity_description);
        descriptionText.setText(description);
    }
    public void chatButton(View view){

    }
    public void joinRequest(View view){
        Intent intent = new Intent(getBaseContext(), requests.class);
        intent.putExtra("activity_name", activityName);
        startActivity(intent);
    }
}
