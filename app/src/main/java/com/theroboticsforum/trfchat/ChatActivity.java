package com.theroboticsforum.trfchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    //vars
    private static final String TAG = "ChatActivity";
    private  String chatKey;
    private ArrayList<Message> chats;
    private  String senderEmail;
    private  String receiverEmail;

    //firebase database
    private DatabaseReference mChatData;
    private FirebaseUser currentUser;

    //widgets
    private RecyclerView mRecyclerView;
    private EditText mMessage;
    private FloatingActionButton sendBtn;

    //custom adapter
    private CustomAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //appBar settings
        getSupportActionBar().setTitle("Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //get the current user object
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //get the chatkey and create a reference to it
        chatKey = getIntent().getStringExtra("chatKey");
        mChatData = FirebaseDatabase.getInstance().getReference().child("chats").child(chatKey);

        //find the widgets from the layout
        chats = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_view);
        mMessage = findViewById(R.id.messageEdt);
        sendBtn = findViewById(R.id.sendBtn);



        //setup the recycler view
        adapter = new CustomAdapter(this , chats);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        //set onClickListener to sendBtn
        sendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendMsg();
                    }
                }
        );

        //get chats from firebase
        getChats();

    }


    private void getChats()
    {
        mChatData.child("messages").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: Getting the chats for key:" + chatKey);
                        //clear the arraylist orelse the chats will repeat
                        chats.clear();
                        for(DataSnapshot msg : dataSnapshot.getChildren())
                        {
                            if(msg.exists())
                            {
                                Message message = msg.getValue(Message.class);
                                chats.add(message);
                            }
                        }
                        //notifyDatasetChange();
                        adapter.notifyItemChanged(chats.size());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: ",databaseError.toException() );
                    }
                }
        );
    }

    private void sendMsg()
    {
        final String msg = mMessage.getText().toString().trim();
        //check if the messageEdt is empty
        if(msg.equals(""))
        {
            //edit text is empty
            Toast.makeText(this, "Type a message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //editTxt is not empty and we are good to go
            //clear the editText
            mMessage.setText("");
            String id = mChatData.child("messages").push().getKey();

            Message message = new Message(msg , currentUser.getEmail());


            mChatData.child("messages").child(id).setValue(message).
                    addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: message sent at chat key" + chatKey+ "\nmessage:" + msg);
                                }
                            }
                    );

            //getChats();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getChats();
    }
}
