package com.mkretail.retail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkretail.retail.Utils.AppConstant;
import com.mkretail.retail.Utils.VolleyCallBack;

import java.util.HashMap;
import java.util.Map;

public class Mediator  {

    private String cart_count_url = AppConstant.CartCount;
    private String add_to_cart = AppConstant.BaseURl+AppConstant.AddToCart;
    private String update_url = AppConstant.UpdateCart;
    private String delete_url = AppConstant.DeleteCart;
    private String add_wish = AppConstant.AddToWishList;

    private Context context;
    String msg;
    public Mediator(Context context) {
        this.context = context;
    }

    public void getCartCount(final String UID, final VolleyCallBack volleyCallBack) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, cart_count_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            volleyCallBack.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(a);
                    }
                });
                builder.create();
                builder.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("UID",UID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    public void addToCart(final String imid, final String QTY, final String Uid, final VolleyCallBack volleyCallBack)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_to_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                            volleyCallBack.onSuccess(response);
                        }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context.getApplicationContext(), "Server down.."+error, Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       dialog.cancel();
                    }
                });
                builder.create();
                builder.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params=new HashMap<>();
                params.put("IMID",imid);
                params.put("Qty",QTY);
                params.put("UID",Uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public void updateCart(final String imid, final String uid, final String qty, final VolleyCallBack volleyCallBack)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, update_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        volleyCallBack.onSuccess(response);

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context.getApplicationContext(), "Server down..."+error, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params=new HashMap<>();
                params.put("IMID",imid);
                params.put("Qty",qty);
                params.put("UID",uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    public  void deleteCart(final String imid, final String uid, final String qty, final VolleyCallBack volleyCallBack)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, delete_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        volleyCallBack.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(context.getApplicationContext(), "Server down..."+error, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params=new HashMap<>();
                params.put("IMID",imid);
                params.put("QTY",qty);
                params.put("UID",uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    public void addToWishList(final String uid, final String imid, final VolleyCallBack volleyCallBack){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_wish,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        volleyCallBack.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UID",uid);
                params.put("IMID",imid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }



    public void getData(final String area_id, String url, final VolleyCallBack volleyCallBack)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        volleyCallBack.onSuccess(response);

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(context.getApplicationContext(), "Server down..."+error, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(a);
                    }
                });
                builder.create();
                builder.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params=new HashMap<>();
                params.put("AreaID",area_id);
                params.put("List","true");
              //  params.put("UID",uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }



    public void getDetails(final String p_imid, final String area_id, String detail_url, final VolleyCallBack volleyCallBack)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, detail_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        volleyCallBack.onSuccess(response);

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(context.getApplicationContext(), "Server down..."+error, Toast.LENGTH_SHORT).show();


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params=new HashMap<>();
                params.put("AID",area_id);
                params.put("IMID",p_imid);
                //  params.put("UID",uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }




}
