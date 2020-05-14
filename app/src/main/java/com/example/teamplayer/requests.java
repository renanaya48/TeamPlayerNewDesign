package com.example.teamplayer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class requests extends AppCompatActivity {
    //a List of type hero for holding list items
    List<requestItem> requestList;
    private DatabaseReference root ;
    //the listview
    ListView listView;
    String activity_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        requestList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        activity_name = getIntent().getStringExtra("activity_name");
        root = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity_name);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_request(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_request(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void append_request(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

       while (i.hasNext()){
            String message = (String) ((DataSnapshot)i.next()).getValue();
           String email = (String) ((DataSnapshot)i.next()).getValue();
            String newMessage= message +" asked to join the group";
            System.out.println("userrrrrrrrrrrrrrrrr");
           System.out.println(email);
            requestList.add(new requestItem(newMessage,email,activity_name));
        }
        request_item_adapter adapter = new request_item_adapter(this, R.layout.activity_request_item, requestList);
        //attaching adapter to the listview
        listView.setAdapter(adapter);



    }


}
