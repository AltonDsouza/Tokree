package com.mkretail.retail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterOTP extends AppCompatActivity {


    private EditText otpET;
    private Button submitOTP;

    private String otpPattern="^[1-9][0-9]{5}$";
    private ProgressDialog pd;

    private String OTPs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        otpET=(EditText)findViewById(R.id.otpET);
        submitOTP=(Button)findViewById(R.id.submitOTP);
        pd=new ProgressDialog(EnterOTP.this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        OTPs = pref.getString("OTPs", "");

        submitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!otpET.getText().toString().matches(otpPattern)){
//                    otpET.setError("Enter Valid OTP");
//                    otpET.requestFocus();
//                }
//
//                else{
                    // verifyOTP();

                    if(OTPs.equals(otpET.getText().toString())){
                        Intent i =new Intent(getApplicationContext(),EnterPassword.class);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }

}
