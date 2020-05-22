package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class search_result extends AppCompatActivity {
    private static final String TAG = "SearchResult";
    private static final String ACTIVITIES_COLLECTION = "Activities";

    private ArrayList<ActivityItems> mActivitiesList;
    private ArrayList<String> detailsToPass;
    private ArrayList<String> activitiesNamesList;
    private ArrayList<String> descriptionsList;
    private ArrayList<String> managerList;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private String currentUserEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Log.d(TAG, "in onCreate");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentUserEmail = user.getEmail();
        Log.d(TAG, "after onCreate");
        Log.d(TAG, "user " + currentUserEmail);
       // ArrayList<String> activitiesNamesLIST= getIntent().getStringArrayListExtra("ACTIVITY_NAME");
       // ArrayList<String> descriptionsLIST= getIntent().getStringArrayListExtra("DESCRIPTION");
        createActivityList();
        buildRecyclerView();
        //setButtons();
    }

    public void createActivityList() {
        boolean isManager=false;
        activitiesNamesList= getIntent().getStringArrayListExtra("ACTIVITY_NAME");
        Log.d(TAG, "numOfList" + String.valueOf(activitiesNamesList.size()));
        descriptionsList= getIntent().getStringArrayListExtra("DESCRIPTION");
        managerList = getIntent().getStringArrayListExtra("MANAGER");


        String activityNameToShow;
        mActivitiesList = new ArrayList<>();

        for(int i=0; i<activitiesNamesList.size(); ++i){
            activityNameToShow = activitiesNamesList.get(i);
            if(managerList.get(i).equals(currentUserEmail)){
                activityNameToShow += " (MANAGER)";
            }
            mActivitiesList.add(new ActivityItems(R.drawable.project_logo, activityNameToShow, descriptionsList.get(i),isManager));
        }
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
            public void onInfoClick(final int position) {
                detailsToPass = new ArrayList<>();
                Log.d(TAG, "goToDetails");
                String documentActivityName = mActivitiesList.get(position).getActivityName();
                Log.i(TAG, "name1 " + documentActivityName);
                if (documentActivityName.contains(" (MANAGER)")){
                    documentActivityName = documentActivityName.replace(" (MANAGER)", "");
                    Log.i(TAG, "name2 " + documentActivityName);
                }
                detailsToPass.add(documentActivityName);
                detailsToPass.add(mActivitiesList.get(position).getDescription());
                Log.i(TAG, "name3 " + documentActivityName);

                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection(ACTIVITIES_COLLECTION).document(documentActivityName);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String numOfParticipants = "";
                                Object participantes = document.get("participantes");
                                Object manger = document.get("manager_email");
                                Log.i(TAG, "manager " + manger);
                                if (participantes == null) {
                                    Log.i(TAG, "null");
                                } else {
                                    numOfParticipants = participantes.toString();
                                    int len = numOfParticipants.length()-1;
                                    numOfParticipants = numOfParticipants.substring(1, len);
                                    Log.i(TAG, numOfParticipants.toString());
                                    String [] strSplit = numOfParticipants.split(", ");
                                    detailsToPass.add(String.valueOf(strSplit.length));
                                }
                                if (manger.toString().equals(currentUserEmail)){
                                    Log.i(TAG, "manager " + manger.toString());
                                    Log.i(TAG, "User " + currentUserEmail);
                                    showDetails(true, numOfParticipants, position);
                                } else{
                                    Log.i(TAG, "not manager " + manger.toString());
                                    Log.i(TAG, "User " + currentUserEmail);
                                    showDetails(false, numOfParticipants, position);
                                }

                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }

                });


            }
        });
    }



    public void showDetails(boolean manager, String participants, int position){
        Intent intent;
        String activityName = activitiesNamesList.get(position);
        String description = descriptionsList.get(position);
        if (manager){
            intent = new Intent(this, manager.class);
            intent.putExtra("ACTIVITY_NAME", activityName);
            intent.putExtra("DESCRIPTION", description);
        } else {
            if(participants.contains(currentUserEmail)){
                intent = new Intent(this, group.class);
                intent.putExtra("ACTIVITY_NAME", activityName);
                intent.putExtra("DESCRIPTION", description);
            }else {
                intent = new Intent(this, activity_details.class);
                intent.putStringArrayListExtra("Details", detailsToPass);
                intent.putStringArrayListExtra("ACTIVITY_NAME", activitiesNamesList);
                intent.putStringArrayListExtra("DESCRIPTION", descriptionsList);
            }
        }
        Log.d(TAG, "show Details");
        //Intent intent = new Intent(this, activity_details.class);

        startActivity(intent);
    }

    public void backButton(View view) {
        Intent intent=new Intent(this,activity_Search.class);
        startActivity(intent);
    }

}
