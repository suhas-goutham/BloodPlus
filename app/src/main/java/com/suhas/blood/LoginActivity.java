package com.suhas.blood;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity implements /*View.OnKeyListener,*/ View.OnClickListener {

    EditText loginId;
    EditText password;
    String loginText;
    String passwordText;
    String userType="donor";
    TextView textViewSignUp;
    String textSignUp;

    ConstraintLayout constraintLayout;
    ImageView imageView;
    Switch userTypeSwitch;

    /*
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()== KeyEvent.ACTION_DOWN){
            login2(v);
        }

        return false;
    }
    */



    @Override
    public void onClick(View v) {

        if(v.getId()== R.id.cardView1 || v.getId()==R.id.relativeLayout1 ){

            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }



    public void login2(View view){

        loginText=loginId.getText().toString().trim();
        passwordText=password.getText().toString().trim();


        if(userTypeSwitch.isChecked()){
            userType="hospital";
        }else{
            userType="donor";
        }


            ParseUser.logInInBackground(loginText, passwordText, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e==null){
                    if (user != null) {

                        if(user.get("userType").equals("donor") && userType.equals("donor")) {

                            Toast.makeText(getApplicationContext(), "Welcome," + user.get("name"), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), DonorActivity.class);
                            startActivity(intent);
                        }else if(user.get("userType").equals("hospital") && userType.equals("hospital")){
                            Toast.makeText(getApplicationContext(), "Welcome," + user.get("name"), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), HospitalActivity.class);
                            intent.putExtra("intentType","login");
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"Wrong user type", Toast.LENGTH_LONG).show();
                            ParseUser.getCurrentUser().logOut();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),"Null user", Toast.LENGTH_LONG).show();
                    }
                    }else{

                        //    Toast.makeText(getApplicationContext(),"U: "+loginText,Toast.LENGTH_SHORT).show();
                        //    Toast.makeText(getApplicationContext(),"P: "+passwordText,Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),  e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        /*
        else{

            ParseUser.logInInBackground(loginText, passwordText, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e==null){
                    if (user != null && user.get("userType").equals("hospital")) {
                        Toast.makeText(getApplicationContext(), "Welcome," + user.get("name"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), HospitalMapsActivity.class);
                        intent.putExtra("intentType","login");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Username/Password/UserType", Toast.LENGTH_LONG).show();
                    }}
                    else{
                      Toast.makeText(getApplicationContext(),"Connection error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
            */
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
        textViewSignUp=(TextView) findViewById(R.id.textViewSignup);

        CardView cardView=(CardView) findViewById(R.id.cardView1);
        RelativeLayout relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout1);

        cardView.setOnClickListener((View.OnClickListener) this);
        relativeLayout.setOnClickListener((View.OnClickListener) this);

        /*
        constraintLayout=(ConstraintLayout) findViewById(R.id.backgroundConstraint);
        imageView=(ImageView) findViewById(R.id.bloodImage);

        constraintLayout.setOnClickListener(this);
        imageView.setOnClickListener(this);
        userTypeSwitch.setOnClickListener(this);

        password.setOnKeyListener(this);
        */

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userTypeSwitch.isChecked()) {
                    Intent intent = new Intent(getApplicationContext(),HospitalRegisterActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(),DonorRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this,R.style.Theme_AppCompat_Dialog);

        builder.setMessage("Are you sure you want to exit?")
                .setTitle("Exit").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        ParseUser.getCurrentUser().logOut();

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
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
