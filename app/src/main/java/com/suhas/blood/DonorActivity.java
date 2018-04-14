package com.suhas.blood;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
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

public class DonorActivity extends AppCompatActivity {

    public void cancelTimer(View view){

        timer.cancel();
    }

    Timer timer=new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        final TextView t4=(TextView) findViewById(R.id.textView4);

        /*
        ParseObject ob=new ParseObject("Request");

        ob.put("username",ParseUser.getCurrentUser().getUsername());
        ob.put("requestStatus",false);

        ob.saveInBackground();
        */

        final ParseQuery<ParseObject> query=ParseQuery.getQuery("Request");
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());

        Intent donorIntent = new Intent(getApplicationContext(),MainActivity.class);
        donorIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent donorPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0,donorIntent, 0);

        Intent donor1Intent = new Intent(getApplicationContext(),LoginActivity.class);

        donorIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent donor1PendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0,donor1Intent, 0);


        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext()).
                setSmallIcon(R.drawable.blood_image).
                setContentTitle("Request for blood").
                setContentText("Do you accept to donate your blood to "+ParseUser.getCurrentUser().get("name")+"hospital?").
                addAction(R.drawable.blood_image,"Accept",donorPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0, mBuilder.build());

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("Count","1");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if(e==null){
                            if(objects.size()>0){
                                if(objects.get(0).get("requestStatus").equals(true)){
                                    timer.cancel();
                                    t4.setText("Got notification");



                                }
                            }
                        }
                    }
                });



            }
        },1000,10000);


    }
}
