package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Request screen Show all the pending requests for the manager
 */
public class requests extends AppCompatActivity {
    //a List of type hero for holding list items
    List<requestItem> requestList;
    public DatabaseReference root ;
    //the listview
    ListView listView;
    String activity_name;
    String  description ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        TextView myRequest = (TextView)  findViewById(R.id.title);
        Color c=  new Color();
        requestList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        activity_name = getIntent().getStringExtra("activity_name");
        description = getIntent().getStringExtra("DESCRIPTION");
        root = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity_name);
        //Get activity requests from DB
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

    /**
     * The function go back to the previous screen when arrow bar is pressed
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), manager.class);
        myIntent.putExtra("ACTIVITY_NAME", activity_name);
        myIntent.putExtra("DESCRIPTION", description);
        startActivityForResult(myIntent, 0);
        return true;
    }

    /**
     * The function Update the list of request to show in case request if approved or denied
     * @param dataSnapshot
     */
    private void append_request(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        //Go over all the requests
       while (i.hasNext()){

           //Add a new request iten to the list
            String message = (String) ((DataSnapshot)i.next()).getValue();
            String email = (String) ((DataSnapshot)i.next()).getValue();
            System.out.println("requestsssssss");
            System.out.println(email);
            String newMessage= message +" asked to join the group";
            requestList.add(new requestItem(newMessage,email,activity_name));
        }
        request_item_adapter adapter = new request_item_adapter(this, R.layout.activity_request_item, requestList);
        //attaching adapter to the listview
        listView.setAdapter(adapter);
    }
}
