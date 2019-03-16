package com.ronan.carpanion.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ronan.carpanion.R;
import com.ronan.carpanion.adapters.TripListAdapter;
import com.ronan.carpanion.entitites.Trip;
import com.ronan.carpanion.entitites.User;
import com.ronan.carpanion.util.DirectionsParser;
import com.ronan.carpanion.viewlayouts.TripViewLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, LocationListener, NavigationView.OnNavigationItemSelectedListener
{
    private Context context;

    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;

    private boolean mLocationPermissionGranted;

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int DEFAULT_ZOOM = 16;

    private LatLng tripDeparture;
    private LatLng tripDestination;

    private ArrayList<LatLng> listPoints;
    private ArrayList<Trip> tripList = new ArrayList<>();

    private CheckableImageButton hamburger;
    private FloatingActionButton addTrip;
    private FloatingActionButton getLocationButton;
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetBehavior addTripSheet;
    private TextView mapText;
    private TextView listText;
    private View mapUnderline;
    private View listUnderline;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView navProfileImage;
    private TextView navName;
    private TextView navUserType;
    private Bitmap profileBitmap;
    private Marker marker;
    private LatLng latLng;

    private User user;

    //Add Trip Variables
    private Geocoder geocoder;
    private LatLng addTripDeparture;
    private LatLng addTripDestination;

    private TextView departureResult;
    private TextView destinationResult;
    private Spinner noOfPassengers;
    private TimePicker timePicker;
    private Button createTripButton;
    private ListView driversList;

    private String departureAddress;
    private String destinationAddress;
    private String userImageForPin;
    private int numberOfPassengers = 1;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = this;

        getSupportActionBar().hide();

        geocoder = new Geocoder(this, Locale.getDefault());
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ((View)findViewById(R.id.place_autocomplete_search_button)).setVisibility(View.GONE);
        ((EditText)findViewById(R.id.place_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.black));

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                if(newState == BottomSheetBehavior.STATE_DRAGGING)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if(newState == BottomSheetBehavior.STATE_EXPANDED)
                {
                    //view.setBackground(Color.transparent);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });

        addTripSheet = BottomSheetBehavior.from(findViewById(R.id.add_trip_bottom_sheet));

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/circular-book.otf");

        mapText = (TextView) findViewById(R.id.mapText);
        mapText.setTypeface(typeface);
        listText = (TextView) findViewById(R.id.listText);
        listText.setTypeface(typeface);
        mapUnderline = (View) findViewById(R.id.mapUnderline);
        listUnderline = (View) findViewById(R.id.listUnderline);
        hamburger = (CheckableImageButton) findViewById(R.id.hamburger);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        driversList = (ListView) findViewById(R.id.driversList);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        View header = navigationView.getHeaderView(0);
        navName = (TextView) header.findViewById(R.id.navName);
        navName.setTypeface(typeface);
        navUserType = (TextView) header.findViewById(R.id.navUserType);
        navUserType.setTypeface(typeface);
        navProfileImage = (ImageView) header.findViewById(R.id.navProfileImage);

        addTrip = (FloatingActionButton) findViewById(R.id.addTrip);
        addTrip.setOnClickListener(this);
        addTrip.setVisibility(View.GONE);
        getLocationButton = (FloatingActionButton) findViewById(R.id.getLocationButton);
        getLocationButton.setOnClickListener(this);

        departureResult = (TextView) findViewById(R.id.departureResult);
        destinationResult = (TextView) findViewById(R.id.destinationResult);
        createTripButton = (Button) findViewById(R.id.createTripButton);
        createTripButton.setTypeface(typeface);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        mLastKnownLocation = locationManager.getLastKnownLocation(provider);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.addressSearch);
        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(50.999929,-10.854492),
                new LatLng(55.354135,-5.339355)
        ));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(Place place)
            {
                tripDeparture = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                tripDestination = place.getLatLng();

                if(user.getUserType() == 0)
                {
                    String url = getRequestUrl(tripDeparture, tripDestination);
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                    addTrip.setVisibility(View.VISIBLE);
                    getLocationButton.setVisibility(View.GONE);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(tripDeparture);
                    builder.include(tripDestination);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 48));
                }
                if(user.getUserType() == 1)
                {
                    System.out.println("PASSENGER");
                }
            }

            @Override
            public void onError(Status status)
            {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    builder = new AlertDialog.Builder(MapsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                }
                else
                {
                    builder = new AlertDialog.Builder(MapsActivity.this);
                }
                builder.setTitle("Error")
                        .setMessage("An Error has occurred, please try again")
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(firebaseUser.getUid());
        ValueEventListener vl = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                navName.setText(user.getFirstName() + " " + user.getLastName());
                String userImage = user.getProfileImage();
                try
                {
                    profileBitmap = new TripViewLayout.GetUserImage().execute(userImage).get();
                    navProfileImage.setImageBitmap(profileBitmap);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }

                if(user.getUserType() == 0)
                {
                    navUserType.setText(R.string.driver);
                }
                else
                {
                    navUserType.setText(R.string.passenger);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };
        ref.addValueEventListener(vl);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("trip");
        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
//                ArrayList<LatLng> locations = new ArrayList<>();
//                int smallestDistance = -1;
//                float[] results = new float[1];

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    tripList.add(postSnapshot.getValue(Trip.class));
                }

                for(Trip t : tripList)
                {
                    final Trip innerTrip = t;

                    if(t.getTripComplete() != null)
                    {
                        tripList.remove(t);
                    }

                    System.out.println(t.getTripComplete());

                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("user").child(t.getDriverID());
                    ValueEventListener vel = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            latLng = new LatLng(Double.parseDouble(innerTrip.getDepartureLat()), Double.parseDouble(innerTrip.getDepartureLong()));
                            User u = dataSnapshot.getValue(User.class);
                            userImageForPin = u.getProfileImage();


                            MarkerOptions options = new MarkerOptions().position(latLng);
                            Bitmap bitmap = createPinBitmap(userImageForPin);

                            if(bitmap != null)
                            {
                                options.title("");
                                options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                options.anchor(0.5f, 0.907f);
                                marker = mMap.addMarker(options);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    };
                    dr.addValueEventListener(vel);

                }

                TripListAdapter adapter = new TripListAdapter(context, tripList);
                driversList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);

        listPoints = new ArrayList<>();
        mapText.setOnClickListener(this);
        listText.setOnClickListener(this);
        createTripButton.setOnClickListener(this);
        hamburger.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public Bitmap createPinBitmap(String pinImage)
    {
        System.out.println("CREATE PIN BITMAP: " + pinImage);
        Bitmap result = null;
        try
        {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.pin);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = new TripViewLayout.GetUserImage().execute(pinImage).get();

            if(bitmap != null)
            {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }

            canvas.restore();

            try
            {
                canvas.setBitmap(null);
            }
            catch (Exception e)
            {

            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        return result;
    }

    public int dp(float value)
    {
        if (value == 0)
        {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);

        updateLocationUI();
        getDeviceLocation();

        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

    private String getRequestUrl(LatLng departure, LatLng destination)
    {
        String str_org = "origin=" + departure.latitude +"," + departure.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;

        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        System.out.println(url);
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException
    {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try
        {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    private void updateLocationUI()
    {
        if(mMap == null)
        {
            return;
        }
        try
        {
            if(mLocationPermissionGranted)
            {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else
            {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        }
        catch (SecurityException e)
        {
            Log.e("Exception: ", e.getMessage());
        }
    }

    private void getDeviceLocation()
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
                        mLastKnownLocation = location;
                    }
                });

//        try
//        {
//            if (mLocationPermissionGranted)
//            {
//                Task locationResult = mFusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            mLastKnownLocation = (Location) task.getResult();
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                        }
//                        else
//                        {
//                            Log.d("", "Current location is null. Using defaults.");
//                            Log.e("", "Exception: %s", task.getException());
//                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
//            }
//        }
//        catch(SecurityException e)
//        {
//            Log.e("Exception: %s", e.getMessage());
//        }
    }

    private void getLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public void addTrip() throws IOException
    {
        List<Address> addresses;
        addTripSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

        addresses = geocoder.getFromLocation(tripDeparture.latitude, tripDeparture.longitude, 1);
        departureAddress = "Departure: " + addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality();
        departureResult.setText(departureAddress);

        addresses = geocoder.getFromLocation(tripDestination.latitude, tripDestination.longitude, 1);
        destinationAddress = "Destination: " + addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality();
        destinationResult.setText(destinationAddress);
    }

    public void submitTrip()
    {
//        long hour = timePicker.getCurrentHour();
//        long min = timePicker.getCurrentMinute();
//        String departureTime = hour + ":" + min;
//
//        System.out.println("Current time: " + System.currentTimeMillis());
//        System.out.println("Departure time: " + hour + " " + min);

//        Calendar cal = Calendar.getInstance();
//        long diff = timePicker.getTimeInMillis() - cal.getTimeInMillis();
//        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diff);
//        int hour = (int) (diffInSec / (60 * 60));
//        int minremaining = (int) (diffInSec % (60 * 60));
//        int min = (int) (minremaining / (60));
//        int secondsRemaining = (int) (minremaining % (60));
//        Log.e("TAG", "Difference in    milliss   calendar      " + diff);
//        Log.e("TAG", "hour      " + hour);
//        Log.e("TAG", "min       " + min);
//        Log.e("TAG", "sec       " + secondsRemaining);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Trip trip = new Trip(userID, 1, "time", String.valueOf(tripDestination.latitude), String.valueOf(tripDestination.longitude), String.valueOf(tripDeparture.latitude), String.valueOf(tripDeparture.longitude));
        databaseReference.child(userID).setValue(trip);

        addTripSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Toast.makeText(this, "Trip successfully added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.addTrip:
                try
                {
                    addTrip();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
            case R.id.hamburger:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.mapText:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mapText.setTextColor(getResources().getColor(R.color.colorPrimary));
                listText.setTextColor(getResources().getColor(R.color.black));
                mapUnderline.setVisibility(View.VISIBLE);
                listUnderline.setVisibility(View.INVISIBLE);
                break;
            case R.id.listText:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                listText.setTextColor(getResources().getColor(R.color.colorPrimary));
                mapText.setTextColor(getResources().getColor(R.color.black));
                mapUnderline.setVisibility(View.INVISIBLE);
                listUnderline.setVisibility(View.VISIBLE);
                break;
            case R.id.createTripButton:
                submitTrip();
                break;
            case R.id.getLocationButton:
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.myTrip:
                startActivity(new Intent(this, MyTripActivity.class));
                break;
            case R.id.myMessages:
                startActivity(new Intent(this, MessagesActivity.class));
                break;
            case R.id.leaderboard:
                //ArrayList<User> userForLeaderboard = new ArrayList<>();
                //userForLeaderboard.add(user);
                Intent intent = new Intent(context, LeaderboardActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LogInActivity.class));
                break;
            case R.id.complete:
                startActivity(new Intent(this, TripCompleteActivity.class));
                break;
        }
        return true;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            String responseString = "";
            try
            {
                responseString = requestDirection(strings[0]);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >
    {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings)
        {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try
            {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists)
        {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists)
            {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path)
                {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.parseColor("#4285F4"));
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null)
            {
                mMap.addPolyline(polylineOptions);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_SHORT).show();
            }
        }
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