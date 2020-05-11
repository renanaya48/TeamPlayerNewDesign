package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;



import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class select_action extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "PostDetailActivity";
    TextView welcomeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        welcomeText = (TextView)findViewById(R.id.welcomeText);
        String email = user.getEmail();
        OneSignal.setSubscription(true);
        OneSignal.sendTag("User_ID",email );
        DocumentReference userNAme = db.collection("Users").document(email);
        userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        String user_name= document.getString("Name");
                        welcomeText.setText("Hello "+user_name);



                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    public void joinActivity(View view) {
        Intent intent=new Intent(this,activity_Search.class);
        startActivity(intent);
    }

    public void createActivity(View view) {
        Intent intent=new Intent(this,activity_create_new.class);
        startActivity(intent);
    }

    public void myActivity(View view) {
        Intent intent=new Intent(this,user_activities.class);
        startActivity(intent);
    }

    public void managerButton(View view) {
        Intent intent = new Intent(this, manager.class);
        startActivity(intent);
    }
    public void groupButton(View view) {
        Intent intent = new Intent(this, group.class);
        startActivity(intent);
    }

    public void detailsButton(View view) {
        Intent intent = new Intent(this, activity_details.class);
        startActivity(intent);
    }
    public void signOutButton(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, activity_Login.class);
        startActivity(intent);
    }


}
