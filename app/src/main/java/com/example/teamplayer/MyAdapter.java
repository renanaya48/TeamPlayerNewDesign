package com.example.teamplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.activityViewHolder>{
    private ArrayList<ActivityItems> mActivitiesList;
    private OnItemClickListener mListener;

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

        public activityViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            mInfoImage = itemView.findViewById(R.id.image_info);

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
    public void onBindViewHolder(activityViewHolder holder, int position) {
        ActivityItems currentItem = mActivitiesList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getActivityName());
        holder.mTextView2.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return mActivitiesList.size();
    }

}
