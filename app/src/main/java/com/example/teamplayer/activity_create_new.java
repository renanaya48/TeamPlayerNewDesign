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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private static final String TAG = "saveToDataBase";
    private static final String ACTIVITIES_COLLECTION = "Activities/";
    Map<String, Object> dataToSave = new HashMap<String, Object>();
    Button buttonAge;
    Button buttonCity;
    Button buttonSportType;
    String className = "activity_create_new";
    boolean clicked = false;
    boolean clicked_city = false;
    String ageThatChosen = "CHOOSE";
    String cityThatChosen = "CHOOSE";
    String sportThatChosen = "CHOOSE";
    View viewToPass;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String user_name;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chats");
    private String activityNameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        buttonAge = (Button) findViewById(R.id.age_range);
        buttonCity = (Button)findViewById(R.id.button_city);
        buttonSportType = (Button)findViewById(R.id.sport_type_button);

        buttonAge.setText(ageThatChosen);
        buttonCity.setText(cityThatChosen);
        buttonSportType.setText(sportThatChosen);


        buttonAge.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                EditText activityName = (EditText) findViewById(R.id.activity_name);
                String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_create_new.this, age_range.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("ACTIVITY_NAME", activityNameText);
                intent.putExtra("CITY", cityThatChosen);
                intent.putExtra("SPORTS", sportThatChosen);
                startActivity(intent);
                clicked = true;

            }
        });

        buttonCity.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                EditText activityName = (EditText) findViewById(R.id.activity_name);
                String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_create_new.this, city.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("ACTIVITY_NAME", activityNameText);
                intent.putExtra("AGE", ageThatChosen);
                intent.putExtra("SPORTS", sportThatChosen);
                startActivity(intent);
                clicked = true;

            }
        });

        buttonSportType.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                EditText activityName = (EditText) findViewById(R.id.activity_name);
                String activityNameText = activityName.getText().toString();
                Intent intent = new Intent(activity_create_new.this, sport_type.class);
                intent.putExtra("ACTIVITY", className);
                intent.putExtra("ACTIVITY_NAME", activityNameText);
                intent.putExtra("AGE", ageThatChosen);
                intent.putExtra("CITY", cityThatChosen);
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
            buttonSportType.setText(sportThatChosen);
        }
        if(!clicked_city){
            cityThatChosen = getIntent().getStringExtra("CITY");
            buttonCity.setText(cityThatChosen);
            ageThatChosen = getIntent().getStringExtra("AGE");
            buttonAge.setText(ageThatChosen);
            sportThatChosen = getIntent().getStringExtra("SPORTS");
            buttonSportType.setText(sportThatChosen);
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
        viewToPass = view;
        EditText activityName = (EditText) findViewById(R.id.activity_name);
         activityNameText = activityName.getText().toString();
        createChatRoom();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection(ACTIVITIES_COLLECTION).document(activityNameText).update("Exist","").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "document updated");
                Snackbar.make(viewToPass, "Please choose a different name, this name already exist",
                        Snackbar.LENGTH_LONG)
                        .show();

                isExist = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "no such doc");
                EditText description = (EditText) findViewById(R.id.descriptionToFill);
                String descriptionText = description.getText().toString();
                if(descriptionText.equals("")){
                    Snackbar.make(viewToPass, "Description is required",
                            Snackbar.LENGTH_LONG)
                            .show();
                }else{
                    createNewDoc();
                    isExist = false;
                }

            }
        });




    }

    public void createChatRoom(){
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        DocumentReference userNAme = db.collection("Users").document(email);
        userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_name= document.getString("name");
                        createChat();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getString("name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public void createChat(){
        Map<String, Object> map = new HashMap<String, Object>();
       // Map<String, Object> mapUser = new HashMap<String, Object>();
       // DatabaseReference UsersList = root.child(activityNameText).child("participants");
       // mapUser.put(user_name,"");
        map.put(activityNameText, "");
        root.updateChildren(map);
       // UsersList.updateChildren(mapUser);
    }

    public void createNewDoc(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        EditText activityName = (EditText) findViewById(R.id.activity_name);
        String activityNameText = activityName.getText().toString();
        EditText maxPlayers = (EditText) findViewById(R.id.max_Players);
        EditText details = (EditText) findViewById(R.id.details);
        CheckBox payment = (CheckBox) findViewById(R.id.payment);
        EditText description = (EditText) findViewById(R.id.descriptionToFill);
        String descriptionText = description.getText().toString();


        String maxPlayersText = maxPlayers.getText().toString();
        String detailsText = details.getText().toString();
        String paymentText;
        if (payment.isChecked()){
            paymentText = "true";
        }else{
            paymentText = "false";
        }

        String ageRange = getIntent().getStringExtra("AGE");
        String city = getIntent().getStringExtra("CITY");
        String sportType = getIntent().getStringExtra("SPORTS");
        Log.d(TAG, "city " +city);
        Log.d(TAG, "sport " +sportType);


        String collectionName = ACTIVITIES_COLLECTION+activityNameText;

        docRef = FirebaseFirestore.getInstance().document(collectionName);
        dataToSave.put("activityName", activityNameText);
        dataToSave.put("maxPlayers", maxPlayersText);
        dataToSave.put("details", detailsText);
        dataToSave.put("payment", paymentText);
        dataToSave.put("ageRange", ageRange);
        dataToSave.put("city", city);
        dataToSave.put("sportType", sportType);
        dataToSave.put("detailsToShow", "");
        dataToSave.put("description", descriptionText);
        dataToSave.put("manager_email", email);

        docRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "document saved!");
                getToTheNextScreen();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "document faild");
            }
        });
    }

    private void getToTheNextScreen() {
        EditText activityName = (EditText) findViewById(R.id.activity_name);
        String activityNameText = activityName.getText().toString();
        EditText description = (EditText) findViewById(R.id.descriptionToFill);
        String descriptionText = description.getText().toString();
        Intent intent = new Intent(activity_create_new.this, manager.class);
        intent.putExtra("ACTIVITY", className);
        intent.putExtra("ACTIVITY_NAME", activityNameText);
//        intent.putExtra("AGE", ageThatChosen);
  //      intent.putExtra("CITY", cityThatChosen);
        intent.putExtra("DESCRIPTION", descriptionText);

        startActivity(intent);
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
