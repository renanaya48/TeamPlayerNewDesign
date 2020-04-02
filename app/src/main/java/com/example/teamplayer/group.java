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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class group extends AppCompatActivity {
    private static final String TAG = "groupActivity";
    private static final String ACTIVITIES_COLLECTION = "Activities";
    String documentActivityName;
    View getView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        TextView nameActivity = (TextView) findViewById(R.id.name_of_activity);
        documentActivityName = getIntent().getStringExtra("ACTIVITY_NAME");
        nameActivity.setText(documentActivityName);
        //TODO: add the name of the user from DB.
        detailstoDB();
    }

    public void detailstoDB(){
        EditText activityDetails = (EditText) findViewById(R.id.details_to_fill);
        String activityDetailsText = activityDetails.getText().toString();
        Button doneDetails = (Button) findViewById(R.id.button_done_details) ;
        doneDetails.setOnClickListener(new View.OnClickListener() {
            //@override
            public void onClick(View v) {
                EditText activityDetails = (EditText) findViewById(R.id.details_to_fill);
                String activityDetailsText = activityDetails.getText().toString();

                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                rootRef.collection(ACTIVITIES_COLLECTION).document(documentActivityName)
                        .update("detailsToShow",activityDetailsText).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "document updated");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "no such doc");

                    }
                });

            }
        });
    }

    public void backButton(View view) {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }
}
