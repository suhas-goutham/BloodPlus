package com.suhas.blood;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import static android.R.attr.dial;

public class ProfileUserActivity extends AppCompatActivity {

    String name = "a", age = "a", bloodGrp = "a", contact = "a",username="a";
    ParseUser userProfile;
    Intent intent;

    void call(View view) {

        intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contact));

        if (!checkPermission(Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 0);
        }


        if (checkPermission(Manifest.permission.CALL_PHONE)) {
           startActivity(intent);
        }


    }


    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case 0:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(intent);
                }
                return;
        }
    }

    void request(View view){

        ParseQuery<ParseObject> query=ParseQuery.getQuery("Request");
        query.whereEqualTo("username",username);


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e==null){

                    if(objects.size()==1 && objects.get(0).get("requestStatus").equals(false)){

                        objects.get(0).put("requestStatus",true);
                        objects.get(0).put("hospitalName",ParseUser.getCurrentUser().get("name"));
                        objects.get(0).put("hospitalGeoPoint",ParseUser.getCurrentUser().get("location"));
                        objects.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Toast.makeText(getApplicationContext(),"Request sent to donor",Toast.LENGTH_SHORT).show();
                                }else{
                                    Log.i("Error","Save error ");

                                }
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(),"User already in action",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        final TextView nameTextView=(TextView) findViewById(R.id.nameTextView);
        final TextView ageTextView=(TextView) findViewById(R.id.ageTextView);
        final TextView bloodTextView=(TextView) findViewById(R.id.bloodTextView);
        final TextView contactTextView=(TextView) findViewById(R.id.contactTextView);

        Button requestButton=(Button) findViewById(R.id.requestButton);
        Button callButton=(Button) findViewById(R.id.callButton);

        String userId=getIntent().getStringExtra("userId");

        Log.i("User Id Profile",userId);

        Log.i("Break Profile","1");

       ParseQuery<ParseUser> query=ParseUser.getQuery();

        Log.i("Break Profile","2"+ParseUser.getCurrentUser().getUsername());
        //query.whereContains("username",userId);
        query.whereEqualTo("username",userId);
        Log.i("Break Profile","3");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null){
                    Log.i("Break Profile","4");
                    if(objects.size()>0){
                        Log.i("Break Profile","5");
                        for(ParseUser ob:objects){
                            Log.i("Break Profile","6");
                                userProfile=ob;
                                username=ob.getUsername();
                                name=ob.get("name").toString();
                                age=ob.get("age").toString();
                                bloodGrp=ob.get("blood_grp").toString();
                                contact=ob.get("mobile").toString();

                                Log.i("Break Profile","8");
                                nameTextView.setText("Name: "+name);
                                ageTextView.setText("Age: "+age);
                                bloodTextView.setText("Blood Group: "+bloodGrp);
                                contactTextView.setText("Contact: "+contact);
                                break;
                        }
                    }
                }else{
                    Log.i("User id Parse","error");
                    Log.i("Break Profile","7");
                }
            }
        });



        Log.i("Break Profile","9");
        Log.i("Name",name);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),HospitalMapsActivity.class);
        startActivity(intent);
    }
}
