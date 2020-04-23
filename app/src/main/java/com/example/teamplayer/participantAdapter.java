package com.example.teamplayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class participantAdapter extends RecyclerView.Adapter<participantAdapter.participantViewHolder>{
    private static final String TAG = "Par Adapter";
    private ArrayList<participants_Items> mParticipantsList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        //void onItemClick(int position);

        void onInfoClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class participantViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        //public TextView mTextView2;
        //public ImageView mInfoImage;

        public participantViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_par);
            mTextView1 = itemView.findViewById(R.id.textView_par);
            //mTextView2 = itemView.findViewById(R.id.activity_description);
            //mInfoImage = itemView.findViewById(R.id.image_info);
            /*
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
            */
        }
    }

    public participantAdapter(ArrayList<participants_Items> participantsList) {
        mParticipantsList = participantsList;
    }

    @Override
    public participantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_participants__items, parent, false);
        participantViewHolder evh = new participantViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(participantViewHolder holder, int position) {
        participants_Items currentItem = mParticipantsList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getParticipantName());
        //holder.mTextView2.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "number of list: " + String.valueOf(mParticipantsList.size()));
        return mParticipantsList.size();
    }
}
