 package com.suhas.blood;

import android.content.Intent;
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

    public void register(View view){

        Toast.makeText(this,"Register",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            ParseUser.getCurrentUser().logOut();
        }catch (Exception e){

        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        
    }

     @Override
     public void onBackPressed() {
         super.onBackPressed();

         Intent intent = new Intent(Intent.ACTION_MAIN);
         intent.addCategory(Intent.CATEGORY_HOME);
         startActivity(intent);
     }
 }
