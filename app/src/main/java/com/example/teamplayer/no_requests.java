package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class no_requests extends AppCompatActivity {

    String activity_name;
    String  description ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_new_requests);
        activity_name = getIntent().getStringExtra("activity_name");
        description = getIntent().getStringExtra("DESCRIPTION");
        //Display back arrow on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Requests");
    }


    /**
     * The function go back to the previous screen when arrow bar is pressed
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), manager.class);
        myIntent.putExtra("ACTIVITY_NAME", activity_name);
        myIntent.putExtra("DESCRIPTION", description);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
