package com.mkretail.retail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkretail.retail.Adapters.DetailsAdapter;
import com.mkretail.retail.Adapters.MyAdapter;
import com.mkretail.retail.Models.DashProductModel;
import com.mkretail.retail.Models.DetailModel;
import com.mkretail.retail.Utils.AppConstant;
import com.mkretail.retail.Utils.Operations;
import com.mkretail.retail.Utils.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResults extends AppCompatActivity implements Operations, MyAdapter.OnItemClickListener {

    private String url = AppConstant.Search;
    private String url1 = AppConstant.WebURL;
    private String detail_url = AppConstant.TokreeProductItems;
    private String nodePrdsUrl = AppConstant.BaseURl+"Nodes/GetNodePrd";

    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    TextView textView;
    DetailsAdapter detailsAdapter;
    List<DetailModel> detailModels = new ArrayList<>();
    private PopupWindow mPopupWindow;
    RelativeLayout mLinearLayout;

    TextView textCartItemCount;
    Mediator mediator;
    private String msg, text, UID, area_id,msg1,reason, mCartItemCount, ncid, TOLIST;
    List<DashProductModel>  models = new ArrayList<>();
//    SearchDetailAdapter searchDetailAdapter;
    MyAdapter myAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_detail);

        recyclerView = findViewById(R.id.search_detail_recyclerView);
        textView = findViewById(R.id.errortext);
        mediator = new Mediator(this);
        mLinearLayout = (RelativeLayout)findViewById(R.id.search);
