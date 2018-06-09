package com.suhas.blood;

import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

public class Authentication_page extends AppCompatActivity {

    CheckBox c1,c2,c3,c4,c5,c6,c7;

    public void submit(View view){
        if(c1.isChecked() || c2.isChecked() ||c3.isChecked() ||c4.isChecked() ||c5.isChecked() ||c6.isChecked() ||c7.isChecked() ){
            Toast.makeText(getApplicationContext(),"Sorry,you are not eligible to donate",Toast.LENGTH_SHORT).show();


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
                                Log.i("authentication","saved");
                            }
                        }
                    }
                });
            }

            Intent intent=new Intent(getApplicationContext(),DonorActivity.class);
            startActivity(intent);
        }else {

            if (ParseUser.getCurrentUser() != null) {



                ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {

                                Calendar c = Calendar.getInstance();
                                Log.i("Current date", c.getTime().toString());

                                objects.get(0).put("date", c.getTime());
                                objects.get(0).put("finishedDonation", true);

                                objects.get(0).saveInBackground();

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                notificationManager.cancel(0);

                                Intent donorIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?daddr=" + objects.get(0).getParseGeoPoint("hospitalGeoPoint").getLatitude() + "," + objects.get(0).getParseGeoPoint("hospitalGeoPoint").getLongitude()));

/*new*/                         ParseObject ob=new ParseObject("DonorReady");

                                ob.put("donor",ParseUser.getCurrentUser().getUsername());
                                ob.put("donorName",ParseUser.getCurrentUser().get("name"));
                                ob.put("blood_grp",ParseUser.getCurrentUser().get("blood_grp"));
                                ob.put("hospital",objects.get(0).get("hospitalName"));
                                ob.put("notified",false);

/*end new*/                     ob.saveInBackground();

                                startActivity(donorIntent);

                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_page);

        c1=(CheckBox) findViewById(R.id.checkBox1);
        c2=(CheckBox) findViewById(R.id.checkBox2);
        c3=(CheckBox) findViewById(R.id.checkBox3);
        c4=(CheckBox) findViewById(R.id.checkBox4);
        c5=(CheckBox) findViewById(R.id.checkBox5);
        c6=(CheckBox) findViewById(R.id.checkBox6);
        c7=(CheckBox) findViewById(R.id.checkBox7);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),DonorActivity.class);
        intent.putExtra("activity","authentication");
        startActivity(intent);
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
