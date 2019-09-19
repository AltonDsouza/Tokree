package com.mkretail.retail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mkretail.retail.Utils.AppConstant;

public class Cart extends AppCompatActivity {
    String UID, area_id, TO_CART;
    String ShowOrHideWebViewInitialUse = "show";
    //private ProgressBar spinner;
    WebView myWebView;
     String url1 = AppConstant.WebURL;
    ProgressDialog progressDialog;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
             myWebView= (WebView) findViewById(R.id.webview);
              progressDialog = new ProgressDialog(this);
             progressDialog.setMessage("Loading...");
             progressDialog.show();
            // spinner = (ProgressBar)findViewById(R.id.progressBar1);
             bottomNavigationView = findViewById(R.id.bottom_nav_view);

             bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                 @Override
                 public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                     switch (menuItem.getItemId())
                     {
                         case R.id.navigation_home:
                             startActivity(new Intent(getApplicationContext(),Dashboard.class));
                             break;
                         case R.id.navigation_category:
                             startActivity(new Intent(getApplicationContext(),Cat.class));
                             break;
                         case R.id.navigation_orders:
                             startActivity(new Intent(getApplicationContext(), Orders.class));
                             break;

                         case R.id.navigation_search:
                             startActivity(new Intent(getApplicationContext(), Search.class));
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
        TO_CART = pref.getString("ToCart","");


        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyLoc",0);
        area_id = preferences.getString("AreaId", "");
        //myWebView.loadUrl("http://fortunehealthplus/Tokree/Cart");
        String urURL= AppConstant.WebURL+"MobCart" + "?data="+UID+"&UserAuth=Ultr0n"+"&PassWD=Alton"
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
            //spinner.setVisibility(View.GONE);
            progressDialog.dismiss();

            view.setVisibility(myWebView.VISIBLE);
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

             if(url.equals(AppConstant.WebURL+"MobileCart/Alton")){
                AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
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
             else if(url.equals("game:gamer")){
                 AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
                 builder.setTitle("SUCCESS");
                 builder.setIcon(R.drawable.correct);
                 builder.setMessage("Your order has been placed successfully!");
                 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
//                         startActivity(new Intent(getApplicationContext(), Dashboard.class));
                         finish();
                     }
                 });
                 builder.create();
                 builder.show();

                 return true;
             }

             else if(url.equals(AppConstant.WebURL+"tokree/empty")){
                 startActivity(new Intent(getApplicationContext(), Dashboard.class));
                 finish();
             }

             else if(url.equalsIgnoreCase(url1+"Home/Login")){
                 startActivity(new Intent(getApplicationContext(), Login.class));
                 finish();
             }

             else if(url.equals(url1+"Cart") || url.equals(url1+"Cart/index")){
                 startActivity(new Intent(getApplicationContext(), Dashboard.class));
                 finish();
             }

            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
            builder.setTitle("SUCCESS");
            builder.setIcon(R.drawable.correct);
            builder.setMessage("Check your internet connection or "+error.toString());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(TO_CART.equals("Dashboard")){
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        finish();
                    }

                    else if(TO_CART.equals("SubCat")){
                        Intent i = new Intent(getApplicationContext(), SubCat.class);
                        i.putExtra("cid",getIntent().getStringExtra("cid"));
                        finish();
                    }
                    else if(TO_CART.equals("Cat")){
                        startActivity(new Intent(getApplicationContext(),Cat.class));
                        finish();
                    }

                    else if(TO_CART.equals("WishList")){
                        startActivity(new Intent(getApplicationContext(), Wishlist.class));
                        finish();
                    }

                    else if(TO_CART.equals("Search")){
                        startActivity(new Intent(getApplicationContext(),Search.class));
                        finish();
                    }
//                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
//                    finish();
                }
            });
            builder.create();
            builder.show();

        }
    }


    @Override
    public void onBackPressed() {
if(myWebView.canGoBack())
{
    myWebView.goBack();
}

       else if(TO_CART.equals("Dashboard")){
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }

        else if(TO_CART.equals("SubCat")){
            Intent i = new Intent(getApplicationContext(), SubCat.class);
            i.putExtra("cid",getIntent().getStringExtra("cid"));
            finish();
        }
        else if(TO_CART.equals("Cat")){
            startActivity(new Intent(getApplicationContext(),Cat.class));
            finish();
        }

        else if(TO_CART.equals("WishList")){
            startActivity(new Intent(getApplicationContext(), Wishlist.class));
            finish();
        }

        else if(TO_CART.equals("Search")){
            startActivity(new Intent(getApplicationContext(),Search.class));
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               // onBackPressed();
                if(TO_CART.equals("Dashboard")){
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                finish();
            }

            else if(TO_CART.equals("SubCat")){
                Intent i = new Intent(getApplicationContext(), SubCat.class);
                i.putExtra("cid",getIntent().getStringExtra("cid"));
                finish();
            }
            else if(TO_CART.equals("Cat")){
                startActivity(new Intent(getApplicationContext(),Cat.class));
                finish();
            }

            else if(TO_CART.equals("WishList")){
                startActivity(new Intent(getApplicationContext(), Wishlist.class));
                finish();
                }

            else if(TO_CART.equals("Search")){
                startActivity(new Intent(getApplicationContext(),Search.class));
                    finish();
            }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
