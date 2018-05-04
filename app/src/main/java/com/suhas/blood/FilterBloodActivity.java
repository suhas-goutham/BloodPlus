package com.suhas.blood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

public class FilterBloodActivity extends AppCompatActivity {

    ArrayList<String> list;

    CheckBox c1;
    CheckBox c2;
    CheckBox c3;
    CheckBox c4;
    CheckBox c5;
    CheckBox c6;
    CheckBox c7;
    CheckBox c8;

    public void searchBlood(View view){

        if(c1.isChecked()){
            list.add(c1.getText().toString());
        }
        if(c2.isChecked()){
            list.add(c2.getText().toString());
        }
        if(c3.isChecked()){
            list.add(c3.getText().toString());
        }
        if(c4.isChecked()){
            list.add(c4.getText().toString());
        }
        if(c5.isChecked()){
            list.add(c5.getText().toString());
        }
        if(c6.isChecked()){
            list.add(c6.getText().toString());
        }
        if(c7.isChecked()){
            list.add(c7.getText().toString());
        }
        if(c8.isChecked()){
            list.add(c8.getText().toString());
        }


        Intent intent=new Intent(getApplicationContext(),HospitalMapsActivity.class);

        intent.putStringArrayListExtra("bloodList",list);
        intent.putExtra("intentType","filter");

        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_blood);

        list=new ArrayList<>(8);

        c1=(CheckBox) findViewById(R.id.checkBoxAp);
        c2=(CheckBox) findViewById(R.id.checkBoxAn);
        c3=(CheckBox) findViewById(R.id.checkBoxBp);
        c4=(CheckBox) findViewById(R.id.checkBoxBn);
        c5=(CheckBox) findViewById(R.id.checkBoxOp);
        c6=(CheckBox) findViewById(R.id.checkBoxOn);
        c7=(CheckBox) findViewById(R.id.checkBoxABp);
        c8=(CheckBox) findViewById(R.id.checkBoxABn);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),HospitalMapsActivity.class);
        startActivity(intent);
    }
}