//        searchDetailAdapter = new SearchDetailAdapter(this,models);
        myAdapter = new MyAdapter(this, models);
        detailsAdapter = new DetailsAdapter(this, detailModels);

       // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space);
        recyclerView.addItemDecoration(new Dashboard.SpacesItemDecoration(spacingInPixels));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(searchDetailAdapter);
        recyclerView.setAdapter(myAdapter);

        text = getIntent().getStringExtra("text");
       // getSearchResults();

        ncid = getIntent().getStringExtra("ncid");
        if(text!=null){
            getSearchResults();
        }
        else {
            getNodePrds();
        }


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UID","");
        TOLIST = pref.getString("ToList","");

       // LogIN = pref.getString("LogIN","");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyLoc",0);
        area_id = preferences.getString("AreaId", "");



    }


    @Override
    protected void onResume() {
        super.onResume();
        getCartCount();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(TOLIST.equals("Dashboard")){
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }
        else {
            startActivity(new Intent(getApplicationContext(), Search.class));
        }

    }

    public void getCartCount() {

        mediator.getCartCount(UID, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    Log.e("cartco",msg);

                    SharedPreferences DId=getApplicationContext().getSharedPreferences("CartCount",0);
                    SharedPreferences.Editor editor = DId.edit();
                    editor.putString("mCartItemCount", msg);
                    editor.commit();
                    invalidateOptionsMenu();
                    supportInvalidateOptionsMenu();
                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addToWishList(String uid, String p_imid){
        mediator.addToWishList(uid, p_imid, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");

                    if(msg.equals("SUCCESS")){
                        Toast toast = Toast.makeText(getApplicationContext(), "Added to wishlist!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    else if(msg.equals("Error")){
                        String reason = jsonObject.getString("reason");
                        if(reason.equals("Already Exists")){
                            Toast toast = Toast.makeText(getApplicationContext(), "Already Exists!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setUpBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);


                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ToCart","Search");
                editor.commit();


                Intent i = new Intent(SearchResults.this,Cart.class);
                startActivity(i);
            }
        });

        return true;
    }


    private void setUpBadge()
    {
        SharedPreferences cc = getApplicationContext().getSharedPreferences("CartCount", 0);
        mCartItemCount = cc.getString("mCartItemCount", "");
        textCartItemCount.setText(mCartItemCount);
    }



    private void getNodePrds(){
        final ProgressDialog progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, nodePrdsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                //  JSONObject jsonObject3 = jsonObject.getJSONObject("result");
                                for(int i = 0;i<jsonArray.length();i++)
                                {
                                    DashProductModel dashProductModel = new DashProductModel();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    dashProductModel.setTitle(jsonObject1.getString("Product_Name"));

                                    //Fetching Image from Json and concatenating the resulting url with the base url
                                    String upper = jsonObject1.getString("Banner");
                                    int upper_bound = upper.length();

                                    String ImageUrl = "";
                                    for(int j = 1; j<upper_bound;j++){
                                        ImageUrl+=Character.toString(upper.charAt(j));
                                    }
                                    // Log.d("ImageURL",url1+ImageUrl);

                                    dashProductModel.setImage(url1+ImageUrl);
                                    dashProductModel.setImid(jsonObject1.getString("IMID"));
                                    dashProductModel.setDescription(jsonObject1.getString("Description"));

                                    String title = jsonObject1.getString("Title");
                                    String inStock = jsonObject1.getString("InStock");
                                    String isExpress = jsonObject1.getString("isExpress");
                                    Float amount = Float.parseFloat(jsonObject1.getString("Amount"));
                                    Float discount =  Float.parseFloat(jsonObject1.getString("SMDiscount"));
                                    String type = jsonObject1.getString("SMDisType");

                                    dashProductModel.setInStock(inStock);
                                    dashProductModel.setIsExpress(isExpress);

                                    if(type.equals("percentage") || type.equals("percent"))
                                    {
                                        if(discount.equals("0"))
                                        {
                                            dashProductModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(amount));

                                        }
                                        else
                                        {
                                            float final_amount = amount*(discount/100);
                                            float price = amount-final_amount;
                                            dashProductModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(price));

                                        }
                                    }

                                    else if(type.equals("price"))
                                    {
                                        float price = amount - discount;
                                        dashProductModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(price));

                                    }
                                    else
                                    {
                                        dashProductModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(amount));

                                    }
                                    models.add(dashProductModel);
                                }
//                                searchDetailAdapter.notifyDataSetChanged();
                                myAdapter.notifyDataSetChanged();
                            }
                            else if(msg.equals("missparam")){

                            }
                            else {

                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("NCID", ncid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void getSearchResults() {
        final ProgressDialog progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            msg = jsonObject.getString("msg");

                            if(msg.equals("SUCCESS"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                //  JSONObject jsonObject3 = jsonObject.getJSONObject("result");
                                for(int i = 0;i<jsonArray.length();i++)
                                {
                                    DashProductModel dashProductModel = new DashProductModel();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    dashProductModel.setTitle(jsonObject1.getString("Product_Name"));
                                    String inStock = jsonObject1.getString("InStock");
                                    String isExpress = jsonObject1.getString("isExpress");

                                    dashProductModel.setIsExpress(isExpress);
                                    dashProductModel.setInStock(inStock);
                                    //Fetching Image from Json and concatenating the resulting url with the base url
                                    String upper = jsonObject1.getString("Banner");
                                    int upper_bound = upper.length();

                                    String ImageUrl = "";
                                    for(int j = 1; j<upper_bound;j++){
                                        ImageUrl+=Character.toString(upper.charAt(j));
                                    }
                                    // Log.d("ImageURL",url1+ImageUrl);

                                    dashProductModel.setImage(url1+ImageUrl);
                                    dashProductModel.setImid(jsonObject1.getString("IMID"));
                                    dashProductModel.setDescription(jsonObject1.getString("Description"));

                                    String title = jsonObject1.getString("Title");
                                    Float amount = Float.parseFloat(jsonObject1.getString("Amount"));
                                    Float discount =  Float.parseFloat(jsonObject1.getString("SMDiscount"));
                                    String type = jsonObject1.getString("SMDisType");

                                    if(type.equals("percentage") || type.equals("percent"))
                                    {
                                        if(discount.equals("0"))
                                        {
                                            dashProductModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(amount));

                                        }
                                        else
                                        {
                                            float final_amount = amount*(discount/100);
                                            float price = amount-final_amount;
                                            dashProductModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(price));

                                        }
                                    }

                                    else if(type.equals("price"))
                                    {
                                        float price = amount - discount;
                                        dashProductModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(price));

                                    }
                                    else
                                    {
                                        dashProductModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(amount));

                                    }
                                    models.add(dashProductModel);
                                }
                               // searchDetailAdapter.notifyDataSetChanged();
                                myAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                            else if(msg.equals("Error"))
                            {
                                recyclerView.setVisibility(View.GONE);
                                textView.setText("No products available, Please Check Later!.");
                                textView.setVisibility(View.VISIBLE);
                                //Toast.makeText(Dashboard.this, "Products not available in your Area!", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                //+response
                                Toast.makeText(getApplicationContext(), "Could not contact server!", Toast.LENGTH_SHORT).show();

                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Server down", Toast.LENGTH_SHORT).show();
                Log.e("error", String.valueOf(error));
                progressDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("SearchK", text);
                params.put("Group","true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    public  void  getDetails(String p_imid, String area_id, String detail_url)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mediator.getDetails(p_imid, area_id, detail_url, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    msg = jsonObject.getString("msg");
                    if(msg.equals("SUCCESS"))
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        Log.e("js",jsonArray.toString());
                        //  JSONObject jsonObject3 = jsonObject.getJSONObject("result");
                        for(int i = 0;i<jsonArray.length();i++)
                        {
                            JSONObject entry = jsonArray.getJSONObject(i);
                            DetailModel Model = new DetailModel();

                            if(entry.getString("Title").equals(""))
                            {

                            }
                            else if(!entry.getString("Title").equals(""))
                            {
                                Model.setTitle(entry.getString("Title"));
                                Model.setImid(entry.getString("IMID"));
                                String discount = entry.getString("SMDisType");
                                String price = "\u20B9"+entry.getString("Amount");
                                Model.setPrice(price);
                            }

                            detailModels.add(Model);
                        }
                        detailsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                    else if(msg.equals("Error"))
                    {
                        Toast.makeText(getApplicationContext(), "No packets available!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //+response
                        Toast.makeText(getApplicationContext(), "Could not contact server!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }


    public void showPopUp(String titl, String imid, String area_id, String detail_url)
    {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_layout,null);

        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }


        detailModels.clear();
        // detailsAdapter.notifyDataSetChanged();

        ImageView closeButton = (ImageView) customView.findViewById(R.id.close);
        TextView title = (TextView)customView.findViewById(R.id.poptitle);
        RecyclerView recyclerView = (RecyclerView)customView.findViewById(R.id.packetrecyclerview);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(detailsAdapter);

        title.setText(titl);
        getDetails(imid, area_id, detail_url);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(mLinearLayout, Gravity.CENTER,0,0);

        View container = mPopupWindow.getContentView().getRootView();
        if(container != null) {
            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams)container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.7f;
            if(wm != null) {
                wm.updateViewLayout(container, p);
            }
        }
    }

    @Override
    public void addToCart(String p_imid, String count) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding product...");
        progressDialog.show();
        //  Log.e("gone","inside");

        mediator.addToCart(p_imid, count, UID, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    msg1 = jsonObject.getString("msg");
                    Log.e("gone",jsonObject.toString());
                    if(msg1.equals("SUCCESS"))
                    {
                        getCartCount();
                        //textCartItemCount.setText(Integer.parseInt(textCartItemCount.getText().toString())+1);
                        Toast.makeText(getApplicationContext(), "Product Added!", Toast.LENGTH_SHORT).show();
                    }
                    else if(msg1.equals("Error"))
                    {
                        reason = jsonObject.getString("reason");
                        if(reason.equals("exceed"))
                        {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SearchResults.this);
                            builder.setTitle("Alert!");
                            builder.setIcon(R.drawable.error);
                            builder.setMessage("Quantity Exceeded!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.create();
                            builder.show();
                        }

                        else if(result.equals("outofstock"))
                        {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SearchResults.this);
                            builder.setTitle("Alert!");
                            builder.setIcon(R.drawable.error);
                            builder.setMessage("Out of stock!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.create();
                            builder.show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void updateCart(String p_imid, String count) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mediator.updateCart(p_imid, UID, count, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    msg1 = jsonObject.getString("msg");
                    if(msg1.equals("SUCCESS"))
                    {
                        getCartCount();
                        Toast.makeText(getApplicationContext(), "Product Updated!", Toast.LENGTH_SHORT).show();
                    }
                    else if(msg1.equals("Error"))
                    {
                        reason = jsonObject.getString("reason");
                        if(reason.equals("outofstock"))
                        {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SearchResults.this);
                            builder.setTitle("Alert!");
                            builder.setIcon(R.drawable.error);
                            builder.setMessage("Out of stock!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.create();
                            builder.show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
                catch (JSONException e)
                {
                }
            }
        });
    }

    @Override
    public void deleteCart(String p_imid, String count) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mediator.deleteCart(p_imid, UID, count, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    msg1 = jsonObject.getString("msg");
                    if(msg1.equals("SUCCESS"))
                    {

                        getCartCount();
                        Toast.makeText(getApplicationContext(), "Product Deleted!", Toast.LENGTH_SHORT).show();
                    }
                    else if(msg1.equals("Error"))
                    {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onItemClick(View v, String title, String p_imid) {
        showPopUp(title,p_imid,area_id,detail_url);

    }

    @Override
    public void addWish(String p_imid) {
        addToWishList(UID, p_imid);

    }


}
