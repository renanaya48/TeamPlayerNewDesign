package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class sport_type extends AppCompatActivity {
    List<String> sports = new ArrayList<>();
    ScrollChoice scrollChoice_sports;
    String dataToSave_sports;
    Button buttonDone_sports;
    String classBack;
    String activityName;
    String age;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_type);
        scrollChoice_sports = (ScrollChoice)findViewById(R.id.sport_type_scroll);
        buttonDone_sports = (Button) findViewById(R.id.done_sport_type);
        classBack = getIntent().getStringExtra("ACTIVITY");
        activityName = getIntent().getStringExtra("ACTIVITY_NAME");
        age = getIntent().getStringExtra("AGE");
        city = getIntent().getStringExtra("CITY");
        setTitle("choose sport type");

        loadData();
        continueLoad();

        buttonDone_sports.setOnClickListener(new View.OnClickListener(){
            //@override
            public void onClick(View v){
                if(classBack.equals("activity_create_new")) {
                    Intent intent = new Intent(sport_type.this, activity_create_new.class);
                    intent.putExtra("SPORTS", dataToSave_sports);
                    intent.putExtra("ACTIVITY_NAME", activityName);
                    intent.putExtra("AGE", age);
                    intent.putExtra("CITY", city);
                    startActivity(intent);
                }else {
                    if (classBack.equals("activity_Search")){
                        Intent intent = new Intent(sport_type.this, activity_Search.class);
                        intent.putExtra("SPORTS", dataToSave_sports);
                        intent.putExtra("AGE", age);
                        intent.putExtra("CITY", city);
                        startActivity(intent);
                    }
                }
            }
        });

    }

    public void continueLoad() {
        scrollChoice_sports.addItems(sports, 15);
        scrollChoice_sports.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                dataToSave_sports = name;
            }
        });
    }

    public void loadData(){
        sports.add("Archery");
        sports.add("Badminton");
        sports.add("Cricket");
        sports.add("Bowling");
        sports.add("Boxing");
        sports.add("Curling");
        sports.add("Tennis");
        sports.add("Skateboarding");
        sports.add("Surfing");
        sports.add("Hockey");
        sports.add("Figure skating");
        sports.add("Yoga");
        sports.add("Fencing");
        sports.add("Fitness");
        sports.add("Gymnastics");
        sports.add("Karate");
        sports.add("Volleyball");
        sports.add("Weightlifting");
        sports.add("Basketball");
        sports.add("Baseball");
        sports.add("Rugby");
        sports.add("Wrestling");
        sports.add("High jumping");
        sports.add("Hang gliding");
        sports.add("Car racing");
        sports.add("Cycling");
        sports.add("Running");
        sports.add("Table tennis");
        sports.add("Fishing");
        sports.add("Judo");
        sports.add("Climbing");
        sports.add("Billiards");
        sports.add("Pool");
        sports.add("Shooting");
        sports.add("Horse racing");
        sports.add("Horseback riding");
        sports.add("Golf");
        sports.add("Football");
        sports.add("Soccer");
        Collections.sort(sports);
    }
}
