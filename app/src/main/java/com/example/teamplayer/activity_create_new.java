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

public class activity_create_new extends AppCompatActivity {
    private DocumentReference docRef = FirebaseFirestore.getInstance().document("Activities/test");
    private static final String TAG = "saveToDataBase";
    List<String> ages = new ArrayList<>();
    List<String> sportType = new ArrayList<>();
    TextView textView;
    ScrollChoice scrollChoice;
    Map<String, Object> dataToSave = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
    }
    public void backButton(View view) {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }

    public void SaveData(View view){
        EditText activityName = (EditText) findViewById(R.id.activity_name);
        //EditText sportType = (EditText) findViewById(R.id.sportType);
        //EditText area = (EditText) findViewById(R.id.activity_name);
        EditText maxPlayers = (EditText) findViewById(R.id.maxPlayers);
        EditText details = (EditText) findViewById(R.id.details);
        CheckBox payment = (CheckBox) findViewById(R.id.payment);

        String activityNameText = activityName.getText().toString();
        String maxPlayersText = maxPlayers.getText().toString();
        String detailsText = details.getText().toString();
        String paymentText = payment.getText().toString();


        dataToSave.put("activityName", activityNameText);
        dataToSave.put("maxPlayers", maxPlayersText);
        dataToSave.put("details", detailsText);
        dataToSave.put("payment", paymentText);

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

    public void initViewAge(View view) {
       // textView = (TextView)findViewById(R.id.txt_result);
        scrollChoice = (ScrollChoice)findViewById(R.id.scroll_choice);
        loadData();
        continueLoad();
    }

    public void loadData() {
        ages.add("12-16");
        ages.add("16-18");
        ages.add("18-21");
        ages.add("21-30");
        ages.add("30-40");
        ages.add("40-50");
        ages.add("50+");
        ages.add("Other");
    }



    public void continueLoad() {
        scrollChoice.addItems(ages, 4);
        scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                dataToSave.put("ageRange", name);
                Log.d(TAG, "age " + name);
                Button button = (Button)findViewById(R.id.age_range);
                button.setText(name);
                //textView.setText("choise  " + name);
            }
        });
    }
}
