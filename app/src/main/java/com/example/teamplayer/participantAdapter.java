package com.example.teamplayer;

import android.content.Context;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class participantAdapter extends RecyclerView.Adapter<participantAdapter.participantViewHolder>{
    private static final String TAG = "Par Adapter";
    private ArrayList<participants_Items> mParticipantsList;
    private OnItemClickListener mListener;

    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    /**
     * Interface that has a function: "onInfoClick"
     */
    public interface OnItemClickListener {
        /**
         * How to act when the delete button clicked
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

    public static class participantViewHolder extends RecyclerView.ViewHolder {
        //members
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public ImageView mInfoImage;

        /**
         * Where to show the data on the screen
         * @param itemView view
         * @param listener the listener
         */
        public participantViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_par);
            mTextView1 = itemView.findViewById(R.id.textView_par);
            mTextView2 = itemView.findViewById(R.id.age);
            mInfoImage = itemView.findViewById(R.id.image_info1);
            Log.d(TAG, "minfoimage" + mInfoImage);
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
    }

    /**
     *constructor
     * @param participantsList the participants list to show
     */
    public participantAdapter(ArrayList<participants_Items> participantsList) {
        mParticipantsList = participantsList;
    }

    @Override
    public participantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_participants__items, parent, false);
        participantViewHolder evh = new participantViewHolder(v, mListener);
        return evh;
    }

    /**
     * Show the list of the participants on the screen
     * @param holder participants holder
     * @param position the place at the list that should be taken
     */
    @Override
    public void onBindViewHolder(final participantViewHolder holder, int position) {
        participants_Items currentItem = mParticipantsList.get(position);

        String parEmail = currentItem.getparEmail();
        final Context context = ApplicationClass.getAppContext();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final StorageReference storageReference = storage.getReference("uploads/" + parEmail);
        storage.getReference("uploads/" + parEmail).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentEmail = user.getEmail();

        if (!currentEmail.equals(parEmail)){
            holder.mInfoImage.setImageResource(R.drawable.ic_delete);
        }

        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getParticipantName());
        holder.mTextView2.setText(currentItem.getAge());
    }

    /**
     *
     * @return the size of the list - the number of participants
     */
    @Override
    public int getItemCount() {
        Log.d(TAG, "number of list: " + String.valueOf(mParticipantsList.size()));
        return mParticipantsList.size();
    }
}
