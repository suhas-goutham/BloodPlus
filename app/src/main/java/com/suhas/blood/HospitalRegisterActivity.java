package com.suhas.blood;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class HospitalRegisterActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    EditText nameH,emailH,passwordH,mobileH,cityH,areaH;
    String name_s,email_s,password_s,mobile_s,city_s,area_s;
    ConstraintLayout hospitalConstraint;
    TextView hospitalTextView;


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){

            registerHospital(v);
        }

        return false;
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.registerConstraint || v.getId()==R.id.textViewHospital){

            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }

    public void registerHospital(View view){

        name_s=nameH.getText().toString();
        email_s=emailH.getText().toString();
        password_s=passwordH.getText().toString();
        mobile_s=mobileH.getText().toString();
        city_s=cityH.getText().toString();
        area_s=areaH.getText().toString();

        final ParseUser user=new ParseUser();

        user.setUsername(email_s);
        user.setPassword(password_s);
        user.setEmail(email_s);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if(e==null){
                    Log.i("SignUp","Success");

                    user.put("city",city_s);
                    user.put("mobile",mobile_s);
                    user.put("area",area_s);
                    user.put("name",name_s);
                    user.put("userType","hospital");

                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Log.i("UserInfo","Success");
                            }else{
                                Log.i("UserInfo","Fail "+e.getMessage());
                            }
                        }
                    });
                }else{
                    Log.i("SignUp","Fail"+e.getMessage());
                }
            }
        });

        Toast.makeText(getApplicationContext(),"Hospital Registered: "+name_s,Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_register);

        nameH=(EditText) findViewById(R.id.nameHText);
        emailH=(EditText) findViewById(R.id.emailHText);
        passwordH=(EditText) findViewById(R.id.passwordHText1);
        mobileH=(EditText) findViewById(R.id.mobileHText);
        cityH=(EditText) findViewById(R.id.cityHText);
        areaH=(EditText) findViewById(R.id.areaHText);

        hospitalConstraint=(ConstraintLayout) findViewById(R.id.registerConstraint);
        hospitalTextView=(TextView) findViewById(R.id.textViewHospital);

        areaH.setOnKeyListener(this);

        hospitalConstraint.setOnClickListener(this);
        hospitalTextView.setOnClickListener(this);


    }

}
