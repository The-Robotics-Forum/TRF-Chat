package com.theroboticsforum.trfchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class RegisterActivity extends AppCompatActivity {

    //widgets
    private Button mSignInButton, mRegisterButton;
    private TextView mEmail,mPassword;
    private ProgressBar mProgress;

    //firebase authentication
    private FirebaseAuth mAuth;



    //vars
    private static final String TAG = "RegisterActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        mSignInButton = findViewById(R.id.signIn);
        mRegisterButton = findViewById(R.id.register);
        mEmail = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mProgress = findViewById(R.id.loading);

        mProgress.setVisibility(View.INVISIBLE);


        mSignInButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signIn();
                    }
                }
        );

        mRegisterButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        register();
                    }
                }
        );


    }

    private void register()
    {
        //register the new user here
        mAuth = FirebaseAuth.getInstance();
        final String email,password;
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();

        if(!isEmpty(email) && !isEmpty(password))
        {
            //none of the field is empty... Can continue with registration

            mAuth.createUserWithEmailAndPassword(email,password).
                    addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    //registration is successfull... direct user to MAinActivity
                                    Log.d(TAG, "onSuccess: Registration Successfull");
                                    Toast.makeText(RegisterActivity.this, "Welcome!!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this , MainActivity.class));

                                }
                            }
                    ).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: Registration Failed!! ",e );
                            Toast.makeText(RegisterActivity.this, "Cannot Register", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }

        else
        {
            //one of the email or password field is empty
            if(isEmpty(email))
            {
                //email field is empty
                Toast.makeText(this, "Enter valid Email", Toast.LENGTH_SHORT).show();
            }
            else{
                //password field is empty
                Toast.makeText(this, "Enter the password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn()
    {
        //move the user to the LogIn activity
        startActivity(new Intent(this, LoginActivity.class));
    }

    private boolean isEmpty(String string) {return string.equals("");}


}
