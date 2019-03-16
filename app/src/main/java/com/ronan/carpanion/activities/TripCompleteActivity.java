package com.ronan.carpanion.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ronan.carpanion.R;
import com.ronan.carpanion.adapters.PassengerListAdapter;
import com.ronan.carpanion.adapters.RatePassengerListAdapter;
import com.ronan.carpanion.entitites.Trip;
import com.ronan.carpanion.entitites.User;
import com.ronan.carpanion.viewlayouts.TripViewLayout;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class TripCompleteActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageButton backButton, thumbsUp, thumbsDown;
    private ImageView userImage;
    private TextView tripCompleteText, driverName, driver, passengerText, departure, destination;
    private Trip trip;
    private User user;
    private ListView passengerList;
    private Context context;
    private Bitmap bitmap;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_complete);

        getSupportActionBar().hide();
        context = this;
        geocoder = new Geocoder(context, Locale.getDefault());

        Intent intent = getIntent();
        trip = (Trip) intent.getSerializableExtra("trip");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/circular-book.otf");
        tripCompleteText = (TextView) findViewById(R.id.tripCompleteText);
        tripCompleteText.setTypeface(typeface);
        driver = (TextView) findViewById(R.id.driver);
        driver.setTypeface(typeface);
        driverName = (TextView) findViewById(R.id.driverName);
        driverName.setTypeface(typeface);
        backButton = (ImageButton) findViewById(R.id.backButton);
        passengerList = (ListView) findViewById(R.id.passengers);
        passengerText = (TextView) findViewById(R.id.passengerText);
        passengerText.setTypeface(typeface);
        userImage = (ImageView) findViewById(R.id.userImage);
        thumbsUp = (ImageButton) findViewById(R.id.thumbsUp);
        thumbsDown = (ImageButton) findViewById(R.id.thumbsDown);
        departure = (TextView) findViewById(R.id.departure);
        departure.setTypeface(typeface);
        destination = (TextView) findViewById(R.id.destination);
        destination.setTypeface(typeface);
        thumbsUp.setOnClickListener(this);
        thumbsDown.setOnClickListener(this);
        backButton.setOnClickListener(this);

        List<Address> addresses = null;
        String departureAddress;
        String destinationAddress;

        try
        {
            addresses = geocoder.getFromLocation(Double.parseDouble(trip.getDepartureLat()), Double.parseDouble(trip.getDepartureLong()), 1);
            departureAddress = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality(); //+ ", " + addresses.get(0).getAdminArea()
            departure.setText(departureAddress);

            addresses = geocoder.getFromLocation(Double.parseDouble(trip.getDestinationLat()), Double.parseDouble(trip.getDestinationLong()), 1);
            destinationAddress = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality(); //+ ", " + addresses.get(0).getAdminArea()
            destination.setText(destinationAddress);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(trip.getDriverID());
        ValueEventListener vl = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                driverName.setText(user.getFirstName() + " " + user.getLastName());
                String img = user.getProfileImage();
                try
                {
                    bitmap = new TripViewLayout.GetUserImage().execute(img).get();
                    userImage.setImageBitmap(bitmap);
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
        ref.addValueEventListener(vl);

        ArrayList<String> passengers = trip.getPassengers();

        if(passengers == null)
        {
            return;
        }

        RatePassengerListAdapter adapter = new RatePassengerListAdapter(context, passengers);
        passengerList.setAdapter(adapter);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.backButton:
                finish();
                break;
            case R.id.thumbsUp:
                thumbsUp.setImageResource(R.drawable.ic_thumb_up_white_clicked);
                thumbsUp.setOnClickListener(null);
                thumbsDown.setOnClickListener(null);
                int userPointUp = user.getUserScore();
                userPointUp++;
                user.setUserScore(userPointUp);
                DatabaseReference tripDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(trip.getDriverID());
                tripDatabase.child("userScore").setValue(userPointUp);
                break;
            case R.id.thumbsDown:
                thumbsDown.setImageResource(R.drawable.ic_thumb_down_white_clicked);
                thumbsUp.setOnClickListener(null);
                thumbsDown.setOnClickListener(null);
                int userPointDown = user.getUserScore();
                userPointDown--;
                user.setUserScore(userPointDown);
                DatabaseReference tripDatabase2 = FirebaseDatabase.getInstance().getReference().child("user").child(trip.getDriverID());
                tripDatabase2.child("userScore").setValue(userPointDown);
                break;
        }
    }
}