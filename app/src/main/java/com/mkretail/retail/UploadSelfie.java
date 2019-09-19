package com.mkretail.retail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadSelfie extends AppCompatActivity {

    private ImageView adds_pic;
    private ImageButton cameraC;
    private ImageButton galleryC;
    private EditText adds_detail;
    private Button AddInsert;

    private String msg;
    final int CODE_GALLERY_REQUEST=999;
//    final int CODE_CAMERA_REQUEST=777;
    public  static final int RequestPermissionCode  = 1 ;

    private Bitmap bitmapC;

    private ProgressDialog pd;

    private String uploadSelfiURL ="http://brootsindia.com/demo1/Event/AddSelfie";
    private String sendNotiURL="https://fortunehealthplus.com/PathPedhi/notiApi.php";

    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_selfie);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        EnableRuntimePermission();

        fvbid();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID=pref.getString("UID","");
        // Toast.makeText(this,UID, Toast.LENGTH_SHORT).show();


        AddInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmapC==null){
                    Toast.makeText(UploadSelfie.this, "Select  Image", Toast.LENGTH_SHORT).show();
                }
               /* else if(adds_detail.getText().toString().length()==0){
                    adds_detail.setError("Enter  Details");
                    adds_detail.requestFocus();
                }
*/
                else{
                    uploadSelfieReq();
                }
            }
        });

        cameraC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 7);
            }
        });

        galleryC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(UploadSelfie.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},CODE_GALLERY_REQUEST);
            }
        });

    }

    private void uploadSelfieReq() {


        pd.setMessage("Uploading Selfie......!");
        pd.show();
        RequestQueue queue= Volley.newRequestQueue(UploadSelfie.this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest=new StringRequest(Request.Method.POST, uploadSelfiURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        // Toast.makeText(UploadSelfie.this,response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject object=new JSONObject(response);
                            msg =object.getString("msg");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (msg.equals("SUCCESS")) {
                            Toast.makeText(UploadSelfie.this, "Selfie Upload", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(UploadSelfie.this,UploadSelfie.class);
                            startActivity(i);
                            finish();

                        }

                        else if(msg.equals("USER EXIST")){

                            Toast.makeText(UploadSelfie.this, "Image Already Uploaded", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            Toast.makeText(UploadSelfie.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(UploadSelfie.this,error.toString(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                String dp = imageTOstring(bitmapC);
                params.put("selfieImage", dp);
                params.put("UID",UID);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }


    private void fvbid() {

        adds_pic=(ImageView)findViewById(R.id.adds_pic);
        cameraC=(ImageButton)findViewById(R.id.cameraC);
        galleryC=(ImageButton)findViewById(R.id.galleryC);
        //adds_detail=(EditText)findViewById(R.id.adds_detail);
        AddInsert=(Button)findViewById(R.id.AddInsert);
        pd=new ProgressDialog(UploadSelfie.this);
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(UploadSelfie.this,
                android.Manifest.permission.CAMERA))
        {

            //Toast.makeText(InsertAd.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(UploadSelfie.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    // Toast.makeText(InsertAd.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(UploadSelfie.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;

            case CODE_GALLERY_REQUEST:

                if(PResult.length>0 && PResult[0]== PackageManager.PERMISSION_GRANTED){
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent,"Select Image"),CODE_GALLERY_REQUEST);
                }
                else{
                    Toast.makeText(this, "You dont have permision", Toast.LENGTH_SHORT).show();

                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 7 && resultCode == RESULT_OK) {

            bitmapC = (Bitmap) data.getExtras().get("data");

            adds_pic.setImageBitmap(bitmapC);
        }

        if(requestCode==CODE_GALLERY_REQUEST && resultCode==RESULT_OK && data !=null){

            Uri filepath=data.getData();
            try {
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                bitmapC= BitmapFactory.decodeStream(inputStream);
                adds_pic.setImageBitmap(bitmapC);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        // super.onActivityResult(requestCode, resultCode, data);
    }

    private String imageTOstring(Bitmap bitmap){

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imagebytes= outputStream.toByteArray();

        String encodedImage= android.util.Base64.encodeToString(imagebytes, android.util.Base64.DEFAULT);
        return encodedImage;
    }

}

