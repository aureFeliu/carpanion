package com.ronan.carpanion.viewlayouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.concurrent.ExecutionException;

public class RatePassengerViewLayout extends RelativeLayout implements View.OnClickListener {
    private TextView passengerName, userScore;
    private Bitmap profileBitmap;
    private ImageView profileImage;
    private ImageButton thumbsUp, thumbsDown;

    private Context context;
    private User user;
    private String passengerID;

    private DatabaseReference database;

    public RatePassengerViewLayout(Context context)
    {
        super(context);
    }

    public RatePassengerViewLayout(Context context, String passenger)
    {
        super(context);
        passengerID = passenger;
        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.listview_rate_passengers, this, true);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/circular-book.otf");
        passengerName = (TextView) findViewById(R.id.passengerName);
        passengerName.setTypeface(typeface);
        userScore = (TextView) findViewById(R.id.userScore);
        userScore.setTypeface(typeface);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        thumbsUp = (ImageButton) findViewById(R.id.thumbsUp);
        thumbsDown = (ImageButton) findViewById(R.id.thumbsDown);

        thumbsDown.setOnClickListener(this);
        thumbsUp.setOnClickListener(this);

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
        switch (view.getId())
        {
            case R.id.thumbsUp:
                thumbsUp.setImageResource(R.drawable.ic_thumb_up_clicked);
                thumbsUp.setOnClickListener(null);
                thumbsDown.setOnClickListener(null);
                int userPointUp = user.getUserScore();
                userPointUp++;
                user.setUserScore(userPointUp);
                DatabaseReference tripDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(passengerID);
                tripDatabase.child("userScore").setValue(userPointUp);
                userScore.setText(String.valueOf(userPointUp));
                break;
            case R.id.thumbsDown:
                thumbsDown.setImageResource(R.drawable.ic_thumb_down_clicked);
                thumbsUp.setOnClickListener(null);
                thumbsDown.setOnClickListener(null);
                int userPointDown = user.getUserScore();
                userPointDown--;
                user.setUserScore(userPointDown);
                DatabaseReference tripDatabase2 = FirebaseDatabase.getInstance().getReference().child("user").child(passengerID);
                tripDatabase2.child("userScore").setValue(userPointDown);
                userScore.setText(String.valueOf(userPointDown));
                break;
        }
    }
}
