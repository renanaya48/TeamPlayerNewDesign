package com.example.teamplayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class request_item_adapter  extends ArrayAdapter<requestItem> {
    //the list values in the List of type hero
    List<requestItem> requestList;
    //private String email;

    private static final String TAG = "accept_request";
    private static final String ACTIVITIES_COLLECTION = "Activities/";

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;
    private  boolean acceptOrDecline =false;
    private String activity_name;

    //constructor initializing the values
    public request_item_adapter(Context context, int resource, List<requestItem> requestList) {
        super(context, resource, requestList);
        this.context = context;
        this.resource = resource;
        this.requestList = requestList;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        TextView textViewName = view.findViewById(R.id.request);
        Button buttonAccept = view.findViewById(R.id.accept);
        Button buttonDecline = view.findViewById(R.id.decline);

        //getting the hero of the specified position
        requestItem requestItem = requestList.get(position);
        activity_name=requestItem.getActivityName();
        //adding values to the list item
        textViewName.setText(requestItem.getMessage());

        //manager email
       final String email = requestItem.getEmail();
       //Log.w(TAG, email);

        //adding a click listener to the button to remove item from the list
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserToActivity(email);
                acceptOrDecline =false;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity_name);
                Query query = ref.orderByKey();

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot);
                        System.out.println(dataSnapshot.getChildren());
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity_name);
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            for (DataSnapshot val: snapshot.getChildren()) {
                                if (val.getKey().equals("user_email")){
                                    if (val.getValue().toString().equals(email)){
                                        snapshot.getRef().removeValue();
                                        //removing the item
                                        requestList.remove(position);

                                        //reloading the list
                                        notifyDataSetChanged();
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });
        buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptOrDecline =false;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity_name);
                    Query query = ref.orderByKey();

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            System.out.println(dataSnapshot);
                            System.out.println(dataSnapshot.getChildren());
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity_name);
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                for (DataSnapshot val: snapshot.getChildren()) {
                                    if (val.getKey().equals("user_email")){
                                        if (val.getValue().toString().equals(email)){
                                            snapshot.getRef().removeValue();
                                            //removing the item
                                            requestList.remove(position);

                                            //reloading the list
                                            notifyDataSetChanged();
                                        }
                                    }

                                }
                            }
                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });

        //finally returning the view
        return view;
    }


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

    //this method will remove the item from the list
    private void removeRequest() {


    }
}
