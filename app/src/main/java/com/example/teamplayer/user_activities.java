package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class user_activities extends AppCompatActivity {
    private static final String ACTIVITIES_COLLECTION = "Activities";
    private FirebaseAuth mAuth;
    private String currentEmail;
    private static final String TAG = "GetActivities";
    private ArrayList<ActivityItems> mActivitiesList;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> activitiesNamesFound = new ArrayList<>();
    ArrayList<String> descriptionsFound = new ArrayList<>();
    ArrayList<String> managerFound = new ArrayList<>();
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);
        setTitle("my activities");
        searchForActivities();
    }

    public void backButton(View view) {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }

    public void searchForActivities(){
        Log.d(TAG, "Inside search for activities func");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentEmail = user.getEmail();
        FirebaseFirestore.getInstance().collection(ACTIVITIES_COLLECTION)
                .whereArrayContains("participantes",  currentEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                activitiesNamesFound.add(document.get("activityName").toString());
                                descriptionsFound.add(document.get("description").toString());
                                managerFound.add(document.get("manager_email").toString());
                            }
                            showOnscroll();

                            if(activitiesNamesFound.isEmpty()){
                                Intent intent=new Intent(user_activities.this, no_activities1.class);
                                startActivity(intent);
                            }else {
                                showOnscroll();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void showOnscroll(){
        boolean isManager = false;
        String activityNameToShow;
        mActivitiesList = new ArrayList<>();
        Log.w(TAG, "list here");
        if(activitiesNamesFound == null){
            Log.w(TAG, "null");
        }
        for(int i = 0; i < activitiesNamesFound.size(); ++i){
            activityNameToShow = activitiesNamesFound.get(i);
            String activityNameNoChanges = activityNameToShow;
            if(managerFound.get(i).equals(currentEmail)) {
                activityNameToShow += " (MANAGER)";
                isManager = true;
            }

            //String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mActivitiesList.add(new ActivityItems(activityNameNoChanges, R.drawable.project_logo, activityNameToShow, descriptionsFound.get(i),isManager));
        }
        buildRecyclerView();
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MyAdapter(mActivitiesList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {

            @Override
            public void onInfoClick(int position) {
                Log.d(TAG, "goToDetails");
                String avtivityName = activitiesNamesFound.get(position);
                String des = descriptionsFound.get(position);
                //move the details to the next screen
                if(managerFound.get(position).equals(currentEmail)){
                    goTotheNextScreen(true, avtivityName, des);
                }
                else{
                    goTotheNextScreen(false, avtivityName, des);
                }


                //goToDetails(position);

            }
        });
    }

    public void goTotheNextScreen(boolean manager, String activityName, String description){
        Intent intent;

        if(manager){
            Log.d(TAG, "manger");
            intent = new Intent(this, manager.class);
        } else {
            intent = new Intent(this, group.class);
            Log.d(TAG, " not manger");
        }
        Log.d(TAG, "show Details");
        //Intent intent = new Intent(this, activity_details.class);
        //intent.putStringArrayListExtra("Details", detailsToPass);

        intent.putExtra("ACTIVITY_NAME", activityName);
        intent.putExtra("DESCRIPTION", description);
        intent.putExtra("GOT_FROM", "my_activities");
        startActivity(intent);

    }


}
