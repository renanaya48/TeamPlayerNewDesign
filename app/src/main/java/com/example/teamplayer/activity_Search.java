package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.webianks.library.scroll_choice.ScrollChoice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activity_Search extends AppCompatActivity {

    //private final FirebaseFirestore;
    private static final String TAG = "ReadData";
    private static final String ACTIVITIES_COLLECTION = "Activities";
    Button buttonAge;
    Button buttonCity;
    Button buttonsportType;
    String className = "activity_Search";
    boolean clicked = false;
    String ageThatChosen = "CHOOSE";
    String sportThatChosen = "CHOOSE";
    String cityThatChosen= "CHOOSE";

    Map<String, Object> documentData;
    ArrayList<String> activitiesNamesFound = new ArrayList<>();
    ArrayList<String> descriptionsFound = new ArrayList<>();
    View view;

    List<String> objectToSearch = new ArrayList<>();
    List<String> fieldToSearch = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__search);

        buttonAge = (Button) findViewById(R.id.age_range2);
        buttonCity = (Button) findViewById(R.id.city_button);
        buttonsportType = (Button) findViewById(R.id.sports_button);
        buttonAge.setText(ageThatChosen);
        buttonCity.setText(cityThatChosen);
        buttonsportType.setText(cityThatChosen);


        buttonAge.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                //EditText activityName = (EditText) findViewById(R.id.activity_name);
                //String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_Search.this, age_range.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("SPORTS", sportThatChosen);
                intent.putExtra("CITY", cityThatChosen);
                startActivity(intent);
                clicked = true;

            }
        });
        buttonsportType.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                //EditText activityName = (EditText) findViewById(R.id.activity_name);
                //String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_Search.this, sport_type.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("AGE", ageThatChosen);
                intent.putExtra("CITY", cityThatChosen);
                startActivity(intent);
                clicked = true;

            }
        });
        buttonCity.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                //EditText activityName = (EditText) findViewById(R.id.activity_name);
                //String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_Search.this, city.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("AGE", ageThatChosen);
                intent.putExtra("SPORTS", sportThatChosen);
                startActivity(intent);
                clicked = true;

            }
        });
        if (!clicked) {
            ageThatChosen = getIntent().getStringExtra("AGE");
            buttonAge.setText(ageThatChosen);
            cityThatChosen = getIntent().getStringExtra("CITY");
            buttonCity.setText(cityThatChosen);
            sportThatChosen = getIntent().getStringExtra("SPORTS");
            buttonsportType.setText(sportThatChosen);

        }

    }

    public void getMultipleDocs(View viewL) {
        view = viewL;
        CheckBox payment = (CheckBox) findViewById(R.id.payment2);

        if(!payment.isChecked()){
            // [START get_multiple]
            FirebaseFirestore.getInstance().collection(ACTIVITIES_COLLECTION)
                    .whereEqualTo("sportType", sportThatChosen).whereEqualTo("ageRange", ageThatChosen)
                    .whereEqualTo("city", cityThatChosen).whereEqualTo("payment", "false")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    activitiesNamesFound.add(document.get("activityName").toString());
                                    descriptionsFound.add(document.get("description").toString());
                                }

                                searchResult(view);

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                    // [END get_multiple]

        }else{
            FirebaseFirestore.getInstance().collection(ACTIVITIES_COLLECTION)
                    .whereEqualTo("sportType", sportThatChosen).whereEqualTo("ageRange", ageThatChosen)
                    .whereEqualTo("city", cityThatChosen).whereEqualTo("payment", "true")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    activitiesNamesFound.add(document.get("activityName").toString());
                                    descriptionsFound.add(document.get("description").toString());
                                }
                                searchResult(view);

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            // [END get_multiple]


        }

    }

    public void getValuesFromUsers(View view){
        EditText sportType = (EditText) findViewById(R.id.activity_name);
        //area
        //agerang

        String activityNameText = sportType.getText().toString();
        if(!activityNameText.equals("")) {
            //objectToSearch.put("activityName", activityNameText);
            objectToSearch.add(activityNameText);
            fieldToSearch.add("activityName");
        }

        fieldToSearch.add("payment");
        //objectToSearch.put("payment", paymentText);
        //area
        //agerange
        Log.d(TAG, objectToSearch.get(0));
    }

    public void checkThatHaveAll(View view){
        Log.d(TAG, "check!");

        if((ageThatChosen == null)
            ||(sportThatChosen==null)
            ||(cityThatChosen==null)){
            Log.d(TAG, "check1!");
            Snackbar.make(view, "Please search by sport type, age range and area",
                    Snackbar.LENGTH_LONG)
                    .show();

        }else{
            getMultipleDocs(view);
        }

    }



    public void backButton(View view) {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }

    public void searchResult(View view) {
        Intent intent=new Intent(this,search_result.class);
        intent.putExtra("ACTIVITY", className);
        intent.putStringArrayListExtra("ACTIVITY_NAME", activitiesNamesFound);
        Log.d(TAG, "size in search " + String.valueOf(activitiesNamesFound.size()));
        intent.putStringArrayListExtra("DESCRIPTION", descriptionsFound);
        startActivity(intent);

    }
}
