package com.ronan.carpanion.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ronan.carpanion.R;
import com.ronan.carpanion.adapters.PassengerListAdapter;
import com.ronan.carpanion.entitites.Trip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MyTripActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView leavingFrom, arrivingAt, leavingAt, passengerText;
    private ListView passengerList;
    private ImageButton backButton;
    private Button startTripButton;

    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private ValueEventListener vl;

    private Trip trip;
    private Context context;
    private Geocoder geocoder;

    private Location destination;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);

        getSupportActionBar().hide();

        context = this;
        geocoder = new Geocoder(context, Locale.getDefault());

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/circular-book.otf");
        leavingFrom = (TextView) findViewById(R.id.leavingFrom);
        leavingFrom.setTypeface(typeface);
        arrivingAt = (TextView) findViewById(R.id.arrivingAt);
        arrivingAt.setTypeface(typeface);
        //leavingAt = (TextView) findViewById(R.id.leavingAt);
        leavingFrom.setTypeface(typeface);
        passengerText = (TextView) findViewById(R.id.passengerText);
        passengerText.setTypeface(typeface);
        passengerList = (ListView) findViewById(R.id.passengerList);
        backButton = (ImageButton) findViewById(R.id.backButton);
        startTripButton = (Button) findViewById(R.id.startTripButton);
        startTripButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("trip").child(firebaseUser.getUid());
        vl = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                trip = dataSnapshot.getValue(Trip.class);

                if(trip == null)
                {
                    noTrip();
                    return;
                }

                List<Address> addresses = null;
                String departureAddress;
                String destinationAddress;

                destination = new Location("");
                destination.setLatitude(Double.parseDouble(trip.getDestinationLat()));
                destination.setLongitude(Double.parseDouble(trip.getDestinationLong()));

                try
                {
                    addresses = geocoder.getFromLocation(Double.parseDouble(trip.getDepartureLat()), Double.parseDouble(trip.getDepartureLong()), 1);
                    departureAddress = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality(); //+ ", " + addresses.get(0).getAdminArea()
                    leavingFrom.setText(departureAddress);

                    addresses = geocoder.getFromLocation(Double.parseDouble(trip.getDestinationLat()), Double.parseDouble(trip.getDestinationLong()), 1);
                    destinationAddress = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality(); //+ ", " + addresses.get(0).getAdminArea()
                    arrivingAt.setText(destinationAddress);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                ArrayList<String> passengers = trip.getPassengers();

                if(passengers == null)
                {
                    return;
                }

                PassengerListAdapter adapter = new PassengerListAdapter(context, passengers);
                passengerList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };
        ref.addValueEventListener(vl);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }

    public void noTrip()
    {
        leavingFrom.setText("No Trip");
    }

    private void startTrip()
    {
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(0)
                .setInterval(3000);

        SmartLocation.with(this)
                .location()
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener()
                {
                    @Override
                    public void onLocationUpdated(Location location)
                    {
                        if(location.distanceTo(destination) < 500)
                        {
                            SmartLocation.with(context).location().stop();
                            complete();
                        }
                    }
                });
    }

    private void complete()
    {
        DatabaseReference tripDatabase = FirebaseDatabase.getInstance().getReference().child("trip").child(trip.getDriverID());
        tripDatabase.child("tripComplete").setValue("true");
        Intent intent = new Intent(context, TripCompleteActivity.class);
        intent.putExtra("trip", trip);
        startActivity(intent);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.backButton:
                finish();
                break;
            case R.id.startTripButton:
                startTrip();
                break;
        }
    }
}
