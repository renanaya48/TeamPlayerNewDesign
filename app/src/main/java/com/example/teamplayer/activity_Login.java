package com.example.teamplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class activity_Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private CheckBox checkbox;
    private EditText password;
    View viewToPass;
    Intent intent;
    TextView failureMessage;
    private static final String TAG = "EmailPassword";
    private boolean ShowText;
    private ImageButton showTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent=new Intent(this,select_action.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(intent);
        }
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__login);

        password= (EditText) findViewById(R.id.password);
        failureMessage = (TextView) findViewById(R.id.logInFailed);
        //Hide Text
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ShowText=false;
        Button logInButton = findViewById(R.id.logInButton);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        showTextButton = (ImageButton) findViewById(R.id.ShowText);

        //Set OnClickListener to show/hide password
        showTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowText= !ShowText;
                if (ShowText){
                    // show password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showTextButton.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                }else {
                    // hide password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showTextButton.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                }

            }
        });


    }


    /**
     * The function Authenticate the user whem log in bottom is pressed
     * @param view
     */
    public void logIn(View view) {
        EditText passwordData = (EditText) findViewById(R.id.password);
        EditText emailData = (EditText) findViewById(R.id.Email);

        //Get the input email and password of the user
        String password = passwordData.getText().toString();
        String email = emailData.getText().toString();

        //Check if email is note empty
        if (email.equals("") || password.equals("")) {
            Snackbar.make(view, "Please enter an email and password",
                    Snackbar.LENGTH_LONG)
                    .show();
        } else {
            //Sighn in with user email and password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                startActivity(intent);
                            } else {

                                String message = "Wrong Email or password";
                                failureMessage.setText(message);
                                // If sign in fails, display a messagesend to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                updateUI(null);
                            }

                        }
                    });
        }
    }


    private void updateUI(FirebaseUser user) {
    }

    /**
     * The dunction send the user email to reset password
     * @param view
     */
    public void resetPassword(View view){
        viewToPass = view;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //Get the email
        EditText emailData = (EditText) findViewById(R.id.Email);
        String emailAddress = emailData.getText().toString();
        //If the email is empty show eroor message
        if (emailAddress.equals("")){
            Snackbar.make(viewToPass, "Please enter an email",
                    Snackbar.LENGTH_LONG)
                    .show();
        }else {
            //Send reset password email
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                Snackbar.make(viewToPass, "Email was send to your email account, please confirm",
                                        Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        }

    }


    public void newUser(View view) {
        Intent intent=new Intent(this,registration.class);
        startActivity(intent);
    }

}
