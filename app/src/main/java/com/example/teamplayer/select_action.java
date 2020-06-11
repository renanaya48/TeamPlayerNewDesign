package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;


import java.io.IOException;

public class select_action extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "PostDetailActivity";
    TextView welcomeText;
    private ImageButton imageChangePhoto;
    private ImageView imageUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
        final Context context= this;
        imageChangePhoto = (ImageButton) findViewById(R.id.PhotoButtun);
        imageUser = (ImageView) findViewById(R.id.profile_image);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final StorageReference storageReference = storage.getReference("uploads/" + userEmail);
        storage.getReference("uploads/" + userEmail).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context /* context */)
                        .load(storageReference)
                        .into(imageUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
            }
        });
        System.out.println();

        imageChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        welcomeText = (TextView)findViewById(R.id.welcomeText);
        String email = user.getEmail();
        OneSignal.setSubscription(true);
        OneSignal.sendTag("User_ID",email );
        DocumentReference userNAme = db.collection("Users").document(email);
        userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        String user_name= document.getString("Name");
                        welcomeText.setText("Hello "+user_name);



                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
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
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                imageUser.setImageDrawable(bitmapDrawable);
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                StorageReference mountainsRef = storageReference.child("uploads/" + userEmail);
                mountainsRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(select_action.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(select_action.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void joinActivity(View view) {
        Intent intent=new Intent(this,activity_Search.class);
        startActivity(intent);
    }

    public void createActivity(View view) {
        Intent intent=new Intent(this,activity_create_new.class);
        startActivity(intent);
    }

    public void myActivity(View view) {
        Intent intent=new Intent(this,user_activities.class);
        startActivity(intent);
    }

    public void managerButton(View view) {
        Intent intent = new Intent(this, manager.class);
        startActivity(intent);
    }
    public void groupButton(View view) {
        Intent intent = new Intent(this, group.class);
        startActivity(intent);
    }

    public void detailsButton(View view) {
        Intent intent = new Intent(this, activity_details.class);
        startActivity(intent);
    }
    public void signOutButton(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, activity_Login.class);
        startActivity(intent);
    }


}
