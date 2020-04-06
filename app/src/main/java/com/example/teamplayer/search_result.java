package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class search_result extends AppCompatActivity {
    private static final String TAG = "SearchResult";

    private ArrayList<ActivityItems> mActivitiesList;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ArrayList<String> activitiesNamesLIST= getIntent().getStringArrayListExtra("ACTIVITY_NAME");
        ArrayList<String> descriptionsLIST= getIntent().getStringArrayListExtra("DESCRIPTION");
        createActivityList();
        buildRecyclerView();
        //setButtons();
    }

    public void createActivityList() {
        ArrayList<String> activitiesNamesLIST= getIntent().getStringArrayListExtra("ACTIVITY_NAME");
        Log.d(TAG, "numOfList" + String.valueOf(activitiesNamesLIST.size()));
        ArrayList<String> descriptionsLIST= getIntent().getStringArrayListExtra("DESCRIPTION");
        //Log.d(TAG, descriptionsLIST.get(0));

        mActivitiesList = new ArrayList<>();
        for(int i=0; i<activitiesNamesLIST.size(); ++i){
            mActivitiesList.add(new ActivityItems(R.drawable.project_logo, activitiesNamesLIST.get(i), descriptionsLIST.get(i)));
        }
        mActivitiesList.add(new ActivityItems(R.drawable.project_logo, "Line 1", "Line 2"));
        mActivitiesList.add(new ActivityItems(R.drawable.project_logo, "Line 3", "Line 4"));
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

    public void backButton(View view) {
        Intent intent=new Intent(this,activity_Search.class);
        startActivity(intent);
    }
}
