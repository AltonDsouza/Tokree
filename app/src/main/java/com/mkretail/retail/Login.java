package com.mkretail.retail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class Login extends AppCompatActivity {

    private EditText phone;
    private EditText password;
    private Button login;
    private Button signUp;

    private TextInputLayout phoneLO;
    private TextInputLayout passLO;

    private String phonePattern="((\\+*)((0[ -]+)*|(91 )*)(\\d{12}+|\\d{10}+))|\\d{5}([- ]*)\\d{6}";

    //private TextView skip;
    ProgressDialog pd;

    private String msg;
    private String UID;
  //  private String ContactNo;
    private TextView forgetPwd;

    private String forPwdOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        findVBID();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //Toast.makeText(this,androidDeviceId, Toast.LENGTH_SHORT).show();

        forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phone.getText().toString().length()==0){
                    phoneLO.setError("Enter Phone No");
                    phoneLO.requestFocus();
                }
                else if(!phone.getText().toString().matches(phonePattern)){
                    phoneLO.setError("Enter a Valid No");
                    phoneLO.requestFocus();
                }
                else{
                    forgetPwdOtp();
                }

            }
        });

  /*      skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,Dashboard.class);
                startActivity(i);
            }
        });
*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(phone.getText().toString().length()==0){
                    phoneLO.setError("Cannot be empty!");
                    phoneLO.requestFocus();
                }
                else if(!phone.getText().toString().matches(phonePattern)){
                    phoneLO.setError("Enter a Valid Phone no");
                    phoneLO.requestFocus();
                }

                else if(password.getText().toString().length()==0){
                    passLO.setError("Please Enter Password");
                    passLO.requestFocus();
                }
                else{
                    LoginRequest();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(Login.this,SignUp.class);
                startActivity(i);
            }
        });
    }

    private void forgetPwdOtp() {

        pd.setMessage("Sending OTP......!");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.TokreeReg,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        try {
                            JSONObject object = new JSONObject(response);
                            //Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                            msg = object.getString("msg");

                            if (msg.equals("Exists")) {
                                Toast.makeText(getApplicationContext(), "OTP Send To Your Mobile No", Toast.LENGTH_SHORT).show();

                                forPwdOtp=object.getString("otp");


                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("forPwdOtp", forPwdOtp);
                                editor.putString("forgetNo", phone.getText().toString());
                                editor.commit();

                                Intent i =new Intent(Login.this,ForgetPassword.class);
                                startActivity(i);
                            }

                            else if (msg.equals("SUCCESS")) {

                                Toast.makeText(getApplicationContext(), "Not Registered Mobile no", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Contact", phone.getText().toString());
                params.put("Type","1");
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void LoginRequest() {
               pd.setMessage("Signing in......!");
               pd.show();

        RequestQueue queue = Volley.newRequestQueue(Login.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST,AppConstant.TokreeLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.e("inside","insdie");
                        try {
                            JSONObject object = new JSONObject(response);
                            // Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                            msg = object.getString("msg");

                            if (msg.equals("Granted")) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                                UID = object.getString("UID");
                                // ContactNo = object.getString("Contact");


                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("LogIN", "1");
                                editor.putString("UID", UID);
                                editor.putString("PhoneNo", phone.getText().toString());

                                editor.commit();

                                // Toast.makeText(Login.this, pref.getString("LogIN", ""), Toast.LENGTH_SHORT).show();
                                // Toast.makeText(Login.this, pref.getString("UID", ""), Toast.LENGTH_SHORT).show();
                                // Toast.makeText(Login.this, pref.getString("PhoneNo", ""), Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(Login.this, LocationSelect.class);
                                startActivity(i);
                                // finish();

                            }

                            else if (msg.equals("block")) {

                                Toast.makeText(getApplicationContext(), "User Block", Toast.LENGTH_SHORT).show();
                            }

                            else if (msg.equals("nouser")) {

                                Toast.makeText(getApplicationContext(), "Phone no not found", Toast.LENGTH_SHORT).show();
                            }

                            else if (msg.equals("error")) {

                                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Contact", phone.getText().toString());
                params.put("Password", password.getText().toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);



    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void findVBID() {
        phone =(EditText)findViewById(R.id.phone);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login1);
        signUp=(Button)findViewById(R.id.signUp);

        phoneLO =(TextInputLayout)findViewById(R.id.phoneLO);
        passLO=(TextInputLayout)findViewById(R.id.passLO);

       // skip=(TextView)findViewById(R.id.skip);

        pd=new ProgressDialog(Login.this);

        forgetPwd=(TextView)findViewById(R.id.forgetPwd);

    }


}


