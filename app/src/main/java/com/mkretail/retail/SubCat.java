package com.mkretail.retail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import com.mkretail.retail.Models.SubCatModel;
import com.mkretail.retail.Utils.AppConstant;
import com.mkretail.retail.Utils.Operations;
import com.mkretail.retail.Utils.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCat extends AppCompatActivity implements Operations, MyAdapter.OnItemClickListener {

    RecyclerView subcattextrecyclerView, productrecyclerview;
    SubCatAdapter subCatAdapter;
    String cid, area_id, UID, var, Sid, TO_LIST, p_imid;
    LinearLayoutManager linearLayoutManager;
    DividerItemDecoration dividerItemDecoration;
    ImageView imageView;
    private String msg, mCartItemCount;
    Mediator mediator = new Mediator(this);
    DetailsAdapter detailsAdapter;
    List<DetailModel> detailModels = new ArrayList<>();

    private  TextView textCartItemCount;
    private String sub_cart_url = AppConstant.TokreeSubCat;
    private String sub_cat_prod_url = AppConstant.TokreeProdList;
    private CoordinatorLayout mLinearLayout;
    private String msg1, reason;
    private ImageView floatingActionButton;
    private String url = AppConstant.WebURL;
    boolean isClicked = false;
    private String detail_url = AppConstant.TokreeProductItems;
    GridLayoutManager gridLayoutManager;
    List<SubCatModel> subCatModels = new ArrayList<>();
    private PopupWindow mPopupWindow;
    boolean comingfromdash = false;
    boolean flag = false;
    private TextView comingSoon;

    NestedScrollView relativeLayout;

    //String sid;
    List<DashProductModel> prodListModels = new ArrayList<>();
    MyAdapter myAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat_list);

        subcattextrecyclerView = findViewById(R.id.subcattextrecyclerview);
        imageView = findViewById(R.id.subcatimage);

        mLinearLayout = (CoordinatorLayout) findViewById(R.id.linear);
        productrecyclerview = findViewById(R.id.subcatproductrecyclerview);
        floatingActionButton = (ImageView) findViewById(R.id.expressfloat);
        detailsAdapter = new DetailsAdapter(this,detailModels);
        relativeLayout = (NestedScrollView) findViewById(R.id.nested);
        comingSoon = (TextView)findViewById(R.id.comingSoon);

        //Sub category text
        subcattextrecyclerView.addItemDecoration(new DividerItemDecoration(SubCat.this,
                LinearLayoutManager.HORIZONTAL));
        subCatAdapter = new SubCatAdapter(subCatModels);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(SubCat.this,
                LinearLayoutManager.HORIZONTAL, false);
        subcattextrecyclerView.setLayoutManager(horizontalLayoutManager);
        subcattextrecyclerView.setHasFixedSize(true);
        subcattextrecyclerView.setAdapter(subCatAdapter);
        subcattextrecyclerView.setNestedScrollingEnabled(false);



        //Sub category products
       // subCatProductAdapter = new SubCatProductAdapter(prodListModels);
        myAdapter = new MyAdapter(this,prodListModels);

        gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space);
        productrecyclerview.addItemDecoration(new Dashboard.SpacesItemDecoration(spacingInPixels));
        productrecyclerview.setHasFixedSize(true);
      //  productrecyclerview.addItemDecoration();
        productrecyclerview.setLayoutManager(gridLayoutManager);
        //productrecyclerview.setAdapter(subCatProductAdapter);
        productrecyclerview.setAdapter(myAdapter);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UID","");
        TO_LIST = pref.getString("ToList","");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyLoc",0);
        area_id = preferences.getString("AreaId", "");


        cid = getIntent().getStringExtra("cid");
        Sid = getIntent().getStringExtra("sid");
        p_imid = getIntent().getStringExtra("p_imid");


        if(getIntent().getStringExtra("sid")!=null){
            get_sub_cat(cid);
            get_sub_cat_prod(cid,Sid,area_id);
//            subcattextrecyclerView.setVisibility(View.GONE);
//            floatingActionButton.setVisibility(View.GONE);
//            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)relativeLayout.getLayoutParams();
//            layoutParams.setMargins(0, 0, 0, 0);
//            relativeLayout.setLayoutParams(layoutParams);
//            comingfromdash = true;
//            subCatAdapter.selectedPos = subCatAdapter.s_id.indexOf(Sid);
//            Log.e("position", String.valueOf(subCatAdapter.s_id.indexOf(Sid)));

            productrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    //  super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
                        floatingActionButton.setVisibility(View.INVISIBLE);
                    } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }
                }
            });

           // subCatAdapter.selectedPos = Integer.parseInt(sid);
        }
        else if(p_imid!=null){
            get_sub_cat_prod(cid, "imid", area_id);
            subcattextrecyclerView.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.GONE);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)relativeLayout.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            relativeLayout.setLayoutParams(layoutParams);
