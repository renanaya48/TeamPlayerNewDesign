package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class edit_activity extends AppCompatActivity {
    //members
    private static final String ACTIVITIES_COLLECTION = "Activities";
    private static final String TAG = "EditActivity";
    String activityName;
    String description;
    String ageRange;
    String city;
    String maxPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);
        activityName = getIntent().getStringExtra("ACTIVITY_NAME");
        findDetails();
        Button saveChanges = (Button)findViewById(R.id.save_changes);

        //get the changes that the manager did
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editDescription = (EditText)findViewById(R.id.edit_description);
                description = editDescription.getText().toString();
                EditText editCity = (EditText)findViewById(R.id.city_editText);
                EditText editAgeRange = (EditText)findViewById(R.id.age_range_fill);
                EditText editMaxPlayers = (EditText)findViewById(R.id.editMaxPlayers);
                city = editCity.getText().toString();
                ageRange = editAgeRange.getText().toString();
                maxPlayers = editMaxPlayers.getText().toString();
                Log.d(TAG, ageRange + " " + city + " " + maxPlayers + " " + description);
                updateActivity();

            }
        });

    }

    /**
     * save the changes to DB
     */
    private void updateActivity() {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(ACTIVITIES_COLLECTION).document(activityName);
        docRef.update("ageRange", ageRange, "city", city,
                "maxPlayers", maxPlayers, "description", description)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "details updated");
                        goToManagerScreen();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "not updated");

            }
        });
    }

    /**
     * After updating the activity, go back to the manager screen
     */
    private void goToManagerScreen() {
        Intent intent = new Intent(this, manager.class);
        intent.putExtra("ACTIVITY_NAME", activityName);
        intent.putExtra("DESCRIPTION", description);
        startActivity(intent);
    }

    /**
     * show the current details of the activity that the manager wants to change
     */
    private void showDetails() {
        TextView editActivityName = (TextView) findViewById(R.id.edit_activity);
        String nameEditToShow = "Edit Activity " + activityName;
        editActivityName.setText(nameEditToShow);
        description = getIntent().getStringExtra("DESCRIPTION");
        EditText editDescription = (EditText)findViewById(R.id.edit_description);
        editDescription.setText(description);
        EditText editCity = (EditText)findViewById(R.id.city_editText);
        EditText editAgeRange = (EditText)findViewById(R.id.age_range_fill);
        EditText editMaxPlayers = (EditText)findViewById(R.id.editMaxPlayers);
        editCity.setText(city);
        editAgeRange.setText(ageRange);
        editMaxPlayers.setText(maxPlayers);

    }

    /**
     * find the current details of the activity from the DB
     */
    public void findDetails(){
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(ACTIVITIES_COLLECTION).document(activityName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        //get the age range, city and max players
                        ageRange = document.getString("ageRange");
                        city = document.getString("city");
                        maxPlayers = document.getString("maxPlayers");
                        showDetails();
                    } else {
                        Log.d(TAG, "No such document");
                        showDetails();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    showDetails();
                }
            }
        });
    }
}
