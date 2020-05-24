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
        //members
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        /**
         * Where to show the data on the screen
         * @param itemView view
         * @param listener the listener
         */
        public participantViewHolder(View itemView, final participantAdapter.OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_par);
            mTextView1 = itemView.findViewById(R.id.textView_par);
            mTextView2 = itemView.findViewById(R.id.age);

        }
    }

    /**
     * contructor
     * @param participantsList the participants list to show
     */
    public participantGroupAdapter(ArrayList<participants_Items> participantsList) {
        mParticipantsList = participantsList;
    }

    @Override
    public participantGroupAdapter.participantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_participant_group, parent, false);
        participantGroupAdapter.participantViewHolder evh = new participantGroupAdapter.participantViewHolder(v, mListener);
        return evh;
    }
    /**
     * Show the list of the participants on the screen
     * @param holder participants holder
     * @param position the place at the list that should be taken
     */
    @Override
    public void onBindViewHolder(participantGroupAdapter.participantViewHolder holder, int position) {
        participants_Items currentItem = mParticipantsList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
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
