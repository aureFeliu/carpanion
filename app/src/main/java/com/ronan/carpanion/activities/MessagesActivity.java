package com.ronan.carpanion.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ronan.carpanion.R;
import com.ronan.carpanion.adapters.MessagesListAdapter;
import com.ronan.carpanion.entitites.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener
{
    private ListView messagesList;
    private ImageButton backButton;
    private TextView messagesText;

    private DatabaseReference messageRef;
    private DatabaseReference userRef;

    private Context context;

    private ArrayList<Message> messages;
    private Set<String> userSet;
    private ArrayList<String> userIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().hide();

        context = this;

        messageRef = FirebaseDatabase.getInstance().getReference().child("messages");
        userRef = FirebaseDatabase.getInstance().getReference().child("user");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/circular-book.otf");

        messagesList = (ListView) findViewById(R.id.messagesList);
        backButton = (ImageButton) findViewById(R.id.backButton);
        messagesText = (TextView) findViewById(R.id.messagesText);
        messagesText.setTypeface(typeface);
        backButton.setOnClickListener(this);

        messageRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                messages = new ArrayList<>();
                userSet = new LinkedHashSet<>();
                userIDs = new ArrayList<>();

                for(DataSnapshot snap: dataSnapshot.getChildren())
                {
                    Message message = snap.getValue(Message.class);
                    if(message.getFromUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) || message.getToUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        messages.add(message);
                        userSet.add(message.getToUser());
                        userSet.add(message.getFromUser());
                    }
                }

                for(Iterator<String> iterator = userSet.iterator(); iterator.hasNext();)
                {
                    String s = iterator.next();
                    if(s.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        iterator.remove();
                    }
                }

                userIDs.addAll(0, userSet);
                MessagesListAdapter adapter = new MessagesListAdapter(context, userIDs);
                messagesList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        messagesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userID", userIDs.get(i));
                context.startActivity(intent);
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