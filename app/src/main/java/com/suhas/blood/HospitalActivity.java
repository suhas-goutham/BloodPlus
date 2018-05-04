package com.suhas.blood;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HospitalActivity extends AppCompatActivity {


    TextView name1, email1, city1, mobile1;
    String names = "", emails = "", citys = "", mobiles = "";

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

}

