package com.suhas.blood;

import android.content.Intent;
import android.media.MediaCodec;
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

public class DonorRegisterActivity extends AppCompatActivity implements /*View.OnKeyListener*/View.OnClickListener {

    EditText name,email,password1,password2,mobile,age;
    CheckBox checkBox;
    TextView signUp;
    String name_s,email_s,password_s1,password_s2,mobile_s,blood_s,age_s,gender_s;
    ConstraintLayout background;
    TextView donorHeading;
    Spinner blood,gender;

    /*
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){

            registerDonor(v);
        }

        return false;
    }
*/
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.relativeLayout2 || v.getId()==R.id.cardView2){

            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }


    public void registerDonor(View view){

        name_s=name.getText().toString().trim();
        email_s=email.getText().toString().trim();
        password_s1=password1.getText().toString().trim();
        password_s2=password2.getText().toString().trim();
        mobile_s=mobile.getText().toString().trim();
        blood_s=blood.getSelectedItem().toString().trim();
        age_s=age.getText().toString().trim();
        gender_s=gender.getSelectedItem().toString().trim();

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

        if(age_s==null || age_s==""){
            status=0;
            Toast.makeText(getApplicationContext(),"Enter correct age",Toast.LENGTH_SHORT).show();
        }else {

            try {
                if (Integer.parseInt(age_s) < 18) {
                    status = 0;
                    Toast.makeText(getApplicationContext(), "You must be atleast 18 years of age to donate", Toast.LENGTH_SHORT).show();
                }

                if (Integer.parseInt(age_s) > 60) {
                    status = 0;
                    Toast.makeText(getApplicationContext(), "You must be less than 60 years of age to donate", Toast.LENGTH_SHORT).show();
                }
            }catch (NumberFormatException e){
                
            }
        }

        if(checkBox.isChecked()==false){
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
                        ob.put("finishedDonation",false);

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

        ParseUser.getCurrentUser().logOut();

        name=(EditText) findViewById(R.id.nameText);
        email=(EditText) findViewById(R.id.emailText);
        password1=(EditText) findViewById(R.id.passwordText1);
        password2=(EditText) findViewById(R.id.passwordText2);
        mobile=(EditText) findViewById(R.id.mobileText);
        blood=(Spinner) findViewById(R.id.bloodSpinner);
        gender=(Spinner) findViewById(R.id.genderSpinner);
        age=(EditText) findViewById(R.id.ageText);
        signUp=(TextView) findViewById(R.id.textViewSignin);
        checkBox=(CheckBox) findViewById(R.id.checkbox_donor);

        CardView cardView=(CardView) findViewById(R.id.cardView2);
        RelativeLayout relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout2);

        cardView.setOnClickListener((View.OnClickListener) this);
        relativeLayout.setOnClickListener((View.OnClickListener) this);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        /*
        background=(RelativeLayout) findViewById(R.id.rel)
                //(ConstraintLayout) findViewById(R.id.donorConstraint);
        donorHeading=(TextView) findViewById(R.id.textViewHospital);
        */
        /*
        background.setOnClickListener(this);
        donorHeading.setOnClickListener(this);
        */
        /*
        age.setOnKeyListener(this);
        */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }
}
