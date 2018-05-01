package com.suhas.blood;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class HospitalMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;
    ParseGeoPoint geoPoint;
    LatLng user;

    private GoogleMap mMap;


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ParseUser.getCurrentUser().logOut();

        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.blood_request:
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    requestAllBlood();
                }else{
                    Toast.makeText(getApplicationContext(),"Location is off",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.filter:
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent intent = new Intent(getApplicationContext(), FilterBloodActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Location is off",Toast.LENGTH_SHORT).show();
                }
                break;
            default:break;
        }

        return false;
    }

    public void requestBlood(){

        if(user!=null) {

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(user).title(ParseUser.getCurrentUser().get("name").toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 18));

            final ArrayList list = getIntent().getStringArrayListExtra("bloodList");

            if (list.size() == 0) {
                requestAllBlood();
            } else {

                Log.i("array", list.toString());

                ParseQuery<ParseUser> query = ParseUser.getQuery();

                query.whereNear("location", geoPoint);

                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {

                                for (ParseUser ob : objects) {

                                    if (ob.get("userType").equals("donor") && list.contains(ob.get("blood_grp").toString())) {

                                        Double dist = geoPoint.distanceInKilometersTo((ParseGeoPoint) ob.getParseGeoPoint("location"));

                                        Double distance = (double) Math.round(dist * 10) / 10;

                                        if(distance<10) {

                                            LatLng user = new LatLng(ob.getParseGeoPoint("location").getLatitude(), ob.getParseGeoPoint("location").getLongitude());

                                            mMap.addMarker(new MarkerOptions().position(user).title(ob.get("name").toString() + "(" + ob.get("blood_grp") + ")").snippet(ob.getUsername()));
                                        }

                                    }
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "No nearby users", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i("ShowUsers", "Fail");
                        }
                    }
                });
            }
            }else{
                Toast.makeText(getApplicationContext(), "Location is off", Toast.LENGTH_SHORT).show();
            }

    }

    public void requestAllBlood(){

        if(user!=null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(user).title(ParseUser.getCurrentUser().get("name").toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(ParseUser.getCurrentUser().getUsername()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 18));

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereNear("location", geoPoint);


            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {

                            for (ParseUser ob : objects) {

                                if (ob.get("userType").equals("donor")) {

                                    Double dist = geoPoint.distanceInKilometersTo((ParseGeoPoint) ob.getParseGeoPoint("location"));

                                    Double distance = (double) Math.round(dist * 10) / 10;
                                    Log.i("Distance" + ob.get("name").toString(), distance.toString());

                                    if(distance<10) {
                                        LatLng user = new LatLng(ob.getParseGeoPoint("location").getLatitude(), ob.getParseGeoPoint("location").getLongitude());

                                        mMap.addMarker(new MarkerOptions().position(user).title(ob.get("name").toString() + "(" + ob.get("blood_grp") + ")").snippet(ob.getUsername()));
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "No nearby users", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.i("ShowUsers", "Fail");
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Location is off",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (lastKnownLocation != null) {
                        user = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        geoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                        ParseUser.getCurrentUser().put("location", geoPoint);
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    Log.i("Hospital location", "Saved successfully");
                                } else {
                                    Log.i("Hospital location", "Failed");
                                }
                            }
                        });


                        if (getIntent().getStringExtra("intentType").equals("filter")) {
                            Log.i("Intent", "Filter");
                            requestBlood();
                        } else {
                            Log.i("Intent", "Login");
                            requestAllBlood();
                        }
                    } else {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        requestAllBlood();

                    }

                }
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            AlertDialog.Builder builder = new AlertDialog.Builder(HospitalMapsActivity.this, R.style.Theme_AppCompat_Dialog);

            builder.setMessage("Do you wish to turn on your location?")
                    .setTitle("Enable location").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    }).
                    setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(), "Location is off", Toast.LENGTH_SHORT).show();
                        }
                    }).show();


        }

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i("Location", location.toString());

                    user = new LatLng(location.getLatitude(), location.getLongitude());
                    geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

                    if (ParseUser.getCurrentUser() != null) {
                        ParseUser.getCurrentUser().put("location", geoPoint);

                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    Log.i("User location", "Saved successfully");
                                } else {
                                    Log.i("User location", "Failed");
                                }
                            }
                        });

                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
                    requestAllBlood();

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };


            if (Build.VERSION.SDK_INT < 23) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                if (getIntent().getStringExtra("intentType").equals("filter")) {
                    Log.i("Intent", "Filter");
                    requestBlood();
                } else {
                    Log.i("Intent", "Login");
                    requestAllBlood();
                }

            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(lastKnownLocation!=null) {
                        user = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        geoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());


                        ParseUser.getCurrentUser().put("location", geoPoint);
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    Log.i("Hospital location", "Saved successfully");
                                } else {
                                    Log.i("Hospital location", "Failed");
                                }
                            }
                        });

                    }else{
                   //     Toast.makeText(getApplicationContext(),"LastKnown is null",Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                        requestAllBlood();
                    }
                }

            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (getIntent().getStringExtra("intentType") != null && getIntent().getStringExtra("intentType").equals("filter")) {
                    Log.i("Intent", "Filter");
                    requestBlood();
                } else {
                    Log.i("Intent", "Login");
                    requestAllBlood();
                }


            }


            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.i("Text", marker.getSnippet());

                    if (!marker.getSnippet().equals(ParseUser.getCurrentUser().getUsername())) {

                        Log.i("Click", "RedMap");
                        Intent intent = new Intent(getApplicationContext(), ProfileUserActivity.class);
                        intent.putExtra("userId", marker.getSnippet());
                        startActivity(intent);
                        Log.i("Click", "RedMapEnd");

                    } else {
                        Log.i("Click", "GreenMap");
                    }
                }
            });

        }
    }


}


