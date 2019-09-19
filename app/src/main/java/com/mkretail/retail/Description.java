package com.mkretail.retail;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Description extends Activity {

    TextView descname, desc;
    Dialog dialog;

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_description);
        dialog = new Dialog(this);
        descname = findViewById(R.id.desc_name);
        desc = findViewById(R.id.desc);
        imageView = findViewById(R.id.desc_image);

        descname.setText(getIntent().getStringExtra("name"));
        desc.setText(getIntent().getStringExtra("desc"));
        Picasso.with(this).load(getIntent().getStringExtra("image")).into(imageView);

     //   this.setFinishOnTouchOutside(false);

    }
}
