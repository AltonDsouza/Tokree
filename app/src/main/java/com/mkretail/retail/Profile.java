package com.mkretail.retail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mkretail.retail.Utils.AppConstant;

public class Profile extends AppCompatActivity {

    String UID, area_id;
    String ShowOrHideWebViewInitialUse = "show";
    private ProgressBar spinner;
    WebView myWebView;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        myWebView= (WebView) findViewById(R.id.prowebView);
        spinner = (ProgressBar)findViewById(R.id.progressBarpro);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_home:
                        startActivity(new Intent(Profile.this, Dashboard.class));
                        break;

                    case R.id.navigation_category:
                        startActivity(new Intent(Profile.this,Cat.class));
                        break;

                    case R.id.navigation_search:
                        startActivity(new Intent(Profile.this,Search.class));
                        break;
                    case R.id.navigation_orders:
                         startActivity(new Intent(Profile.this,Orders.class));
                        break;

                }
                return true;
            }
        });
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UID","");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyLoc",0);
        area_id = preferences.getString("AreaId", "");

        if(UID.equals(""))
        {
            startActivity(new Intent(getApplicationContext(),Login.class));
        }

        //myWebView.loadUrl("http://fortunehealthplus/Tokree/Cart");
        String urURL= AppConstant.WebURL+"MobCart/Profile" + "?data="+UID+"&UserAuth=Ultr0n"+"&PassWD=Alton"
                +"&AreaID="+area_id;
        Log.d("URL",urURL);
        myWebView.loadUrl(urURL);



        myWebView.setWebViewClient(new CustomWebViewClient());


        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);



    }
    private class CustomWebViewClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (ShowOrHideWebViewInitialUse.equals("show")) {
                myWebView.setVisibility(myWebView.INVISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            ShowOrHideWebViewInitialUse = "hide";
            spinner.setVisibility(View.GONE);

            view.setVisibility(myWebView.VISIBLE);
            super.onPageFinished(view, url);

        }
    }


    @Override
    public void onBackPressed() {
        if(myWebView.canGoBack())
        {
            myWebView.goBack();
        }
        else {
            startActivity(new Intent(getApplicationContext(),Dashboard.class));
        }    }
}

