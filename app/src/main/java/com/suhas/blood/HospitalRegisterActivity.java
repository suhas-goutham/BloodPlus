package com.suhas.blood;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.regex.Pattern;

public class HospitalRegisterActivity extends AppCompatActivity implements /*View.OnKeyListener,*/ View.OnClickListener {

    EditText nameH,emailH,passwordH1,passwordH2,mobileH,cityH;
    CheckBox checkBox1;
    String name_s,email_s,password_s1,password_s2,mobile_s,city_s;
    ConstraintLayout hospitalConstraint;
    TextView hospitalTextView;
    TextView signIn;

    /*
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){

            registerHospital(v);
        }

        return false;
    }
*/
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.cardView3 || v.getId()==R.id.relativeLayout3){

            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }


    public void registerHospital(View view){

        name_s=nameH.getText().toString().trim();
        email_s=emailH.getText().toString().trim();
        password_s1=passwordH1.getText().toString().trim();
        password_s2=passwordH2.getText().toString().trim();
        mobile_s=mobileH.getText().toString().trim();
        city_s=cityH.getText().toString().trim()        ;

        int status=1;

        if(!((Pattern.matches("[a-zA-Z]{1,10}[\\s]{1,1}[a-zA-Z]{1,10}",name_s))||(Pattern.matches("[a-zA-Z]{1,10}",name_s)))){
            Toast.makeText(getApplicationContext(),"Enter correct name",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(!Pattern.matches("[a-zA-Z0-9]{1,20}[@][a-zA-Z]{1,10}(.com|.org|.edu)",email_s)){
            Toast.makeText(getApplicationContext(),"Enter valid email ID",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(!(password_s1.length()>=6)){
            Toast.makeText(getApplicationContext(),"Enter password of atleast 6 characters",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(!(password_s2.equals(password_s1))){
            Toast.makeText(getApplicationContext(),"Enter same password",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(!Pattern.matches("(9|8|7)[0-9]{9,9}",mobile_s)){
            status=0;
            Toast.makeText(getApplicationContext(),"Enter valid mobile number",Toast.LENGTH_SHORT).show();
        }

        if(!((Pattern.matches("[a-zA-Z]{1,15}",city_s)))){
            Toast.makeText(getApplicationContext(),"Enter correct city",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(checkBox1.isChecked()==false){
            Toast.makeText(getApplicationContext(),"Please tick the terms and conditions",Toast.LENGTH_SHORT).show();
            status=0;
        }

        if(status==1) {
            final ParseUser user = new ParseUser();

            user.setUsername(email_s);
            user.setPassword(password_s1);
            user.setEmail(email_s);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        Log.i("SignUp", "Success");

                        user.put("city", city_s);
                        user.put("mobile", mobile_s);
                        user.put("name", name_s);
                        user.put("userType", "hospital");

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

                        Toast.makeText(getApplicationContext(), "Hospital Registered: " + name_s, Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_hospital_register);

        ParseUser.getCurrentUser().logOut();

        nameH=(EditText) findViewById(R.id.nameHText);
        emailH=(EditText) findViewById(R.id.emailHText);
        passwordH1=(EditText) findViewById(R.id.passwordHText1);
        passwordH2=(EditText) findViewById(R.id.passwordHText2);
        mobileH=(EditText) findViewById(R.id.mobileHText);
        cityH=(EditText) findViewById(R.id.cityHText);
        signIn=(TextView) findViewById(R.id.textViewSignin1);
        checkBox1=(CheckBox) findViewById(R.id.checkbox_hosp);

        CardView cardView=(CardView) findViewById(R.id.cardView3);
        RelativeLayout relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout3);

        cardView.setOnClickListener((View.OnClickListener) this);
        relativeLayout.setOnClickListener((View.OnClickListener) this);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        /*

        hospitalConstraint=(ConstraintLayout) findViewById(R.id.registerConstraint);
        hospitalTextView=(TextView) findViewById(R.id.textViewHospital);

        cityH.setOnKeyListener(this);

        hospitalConstraint.setOnClickListener(this);
        hospitalTextView.setOnClickListener(this);

        */
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }
}
