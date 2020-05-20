package com.example.teamplayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class participantGroupAdapter extends RecyclerView.Adapter<participantGroupAdapter.participantViewHolder>{
    private static final String TAG = "Par Adapter";
    private ArrayList<participants_Items> mParticipantsList;
    private participantAdapter.OnItemClickListener mListener;


    public static class participantViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public participantViewHolder(View itemView, final participantAdapter.OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_par);
            mTextView1 = itemView.findViewById(R.id.textView_par);
            mTextView2 = itemView.findViewById(R.id.age);

        }
    }

    public participantGroupAdapter(ArrayList<participants_Items> participantsList) {
        mParticipantsList = participantsList;
    }

    @Override
    public participantGroupAdapter.participantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_participant_group, parent, false);
        participantGroupAdapter.participantViewHolder evh = new participantGroupAdapter.participantViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(participantGroupAdapter.participantViewHolder holder, int position) {
        participants_Items currentItem = mParticipantsList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getParticipantName());
        holder.mTextView2.setText(currentItem.getAge());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "number of list: " + String.valueOf(mParticipantsList.size()));
        return mParticipantsList.size();
    }
}
