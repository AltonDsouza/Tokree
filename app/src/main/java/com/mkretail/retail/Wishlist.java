package com.mkretail.retail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.mkretail.retail.Models.DashProductModel;
import com.mkretail.retail.Models.DetailModel;
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

public class Wishlist extends AppCompatActivity implements Operations {

    RecyclerView recyclerView ;
    WishAdapter wishAdapter;
    ProgressDialog progressDialog;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    Mediator mediator = new Mediator(this);
    DetailsAdapter detailsAdapter;
    PopupWindow mPopupWindow;
    private String mCartItemCount;
    TextView textCartItemCount;
    private String del_url = AppConstant.DelWishList;
    private String url1 = AppConstant.WebURL;
    private String detail_url = AppConstant.TokreeProductItems;
    private String url = AppConstant.GetWishList;
    private String UID, area_id;
    LinearLayout  mLinearLayout;
    private TextView nowish;
    List<DashProductModel> models = new ArrayList<>();

    List<DetailModel> detailModels = new ArrayList<>();
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        recyclerView = findViewById(R.id.wishlistRecyclerView);
        wishAdapter = new WishAdapter(this, models);
        mLinearLayout = (LinearLayout)findViewById(R.id.wishlin);
        nowish = (TextView)findViewById(R.id.nowish);
        progressDialog = new ProgressDialog(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        //area_id = pref.getString("AreaId", "");
        UID = pref.getString("UID","");

        SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyLoc", 0);
        area_id = pref1.getString("AreaId","");

        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space);
        recyclerView.addItemDecoration(new Dashboard.SpacesItemDecoration(spacingInPixels));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(wishAdapter);

        wishAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
           //     super.onChanged();

