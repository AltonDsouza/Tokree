package com.mkretail.retail;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mkretail.retail.Utils.AppConstant;

public class AboutUS extends AppCompatActivity {

    WebView webView;
    ProgressDialog progressDialog;
    String ShowOrHideWebViewInitialUse = "show";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);
        webView = findViewById(R.id.about);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String urURL= AppConstant.WebURL+"Home/about";
        webView.loadUrl(urURL);
        webView.setWebViewClient(new CustomWebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private class CustomWebViewClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (ShowOrHideWebViewInitialUse.equals("show")) {
                webView.setVisibility(webView.INVISIBLE);
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            ShowOrHideWebViewInitialUse = "hide";
            //spinner.setVisibility(View.GONE);
            progressDialog.dismiss();

            view.setVisibility(webView.VISIBLE);
            super.onPageFinished(view, url);

        }

    }

}
