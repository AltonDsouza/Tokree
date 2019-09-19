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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditName extends AppCompatActivity {

    private TextInputLayout fnameLO;
    private TextInputLayout lnameLO;

    private EditText fname;
    private EditText lname;

    private TextView changeName;
    private TextView changePwd;

    private TextView name;
    private TextView mobileno;
    private TextView email;

    private String fnameS;
    private String lnameS;
    private String mobilenoS;
    private String emailS;

    private String UID;
    private String Email;
    private String LogIN;

    private ProgressDialog pd;

    private JSONArray leadJsonArray;

    private String responsemsg;

    private String msg;
    private String chgPwdOtp;

    private String FromCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        fvbid();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        Email = pref.getString("Email", "");
        UID=pref.getString("UID","");
        LogIN=pref.getString("LogIN","");
        FromCart=pref.getString("FromCart","0");


        if(LogIN.equals("1"))
        {
            email.setText(Email);

        }

        getBasicDetails(UID);


        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fname.getText().toString().length()==0){
                    fnameLO.setError("First Name Cannot Be Empty");
                    fnameLO.requestFocus();
                }
                else if(lname.getText().toString().length()==0){
                    lnameLO.setError("Last Name Cannot Be Empty");
                    lnameLO.requestFocus();
                }
                else {

                    changedName(UID);

                }
            }
        });

        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changePwdOtp(Email);
            }
        });

    }

    private void changePwdOtp(final String email) {

        pd.setMessage("Sending OTP......!");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.ChangePwdOtp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        try {
                            JSONObject object = new JSONObject(response);
                            // Toast.makeText(EditName.this, response, Toast.LENGTH_SHORT).show();
                            msg = object.getString("msg");
                            chgPwdOtp=object.getString("OTP");

                            if (msg.equals("Mail Send")) {
                                Toast.makeText(EditName.this, "OTP Send To Your Email ID", Toast.LENGTH_SHORT).show();

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("chgPwdOtp", chgPwdOtp);
                                editor.commit();

                                Intent i =new Intent(EditName.this,ChangePassword.class);
                                startActivity(i);
                            }

                            else if (msg.equals("Mail Not Send")) {

                                Toast.makeText(EditName.this, "Mail Not Send", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                Toast.makeText(EditName.this, "Error", Toast.LENGTH_SHORT).show();
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
                params.put("Email",email);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    private void changedName(final String UID) {

        pd.setMessage("Loading......!");
        pd.show();
        RequestQueue queue= Volley.newRequestQueue(EditName.this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest=new StringRequest(Request.Method.POST, AppConstant.UpdateName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();

                        try {
                            JSONObject object=new JSONObject(response);
                            responsemsg=object.getString("msg");
                            Toast.makeText(EditName.this, response, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (responsemsg.equals("Done")) {


                            if(FromCart.equals("1")){
                                Toast.makeText(EditName.this, "Name Updated", Toast.LENGTH_SHORT).show();
                               /* Intent i=new Intent(EditName.this,ConfirmAdd.class);
                                startActivity(i);
                                finish();*/
                            }
                            else{
                                Toast.makeText(EditName.this, "Name Updated", Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(EditName.this,Profile.class);
                                startActivity(i);
                                finish();
                            }

                        }

                        else{
                            Toast.makeText(EditName.this, "Error", Toast.LENGTH_SHORT).show();
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
                params.put("FirstName",fname.getText().toString());
                params.put("LastName",lname.getText().toString());
                params.put("UID",UID);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void getBasicDetails(final String uid) {

        pd.setMessage("Loading Customer Details......!");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(EditName.this);
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST,AppConstant.GetUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        try {
                            JSONObject object = new JSONObject(response);
                            leadJsonArray = object.getJSONArray("list");
                            for(int n = 0; n < leadJsonArray.length(); n++)
                            {
                                JSONObject object1 = leadJsonArray.getJSONObject(n);

                                fnameS=object1.getString("FirstName");
                                fname.setText(fnameS);

                                lnameS=object1.getString("LastName");
                                lname.setText(lnameS);
                                name.setText(fnameS+" "+lnameS);

                                emailS=object1.getString("");
                                email.setText(emailS);

                                mobilenoS=object1.getString("Contact");
                                mobileno.setText(mobilenoS);

                            }
                        } catch (JSONException e) {
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
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("UID",uid);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


    private void fvbid() {

        fnameLO=(TextInputLayout)findViewById(R.id.fnameLO);
        lnameLO=(TextInputLayout)findViewById(R.id.lnameLO);

        fname=(EditText)findViewById(R.id.fname);
        lname=(EditText)findViewById(R.id.lname);

        changeName=(TextView)findViewById(R.id.changeName);
        changePwd=(TextView)findViewById(R.id.changePwd);

        name=(TextView)findViewById(R.id.name);
        mobileno=(TextView)findViewById(R.id.mobileno);
        email=(TextView)findViewById(R.id.email);
        pd=new ProgressDialog(EditName.this);



    }
}

