package com.suhas.blood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

public class HospitalMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;
    ParseGeoPoint geoPoint;
    LatLng user;

    private GoogleMap mMap;

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
                requestAllBlood();
                break;
            case R.id.filter:
                Intent intent=new Intent(getApplicationContext(),FilterBloodActivity.class);
                startActivity(intent);
                break;
            default:break;
        }

        return false;
    }

    public void requestBlood(){

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(user).title(ParseUser.getCurrentUser().get("name").toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,10));

        final ArrayList list=getIntent().getStringArrayListExtra("bloodList");

        Log.i("array",list.toString());

        ParseQuery<ParseUser> query=ParseUser.getQuery();

        query.whereNear("location",geoPoint);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){

                        for(ParseUser ob:objects){

                            if(ob.get("userType").equals("donor") && list.contains(ob.get("blood_grp").toString())){

                            Double dist=geoPoint.distanceInKilometersTo((ParseGeoPoint) ob.getParseGeoPoint("location"));

                            Double distance=(double) Math.round(dist*10)/10;

                            LatLng user = new LatLng(ob.getParseGeoPoint("location").getLatitude(),ob.getParseGeoPoint("location").getLongitude());

                            mMap.addMarker(new MarkerOptions().position(user).title(ob.get("name").toString()+"("+ob.get("blood_grp")+")").snippet(ob.getUsername()));


                            }
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"No nearby users",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.i("ShowUsers","Fail");
                }
            }
        });


    }

    public void requestAllBlood(){

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(user).title(ParseUser.getCurrentUser().get("name").toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(ParseUser.getCurrentUser().getUsername()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,10));

        ParseQuery<ParseUser> query=ParseUser.getQuery();

        query.whereNear("location",geoPoint);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){

                        for(ParseUser ob:objects){

                            if(ob.get("userType").equals("donor")){

                                Double dist=geoPoint.distanceInKilometersTo((ParseGeoPoint) ob.getParseGeoPoint("location"));

                                Double distance=(double) Math.round(dist*10)/10;

                                LatLng user = new LatLng(ob.getParseGeoPoint("location").getLatitude(),ob.getParseGeoPoint("location").getLongitude());

                                mMap.addMarker(new MarkerOptions().position(user).title(ob.get("name").toString()+"("+ob.get("blood_grp")+")").snippet(ob.getUsername()));
                            }
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"No nearby users",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.i("ShowUsers","Fail");
                }
            }
        });


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

                    user= new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    geoPoint=new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

                    ParseUser.getCurrentUser().put("location",geoPoint);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){

                                Log.i("Hospital location","Saved successfully");
                            }else{
                                Log.i("Hospital location","Failed");
                            }
                        }
                    });


                    if(getIntent().getStringExtra("intentType").equals("filter")){
                        Log.i("Intent","Filter");
                        requestBlood();
                    }else{
                        Log.i("Intent","Login");
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
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());

                user=new LatLng(location.getLatitude(),location.getLongitude());
                geoPoint=new ParseGeoPoint(location.getLatitude(),location.getLongitude());

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

            if(getIntent().getStringExtra("intentType").equals("filter")){
                Log.i("Intent","Filter");
                requestBlood();
            }else{
                Log.i("Intent","Login");
                requestAllBlood();
            }

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                user= new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                geoPoint=new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

                ParseUser.getCurrentUser().put("location",geoPoint);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){

                            Log.i("Hospital location","Saved successfully");
                        }else{
                            Log.i("Hospital location","Failed");
                        }
                    }
                });



            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if(getIntent().getStringExtra("intentType")!=null && getIntent().getStringExtra("intentType").equals("filter")){
                Log.i("Intent","Filter");
                requestBlood();
            }else{
                Log.i("Intent","Login");
                requestAllBlood();
            }


        }



        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i("Text",marker.getSnippet());

                if(!marker.getSnippet().equals(ParseUser.getCurrentUser().getUsername())) {

                    Log.i("Click","RedMap");
                    Intent intent = new Intent(getApplicationContext(), ProfileUserActivity.class);
                    intent.putExtra("userId", marker.getSnippet());
                    startActivity(intent);
                    Log.i("Click","RedMapEnd");

                }else{
                    Log.i("Click","GreenMap");
                }
            }
        });

    }
}
