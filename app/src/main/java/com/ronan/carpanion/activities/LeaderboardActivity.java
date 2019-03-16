package com.ronan.carpanion.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ronan.carpanion.R;
import com.ronan.carpanion.adapters.LeaderboardListAdapter;
import com.ronan.carpanion.entitites.User;
import com.ronan.carpanion.viewlayouts.TripViewLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView userPlace;
    private TextView userPoints;
    private TextView leaderboardText;
    private ImageView userImage;
    private ListView leaderboardList;
    private Bitmap userBitmap;
    private ImageButton backButton;

    private User user;
    private Context context;
    private DatabaseReference reference;
    private ArrayList<User> userScoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        getSupportActionBar().hide();
        context = this;

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/circular-book.otf");
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/circular-bold.otf");

        userScoreList = new ArrayList<>();
        userPlace = (TextView) findViewById(R.id.userPlace);
        userPlace.setTypeface(typefaceBold);
        userPoints = (TextView) findViewById(R.id.thisUserScore);
        userPoints.setTypeface(typefaceBold);
        userImage = (ImageView) findViewById(R.id.userImage);
        leaderboardList = (ListView) findViewById(R.id.leaderboardList);
        leaderboardText = (TextView) findViewById(R.id.leaderboardText);
        leaderboardText.setTypeface(typeface);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        try
        {
            userBitmap = new TripViewLayout.GetUserImage().execute(user.getProfileImage()).get();
            userImage.setImageBitmap(userBitmap);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        userPoints.setText(String.valueOf(user.getUserScore()) + "pts");

        Query userScoreQuery = FirebaseDatabase.getInstance().getReference().child("user").orderByChild("userScore");
        ValueEventListener val = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot eachDataSnapShot : dataSnapshot.getChildren())
                {
                    System.out.println("KEY: " + eachDataSnapShot.getKey() + " VALUE: " + eachDataSnapShot.getValue());
                    userScoreList.add(eachDataSnapShot.getValue(User.class));
                }
                populateList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println(databaseError);
            }
        };
        userScoreQuery.addValueEventListener(val);
    }

    private void populateList()
    {
        Collections.reverse(userScoreList);

        int position = 0;
        boolean found = false;
        for(User use : userScoreList)
        {
            if(use.getFirstName().equals(user.getFirstName()) && use.getProfileImage().equals(user.getProfileImage()) && use.getLastName().equals(user.getLastName()))
            {
                System.out.println("Found him");
                position++;
                found = true;
            }
            else if(!found)
            {
                position++;
            }
        }
        System.out.println(position);

        if(position == 1)
        {
            userPlace.setText(position + "st");
        }
        else if(position == 2)
        {
            userPlace.setText(position + "nd");
        }
        else if(position == 3)
        {
            userPlace.setText(position + "rd");
        }
        else
        {
            userPlace.setText(position + "th");
        }

        LeaderboardListAdapter adapter = new LeaderboardListAdapter(context, userScoreList);
        leaderboardList.setAdapter(adapter);
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