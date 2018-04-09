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

public class DonorRegisterActivity extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {

    EditText name,email,password,mobile,blood,age;
    String name_s,email_s,password_s,mobile_s,blood_s,age_s;
    ConstraintLayout background;
    TextView donorHeading;

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
        blood_s=blood.getText().toString();
        age_s=age.getText().toString();

        final ParseUser user=new ParseUser();

        user.setUsername(email_s);
        user.setPassword(password_s);
        user.setEmail(email_s);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if(e==null){
                    Log.i("SignUp","Success");

                    user.put("blood_grp",blood_s);
                    user.put("mobile",mobile_s);
                    user.put("age",age_s);
                    user.put("name",name_s);
                    user.put("userType","donor");

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

        Toast.makeText(getApplicationContext(),"Donor Registered: "+name_s,Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_register);

        name=(EditText) findViewById(R.id.nameText);
        email=(EditText) findViewById(R.id.emailText);
        password=(EditText) findViewById(R.id.passwordText1);
        mobile=(EditText) findViewById(R.id.mobileText);
        blood=(EditText) findViewById(R.id.bloodGroupText);
        age=(EditText) findViewById(R.id.ageText);

        background=(ConstraintLayout) findViewById(R.id.donorConstraint);
        donorHeading=(TextView) findViewById(R.id.textViewHospital);

        background.setOnClickListener(this);
        donorHeading.setOnClickListener(this);

        age.setOnKeyListener(this);

    }


}
