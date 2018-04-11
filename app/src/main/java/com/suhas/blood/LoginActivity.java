package com.suhas.blood;

import android.app.PendingIntent;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    EditText loginId;
    EditText password;
    String loginText;
    String passwordText;
    String userType="donor";


    ConstraintLayout constraintLayout;
    ImageView imageView;
    Switch userTypeSwitch;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()== KeyEvent.ACTION_DOWN){
            login2(v);
        }

        return false;
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.backgroundConstraint || v.getId()==R.id.bloodImage || v.getId()==R.id.donorText || v.getId()==R.id.hospitalText){

            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }

    public void login2(View view){

        loginText=loginId.getText().toString();
        passwordText=password.getText().toString();





        if(userTypeSwitch.isChecked()){
            userType="hospital";
        }


        if(userType.equals("donor")) {
            ParseUser.logInInBackground(loginText, passwordText, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null && user.get("userType").equals("donor")) {

                        Toast.makeText(getApplicationContext(), "Welcome," + user.get("name"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), DonorMapsActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Username/Password/UserType", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{

            ParseUser.logInInBackground(loginText, passwordText, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null && user.get("userType").equals("hospital")) {
                        Toast.makeText(getApplicationContext(), "Welcome," + user.get("name"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), HospitalMapsActivity.class);
                        intent.putExtra("intentType","login");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Username/Password/UserType", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(0);

        loginId=(EditText) findViewById(R.id.loginText);
        password=(EditText) findViewById(R.id.passwordText);
        userTypeSwitch=(Switch) findViewById(R.id.switch1);

        constraintLayout=(ConstraintLayout) findViewById(R.id.backgroundConstraint);
        imageView=(ImageView) findViewById(R.id.bloodImage);

        constraintLayout.setOnClickListener(this);
        imageView.setOnClickListener(this);
        userTypeSwitch.setOnClickListener(this);

        password.setOnKeyListener(this);


    }


}
