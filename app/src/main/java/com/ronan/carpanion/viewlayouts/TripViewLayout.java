package com.ronan.carpanion.viewlayouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ronan.carpanion.R;
import com.ronan.carpanion.entitites.Trip;
import com.ronan.carpanion.entitites.User;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TripViewLayout extends RelativeLayout implements View.OnClickListener
{
    private TextView driverName;
    private TextView leavingIn;
    private TextView from;
    private TextView to;
    private Button reserveSeat;
    private ImageView profileImage;
    private Bitmap profileBitmap;

    private Context context;
    private Trip trip;
    private User user;

    private Geocoder geocoder;
    private DatabaseReference database;

    public TripViewLayout(Context context)
    {
        super(context);
    }

    public TripViewLayout(Context context, Trip trip)
    {
        super(context);

        this.context = context;
        geocoder = new Geocoder(context, Locale.getDefault());

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.listview_trips, this, true);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/circular-book.otf");
        driverName = (TextView) findViewById(R.id.driverName);
        driverName.setTypeface(typeface);
        leavingIn = (TextView) findViewById(R.id.leavingIn);
        leavingIn.setTypeface(typeface);
        from = (TextView) findViewById(R.id.from);
        from.setTypeface(typeface);
        to = (TextView) findViewById(R.id.to);
        to.setTypeface(typeface);
        reserveSeat = (Button) findViewById(R.id.reserveSeat);
        reserveSeat.setTypeface(typeface);
        profileImage = (ImageView) findViewById(R.id.profileImage);

        reserveSeat.setOnClickListener(this);
        setTrip(trip);
    }

    public void setTrip(Trip t)
    {
        trip = t;

        if(t.getPassengers() != null)
        {
            if(t.getNumberOfPassengers() == t.getPassengers().size())
            {
                reserveSeat.setText("Car full");
                reserveSeat.setBackgroundResource(R.drawable.blue_button);
                reserveSeat.setOnClickListener(null);
            }
        }

        List<Address> addresses = null;
        String departureAddress;
        String destinationAddress;

        try
        {
            addresses = geocoder.getFromLocation(Double.parseDouble(t.getDepartureLat()), Double.parseDouble(t.getDepartureLong()), 1);
            departureAddress = "From: " + addresses.get(0).getLocality(); //+ ", " + addresses.get(0).getAdminArea()
            from.setText(departureAddress);

            addresses = geocoder.getFromLocation(Double.parseDouble(t.getDestinationLat()), Double.parseDouble(t.getDestinationLong()), 1);
            destinationAddress = "To: " + addresses.get(0).getLocality(); //+ ", " + addresses.get(0).getAdminArea()
            to.setText(destinationAddress);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        database = FirebaseDatabase.getInstance().getReference().child("user").child(t.getDriverID());
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                driverName.setText(user.getFirstName() + " " + user.getLastName());
                String userImage = user.getProfileImage();
                try
                {
                    profileBitmap = new GetUserImage().execute(userImage).get();
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

        leavingIn.setText("Leaving in " + trip.getDepartureTime());
    }

    @Override
    public void onClick(View view)
    {
        ArrayList<String> passengerArray = trip.getPassengers();

        if(passengerArray == null)
        {
            passengerArray = new ArrayList<>();
        }

        passengerArray.add(FirebaseAuth.getInstance().getUid());
        Trip tripCommit = new Trip(trip.getDriverID(), trip.getNumberOfPassengers(), trip.getDepartureTime(), trip.getDestinationLat(), trip.getDestinationLong(), trip.getDepartureLat(), trip.getDepartureLong(), trip.getTimestamp(), passengerArray);
        DatabaseReference tripDatabase = FirebaseDatabase.getInstance().getReference().child("trip").child(trip.getDriverID());
        tripDatabase.setValue(tripCommit);

        Toast toast = Toast.makeText(context, "Seat Reseved", Toast.LENGTH_SHORT);
        toast.show();
        reserveSeat.setText("Seat Reserved");
        reserveSeat.setBackgroundResource(R.drawable.blue_button);
        reserveSeat.setOnClickListener(null);
    }

    public static class GetUserImage extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... strings)
        {
            Bitmap bitmap = null;
            try
            {
                URL imageURL = new URL(strings[0]);
                URLConnection connection = imageURL.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                bufferedInputStream.close();
                inputStream.close();
            }
            catch (IOException ex)
            {
                Log.e("", "Error", ex);
            }
            return bitmap;
        }
    }
}
