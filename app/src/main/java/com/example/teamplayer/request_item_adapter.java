package com.example.teamplayer;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class request_item_adapter  extends ArrayAdapter<requestItem> {
    List<requestItem> requestList;

    private ImageView imageUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final String TAG = "accept_request";
    private static final String ACTIVITIES_COLLECTION = "Activities/";

    //activity context
    Context context;

    int resource;
    private  boolean acceptOrDecline =false;
    private String activity_name;

    public request_item_adapter(Context context, int resource, List<requestItem> requestList) {
        super(context, resource, requestList);
        this.context = context;
        this.resource = resource;
        this.requestList = requestList;
    }

    /**
     * The function returns the ListView Item as a View
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //Get the view
        View view = layoutInflater.inflate(resource, null, false);

        //Get the view elements
        TextView textViewName = view.findViewById(R.id.request);
        ImageButton buttonAccept = view.findViewById(R.id.accept);
        ImageButton buttonDecline = view.findViewById(R.id.decline);
        requestItem requestItem = requestList.get(position);

        //Get and set the activity name/
        activity_name=requestItem.getActivityName();
        //add values to the list item
        textViewName.setText(requestItem.getMessage());
        final Context context= ApplicationClass.getAppContext();
        imageUser = (ImageView) view.findViewById(R.id.profile_image);

        //Get the user Photo
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final String email = requestItem.getEmail();
        final StorageReference storageReference = storage.getReference("uploads/" + email);

        //Upload the user photo from DB
        storage.getReference("uploads/" + email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context /* context */)
                        .load(storageReference)
                        .into(imageUser);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageUser.setImageDrawable(imageUser.getDrawable());
            }
        });


        //Add in click listener when the manager aprroves to join the group
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Add the user to the activity
                addUserToActivity(email);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query userQuery = ref.child("Groups").child(activity_name).orderByChild("user_email").equalTo(email);

                //Remove from requests list
                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            requestList.remove(position);

                            notifyDataSetChanged();
                            String message =  " Your request to join "+activity_name +" group has been approved" ;

                            //Send user notification
                            NotificationSender sender=new NotificationSender(email,message);
                            sender.sendNotification();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            }
        });

        //Add in click listener when the manager decline to join the group

        buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Delete the user from requests list in DB

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query userQuery = ref.child("Groups").child(activity_name).orderByChild("user_email").equalTo(email);
                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            requestList.remove(position);

                            //reloading the list
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });

            }
        });

        //finally returning the view
        return view;
    }


    /**
     * The Function add user to the activity
     * @param email
     */
    public void addUserToActivity(final String email){
        Log.w(TAG, email);
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection(ACTIVITIES_COLLECTION).document(activity_name)
                .update("participantes", FieldValue.arrayUnion(email))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "array updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "not updated");

            }
        });

    }
}
