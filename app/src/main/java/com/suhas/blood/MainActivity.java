 package com.suhas.blood;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

 public class MainActivity extends AppCompatActivity {

    public void login(View view){

        Toast.makeText(this,"LogIn",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        },3000);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.

        try{
            ParseUser.getCurrentUser().logOut();
        }catch (Exception e){

        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        
    }

     @Override
     protected void onResume() {
         super.onResume();

         View decorView = getWindow().getDecorView();
// Hide the status bar.
         int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
         decorView.setSystemUiVisibility(uiOptions);

         getSupportActionBar().hide();

         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                 startActivity(intent);
             }
         },3000);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.


     }

     @Override
     public void onBackPressed() {
         super.onBackPressed();

         Intent intent = new Intent(Intent.ACTION_MAIN);
         intent.addCategory(Intent.CATEGORY_HOME);
         startActivity(intent);
     }
 }
