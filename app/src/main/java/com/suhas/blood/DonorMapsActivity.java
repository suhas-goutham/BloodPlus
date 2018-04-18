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
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.List;

public class DonorMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;

    private GoogleMap mMap;

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

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
                LatLng user=new LatLng(location.getLatitude(),location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(user).title("You are here").snippet(ParseUser.getCurrentUser().getUsername()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,10));

                final ParseGeoPoint geoPoint=new ParseGeoPoint(location.getLatitude(),location.getLongitude());

                ParseUser.getCurrentUser().put("location",geoPoint);

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){

                            Log.i("User location","Saved successfully");
                        }else{
                            Log.i("User location","Failed");
                        }
                    }
                });

                ParseQuery<ParseUser> query=ParseUser.getQuery();
                query.whereNear("location",geoPoint);

                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if(e==null){
                            if(objects.size()>0){

                                for(ParseUser ob:objects){

                                    if(ob.get("userType").equals("hospital")){

                                        Double dist=geoPoint.distanceInKilometersTo((ParseGeoPoint) ob.getParseGeoPoint("location"));

                                        Double distance=(double) Math.round(dist*10)/10;

                                        Log.i("Distance"+ob.get("name").toString(),distance.toString());

                                        LatLng user = new LatLng(ob.getParseGeoPoint("location").getLatitude(),ob.getParseGeoPoint("location").getLongitude());

                                        mMap.addMarker(new MarkerOptions().position(user).title(ob.get("name").toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(ob.getUsername()));
                                    }
                                }

                            }else{
                                Toast.makeText(getApplicationContext(),"No nearby hospitals",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.i("ShowUsers","Fail");
                        }
                    }
                });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                LatLng lastKnown = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(lastKnown).title("You are here").snippet(ParseUser.getCurrentUser().getUsername()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnown,10));

                final ParseGeoPoint geoPoint=new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

                ParseUser.getCurrentUser().put("location",geoPoint);

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                            if(e==null){

                                Log.i("User location","Saved successfully");
                            }else{
                                Log.i("User location","Failed");
                            }
                    }
                });

                ParseQuery<ParseUser> query=ParseUser.getQuery();
                query.whereNear("location",geoPoint);

                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if(e==null){
                            if(objects.size()>0){

                                for(ParseUser ob:objects){

                                    if(ob.get("userType").equals("hospital")){

                                        Double dist=geoPoint.distanceInKilometersTo((ParseGeoPoint) ob.getParseGeoPoint("location"));

                                        Double distance=(double) Math.round(dist*10)/10;

                                        Log.i("Distance"+ob.get("name").toString(),distance.toString());

                                        LatLng user = new LatLng(ob.getParseGeoPoint("location").getLatitude(),ob.getParseGeoPoint("location").getLongitude());

                                        mMap.addMarker(new MarkerOptions().position(user).title(ob.get("name").toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(ob.getUsername()));
                                    }
                                }

                            }else{
                                Toast.makeText(getApplicationContext(),"No nearby hospitals",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.i("ShowUsers","Fail");
                        }
                    }
                });
            }

        }


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                Log.i("Text",marker.getSnippet());

                if(!marker.getSnippet().equals(ParseUser.getCurrentUser().getUsername())) {


                    /*
                    Intent intent = new Intent(getApplicationContext(), ProfileUserActivity.class);
                    intent.putExtra("userId", marker.getSnippet());
                    startActivity(intent);
                    */

                    AlertDialog.Builder builder = new AlertDialog.Builder(DonorMapsActivity.this,R.style.Theme_AppCompat_Dialog);

                    builder.setMessage("Do you wish to donate your blood to "+marker.getTitle()+" hospital?")
                            .setTitle("Confirm donation").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button

                            ParseQuery<ParseObject> query=new ParseQuery<ParseObject>("Request");
                            query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());

                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e==null){

                                        ParseGeoPoint parseGeoPoint=new ParseGeoPoint(marker.getPosition().latitude,marker.getPosition().longitude);
                                        Calendar c=Calendar.getInstance();

                                        objects.get(0).put("requestStatus",true);
                                        objects.get(0).put("hospitalName",marker.getTitle());
                                        objects.get(0).put("hospitalGeoPoint",parseGeoPoint);
                                        objects.get(0).put("date",c.getTime());
                                        objects.get(0).put("finishedDonation",true);

                                        objects.get(0).saveInBackground();

                                    }
                                }
                            });

                            LatLng latLng=marker.getPosition();

                            Intent donorIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=" + latLng.latitude+ "," + latLng.longitude));

                            startActivity(donorIntent);

                        }
                    }).
                    setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i("Alert","cancelled");
                        }
                    }).show();

                    //AlertDialog dialog = builder.create();

                    //dialog.show();

                    Log.i("Click","GreenMap");

                }else{
                    Log.i("Click","RedMap");
                }
            }
        });

    }
}
