package com.ronan.carpanion.viewlayouts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
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
import com.ronan.carpanion.activities.MessageActivity;
import com.ronan.carpanion.entitites.User;

import java.util.concurrent.ExecutionException;

public class PassengerViewLayout extends RelativeLayout implements View.OnClickListener
{
    private TextView passengerName, userScore;
    private Bitmap profileBitmap;
    private ImageView profileImage;
    private Button message;

    private Context context;
    private User user;
    private String passengerID;

    private DatabaseReference database;

    public PassengerViewLayout(Context context)
    {
        super(context);
    }

    public PassengerViewLayout(Context context, String passenger)
    {
        super(context);
        passengerID = passenger;
        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.listview_passengers, this, true);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/circular-book.otf");
        passengerName = (TextView) findViewById(R.id.passengerName);
        passengerName.setTypeface(typeface);
        userScore = (TextView) findViewById(R.id.userScore);
        userScore.setTypeface(typeface);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        message = (Button) findViewById(R.id.message);
        message.setTypeface(typeface);

        message.setOnClickListener(this);

        setPassenger(passenger);
    }

    public void setPassenger(String passenger)
    {
        database = FirebaseDatabase.getInstance().getReference().child("user").child(passenger);
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                passengerName.setText(user.getFirstName() + " " + user.getLastName());
                userScore.setText(String.valueOf(user.getUserScore()));

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

    @Override
    public void onClick(View view)
    {
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("userID", passengerID);
        context.startActivity(intent);
    }
}
