package com.theroboticsforum.trfchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //vars
    private static final String TAG = "LoginActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;


    //widgets
    private Button mLoginButton, mRegisterButton;
    private EditText mUsername, mPassword;
    private ProgressBar mProgressBar;

    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        mLoginButton = findViewById(R.id.login);
        mRegisterButton = findViewById(R.id.register);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mProgressBar = findViewById(R.id.loading);
        mProgressBar.setVisibility(View.INVISIBLE);

        mRegisterButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(LoginActivity.this , RegisterActivity.class ));
                        finish();
                    }
                }
        );

        setupFirebaseAuth();
        if(servicesOK()){
            mLoginButton.setOnClickListener(this);
        }


    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //someone is logged in...
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    //this is a commit
    //this is another comment

    public boolean servicesOK(){
        Log.d(TAG, "servicesOK: Checking Google Services.");

        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if(isAvailable == ConnectionResult.SUCCESS){
            //everything is ok and the user can make mapping requests
            Log.d(TAG, "servicesOK: Play Services is OK");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)){
            //an error occured, but it's resolvable
            Log.d(TAG, "servicesOK: an error occured, but it's resolvable.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, isAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            //erroe cannot be resolved
            Toast.makeText(this, "Can't connect to services", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private boolean isEmpty(String string){
        return string.equals("");
    }

    @Override
    public void onClick(View view) {
        mProgressBar.setVisibility(View.VISIBLE);
        if(view.getId() == R.id.login){
            //check if the fields are filled out
            if(!isEmpty(mUsername.getText().toString().trim())
                    && !isEmpty(mPassword.getText().toString().trim())){
                Log.d(TAG, "onClick: attempting to authenticate.");

                showDialog();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(mUsername.getText().toString(),
                        mPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                hideDialog();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                });
            }else{
                Toast.makeText(this, "Fill in all the details.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}
