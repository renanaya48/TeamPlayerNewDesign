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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class activity_create_new extends AppCompatActivity{
    private volatile boolean isExist=false;
    private DocumentReference docRef;
    //private DocumentReference docRef = FirebaseFirestore.getInstance().document("Activities/test2");
    private static final String TAG = "saveToDataBase";
    private static final String ACTIVITIES_COLLECTION = "Activities/";
    List<String> ages = new ArrayList<>();
    List<String> sportType = new ArrayList<>();
    TextView textView;
    ScrollChoice scrollChoice;
    Map<String, Object> dataToSave = new HashMap<String, Object>();
    Button buttonAge;
    String className = "activity_create_new";
    boolean clicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        buttonAge = (Button) findViewById(R.id.age_range);
        String ageThatChosen = "CHOOSE";
        buttonAge.setText(ageThatChosen);


        buttonAge.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                EditText activityName = (EditText) findViewById(R.id.activity_name);
                String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_create_new.this, age_range.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("ACTIVITY_NAME", activityNameText);
                startActivity(intent);
                clicked = true;

            }
        });
        if (!clicked) {
            ageThatChosen = getIntent().getStringExtra("AGE");
            buttonAge.setText(ageThatChosen);
        }
        EditText activityName = (EditText) findViewById(R.id.activity_name);
        String name = getIntent().getStringExtra("ACTIVITY_NAME");
        activityName.setText(name);
    }


    public void backButton(View view) {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }

    public void SaveData(View view) throws InterruptedException {
        EditText activityName = (EditText) findViewById(R.id.activity_name);
        String activityNameText = activityName.getText().toString();

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        //DocumentReference docIdRef = rootRef.collection(ACTIVITIES_COLLECTION).document(activityNameText);
        rootRef.collection(ACTIVITIES_COLLECTION).document(activityNameText).update("Exist","").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "document updated");
                //TODO: message to change name
                isExist = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "no such doc");
                createNewDoc();
                isExist = false;
            }
        });




    }

    public void createNewDoc(){
        EditText activityName = (EditText) findViewById(R.id.activity_name);
        String activityNameText = activityName.getText().toString();
        EditText sportType = (EditText) findViewById(R.id.sport_type);
        //EditText area = (EditText) findViewById(R.id.activity_name);
        EditText maxPlayers = (EditText) findViewById(R.id.max_Players);
        EditText details = (EditText) findViewById(R.id.details);
        CheckBox payment = (CheckBox) findViewById(R.id.payment);


        String maxPlayersText = maxPlayers.getText().toString();
        String detailsText = details.getText().toString();
        String paymentText = payment.getText().toString();
        String sportTypeText = sportType.getText().toString();
        String ageRange = getIntent().getStringExtra("AGE");
        Log.d(TAG, "age range: " +ageRange);

        String collectionName = ACTIVITIES_COLLECTION+activityNameText;

        //checkIfDocExsist(activityNameText);

        docRef = FirebaseFirestore.getInstance().document(collectionName);
        dataToSave.put("activityName", activityNameText);
        dataToSave.put("maxPlayers", maxPlayersText);
        dataToSave.put("details", detailsText);
        dataToSave.put("payment", paymentText);
        dataToSave.put("ageRange", ageRange);
        dataToSave.put("sportType", sportTypeText);

        docRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "document saved!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "document faild");
            }
        });
    }



    public synchronized boolean isExistsDoc (String activityNameText){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        //DocumentReference docIdRef = rootRef.collection(ACTIVITIES_COLLECTION).document(activityNameText);
        rootRef.collection(ACTIVITIES_COLLECTION).document(activityNameText).update("Exist","").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "document updated");
                isExist = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "no such doc");
                isExist = false;
            }
        });
        return isExist;
    }




}
