package com.example.teamplayer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class activity_details extends AppCompatActivity {
    //members
    private static final String ACTIVITIES_COLLECTION = "Activities";
    private String activity_name;
    private String manager_email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private static final String TAG = "PostDetailActivity";
    private String user_name;
    private String user_email;
    private DatabaseReference root ;
    private DatabaseReference userRoot ;
    private String temp_key;
    ArrayList<String> detailsList;
    ArrayList<String> activitiesNameList;
    ArrayList<String> descriptionsList;
    String ageRange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        detailsList = getIntent().getStringArrayListExtra("Details");
        activitiesNameList = getIntent().getStringArrayListExtra("ACTIVITY_NAME");
        descriptionsList = getIntent().getStringArrayListExtra("DESCRIPTION");
        getAgeRange();

        mAuth = FirebaseAuth.getInstance();
        activity_name = detailsList.get(0);
        //get the manager email
        DocumentReference userNAme = db.collection("Activities").document(activity_name);
        userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        manager_email= document.getString("manager_email");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getString("name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        root = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity_name);
        FirebaseUser user = mAuth.getCurrentUser();
        user_email = user.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = root.orderByChild("user_email").equalTo(user_email);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                   Button requestButton = (Button) findViewById(R.id.request);
                    requestButton.setClickable(false);
                    requestButton.setText("Send");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });



    }

    /**
     * show the detail of the activity: name, description, participants number, age range.
     */
    public void showDetails(){
        TextView activityNameToShow = (TextView) findViewById(R.id.activity_details_name);
        activityNameToShow.setText(detailsList.get(0));
        TextView descriptionToShow = (TextView) findViewById(R.id.description_details_1);
        descriptionToShow.setText(detailsList.get(1));
        TextView participantsNumToShow = (TextView) findViewById(R.id.number_of_participations);
        String particNum = "Number Of Participants: " + detailsList.get(2);
        participantsNumToShow.setText(particNum);
        TextView ageRangeToShow = (TextView)findViewById(R.id.age_Range_textView);
        String ageRangeDetails = "Age Range: " + ageRange;
        ageRangeToShow.setText(ageRangeDetails);


    }

    /**
     * get the age range of the activity from the DB
     */
    private void getAgeRange() {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(ACTIVITIES_COLLECTION).document(detailsList.get(0));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ageRange = document.getString("ageRange");
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

    /**
     * back to search results
     * @param view the View
     */
    public void backToResults(View view) {
        Intent intent=new Intent(this, search_result.class);
        intent.putStringArrayListExtra("ACTIVITY_NAME", activitiesNameList);
        intent.putStringArrayListExtra("DESCRIPTION", descriptionsList);
        startActivity(intent);
    }


    /**
     * The function send request to manager to join group
     * @param view
     */
    public void request(View view){
        //Get current user email
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user_email = user.getEmail();
        //Check if user is already in the group
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = ref.child("Groups").child(activity_name).orderByChild("user_email").equalTo(user_email);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //If user is not a member
                if (!dataSnapshot.exists()){
                    DocumentReference userNAme = db.collection("Users").document(user_email);
                    userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    //Get user name and send request notification to maneger
                                    temp_key = root.push().getKey();
                                    user_name= document.getString("Name");
                                    String message = user_name +" send request to join group "+activity_name;
                                    NotificationSender sender=new NotificationSender(manager_email,message);
                                    sender.sendNotification();
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getString("Name"));
                                    //sendNotification();
                                    //Add request to DB
                                    userRoot= root.child(temp_key);
                                    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    System.out.println("userYIdddddddddddd222222222222222");
                                    System.out.println(userUid);
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("name", user_name);
                                    map.put("user_email", user_email);
                                    map.put("user_Uid", userUid);
                                    userRoot.updateChildren(map);

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }


}
