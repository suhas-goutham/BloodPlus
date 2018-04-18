package com.suhas.blood;

import android.content.Intent;
import android.media.MediaCodec;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;
import java.util.regex.Pattern;

public class DonorRegisterActivity extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {

    EditText name,email,password,mobile,age;
    String name_s,email_s,password_s,mobile_s,blood_s,age_s,gender_s;
    ConstraintLayout background;
    TextView donorHeading;
    Spinner blood,gender;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){

            registerDonor(v);
        }

        return false;
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.donorConstraint || v.getId()==R.id.textViewHospital){

            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }

    public void registerDonor(View view){

        name_s=name.getText().toString();
        email_s=email.getText().toString();
        password_s=password.getText().toString();
        mobile_s=mobile.getText().toString();
        blood_s=blood.getSelectedItem().toString();
        age_s=age.getText().toString();
        gender_s=gender.getSelectedItem().toString();

        int status=1;

        if(!((Pattern.matches("[a-zA-Z]{1,10}[\\s]{1,1}[a-zA-Z]{1,10}",name_s))||(Pattern.matches("[a-zA-Z]{1,10}",name_s)))){
            Toast.makeText(getApplicationContext(),"Enter correct name",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(!Pattern.matches("[a-zA-Z]{1,10}[@][a-zA-Z]{1,10}(.com|.org|.edu)",email_s)){
            Toast.makeText(getApplicationContext(),"Enter valid email ID",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(!(password_s.length()>=6)){
            Toast.makeText(getApplicationContext(),"Enter password of atleast 6 characters",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(!Pattern.matches("(9|8|7)[0-9]{9,9}",mobile_s)){
            status=0;
            Toast.makeText(getApplicationContext(),"Enter valid mobile number",Toast.LENGTH_SHORT).show();
        }

        if(age_s==null){
            status=0;
            Toast.makeText(getApplicationContext(),"Enter correct age",Toast.LENGTH_SHORT).show();
        }

        if(status==1) {
            final ParseUser user = new ParseUser();

            user.setUsername(email_s);
            user.setPassword(password_s);
            user.setEmail(email_s);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        Log.i("SignUp", "Success");

                        user.put("blood_grp", blood_s);
                        user.put("mobile", mobile_s);
                        user.put("age", age_s);
                        user.put("name", name_s);
                        user.put("userType", "donor");
                        user.put("gender",gender_s);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.i("UserInfo", "Success");
                                } else {
                                    Log.i("UserInfo", "Fail " + e.getMessage());
                                }
                            }
                        });

                        ParseObject ob=new ParseObject("Request");

                        ob.put("username",email_s);
                        ob.put("requestStatus",false);

                        ob.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Log.i("Request object","saved");
                                }else{
                                    Log.i("Request object","Not saved");
                                }
                            }
                        });

                        Toast.makeText(getApplicationContext(), "Donor Registered: " + name_s, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                      Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_register);

        name=(EditText) findViewById(R.id.nameText);
        email=(EditText) findViewById(R.id.emailText);
        password=(EditText) findViewById(R.id.passwordText1);
        mobile=(EditText) findViewById(R.id.mobileText);
        blood=(Spinner) findViewById(R.id.bloodSpinner);
        gender=(Spinner) findViewById(R.id.genderSpinner);
        age=(EditText) findViewById(R.id.ageText);

        background=(ConstraintLayout) findViewById(R.id.donorConstraint);
        donorHeading=(TextView) findViewById(R.id.textViewHospital);

        background.setOnClickListener(this);
        donorHeading.setOnClickListener(this);

        age.setOnKeyListener(this);

    }


}