//            comingfromdash = true;
        }
        else {
            get_sub_cat(cid);
            get_sub_cat_prod(cid,"",area_id);


            productrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    //  super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
                        floatingActionButton.setVisibility(View.INVISIBLE);
                    } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        //Sub category text
     final int speedScroll = 1000;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;
            @Override
            public void run() {
                if(count < subCatAdapter.getItemCount()){
                    Log.e("count", String.valueOf(subCatAdapter.getItemCount()));
                    if(count==subCatAdapter.getItemCount()-1){
                        flag = false;
                    }else if(count == 0){
                        flag = true;
                    }

                    if(flag) {
                        count++;
                            if(subcattextrecyclerView.getScrollState()==RecyclerView.SCROLL_STATE_DRAGGING){
                                subcattextrecyclerView.stopScroll();
                            }
                            else if(subCatAdapter.selectedPos>0){
                                subcattextrecyclerView.stopScroll();
                            }
                            else {
                                subcattextrecyclerView.smoothScrollToPosition(count);
                                handler.postDelayed(this,speedScroll);
                            }
                    }
                    else {
                        subcattextrecyclerView.smoothScrollToPosition(subCatAdapter.selectedPos);
                        //count--;
                    }
                }
            }
        };
        handler.postDelayed(runnable,speedScroll);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartCount();
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();

        if(TO_LIST.equals("Dashboard")){
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }
        else if(TO_LIST.equals("Cat")){
            startActivity(new Intent(getApplicationContext(), Cat.class));
        }
        else {
            startActivity(new Intent(getApplicationContext(), Cat.class));

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

    public void get_sub_cat(final String cid)
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading product...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sub_cart_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            msg = jsonObject.getString("msg");

                            if(msg.equals("SUCCESS"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                saveJSONArray(jsonArray);
                              //  Log.e("json",var);
                                subCatModels.add(new SubCatModel("", "All Products"));
                                for(int i = 0;i<jsonArray.length();i++)
                                {

                                    SubCatModel dashProductModel = new SubCatModel();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    //String s_id = jsonObject1.getString("SID");
                                    dashProductModel.setSubCatID(jsonObject1.getString("SID"));
                                    dashProductModel.setSubCatName(jsonObject1.getString("Subcategory_Name"));
                                  //  sid.add(s_id);
                                    subCatModels.add(dashProductModel);
                                }

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                            }
                            subCatAdapter.notifyDataSetChanged();

                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), ""+error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params=new HashMap<>();
                params.put("CID",cid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    //Fetching products available in the given sub-category id.
    public void get_sub_cat_prod(final String cid, final String sid, final String area_id)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sub_cat_prod_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS"))
                            {
                                productrecyclerview.setVisibility(View.VISIBLE);
                                imageView.setVisibility(View.GONE);
                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for(int i = 0;i<jsonArray.length();i++)
                                {
                                    JSONObject entry = jsonArray.getJSONObject(i);
                                   // Log.e("con",entry.toString());
                                    DashProductModel prodListModel = new DashProductModel();
                                    String express = entry.getString("isExpress");
                                    String inStock = entry.getString("InStock");

                                    if(isClicked==true)
                                    {
                                        if(express.equals("1"))
                                        {
                                            flag = true;
                                            prodListModel.setTitle(entry.getString("Product_Name"));
                                            String upper = entry.getString("Banner");
                                            int upper_bound = upper.length();

                                            String ImageUrl = "";
                                            for(int k = 1; k<upper_bound;k++){
                                                ImageUrl+=Character.toString(upper.charAt(k));
                                            }
                                            prodListModel.setImage(url+ImageUrl);
                                            prodListModel.setImid(entry.getString("IMID"));
                                            prodListModel.setIsExpress(express);
                                            prodListModel.setInStock(inStock);
//                                            prodListModel.setIsExpress(express);

                                            // JSONArray jsonArray1= entry.getJSONArray("Detail");

                                            String title = entry.getString("Title");
                                            Float amount = Float.parseFloat(entry.getString("Amount"));
                                            Float discount =  Float.parseFloat(entry.getString("SMDiscount"));
                                            String type = entry.getString("SMDisType");

                                            if(type.equals("percentage") || type.equals("percent"))
                                            {
                                                if(discount==0 || discount==0.0)
                                                {
                                                    prodListModel.setPrice(title+" "+"\u20B9"+String.valueOf(amount));
                                                    prodListModel.setOriginalAmount("");
                                                }
                                                else
                                                {//Discount
                                                    float final_amount = amount*(discount/100);
                                                    float price = amount-final_amount;
                                                    prodListModel.setPrice(title+" "+"\u20B9"+String.valueOf(price));
                                                    prodListModel.setOriginalAmount(String.valueOf(amount));
                                                }
                                            }

                                            else if(type.equals("price"))
                                            {//Discount
                                                float price = amount - discount;
                                                prodListModel.setPrice(title+" "+"\u20B9"+String.valueOf(price));
                                                prodListModel.setOriginalAmount(String.valueOf(amount));
                                            }
                                            else
                                            {
                                                prodListModel.setPrice(title+" "+"\u20B9"+String.valueOf(amount));
                                                prodListModel.setOriginalAmount("");
                                            }
                                            prodListModels.add(prodListModel);
                                        }
                                    }

                                    else {
                                        prodListModel.setTitle(entry.getString("Product_Name"));
                                        String upper = entry.getString("Banner");
//                                        String isExpress = entry.getString("isExpress");
                                        int upper_bound = upper.length();

                                        String ImageUrl = "";
                                        for(int k = 1; k<upper_bound;k++){
                                            ImageUrl+=Character.toString(upper.charAt(k));
                                        }
                                        prodListModel.setImage(url+ImageUrl);
                                        prodListModel.setImid(entry.getString("IMID"));
                                        prodListModel.setDescription(entry.getString("Description"));
                                        prodListModel.setIsExpress(express);
                                        prodListModel.setInStock(inStock);

                                        // JSONArray jsonArray1= entry.getJSONArray("Detail");

                                        String title = entry.getString("Title");
                                        Float amount = Float.parseFloat(entry.getString("Amount"));
                                        Float discount =  Float.parseFloat(entry.getString("SMDiscount"));
                                        String type = entry.getString("SMDisType");

                                        if(type.equals("percentage") || type.equals("percent"))
                                        {
                                            if(discount==0 || discount==0.0)
                                            {
                                                prodListModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(amount));

                                            }
                                            else
                                            {
                                                float final_amount = amount*(discount/100);
                                                float price = amount-final_amount;
                                                prodListModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(price));

                                            }
                                        }

                                        else if(type.equals("price"))
                                        {
                                            float price = amount - discount;
                                            prodListModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(price));

                                        }
                                        else
                                        {
                                            prodListModel.setPrice(title+" "+"@ "+"\u20B9"+String.valueOf(amount));

                                        }
                                        prodListModels.add(prodListModel);
                                    }
                                    }
                                    //subCatProductAdapter.notifyDataSetChanged();
                                myAdapter.notifyDataSetChanged();

                                }

                            else if(msg.equals("Error"))
                            {
                              productrecyclerview.setVisibility(View.GONE);
                              imageView.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(SubCat.this);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Cat.class));

