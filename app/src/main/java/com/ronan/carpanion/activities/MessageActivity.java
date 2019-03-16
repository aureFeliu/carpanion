package com.ronan.carpanion.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ronan.carpanion.R;
import com.ronan.carpanion.entitites.Message;
import com.ronan.carpanion.entitites.User;
import com.ronan.carpanion.adapters.MessagesAdapter;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView messageRecyclerView;
    private LinearLayoutManager layoutManager;
    private EditText messageEditText;
    private TextView sendButton;
    private TextView userNameHeading;
    private ImageButton backButton;

    private DatabaseReference messageRef;
    private DatabaseReference userRef;
    private List<Message> messageList = new ArrayList<>();
    private MessagesAdapter adapter = null;
    private Context context;

    private String recieverID;
    private String recieverName;
    private String profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getSupportActionBar().hide();
        context = this;

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/circular-book.otf");
        messageRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendButton = (TextView) findViewById(R.id.sendButton);
        sendButton.setTypeface(typeface);
        userNameHeading = (TextView) findViewById(R.id.userNameHeading);
        userNameHeading.setTypeface(typeface);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        messageRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(layoutManager);

        messageRef = FirebaseDatabase.getInstance().getReference().child("messages");
        userRef = FirebaseDatabase.getInstance().getReference().child("user");

        recieverID = getIntent().getStringExtra("userID");

        //sendImageButton.setOnClickListener(new View.OnClickListener()
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String message = messageEditText.getText().toString();
                String senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(message.isEmpty())
                {
                    Toast.makeText(MessageActivity.this, "You must enter a message", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendMessage(message, senderID, recieverID);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getRecipientName(recieverID);
        getMessages();
    }

    private void sendMessage(String message, String senderID, String recieverID)
    {
        messageList.clear();

        Message newMessage = new Message(senderID, recieverID, message);
        messageRef.push().setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(!task.isSuccessful())
                {
                    Toast.makeText(MessageActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    messageEditText.setText(null);

                    if(getCurrentFocus()!=null)
                    {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }
        });
    }

    private void getMessages()
    {
        messageRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                messageList.clear();

                for(DataSnapshot snap: dataSnapshot.getChildren())
                {
                    Message message = snap.getValue(Message.class);
                    if(message.getFromUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && message.getToUser().equals(recieverID) || message.getFromUser().equals(recieverID) && message.getToUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        messageList.add(message);
                    }
                }

                adapter = new MessagesAdapter(messageList, profileImage, context);
                messageRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void getRecipientName(final String receiverId)
    {
        userRef.child(receiverId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User recepient = dataSnapshot.getValue(User.class);
                recieverName = recepient.getFirstName() + " " + recepient.getLastName();
                profileImage = recepient.getProfileImage();

                try
                {
                    userNameHeading.setText(recieverName);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.backButton:
                finish();
                break;
        }
    }
}
