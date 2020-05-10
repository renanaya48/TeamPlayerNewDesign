package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.onesignal.OneSignal;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private DatabaseReference root ;
    TextView PasswordFailure;
    TextView EmailFailure;
    private static final String USERS_COLLECTION = "Users/";
    private DocumentReference docRef;
    Map<String, Object> dataToSave = new HashMap<String, Object>();

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        PasswordFailure = (TextView) findViewById(R.id.passwordFailed);
        EmailFailure = (TextView) findViewById(R.id.emailFailed);
        root = FirebaseDatabase.getInstance().getReference();
        EditText password= (EditText) findViewById(R.id.password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        EditText passwordAgain= (EditText) findViewById(R.id.passwordConfrim);
        passwordAgain.setTransformationMethod(PasswordTransformationMethod.getInstance());
        chooseDate();
    }

    public void chooseDate(){

        mDisplayDate = (TextView) findViewById(R.id.select_date);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        registration.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                String date =  day + "/" + month + "/" + year;

                if(Calendar.getInstance().get(Calendar.YEAR)< year){
                    /*
                    Snackbar.make(view, "Please add your date of birth",
                            Snackbar.LENGTH_LONG)
                            .show();

                     */
                    Log.d(TAG, "wrong date");
                }else{
                    mDisplayDate.setText(date);
                }
            }
        };

    }

    public void saveData(View view) {
        TextView date = (TextView)findViewById(R.id.select_date);
        String dateText = date.getText().toString();
        if(dateText.equals("select a date")){
            Snackbar.make(view, "Please add your date of birth",
                    Snackbar.LENGTH_LONG)
                    .show();
        }else {
            EditText passwordData = (EditText) findViewById(R.id.password);
            EditText confirmPassword = (EditText) findViewById(R.id.passwordConfrim);
            final String userName = ((EditText) findViewById(R.id.name)).getText().toString();
            EditText emailData = (EditText) findViewById(R.id.Email);
            final String password = passwordData.getText().toString();
            String passwordConfirm = confirmPassword.getText().toString();
            final String email = emailData.getText().toString();
            if (checkPassword(password, passwordConfirm)) {
                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        Log.d(TAG, "" + task.getResult().getSignInMethods().size());
                        if (task.getResult().getSignInMethods().size() == 0) {
                            registerUser(email, password);
                        } else {
                            String message = "Email already in use";
                            EmailFailure.setText(message);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    }



    public void registerUser (String email, String password){
        final String userEmail = email;
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    final FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    final AlertDialog dialog;
                                    if (task.isSuccessful()) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(registration.this);
                                        builder.setTitle("Verify Email").setMessage("Email send to your account please confirm");
                                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                user.reload();
                                                if(user.isEmailVerified()){
                                                    updateUI(user);
                                                    createNewDoc();

                                                    login();
                                                }else {
                                                    builder.show();
                                                }


                                            }
                                        });

                                        dialog = builder.create();
                                        dialog.show();
                                        Log.d(TAG, "Email sent.");
                                    }
                                }
                            });


                } else {
                    String message = "Wrong email";
                    EmailFailure.setText(message);
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(registration.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

                // ...
            }
        });
    }

    public void login() {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser user) {
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    public boolean checkPassword(String password,String confirmPassword){
        String  message;

        if (!password.equals( confirmPassword)){
            message = "Unmatched passwords";
            PasswordFailure.setText(message);
            return false;
        }
        if (password.length() < 6){
            message = "Password should be \n at least 6 characters";
            PasswordFailure.setText(message);
            return false;
        }
        message = "";
        PasswordFailure.setText(message);
        return true;

    }
    public void createNewDoc(){
        EditText name = (EditText) findViewById(R.id.name);
        String nameText = name.getText().toString();
        EditText email = (EditText) findViewById(R.id.Email);
        String emailText = email.getText().toString();
        TextView date = (TextView)findViewById(R.id.select_date);
        String dateText = date.getText().toString();

        String collectionName = USERS_COLLECTION + emailText;

        docRef = FirebaseFirestore.getInstance().document(collectionName);
        dataToSave.put("Name", nameText);
        dataToSave.put("Email", emailText);
        dataToSave.put("DateOfBirth", dateText);

        docRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "document saved!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "document faild");
            }
        });
    }
}

