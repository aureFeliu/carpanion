package com.ronan.carpanion.viewlayouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ronan.carpanion.R;
import com.ronan.carpanion.activities.LeaderboardActivity;
import com.ronan.carpanion.entitites.User;

import java.util.concurrent.ExecutionException;

public class LeaderboardViewLayout extends RelativeLayout
{
    private TextView place;
    private ImageView profileImage;
    private TextView userName;
    private TextView userPoints;
    private Bitmap profileBitmap;

    private User user;

    private Context context;

    public LeaderboardViewLayout(Context context)
    {
        super(context);
    }

    public LeaderboardViewLayout(Context context, User user, int position)
    {
        super(context);

        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.listview_leaderboard, this, true);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/circular-book.otf");
        place = (TextView) findViewById(R.id.place);
        place.setTypeface(typeface);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        userName = (TextView) findViewById(R.id.userName);
        userName.setTypeface(typeface);
        userPoints = (TextView) findViewById(R.id.userPoints);
        userPoints.setTypeface(typeface);

        setUser(user, position);
    }

    public void setUser(User u, int position)
    {
        user = u;
        userName.setText(u.getFirstName() + " " + u.getLastName());
        userPoints.setText(String.valueOf(u.getUserScore()));
        place.setText(String.valueOf(position));

        String userImage = u.getProfileImage();
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
}
