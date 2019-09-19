package com.mkretail.retail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mkretail.retail.Utils.AppConstant;

public class Orders extends AppCompatActivity {

    String UID, area_id;
    String ShowOrHideWebViewInitialUse = "show";
    private ProgressBar spinner;
    WebView myWebView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorders);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        myWebView= (WebView) findViewById(R.id.orderwebview);
        spinner = (ProgressBar)findViewById(R.id.progressBarorder);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_home:
                        startActivity(new Intent(Orders.this, Dashboard.class));
                        break;

                    case R.id.navigation_category:
                        startActivity(new Intent(Orders.this,Cat.class));
                        break;

                    case R.id.navigation_search:
                        startActivity(new Intent(Orders.this,Search.class));
                        break;
                    case R.id.navigation_orders:
                       // startActivity(new Intent(Orders.this,Orders.class));
                        break;
                    case R.id.whatsapp:
                        String url = "https://api.whatsapp.com/send?phone=+919222277702";
                        //919222277702
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                }
                return true;
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UID","");

        if(UID.equals(""))
        {
            startActivity(new Intent(getApplicationContext(),Login.class));
        }


        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyLoc",0);
        area_id = preferences.getString("AreaId", "");
        //myWebView.loadUrl("http://fortunehealthplus/Tokree/Cart");
        String urURL= AppConstant.WebURL+ "MobCart/OrderList" + "?data="+UID+"&UserAuth=Ultr0n"+"&PassWD=Alton"
                +"&AreaID="+area_id;
        Log.d("URL",urURL);
        myWebView.loadUrl(urURL);



        myWebView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

       // getData();
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
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.equals(AppConstant.WebURL+"tokree/empty")){
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                return true;
            }
            else if(url.equals(AppConstant.WebURL+"MobileCart/Alton")){
                AlertDialog.Builder builder = new AlertDialog.Builder(Orders.this);
                builder.setTitle("SUCCESS");
                builder.setIcon(R.drawable.correct);
                builder.setMessage("Your order has been placed successfully!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        finish();
                    }
                });
                builder.create();
                builder.show();

                return true;
            }
            return false;
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
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_orders);
    }
    @Override
    public void onBackPressed() {
        if(myWebView.canGoBack())
        {
            myWebView.goBack();
        }
        else {
            startActivity(new Intent(getApplicationContext(),Dashboard.class));
        }
    }
}



