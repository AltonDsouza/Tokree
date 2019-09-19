package com.mkretail.retail;

import android.app.ProgressDialog;
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

public class SignUp extends AppCompatActivity {

    private TextInputLayout phoneRLO;
/*    private TextInputLayout passRLO;
    private TextInputLayout CpassLO;*/


    private EditText phoneR;
  /*  private EditText passwordR;
    private EditText Cpassword;*/

    private Button signIn;

    private String phonePattern="((\\+*)((0[ -]+)*|(91 )*)(\\d{12}+|\\d{10}+))|\\d{5}([- ]*)\\d{6}";

    private ProgressDialog pd;
    private String msg;
    private String OTPs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fvbID();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneR.getText().toString().length()==0){
                    phoneRLO.setError("Cannot be empty!");
                    phoneRLO.requestFocus();
                }
                else if(!phoneR.getText().toString().matches(phonePattern)){
                    phoneRLO.setError("Enter a Valid Phone Number.");
                    phoneRLO.requestFocus();
                }

                else{
                    sendRegOTP();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("OTPs", "1234");
                    editor.putString("PhoneNo",phoneR.getText().toString());
                    editor.commit();
                    Intent i =new Intent(SignUp.this,EnterOTP.class);
                    startActivity(i);
                }

            }
        });
    }

    private void sendRegOTP() {
        pd.setMessage("Sending OTP......!");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String response = null;
        Log.e("ins","inside");

       // final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.TokreeReg,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                           // Toast.makeText(SignUp.this, response, Toast.LENGTH_SHORT).show();
                            msg = object.getString("msg");
                            Log.e("inside",msg);

                            if(msg.equals("SUCCESS")) {
                                Toast.makeText(getApplicationContext(), "OTP Sent to phone", Toast.LENGTH_SHORT).show();

                                OTPs=object.getString("otp");
                                Log.e("OTP",OTPs);

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("OTPs", OTPs);
                                editor.putString("PhoneNo",phoneR.getText().toString());
                                editor.commit();



                                Intent i =new Intent(SignUp.this,EnterOTP.class);
                                startActivity(i);
                            }

                            else if(msg.equals("Exists")) {
                                Toast.makeText(getApplicationContext(), "Phone No Already Exists", Toast.LENGTH_SHORT).show();
                            }


                            else{
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();


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
                params.put("Contact", phoneR.getText().toString());
                params.put("Type","0");
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    private void fvbID() {

        phoneRLO=(TextInputLayout)findViewById(R.id.phoneRLO);
      /*  passRLO=(TextInputLayout)findViewById(R.id.passRLO);
        CpassLO=(TextInputLayout)findViewById(R.id.CpassLO);*/
        phoneR=(EditText)findViewById(R.id.phoneR);

   /*     passwordR=(EditText)findViewById(R.id.passwordR);
        Cpassword=(EditText)findViewById(R.id.Cpassword);
*/
        signIn=(Button)findViewById(R.id.getOTP);

        pd=new ProgressDialog(SignUp.this);

    }
}

