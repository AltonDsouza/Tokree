package com.mkretail.retail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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

public class ChangePassword extends AppCompatActivity {

    private TextView emailID;
    private TextInputLayout oldPwdLO;
    private EditText oldPwd;
    private TextInputLayout newPwdLO;
    private EditText newPwd;
    private TextInputLayout confPwdLO;
    private EditText confPwd;
    private TextInputLayout otpLO;
    private EditText otp;
    private TextView SavePass;

    private String otpPattern="^[1-9][0-9]{5}$";
    private String chgPwdOtp;

    private ProgressDialog pd;

    private String UID;
    private String responsemsg;
    private String Email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        fvbid();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        chgPwdOtp = pref.getString("chgPwdOtp", "");
        UID=pref.getString("UID","");
        Email = pref.getString("Email", "");

        emailID.setText(Email);

        SavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oldPwd.getText().toString().length()==0){
                    oldPwdLO.setError("Old Password Cannot be Empty");
                    oldPwdLO.requestFocus();
                }
                else if(newPwd.getText().toString().length()==0){
                    newPwdLO.setError("New Password Cannot be Empty");
                    newPwdLO.requestFocus();
                }
                else if(confPwd.getText().toString().length()==0){
                    confPwdLO.setError("Confirm Password Cannot be Empty");
                    confPwdLO.requestFocus();
                }
                else if(!confPwd.getText().toString().matches(newPwd.getText().toString())){
                    confPwdLO.setError("Password Not Match");
                    confPwdLO.requestFocus();
                }

                else if(!otp.getText().toString().matches(otpPattern)){
                    otpLO.setError("Enter A Valid OTP");
                    otpLO.requestFocus();
                }

                else if(!otp.getText().toString().equals(chgPwdOtp)){
                    otpLO.setError("Invalid OTP");
                    otpLO.requestFocus();
                    Toast.makeText(ChangePassword.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }

                else{
                    changePwdReq();
                }
            }
        });

    }

    private void changePwdReq() {

        pd.setMessage("Loading......!");
        pd.show();
        RequestQueue queue= Volley.newRequestQueue(ChangePassword.this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest=new StringRequest(Request.Method.POST, AppConstant.ChangePassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();

                        try {
                            JSONObject object=new JSONObject(response);
                            responsemsg=object.getString("msg");
                            Toast.makeText(ChangePassword.this, response, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (responsemsg.equals("Pwd Updated")) {
                            Toast.makeText(ChangePassword.this, "Password Changed", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(ChangePassword.this,Profile.class);
                            startActivity(i);
                            finish();
                        }

                        else if(responsemsg.equals("Incorrect Pwd")){
                            Toast.makeText(ChangePassword.this, "Old Password Not Matched",Toast.LENGTH_SHORT).show();
                        }

                        else{
                            Toast.makeText(ChangePassword.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.hide();
                        error.printStackTrace();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("oldPwd",oldPwd.getText().toString());
                params.put("newPwd",newPwd.getText().toString());
                params.put("UID",UID);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void fvbid() {

        emailID=(TextView)findViewById(R.id.emailID);
        oldPwdLO=(TextInputLayout)findViewById(R.id.oldPwdLO);
        oldPwd=(EditText)findViewById(R.id.oldPwd);
        newPwdLO=(TextInputLayout)findViewById(R.id.newPwdLO);
        newPwd=(EditText)findViewById(R.id.newPwd);
        confPwdLO=(TextInputLayout)findViewById(R.id.confPwdLO);
        confPwd=(EditText)findViewById(R.id.confPwd);
        otpLO=(TextInputLayout) findViewById(R.id.otpLO);
        otp=(EditText)findViewById(R.id.otp);
        SavePass=(TextView)findViewById(R.id.SavePass);
        pd=new ProgressDialog(this);

    }
}
