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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);
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
                            }
                            if(activitiesNamesFound.isEmpty()){
                                Intent intent=new Intent(user_activities.this, no_result.class);
                                startActivity(intent);
                            }
                            showOnscroll();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]

    }

    public void showOnscroll(){

        mActivitiesList = new ArrayList<>();
        Log.w(TAG, "list here");
        if(activitiesNamesFound==null){
            Log.w(TAG, "null");
        }
        for(int i=0; i<activitiesNamesFound.size(); ++i){
            mActivitiesList.add(new ActivityItems(R.drawable.project_logo, activitiesNamesFound.get(i), descriptionsFound.get(i)));
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
                //goToDetails(position);

            }
        });
    }


}
