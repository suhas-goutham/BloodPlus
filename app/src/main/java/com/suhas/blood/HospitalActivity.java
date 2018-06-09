package com.suhas.blood;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HospitalActivity extends AppCompatActivity {


    TextView name1, email1, city1, mobile1;
    String names = "", emails = "", citys = "", mobiles = "";
    Timer timer1=new Timer();

    public void goToHospital(View view){

        Intent intent=new Intent(getApplicationContext(),HospitalMapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        name1 = (TextView) findViewById(R.id.name1);
        email1 = (TextView) findViewById(R.id.email1);
        city1 = (TextView) findViewById(R.id.city1);
        mobile1 = (TextView) findViewById(R.id.contact1);

        if (ParseUser.getCurrentUser() != null) {

            ParseQuery<ParseUser> query1 = ParseUser.getQuery();
            query1.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query1.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() == 1) {
                            names = objects.get(0).get("name").toString();
                            emails = objects.get(0).getUsername();
                            citys = objects.get(0).get("city").toString();
                            mobiles = objects.get(0).get("mobile").toString();


                            name1.setText("Hospital Name: " + names);
                            email1.setText("Email: " + emails);
                            city1.setText("City: " + citys);
                            mobile1.setText("Contact number: " + mobiles);

                        }
                    } else {
                        Log.i("Info", "Query 1 null");
                    }
                }
            });

/*new*/
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("DonorReady");
            query.whereEqualTo("hospital", ParseUser.getCurrentUser().get("name"));
            final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

            if(ParseUser.getCurrentUser().get("userType").equals("hospital")) {
                timer1.scheduleAtFixedRate(new TimerTask() {
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
                                                if (ob.get("notified").equals(false) && ob.get("hospital").equals(ParseUser.getCurrentUser().get("name"))) {

                                            /*    Intent donorIntent = new Intent(getApplicationContext(), Authentication_page.class);

                                                donorIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                final PendingIntent donorPendingIntent =
                                                        PendingIntent.getActivity(getApplicationContext(), 0, donorIntent, 0);
                                              */

                                                    NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext()).
                                                            setSmallIcon(R.drawable.blood_image).
                                                            setContentTitle("Donor is ready").
                                                            setContentText("Donor " + ob.get("donorName") + "(" + ob.get("blood_grp") + ") is on the way").
                                                            setAutoCancel(true);

                                                    notificationManager.notify(1, mBuilder.build());

                                                    Log.i("User_Donor_Ready",ParseUser.getCurrentUser().get("name").toString());

                                                    ob.put("notified", true);
                                                    ob.saveInBackground();
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        });


                    }
                }, 1000, 10000);
/*end new*/
            }
        }
    }

    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(HospitalActivity.this,R.style.Theme_AppCompat_Dialog);

        builder.setMessage("Are you sure you want to logout?")
                .setTitle("Logout").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        timer1.cancel();
                        ParseUser.getCurrentUser().logOut();

                        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);

                    }
                }).
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Alert","cancelled");
                    }
                }).show();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

}

