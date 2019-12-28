package com.theroboticsforum.trfchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    //widgets
    private EditText mUsername;
    private Button mSendButton, mRequestButton;
    private ProgressBar mProgressBar;
    
    //vars
    private static final String TAG = "MainActivity";


    //firebase authetication
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;


    //Firebase Database
    private DatabaseReference mChatData = FirebaseDatabase.getInstance().getReference("chats");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        setupFirebaseAuth();
        currentUser  = FirebaseAuth.getInstance().getCurrentUser();
        getSupportActionBar().setTitle("TRF Chat");


        mUsername = findViewById(R.id.username);
        mSendButton = findViewById(R.id.sendBtn);
        mRequestButton = findViewById(R.id.see_request_btn);
        mProgressBar = findViewById(R.id.loading);
        mProgressBar.setVisibility(View.INVISIBLE);


        mRequestButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //check if the user has any requests
                        //check if user is receiver of any chatRequests
                        checkRequests();
                    }
                }
        );

        mSendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //send the request to the user
                        //make user the sender of the chatRequest
                        sendRequest();
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.sign_out)
        {
            //signOut button is pressed
            signOut();
        }
        return true;
    }

    private void signOut() {
        Log.d(TAG, "signOut: sigining out.");
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Bye! Bye! ", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "signOut: User signed out");
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");
        FirebaseApp.initializeApp(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    //user is signed out... revert to login page
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);//most important line of the code
        //checkRequests();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private boolean isEmpty(String string){ return string.equals("");}

    private void sendRequest()
    {
        final String receiverEmail = mUsername.getText().toString().trim();
        String senderEmail = currentUser.getEmail();

        //check if the editTExt is empty
        if(isEmpty(receiverEmail))
        {
            //edit text is empty
            Toast.makeText(this, "Enter the email", Toast.LENGTH_SHORT).show();
        }
        else {
            //email field is not empty, we are good ot go
            mProgressBar.setVisibility(View.VISIBLE);

            //create a chatkey and push
            String chatKey = mChatData.push().getKey();
            mChatData.child(chatKey).child("sender").setValue(senderEmail);
            mChatData.child(chatKey).child("receiver").setValue(receiverEmail);

            Intent i = new Intent(MainActivity.this , ChatActivity.class);
            i.putExtra("chatKey" , chatKey);
            startActivity(i);
            finish();
        }

    }

    private void checkRequests()
    {
        //check for requests
        mProgressBar.setVisibility(View.VISIBLE);

        //firebasecheck if the request exits in chatDatabase
        mChatData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot chatRoom : dataSnapshot.getChildren())
                {
                    if(chatRoom.exists())
                    {
                        String receiverEmail = (String) chatRoom.child("receiver").getValue();
                        //check if the chatRoom sender is equal to our email
                        if(receiverEmail.equals(currentUser.getEmail()))
                        {
                            //chat request exists and chatroom already exists
                            Intent i = new Intent(MainActivity.this , ChatActivity.class);
                            i.putExtra("chatKey" , chatRoom.getKey());
                            startActivity(i);
                            //finish();

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ",databaseError.toException() );
            }
        });

        mProgressBar.setVisibility(View.INVISIBLE);
        //if the control reaches here.. that means, no request has been found
        //indicate the user using a simple Toast message
        Toast.makeText(this, "No requests found", Toast.LENGTH_SHORT).show();

    }


}
