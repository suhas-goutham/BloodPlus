package com.suhas.blood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RegisterActivity extends AppCompatActivity {

    Intent intent;

    public void donorRegister(View view){
        intent=new Intent(getApplicationContext(),DonorRegisterActivity.class);
        startActivity(intent);
    }

    public void hospitalRegister(View view){
        intent=new Intent(getApplicationContext(),HospitalRegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
