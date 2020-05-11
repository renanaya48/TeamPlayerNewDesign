package com.example.teamplayer;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        ActivityItems currentItem = mActivitiesList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getActivityName()+" - pending request");
        holder.mTextView2.setText(currentItem.getDescription());

        root = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentItem.getActivityName());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user_email = user.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = root.orderByChild("user_email").equalTo(user_email);userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    holder.setBackGround();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mActivitiesList.size();
    }

}