package com.ronan.carpanion.viewlayouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ronan.carpanion.R;
import com.ronan.carpanion.entitites.User;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

public class MessagesViewLayout extends RelativeLayout
{
    private TextView userName, lastMessage;
    private Bitmap profileBitmap;
    private ImageView profileImage;

    private Context context;
    private User user;
    private String userID;

    private DatabaseReference database;

    public MessagesViewLayout(Context context)
    {
        super(context);
    }

    public MessagesViewLayout(Context context, String user)
    {
        super(context);

        userID = user;
        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.listview_messages, this, true);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/circular-book.otf");
        userName = (TextView) findViewById(R.id.userName);
        userName.setTypeface(typeface);
        lastMessage = (TextView) findViewById(R.id.lastMessage);
        lastMessage.setTypeface(typeface);
        profileImage = (ImageView) findViewById(R.id.profileImage);

        setUser(userID);
    }

    public void setUser(String userid)
    {
        database = FirebaseDatabase.getInstance().getReference().child("user").child(userid);
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                userName.setText(user.getFirstName() + " " + user.getLastName());
                //userScore.setText(String.valueOf(user.getUserScore()));

                String userImage = user.getProfileImage();
                try
                {
                    profileBitmap = new TripViewLayout.GetUserImage().execute(userImage).get();
                    profileImage.setImageBitmap(profileBitmap);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };
        database.addValueEventListener(valueEventListener);
    }
}
