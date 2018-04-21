package com.suhas.blood;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DonorActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    LatLng user;
    TextView name1,age1,gender1,blood1,contact1,previous1,hospital1,date1;
    String names="",ages="",genders="",bloods="",contacts="",previouss="",hospitals="",dates="";

    public void goToDonate(View view){

        timer.cancel();

        ParseQuery<ParseObject> query=new ParseQuery<ParseObject>("Request");
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.get(0).get("requestStatus").equals(false)){

                        Intent intent=new Intent(getApplicationContext(),DonorMapsActivity.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(getApplicationContext(),"Cannot donate now,please wait for a few days",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    Timer timer=new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        name1=(TextView) findViewById(R.id.name1);
        age1=(TextView) findViewById(R.id.age1);
        gender1=(TextView) findViewById(R.id.gender1);
        blood1=(TextView) findViewById(R.id.blood1);
        contact1=(TextView) findViewById(R.id.contact1);
        previous1=(TextView) findViewById(R.id.previous1);
        hospital1=(TextView) findViewById(R.id.hospital1);
        date1=(TextView) findViewById(R.id.date1);

        /*
        if(getIntent().getStringExtra("activity")!=null && getIntent().getStringExtra("activity").equals("authentication")){
            if(ParseUser.getCurrentUser()!=null){
                ParseQuery<ParseObject> q=new ParseQuery<ParseObject>("Request");
                q.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());

                q.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e==null){
                            if(objects.size()==1){
                                objects.get(0).put("requestStatus",false);
                                objects.get(0).saveInBackground();
                                Log.i("Saved","authentication");
                            }
                        }
                    }
                });
            }
        }
        */

        if(ParseUser.getCurrentUser()!=null) {

            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());


            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Log.i("Count", "1");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null) {
                                if (objects.size() > 0) {
                                    for (ParseObject ob : objects) {
                                        if (ParseUser.getCurrentUser() != null) {
                                            if (ob.get("requestStatus").equals(true) && ob.get("username").equals(ParseUser.getCurrentUser().getUsername()) && ob.get("finishedDonation").equals(false)) {

                                                Intent donorIntent = new Intent(getApplicationContext(), Authentication_page.class);

                                                donorIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                final PendingIntent donorPendingIntent =
                                                        PendingIntent.getActivity(getApplicationContext(), 0, donorIntent, 0);

                                                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext()).
                                                        setSmallIcon(R.drawable.blood_image).
                                                        setContentTitle("Request for blood").
                                                        setContentText("Do you accept to donate your blood to " + objects.get(0).get("hospitalName") + " hospital?").
                                                        setContentIntent(donorPendingIntent).
                                                        setAutoCancel(true);

                                                timer.cancel();
                                                notificationManager.notify(0, mBuilder.build());

                                            }

                                        }
                                    }
                                }
                            }
                        }
                    });


                }
            }, 1000, 10000);


            ParseQuery<ParseUser> query1 = ParseUser.getQuery();
            query1.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query1.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() == 1) {
                            names = objects.get(0).get("name").toString();
                            ages = objects.get(0).get("age").toString();
                            genders = objects.get(0).get("gender").toString();
                            bloods = objects.get(0).get("blood_grp").toString();
                            contacts = objects.get(0).get("mobile").toString();


                            name1.setText("Name: " + names);
                            age1.setText("Age: " + ages);
                            gender1.setText("Gender: " + genders);
                            blood1.setText("Blood Group: " + bloods);
                            contact1.setText("Contact number: " + contacts);

                        }
                    } else {
                        Log.i("Info", "Query 1 null");
                    }
                }
            });

            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Request");
            query2.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query2.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() == 1) {
                            if (objects.get(0).get("finishedDonation").equals(true)) {


                                hospitals = objects.get(0).get("hospitalName").toString();
                                dates = objects.get(0).get("date").toString();

                                String date11 = dates.substring(4, 11);
                                String date12 = dates.substring(30);

                                hospital1.setText("Hospital name: " + hospitals);
                                date1.setText("Date: " + date11 + " " + date12);

                            } else {
                                previous1.setText("Previous Donation Details: None");
                            }
                        }
                    } else {
                        Log.i("Info", "Query 2 null");
                    }
                }
            });

        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
                user=new LatLng(location.getLatitude(),location.getLongitude());

                ParseGeoPoint geoPoint=new ParseGeoPoint(location.getLatitude(),location.getLongitude());

                if(ParseUser.getCurrentUser()!=null) {
                    ParseUser.getCurrentUser().put("location", geoPoint);

                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                Log.i("User location", "successfully");
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

                user = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                ParseGeoPoint geoPoint=new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

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

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timer.cancel();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ParseUser.getCurrentUser().logOut();

        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }
}
