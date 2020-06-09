package com.example.teamplayer;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import androidx.appcompat.app.AppCompatActivity;


public class chat extends AppCompatActivity {

    private ImageButton btn_send_msg;
    private static final String TAG = "PostDetailActivity";
    private EditText input_msg;
    private TextView chat_conversation;
    private String chat_msg,chat_user_name;
    private String user_name,room_name;
    private DatabaseReference root ;
    private String temp_key;
    private FirebaseAuth mAuth;
    private String user_email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<message_item> messageList;
    ListView listView;
    String  description ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btn_send_msg = (ImageButton) findViewById(R.id.btn_send);
        mAuth = FirebaseAuth.getInstance();

        //Get current user
        final FirebaseUser user = mAuth.getCurrentUser();
        final String email = user.getEmail();
        user_email= email;

        //Display back arrow on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.chatView);
        messageList = new ArrayList<>();

        //Get user name from DB
        DocumentReference userNAme = db.collection("Users").document(email);
        userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        user_name= document.getString("Name");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getString("name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        input_msg = (EditText) findViewById(R.id.msg_input);
        chat_conversation = (TextView) findViewById(R.id.textView);

        //Get the activity name and description
        room_name = getIntent().getExtras().get("room_name").toString();
        description = getIntent().getStringExtra("DESCRIPTION");
        setTitle(room_name);

        //Get Chat messages in DB
        root = FirebaseDatabase.getInstance().getReference().child("Chats").child(room_name);

        //Set on click lisitner to message send
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message =input_msg.getText().toString();
                if (!message.equals("")) {

                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);
                    //Get the current date and time
                    String timeStamp = new SimpleDateFormat("dd/MM/yy HH:mm").format(Calendar.getInstance().getTime());
                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();

                    //Save message to DB
                    map2.put("name", user_name);
                    map2.put("msg", input_msg.getText().toString());
                    map2.put("time", timeStamp);
                    map2.put("email", email);

                    message_root.updateChildren(map2);
                    input_msg.setText("");
                }
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

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


    //The function append all the messages to screen
    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            //Extract all messages details
            String email =(String)((DataSnapshot)i.next()).getValue();
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            String time_now =""+((DataSnapshot)i.next()).getValue();

            //Check if the current user is the message sender
            if (email.equals(user_email)) {
                messageList.add(new message_item(chat_msg, chat_user_name, time_now,true));
            }else {
                messageList.add(new message_item(chat_msg, chat_user_name, time_now,false));
            }
        }

        //Ctrate the messages adapter and show to user
        MessageListAdapter adapter = new MessageListAdapter(this, R.layout.recieve_message, messageList);
        //attaching adapter to the listview
        listView.setAdapter(adapter);


    }


    /**
     * The function go back to the previous screen when arrow bar is pressed
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), manager.class);
        myIntent.putExtra("ACTIVITY_NAME", room_name);
        myIntent.putExtra("DESCRIPTION", description);
        startActivityForResult(myIntent, 0);
        return true;
    }



}

