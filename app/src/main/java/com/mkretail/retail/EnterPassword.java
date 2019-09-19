package com.mkretail.retail;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkretail.retail.Utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnterPassword extends AppCompatActivity {


    private TextInputLayout passRLO;
    private TextInputLayout CpassLO;

    private EditText passwordR;
    private EditText Cpassword;

    private EditText refercodetext;
    private Button check;

    private Button reg;

    private ProgressDialog pd;

    private String msg;
    private String UID;
    private String PhoneNo;

    private String url = AppConstant.TokreeRefer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        fvbID();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        PhoneNo = pref.getString("PhoneNo", "");

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkReferralCode();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordR.getText().toString().length()==0){
                    passRLO.setError("Enter Password");
                    passRLO.requestFocus();
                }
                else if(Cpassword.getText().toString().length()==0){
                    CpassLO.setError("Enter Confirm Password");
                    CpassLO.requestFocus();
                }
                else if(!passwordR.getText().toString().equals(Cpassword.getText().toString())){
                    CpassLO.setError("Password Not Match");
                    CpassLO.requestFocus();
                }
                else{
                    registerUser();
                }
            }
        });

    }


    private void checkReferralCode()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking...");
        progressDialog.show();
     //   pd.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS"))
                            {
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EnterPassword.this);
                                builder.setTitle("Congrats!");
                                builder.setIcon(R.drawable.correct);
                                builder.setMessage("Your referral code is correct!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.create();
                                builder.show();
                            }
                            else if(msg.equals("Error"))
                            {
                                Toast.makeText(getApplicationContext(), "Referral code incorrect!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), " "+error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Reffer",refercodetext.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    private void registerUser() {

        pd.setMessage("Registration......!");
        pd.show();
        pd.setCancelable(false);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST,AppConstant.TokreeUserRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        try {
                            JSONObject object = new JSONObject(response);
                           // Toast.makeText(EnterPassword.this, response, Toast.LENGTH_SHORT).show();
                            msg = object.getString("msg");


                            if (msg.equals("SUCCESS")) {
                                Toast.makeText(getApplicationContext(), "Customer Successfully Register", Toast.LENGTH_SHORT).show();

                                UID=object.getString("UID");


                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("UID", UID);
                                editor.putString("LogIN", "1");
                                editor.commit();

                                Intent i =new Intent(EnterPassword.this,LocationSelect.class);
                               // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                //finish();
                            }
                            else if(msg.equals("User Already Exist"))
                            {
                                Toast.makeText(getApplicationContext(), "User already exists!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }


                            else {
                                Toast.makeText(getApplicationContext(), "Could not contact Server", Toast.LENGTH_SHORT).show();
                                Log.e("ERR", response);
                                pd.dismiss();
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EnterPassword.this, ""+error, Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Contact", PhoneNo);
                params.put("Password",passwordR.getText().toString());
                params.put("ReferCode",refercodetext.getText().toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


    private void fvbID() {

        passRLO=(TextInputLayout)findViewById(R.id.passRLO);
        CpassLO=(TextInputLayout)findViewById(R.id.CpassLO);

        passwordR=(EditText)findViewById(R.id.passwordR);
        Cpassword=(EditText)findViewById(R.id.Cpassword);
        reg=(Button)findViewById(R.id.reg);
        pd=new ProgressDialog(this);

        check=(Button)findViewById(R.id.check);
        refercodetext=(EditText) findViewById(R.id.Crefer);
    }
}

