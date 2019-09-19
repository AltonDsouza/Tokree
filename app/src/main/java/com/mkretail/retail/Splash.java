package com.mkretail.retail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;


public class Splash extends AppCompatActivity {


    private static int SPLASH_TIME_OUT =3000;


    private boolean FirstLogin;

    private String LogIN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        // Toast.makeText(this,androidDeviceId, Toast.LENGTH_SHORT).show();

        final SharedPreferences pref=getApplicationContext().getSharedPreferences("MyPref",0);
        FirstLogin=pref.getBoolean("once",false);
        LogIN=pref.getString("LogIN","");



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

                if (cd.isConnectingToInternet()) {

                    if(!LogIN.equals("1")){
                        Intent i=new Intent(Splash.this,Login.class);
                        startActivity(i);
                        finish();

                      /*  SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("once",true);
                        editor.commit();*/
                    }

                    else{
                        Intent i=new Intent(Splash.this,Dashboard.class);
                        startActivity(i);
                        finish();
                    }

                } else {
                    cd.showAlertDialog(Splash.this, "No Internet Connection", "Please Connect To Internet to Procceed");
                }

            }
        }, SPLASH_TIME_OUT);
    }

}



