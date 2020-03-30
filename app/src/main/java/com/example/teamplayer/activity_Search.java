package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.List;

public class activity_Search extends AppCompatActivity {

    //private final FirebaseFirestore;
    private static final String TAG = "ReadData";
    private static final String ACTIVITIES_COLLECTION = "Activities";

    List<String> ages = new ArrayList<>();
    List<String> objectToSearch = new ArrayList<>();
    List<String> fieldToSearch = new ArrayList<>();
    //Map<String, Object> objectToSearch = new HashMap<String, Object>();
    TextView textView;
    ScrollChoice scrollChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__search);

        initView();

        loadData();
        //getMultipleDocs();
        scrollChoice.addItems(ages, 4);
        scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                textView.setText("choise  " + name);
            }
        });

    }

    public void getMultipleDocs() {
        // [START get_multiple]
        for(int i=0; i<objectToSearch.size(); ++i){
            FirebaseFirestore.getInstance().collection(ACTIVITIES_COLLECTION)
                    .whereEqualTo(fieldToSearch.get(i), objectToSearch.get(i))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            // [END get_multiple]
        }



    }

    public void getValuesFromUsers(View view){
        EditText sportType = (EditText) findViewById(R.id.activity_name);
        EditText maxPlayers = (EditText) findViewById(R.id.max_Players);
        CheckBox payment = (CheckBox) findViewById(R.id.payment);
        //area
        //agerang

        String activityNameText = sportType.getText().toString();
        String maxPlayersText = maxPlayers.getText().toString();
        String paymentText = payment.getText().toString();
        if(!activityNameText.equals("")) {
            //objectToSearch.put("activityName", activityNameText);
            objectToSearch.add(activityNameText);
            fieldToSearch.add("activityName");
        }
        if(!maxPlayersText.equals("")) {
            objectToSearch.add(maxPlayersText);
            fieldToSearch.add("maxPlayers");
        }
        objectToSearch.add(paymentText);
        fieldToSearch.add("payment");
        //objectToSearch.put("payment", paymentText);
        //area
        //agerange
        Log.d(TAG, objectToSearch.get(0));
        getMultipleDocs();
    }

    private void loadData() {
        ages.add("12-16");
        ages.add("16-18");
        ages.add("18-21");
        ages.add("21-30");
        ages.add("30-40");
        ages.add("40-50");
        ages.add("50+");
        ages.add("Other");
    }

    private void initView() {
        textView = (TextView)findViewById(R.id.txt_result);
        scrollChoice = (ScrollChoice)findViewById(R.id.scroll_choice);
    }

    public void backButton(View view) {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }

    public void searchResult(View view) {
        Intent intent=new Intent(this,search_result.class);
        startActivity(intent);
    }
}