                if (wishAdapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    nowish.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    nowish.setVisibility(View.GONE);
                }
            }
        });


        getWishList();
    }

    @Override
    protected void onResume() {
        super.onResume();
         getCartCount();
    }

    public void getCartCount() {

        mediator.getCartCount(UID, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    Log.e("cartco",msg);
                    //  textCartItemCount.setText(msg);
                    SharedPreferences DId=getApplicationContext().getSharedPreferences("CartCount",0);
                    SharedPreferences.Editor editor = DId.edit();
                    editor.putString("mCartItemCount", msg);
                    editor.commit();
                    invalidateOptionsMenu();
                    supportInvalidateOptionsMenu();
                }
                catch (JSONException e)
                {

                }
            }
        });

    }

    private void getWishList() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                for(int i = 0; i<jsonArray.length(); i++){
                                    DashProductModel dashProductModel = new DashProductModel();

                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    dashProductModel.setTitle(jsonObject1.getString("Product_Name"));
                                    dashProductModel.setDescription(jsonObject1.getString("Description"));
                                    dashProductModel.setImid(jsonObject1.getString("IMID"));
                                    dashProductModel.setWishlistid(jsonObject1.getString("WLID"));

                                    String upper = jsonObject1.getString("Banner");
                                    int upper_bound = upper.length();
                                    String ImageUrl = "";
                                    for(int j = 1; j<upper_bound;j++){
                                        ImageUrl+=Character.toString(upper.charAt(j));
                                    }
                                    // Log.d("ImageURL",url1+ImageUrl);
                                    dashProductModel.setImage(url1+ImageUrl);

                                    String title = jsonObject1.getString("SMTitle");
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
                                wishAdapter.notifyDataSetChanged();
                            }
                            else if(msg.equals("Error")){
                                    nowish.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                            }

                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(Wishlist.this);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       finish();
                    }
                });
                builder.create();
                builder.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UID", UID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void deleteWish(final String IMID, final String WLID){
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, del_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                Toast toast = Toast.makeText(getApplicationContext(), "Delete Success!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                            else if(msg.equals("Error")){
                                String reason = jsonObject.getString("reason");
                                if(reason.equals("")){

                                }
                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(Wishlist.this);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.error);
                builder.setMessage("Please check your internet connection and try again...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UID",UID);
                params.put("IMID",IMID);
                params.put("WLID",WLID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                editor.putString("ToCart","WishList");
                editor.commit();


                Intent i = new Intent(Wishlist.this,Cart.class);
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


    public void showPopUp(String titl, String imid, String area_id, String detail_url)
    {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_layout,null);

        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        //  getWindow().getDecorView().setBackground(new ColorDrawable(Color.TRANSPARENT));

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }
      detailModels.clear();
        // detailsAdapter.notifyDataSetChanged();

        ImageView closeButton = (ImageView) customView.findViewById(R.id.close);
        TextView title = (TextView)customView.findViewById(R.id.poptitle);
        RecyclerView recyclerView = (RecyclerView)customView.findViewById(R.id.packetrecyclerview);
        detailsAdapter = new DetailsAdapter(this,detailModels);


        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
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
        mPopupWindow.setAnimationStyle(R.style.popup_window_animation);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mPopupWindow.setFocusable(true);

        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

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
                   String msg = jsonObject.getString("msg");
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
                    String msg1 = jsonObject.getString("msg");
                    Log.e("gone",jsonObject.toString());
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
                        String reason = jsonObject.getString("reason");
                        if(reason.equals("exceed"))
                        {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
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
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
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
                        Toast.makeText(Wishlist.this, ""+result, Toast.LENGTH_SHORT).show();
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
                    String msg1 = jsonObject.getString("msg");
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
                        String reason = jsonObject.getString("reason");
                        if(reason.equals("outofstock"))
                        {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
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
    public void deleteCart(String p_imid, String count) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting please wait...");
        progressDialog.show();

        mediator.deleteCart(p_imid, UID, count, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg1 = jsonObject.getString("msg");
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

    public class WishAdapter extends RecyclerView.Adapter<WishAdapter.ViewHolder>{
            private Context context;
            private List<DashProductModel> models1;

        public WishAdapter(Context context, List<DashProductModel> models) {
            this.context = context;
            this.models1 = models;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(context).inflate(R.layout.custom_wish_list, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
            final DashProductModel dashProductModel = models1.get(i);
            //Do the changes...
            Picasso.with(context).load(dashProductModel.getImage()).into(viewHolder.prodImage);
            viewHolder.p_imid.setText(dashProductModel.getImid());
            viewHolder.prodLabel.setText(dashProductModel.getPrice());
            viewHolder.prodName.setText(dashProductModel.getTitle());

            viewHolder.deleteW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Wishlist.this);
                    builder.setTitle("Alert!");
                    builder.setIcon(R.drawable.error);
                    builder.setMessage("Do you really want to delete?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteWish(dashProductModel.getImid(), dashProductModel.getWishlistid());
                            models1.remove(i);
                            notifyItemRemoved(i);
                            notifyItemRangeRemoved(i, models1.size());

                            if(models.isEmpty()){
                                nowish.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create();
                    builder.show();
                   //dashProductModel.getWishlistid()
                }
            });

            viewHolder.addCartW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = String.valueOf(dashProductModel.getTitle());
                    String p_imid = String.valueOf(dashProductModel.getImid());
                    showPopUp(name,p_imid,area_id,detail_url);
                }
            });
        }

        @Override
        public int getItemCount() {

            return models1.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            ImageView prodImage,deleteW ;
            TextView prodName, prodLabel, p_imid, addCartW;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                prodImage = itemView.findViewById(R.id.PimageW);
                prodName = itemView.findViewById(R.id.PnameW);
                prodLabel = itemView.findViewById(R.id.PpriceW);
                deleteW = itemView.findViewById(R.id.deleteW);
                addCartW = itemView.findViewById(R.id.addCartW);
                p_imid = itemView.findViewById(R.id.P_idW);
            }
        }
    }

}
