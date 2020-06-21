package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;

public class no_requests extends AppCompatActivity {

    String activity_name;
    String  description ;
    String backTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_requests);
        activity_name = getIntent().getStringExtra("activity_name");
        description = getIntent().getStringExtra("DESCRIPTION");
        backTo = getIntent().getStringExtra("GOT_FROM");
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
        myIntent.putExtra("GOT_FROM", backTo);
        if(backTo.equals("search_result")){
            ArrayList<String> activitiesNamesList= getIntent().getStringArrayListExtra("ACTIVITIES_NAME_LIST");
            ArrayList <String> descriptionsList= getIntent().getStringArrayListExtra("DESCRIPTIONS_LIST");
            ArrayList <String>managerList = getIntent().getStringArrayListExtra("MANAGER_LIST");

            myIntent.putStringArrayListExtra("ACTIVITIES_NAME_LIST", activitiesNamesList);
            myIntent.putStringArrayListExtra("DESCRIPTIONS_LIST", descriptionsList);
            myIntent.putStringArrayListExtra("MANAGER_LIST", managerList);
        }
        startActivityForResult(myIntent, 0);
        return true;
    }
}
