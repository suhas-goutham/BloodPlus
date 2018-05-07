package com.suhas.blood;

import android.app.Application;
import com.parse.Parse;

import com.parse.ParseACL;

import com.parse.ParseException;

import com.parse.ParseObject;

import com.parse.ParseUser;

import com.parse.SaveCallback;



public class StarterApplication extends Application {



        @Override

        public void onCreate() {

            super.onCreate();



            // Enable Local Datastore.

            Parse.enableLocalDatastore(this);



            // Add your initialization code here

            Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())

                    .applicationId("74ea339cb7993abf29b68c3572cf638afa43a9b6")

                    .clientKey("ebdf6602399f412ed173c4be38720cd60ab9ce4b")

                    .server("http://13.59.33.21:80/parse/")

                    .build()

            );




          // ParseUser.enableAutomaticUser();



            ParseACL defaultACL = new ParseACL();

            defaultACL.setPublicReadAccess(true);

            defaultACL.setPublicWriteAccess(true);

            ParseACL.setDefaultACL(defaultACL, true);




        }

    }



