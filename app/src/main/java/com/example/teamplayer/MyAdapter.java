package com.example.teamplayer;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.activityViewHolder>{
    private ArrayList<ActivityItems> mActivitiesList;
    private OnItemClickListener mListener;
    private String user_email;
    private DatabaseReference root ;
    private FirebaseAuth mAuth;
    private static final String TAG = "PostDetailActivity";



    public interface OnItemClickListener {
        //void onItemClick(int position);

        void onInfoClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class activityViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public ImageView mInfoImage;

        View view;

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
        public void setBackGround(){
            view.setBackgroundColor(Color.YELLOW);
        }
    }

    public MyAdapter(ArrayList<ActivityItems> activitiesList) {
        mActivitiesList = activitiesList;
    }

    @Override
    public activityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_items, parent, false);
        activityViewHolder evh = new activityViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(final activityViewHolder holder, int position) {
        final ActivityItems currentItem = mActivitiesList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getActivityName());
        holder.mTextView2.setText(currentItem.getDescription());
        //Get activity name
        final String activityName=currentItem.getActivityName().replace(" (MANAGER)" ,"").trim();
        //Check if the manager has new join request
       checkManagerRequest(activityName,currentItem,holder);
       //Check if the user has requested to join the group
       checkUserRequest(activityName,currentItem,holder);

    }

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