package com.example.teamplayer;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.activityViewHolder>{
    private ArrayList<ActivityItems> mActivitiesList;
    private OnItemClickListener mListener;
    private String user_email;
    private DatabaseReference root ;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final String TAG = "PostDetailActivity";


    /**
     * Interface that has a function: "onInfoClick"
     */
    public interface OnItemClickListener {
        /**
         * How to act when the info button clicked
         * @param position the position at the list of the activities that shown
         */
        void onInfoClick(int position);
    }

    /**
     * Set the OnClick listener
     * @param listener the listener to set
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class activityViewHolder extends RecyclerView.ViewHolder {
        //members
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public ImageView mInfoImage;
        public View view;

        /**
         * constructor
         * @param itemView View
         * @param listener listener when the button clicked
         */
        public activityViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.activity_description);
            mInfoImage = itemView.findViewById(R.id.image_info);
            view=itemView;



            mInfoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onInfoClick(position);
                        }
                    }
                }
            });
        }

        /**
         * Set the background to yellow
         */
        public void setBackGround(){
            view.setBackgroundColor(Color.rgb(68, 71, 155));
        }
    }

    /**
     * constructor
     * @param activitiesList the list of the activities that be shown
     */
    public MyAdapter(ArrayList<ActivityItems> activitiesList) {
        mActivitiesList = activitiesList;
    }

    @Override
    public activityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_items, parent, false);
        activityViewHolder evh = new activityViewHolder(v, mListener);
        return evh;
    }

    /**
     * Show the list of the activities on the screen
     * @param holder activity holder
     * @param position the place at the list that should be taken
     */
    @Override
    public void onBindViewHolder(final activityViewHolder holder, int position) {
        final ActivityItems currentItem = mActivitiesList.get(position);

        String activityID = currentItem.getActivityID();
        final Context context = ApplicationClass.getAppContext();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final StorageReference storageReference = storage.getReference("uploads/" + activityID);
        try {
            storage.getReference("uploads/" + activityID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(storageReference)
                            .into(holder.mImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "No such Image");
                }
            });
        }catch (Exception e){

        }

        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getActivityName());
        holder.mTextView2.setText(currentItem.getDescription());
        //Get activity name
        final String activityName=currentItem.getActivityName().replace(" (MANAGER)" ,"").trim();
        //Check if the manager has new join request
       checkManagerRequest(activityName,currentItem,holder);
       //Check if the user has requested to join the group
       checkUserRequest(activityName,currentItem,holder);
    }

    /**
     *
     * @return the size of the list - the number of activities
     */
    @Override
    public int getItemCount() {
        return mActivitiesList.size();
    }

    /**
     * The function check if a user is the activity manager and if he has ew join request
     * @param activityName
     * @param currentItem
     * @return
     */
    public void checkManagerRequest(final String activityName, final ActivityItems currentItem, final activityViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        //Check if there are join request for the specific group
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(activityName)) {
                    //Check if current user is the manager
                    if (currentItem.isManager()){
                        //Show manager new requests are pending and change activity color
                        String textToShow=currentItem.getActivityName()+" -New pending requests";
                        holder.mTextView1.setText(textToShow);
                        holder.setBackGround();

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * The function checks if the user has requested to join the group
     * @param activityName
     * @param currentItem
     * @param holder
     */
    public void checkUserRequest(final String activityName, final ActivityItems currentItem, final activityViewHolder holder){
        root = FirebaseDatabase.getInstance().getReference().child("Groups").child(activityName);
        mAuth = FirebaseAuth.getInstance();
        //Get currrent user email
        FirebaseUser user = mAuth.getCurrentUser();
        user_email = user.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //Check if the user is in the request list in the DB
        Query userQuery = root.orderByChild("user_email").equalTo(user_email);userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //Show the user the request is waiting for approval and change activity color
                    holder.mTextView1.setText(currentItem.getActivityName() + " - Waiting for approval");
                    holder.setBackGround();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }


}