//                        Intent a = new Intent(Intent.ACTION_MAIN);
//                        a.addCategory(Intent.CATEGORY_HOME);
//                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(a);
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

                if(sid.equals("")){
                    params.put("SubCategory","");
                    params.put("Category",cid);

                }
                else if(sid.equals("imid")){
                    params.put("IMID",p_imid);
                }
                else //if(!sid.isEmpty())
                {
                    params.put("SubCategory",sid);
                    params.put("Category",cid);

                }


                params.put("AreaID",area_id);
                params.put("IOS","true");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                editor.putString("ToCart","SubCat");
                editor.commit();


                Intent i = new Intent(SubCat.this,Cart.class);
                startActivity(i);
            }
        });
        return true;
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
                     //   Log.e("js",jsonArray.toString());
                        //  JSONObject jsonObject3 = jsonObject.getJSONObject("result");
                        for(int i = 0;i<jsonArray.length();i++)
                        {
                            JSONObject entry = jsonArray.getJSONObject(i);
                            DetailModel Model = new DetailModel();

//                            else if(!entry.getString("Title").equals(""))
//                            {
//                                Model.setTitle(entry.getString("Title"));
                                Model.setImid(entry.getString("IMID"));
                                String type = entry.getString("SMDisType");
                                Float amount = Float.valueOf(entry.getString("Amount"));
                                Float discount =  Float.parseFloat(entry.getString("SMDiscount"));

                            if(type.equals("percentage") || type.equals("percent"))
                            {
                                if(discount==0 || discount==0.0)
                                {
                                    String price = "\u20B9"+String.valueOf(amount);
                                    Model.setPrice(price);
                                    Model.setOriginalAmount("");
                                }
                                else
                                {//Discount
                                    float final_amount = amount*(discount/100);
                                    float price = amount-final_amount;
                                    String _price = "\u20B9"+String.valueOf(price);
                                    Model.setPrice(_price);
                                    Model.setOriginalAmount("\u20B9"+String.valueOf(amount));

                                }
                            }

                            else if(type.equals("price"))
                            {//Discount
                                float price = amount - discount;
                                String _price = "\u20B9"+String.valueOf(price);
                                Model.setPrice(_price);
                                Model.setOriginalAmount("\u20B9"+String.valueOf(amount));
                            }
                            else
                            {
                                String price = "\u20B9"+String.valueOf(amount);
                                Model.setPrice(price);
                                Model.setOriginalAmount("");
                            }
//                            }

                            detailModels.add(Model);
                        }
                        detailsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                    else if(msg.equals("Error"))
                    {
                        Toast.makeText(SubCat.this, "No packets available!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(SubCat.this, "Could not contact server!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Toast.makeText(SubCat.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

        ImageView closeButton = (ImageView) customView.findViewById(R.id.close);
        TextView title = (TextView)customView.findViewById(R.id.poptitle);
        RecyclerView recyclerView = (RecyclerView)customView.findViewById(R.id.packetrecyclerview);
        detailsAdapter = new DetailsAdapter(this,detailModels);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(dividerItemDecoration);
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
            p.dimAmount = 0.8f;
            if(wm != null) {
                wm.updateViewLayout(container, p);
            }
        }
    }

    private void setUpBadge()
    {
        SharedPreferences cc = getApplicationContext().getSharedPreferences("CartCount", 0);
        mCartItemCount = cc.getString("mCartItemCount", "");
        textCartItemCount.setText(mCartItemCount);
    }

    public  void saveJSONArray(JSONArray array) {
        var = array.toString();
    }

    @Override
    public void addToCart(String p_imid, String count) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        //  Log.e("gone","inside");
        mediator.addToCart(p_imid, count, UID, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    msg1 = jsonObject.getString("msg");
                    // Log.e("gone",jsonObject.toString());
                    if(msg1.equals("SUCCESS"))
                    {
                        //Add Alert Box
                        //   dashboard.getCartCount(context);
                        getCartCount();
                        //textCartItemCount.setText(Integer.parseInt(textCartItemCount.getText().toString())+1);
                        Toast.makeText(getApplicationContext(), "Product Added!", Toast.LENGTH_SHORT).show();
                    }
                    else if(msg1.equals("Error"))
                    {
                        reason = jsonObject.getString("reason");
                        if(reason.equals("exceed"))
                        {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SubCat.this);
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
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SubCat.this);
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
                    Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
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
                        //Add Alert Box
                        //   dashboard.getCartCount(context);
                        getCartCount();
                        //textCartItemCount.setText(Integer.parseInt(textCartItemCount.getText().toString())+1);
                        Toast.makeText(getApplicationContext(), "Product Updated!", Toast.LENGTH_SHORT).show();
                    }
                    else if(msg1.equals("Error"))
                    {
                        reason = jsonObject.getString("reason");
                        if(reason.equals("outofstock"))
                        {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SubCat.this);
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
                        //Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // onBackPressed();
                if(TO_LIST.equals("Dashboard")){
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    finish();
                }
                else {
                    startActivity(new Intent(getApplicationContext(), Cat.class));
                }

                return true;
        }
        return super.onOptionsItemSelected(item);

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
                    progressDialog.dismiss();

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
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onItemClick(View v, String title, String p_imid) {
        showPopUp(title, p_imid, area_id, detail_url);

    }

    @Override
    public void addWish(String p_imid) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding....");
        progressDialog.show();
        mediator.addToWishList(UID, p_imid, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
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
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
    }

    public class SubCatAdapter extends RecyclerView.Adapter<SubCatAdapter.Viewholder> {
        String sid;

        int selectedPos= 0;
        private ArrayList<String> s_id = new ArrayList<>();
        private List<SubCatModel> subCatModels;

        // private SubCatTextClick subCatTextClick;
        public SubCatAdapter(List<SubCatModel> subCatModels) {
            this.subCatModels = subCatModels;
            //this.subCatTextClick = (SubCatTextClick)context;
        }

        @NonNull
        @Override
        public SubCatAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_cat_text_item,viewGroup,false);
            return new Viewholder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final SubCatAdapter.Viewholder viewholder, final int i) {
            viewholder.bind(subCatModels.get(i));
        }

        @Override
        public int getItemCount() {
            return subCatModels.size();
        }

        public class Viewholder extends RecyclerView.ViewHolder
        {
            TextView textView;
            public Viewholder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.sub_cat_text);
            }

            public void bind(final SubCatModel item){
//                final SubCatModel  subCatModel = subCatModels.get(i);
                textView.setText(item.getSubCatName());

                //Fetching the sid's stored in var
                try {
                    JSONArray jsonArray = new JSONArray(var);
                    s_id.add("");
                    //Log.e("jsonA", String.valueOf(jsonArray));
                    for(int j = 0; j<jsonArray.length();j++){
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        s_id.add(jsonObject.getString("SID"));
                    }
                    Log.e("S_ID", String.valueOf(s_id));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (selectedPos == getAdapterPosition()) {
                    textView.setBackgroundColor(Color.parseColor("#1B5E20"));
                    textView.setTextColor(Color.parseColor("#FAFAFA"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#EBE7E7"));
                    textView.setTextColor(Color.parseColor("#212121"));
                }
//
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.d("selected", String.valueOf(selectedPos));
                        sid = s_id.get(selectedPos);

                        //Express Delivery
                        if(isClicked==false){
                            isClicked = true;

                            Picasso.with (SubCat.this)
                                    .load (R.drawable.normal).fit().centerCrop()
                                    .into (floatingActionButton);

                            setTitle("Express Delivery products");
                            prodListModels.clear();
                           // subCatProductAdapter.notifyDataSetChanged();
                            myAdapter.notifyDataSetChanged();
                            get_sub_cat_prod(cid,sid,area_id);
                            if(flag==false){
                                //No express Products
                                productrecyclerview.setVisibility(View.GONE);
                                comingSoon.setVisibility(View.VISIBLE);
                            }
                        }
                        //Normal Delivery
                        else {
                            isClicked =false;
                            comingSoon.setVisibility(View.GONE);
                            Picasso.with (SubCat.this)
                                    .load (R.drawable.switchtoexpress).fit().centerCrop()
                                    .into (floatingActionButton);

                            setTitle("Normal delivery products");
                            prodListModels.clear();
                            //subCatProductAdapter.notifyDataSetChanged();
                            myAdapter.notifyDataSetChanged();
                            get_sub_cat_prod(cid,sid,area_id);
                        }
                    }
                });

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //For the color of selected item
                        selectedPos = getAdapterPosition();
                        notifyDataSetChanged();

                        //For subcat products first clear list and then load.
                        prodListModels.clear();
                        //subCatProductAdapter.notifyDataSetChanged();
                        myAdapter.notifyDataSetChanged();
                       // Log .e("SID", item.getSubCatID());
                        get_sub_cat_prod(cid,item.getSubCatID(),area_id);
                    }
                });
            }

        }
    }
}
