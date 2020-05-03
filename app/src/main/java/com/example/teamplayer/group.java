package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class group extends AppCompatActivity {
    private static final String TAG = "groupActivity";
    private static final String ACTIVITIES_COLLECTION = "Activities";
    private static final String USERS_COLLECTION = "Users";
    String documentActivityName;
    View getView;
    private ArrayList<participants_Items> mParticipantsList;

    private RecyclerView mRecyclerView;
    private participantAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        TextView nameActivity = (TextView) findViewById(R.id.name_of_activity);
        documentActivityName = getIntent().getStringExtra("ACTIVITY_NAME");
        nameActivity.setText(documentActivityName);
        documentActivityName = "Run123";
        //TODO: add the name of the user from DB.
        detailstoDB();

        createParticipantsList();
    }

    public void detailstoDB(){
    }

    public void backButton(View view) {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }
    public void chatButton(View view){
        Intent intent = new Intent(getApplicationContext(), chat.class);
        intent.putExtra("room_name", documentActivityName);
        startActivity(intent);
    }



    public void createParticipantsList() {
        mParticipantsList = new ArrayList<>();
        Log.i(TAG, documentActivityName);
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(ACTIVITIES_COLLECTION).document(documentActivityName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Object usersEmails = document.get("participantes");
                        if(usersEmails==null){
                            Log.i(TAG, "null");
                        }
                        else {
                            String listOfEmails = usersEmails.toString();
                            int len = listOfEmails.length()-1;
                            listOfEmails = listOfEmails.substring(1, len);
                            Log.i(TAG, usersEmails.toString());
                            String [] strSplit = listOfEmails.split(", ");
                            for ( int i=0; i<strSplit.length; ++i){
                                Log.i(TAG, strSplit[i]);
                                FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                                        .whereEqualTo("Email", strSplit[i])
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                //int counter = 0;
                                                //ArrayList<participants_Items> participantList = new ArrayList<>();
                                                if(task.isSuccessful()){
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String name = document.getString("Name");
                                                        String d = document.getString("DateOfBirth");

                                                        Calendar dob = Calendar.getInstance();
                                                        Calendar today = Calendar.getInstance();

                                                        String[] parts = d.split("/");
                                                        int year = Integer.parseInt(parts[2]);
                                                        int month = Integer.parseInt(parts[1]);
                                                        int day = Integer.parseInt(parts[0]);
                                                        System.out.println("dgbfdbh " + day + "/" + month + "/" + year);

                                                        dob.set(year, month, day);
                                                        int age = today.get(Calendar.YEAR)-dob.get(Calendar.YEAR);
                                                        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
                                                            age--;
                                                        }
                                                        Log.d(TAG, String.valueOf(age));

                                                        Log.d(TAG, name);
                                                        mParticipantsList.add(new participants_Items(R.drawable.project_logo, name, "Age: " + String.valueOf(age)));
                                                    }
                                                    buildRecyclerView();
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }

                                            }

                                        });
                                // [END get_multiple]

                            }
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

    public void buildRecyclerView() {
        Log.w(TAG, "build Recycle " + mParticipantsList.get(0).getParticipantName());
        Log.w(TAG, String.valueOf(mParticipantsList.size()));
        mRecyclerView = findViewById(R.id.par_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new participantAdapter(mParticipantsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new participantAdapter.OnItemClickListener() {

            @Override
            public void onInfoClick(int position) {
                Log.d(TAG, "goToDetails");
                //goToDetails(position);

            }
        });
    }

    public void searchResult() {
        Intent intent=new Intent(this,search_result.class);
        //intent.putExtra("ACTIVITY", className);
        //intent.putStringArrayListExtra("ACTIVITY_NAME", activitiesNamesFound);
        //Log.d(TAG, "size in search " + String.valueOf(activitiesNamesFound.size()));
        //intent.putStringArrayListExtra("DESCRIPTION", descriptionsFound);
        startActivity(intent);

    }
}
