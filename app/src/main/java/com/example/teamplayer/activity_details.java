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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class activity_details extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        detailsList = getIntent().getStringArrayListExtra("Details");
        activitiesNameList = getIntent().getStringArrayListExtra("ACTIVITY_NAME");
        descriptionsList = getIntent().getStringArrayListExtra("DESCRIPTION");
        showDetails();

        mAuth = FirebaseAuth.getInstance();
        activity_name = detailsList.get(0);
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

    public void showDetails(){
        TextView activityNameToShow = (TextView) findViewById(R.id.activity_details_name);
        activityNameToShow.setText(detailsList.get(0));
        TextView descriptionToShow = (TextView) findViewById(R.id.description_details_1);
        descriptionToShow.setText(detailsList.get(1));
        TextView participantsNumToShow = (TextView) findViewById(R.id.number_of_participations);
        participantsNumToShow.setText("Number Of Participants: " + detailsList.get(2));
    }

    public void backToResults(View view) {
        Intent intent=new Intent(this, search_result.class);
        intent.putStringArrayListExtra("ACTIVITY_NAME", activitiesNameList);
        intent.putStringArrayListExtra("DESCRIPTION", descriptionsList);
        startActivity(intent);
    }


    public void request(View view){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user_email = user.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = ref.child("Groups").child(activity_name).orderByChild("user_email").equalTo(user_email);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    DocumentReference userNAme = db.collection("Users").document(user_email);
                    userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    temp_key = root.push().getKey();
                                    user_name= document.getString("Name");
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getString("Name"));
                                    sendNotification();
                                    userRoot= root.child(temp_key);
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("name", user_name);
                                    map.put("user_email", user_email);
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
    private void sendNotification()
    {

        Toast.makeText(this, "Current Recipients is : user1@gmail.com ( Just For Demo )", Toast.LENGTH_SHORT).show();
        System.out.println("emaillllllllllllllllllll");
        System.out.println(manager_email);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email = manager_email;

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic N2Q5ZWFkYTMtZWQ3NS00YjY3LWExYTEtMzgzZGE2ZWNjNTc5");
                        con.setRequestMethod("POST");
                        String message = user_name +" send request to join group "+activity_name;
                        String strJsonBody = "{"
                                + "\"app_id\": \"f133e9ac-0ffa-46ff-977a-acab61b82fff\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\":\"" + message + "\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }



}
