package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class manager extends AppCompatActivity {

    private static final String TAG = "managerActivity";
    private static final String ACTIVITIES_COLLECTION = "Activities";
    private static final String USERS_COLLECTION = "Users";
    private String activityName;
    String description;
    String documentActivityName;
    private ArrayList<participants_Items> mParticipantsList;
    //private ArrayList<String> emailsList = new ArrayList<>();
    String emailToDelete;
    private ImageView groupImage;
    private ImageButton changeImage;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private RecyclerView mRecyclerView;
    private participantAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Uri filePath;
    int positionParticipant;
    View view1;

    //Root for requests in DB
    public DatabaseReference rootRequests ;

    private final int PICK_IMAGE_REQUEST = 71;

    public class YesListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            view1 = v;
            deleteActivity(v);
        }

    }

    public class YesMovePersonListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            moveParticipant(v);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityName = getIntent().getStringExtra("ACTIVITY_NAME");
        description = getIntent().getStringExtra("DESCRIPTION");
        System.out.println("activity name: " + activityName);
        setTitle(activityName);

        setContentView(R.layout.activity_manager);
        downloadImage();
        TextView activity_name = (TextView) findViewById(R.id.password);
        activity_name.setText(activityName);
        TextPaint paint = activity_name.getPaint();
        float width = paint.measureText(activityName);
        Shader textShader = new LinearGradient(0, 0, width, activity_name.getTextSize(),
                new int[]{
                        Color.parseColor("#2e3191"),
                        Color.parseColor("#0000E5"),
                        Color.parseColor("#2e3191"),
                }, null, Shader.TileMode.REPEAT);
        activity_name.getPaint().setShader(textShader);
        TextView descriptionText = (TextView) findViewById(R.id.activity_description);
        descriptionText.setText(description);
        createParticipantsList();
        Button editButton = (Button) findViewById(R.id.edit_button);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        final Button chatButton = (Button) findViewById(R.id.button_Chat);
        Button requestButton = (Button) findViewById(R.id.button_join_request);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               chatButton(view);
            }
        });
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinRequest(view);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1 = view;
                Snackbar mySnackbar = Snackbar.make(view, "Are You Sure You Want To Delete Activity?",
                        Snackbar.LENGTH_LONG);
                mySnackbar.setAction("YES", new YesListener());
                mySnackbar.show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1 = view;
                editActivity();

            }
        });


    }

     public void editActivity() {
        Intent intent = new Intent(this, edit_activity.class);
        intent.putExtra("ACTIVITY_NAME", activityName);
        intent.putExtra("DESCRIPTION", description);
        startActivity(intent);
    }

    /**
     * The function go back to the previous screen when arrow bar is pressed
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        String backTO = getIntent().getStringExtra("GOT_FROM");
        Intent myIntent;
        if(backTO.equals("my_activities")){
            myIntent = new Intent(getApplicationContext(), user_activities.class);
        }else{
            myIntent = new Intent(getApplicationContext(), search_result.class);

            ArrayList <String> activitiesNamesList= getIntent().getStringArrayListExtra("ACTIVITIES_NAME_LIST");
            ArrayList <String> descriptionsList= getIntent().getStringArrayListExtra("DESCRIPTIONS_LIST");
            ArrayList <String>managerList = getIntent().getStringArrayListExtra("MANAGER_LIST");

            myIntent.putStringArrayListExtra("ACTIVITY_NAME", activitiesNamesList);
            myIntent.putStringArrayListExtra("DESCRIPTION", descriptionsList);
            myIntent.putStringArrayListExtra("MANAGER", managerList);

        }
        //myIntent.putExtra("ACTIVITY_NAME", documentActivityName);
        //myIntent.putExtra("DESCRIPTION", description);
        startActivityForResult(myIntent, 0);
        return true;
    }




    public void chatButton(View view){
        Intent intent = new Intent(getApplicationContext(), chat.class);
        String backTO = getIntent().getStringExtra("GOT_FROM");
        intent.putExtra("GOT_FROM", backTO);
        intent.putExtra("room_name", activityName);
        intent.putExtra("DESCRIPTION", description);
        startActivity(intent);

    }
    public void joinRequest(View view){
        rootRequests = FirebaseDatabase.getInstance().getReference().child("Groups").child(activityName);
        setTitle("My Requests");
        //Add Evant lisutner to the request reference in DB
        rootRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Intent intent = new Intent(getBaseContext(), no_requests.class);
                    String backTO = getIntent().getStringExtra("GOT_FROM");
                    intent.putExtra("GOT_FROM", backTO);
                    intent.putExtra("activity_name", activityName);
                    intent.putExtra("DESCRIPTION", description);
                    rootRequests.removeEventListener(this);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getBaseContext(), requests.class);
                    String backTO = getIntent().getStringExtra("GOT_FROM");
                    intent.putExtra("GOT_FROM", backTO);
                    intent.putExtra("activity_name", activityName);
                    intent.putExtra("DESCRIPTION", description);
                    rootRequests.removeEventListener(this);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void createParticipantsList() {
        mParticipantsList = new ArrayList<>();
        System.out.println("dataaaaaaaaaaaaaaaa");
        System.out.println(ACTIVITIES_COLLECTION);
        System.out.println(activityName);
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(ACTIVITIES_COLLECTION).document(activityName);
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
                            if(listOfEmails==null){
                                Log.d(TAG, "list of email NULL");
                            }else{
                                Log.d(TAG, "list of email not NULL");
                            }
                            String [] strSplit = listOfEmails.split(", ");
                            for ( int i=0; i<strSplit.length; ++i){
                                //emailsList.add(strSplit[i]);
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
                                                        if (d == null) {
                                                            Log.d(TAG, "list of d NULL");
                                                        } else {
                                                            Log.d(TAG, "list of d not NULL");
                                                            String[] parts = d.split("/");
                                                            int year = Integer.parseInt(parts[2]);
                                                            int month = Integer.parseInt(parts[1]);
                                                            int day = Integer.parseInt(parts[0]);
                                                            System.out.println("dgbfdbh " + day + "/" + month + "/" + year);

                                                            dob.set(year, month, day);
                                                            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                                                            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                                                                age--;
                                                            }
                                                            Log.d(TAG, String.valueOf(age));

                                                            Log.d(TAG, name);
                                                            /**
                                                             *
                                                             * @return the picture of the activity
                                                             */

                                                            String participantsEmail = document.getId();
                                                            Log.d(TAG, participantsEmail);
                                                            mParticipantsList.add(new participants_Items(participantsEmail, R.drawable.project_logo, name, "Age: " + String.valueOf(age)));
                                                        }

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

    /**
     * Build the list of the participants
     */
    public void buildRecyclerView() {
        Log.w(TAG, "build Recycle " + mParticipantsList.get(0).getParticipantName());
        Log.w(TAG, String.valueOf(mParticipantsList.size()));
        mRecyclerView = findViewById(R.id.manager_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new participantAdapter(mParticipantsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new participantAdapter.OnItemClickListener() {

            @Override
            public void onInfoClick(int position) {
                positionParticipant = position;

                Log.d(TAG, "goToDetails");
                View v = findViewById(R.id.manager_recyclerView);
                Snackbar mySnackbar = Snackbar.make(v, "Are You Sure You Want To Move this Participant From activity?",
                        Snackbar.LENGTH_LONG);
                mySnackbar.setAction("YES", new YesMovePersonListener());
                mySnackbar.show();
                //goToDetails(position);

            }
        });
    }



    public void deleteActivity(View view){
        FirebaseFirestore.getInstance().collection(ACTIVITIES_COLLECTION).document(activityName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        goToSelectActionScreen();
                   }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    /**
     * Move the participant from DB
     * @param v View
     */
    public void moveParticipant(View v){
        Log.w(TAG, "leave activity");
        final String currentEmail;
        String nameToDelete = mParticipantsList.get(positionParticipant).getParticipantName();
        Log.d(TAG, nameToDelete);

        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .whereEqualTo("Name", nameToDelete)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //int counter = 0;
                        //ArrayList<participants_Items> participantList = new ArrayList<>();
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                emailToDelete = document.getString("Email");
                                Log.d(TAG, emailToDelete + " SHOW ");
                            }
                            deleteFromDB();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }

                });
    }

    public void deleteFromDB(){

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(ACTIVITIES_COLLECTION).document(activityName);
        docRef.update("participantes", FieldValue.arrayRemove(emailToDelete))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "array updated");
                        refreshScreen();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "not updated");

            }
        });

    }

    public void refreshScreen(){
        Intent intent = new Intent(this, manager.class);
        intent.putExtra("ACTIVITY_NAME", activityName);
        intent.putExtra("DESCRIPTION", description);
        startActivity(intent);

    }

    public void goToSelectActionScreen(){
        Intent intent = new Intent(this, select_action.class);
        startActivity(intent);
    }
    public void downloadImage(){
        final Context context= this;
        groupImage = (ImageView) findViewById(R.id.GroupImage);
        changeImage = (ImageButton) findViewById(R.id.change_image);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final StorageReference storageReference = storage.getReference("uploads/" + activityName);
        storage.getReference("uploads/" + activityName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context /* context */)
                        .load(storageReference)
                        .into(groupImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
            }
        });
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                groupImage.setImageBitmap(bitmap);
                StorageReference mountainsRef = storageReference.child("uploads/" + activityName);
                mountainsRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(manager.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(manager.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }catch (IOException e){

            }
        }
    }
}
