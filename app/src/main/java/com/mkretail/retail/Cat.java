package com.mkretail.retail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkretail.retail.Adapters.DataAdapter;
import com.mkretail.retail.Models.CatModel;
import com.mkretail.retail.Utils.AppConstant;
import com.mkretail.retail.Utils.CatClick;
import com.mkretail.retail.Utils.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Cat extends AppCompatActivity implements CatClick{

    String msg, mCartItemCount;
    List<CatModel> list =  new ArrayList<>();
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    DataAdapter dataAdapter;
    String UID;
    Mediator mediator = new Mediator(this);
    //private String cart_count_url = AppConstant.CartCount;

    TextView textCartItemCount;
    ProgressDialog progressDialog;
    BottomNavigationView bottomNavigationView;
    private String url= AppConstant.TokreeCat;
    private String url1 = AppConstant.WebURL;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat);


        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_category);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.navigation_home:
                        startActivity(new Intent(Cat.this,Dashboard.class));
                        break;

                    case R.id.navigation_category:
                       // startActivity(new Intent(getApplicationContext(),Cat.class));
                        break;

                    case R.id.navigation_search:
                        startActivity(new Intent(Cat.this, Search.class));
                        break;

                    case R.id.navigation_orders:
                        startActivity(new Intent(getApplicationContext(),Orders.class));
                        break;

                    case R.id.whatsapp:
                        String url = "https://api.whatsapp.com/send?phone=+919222277702";
                        //919222277702
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                }
                return true;
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UID","");

        progressDialog = new ProgressDialog(this);
       // list.add(new CatModel("122","Vegetable",R.drawable.profile));
        recyclerView = findViewById(R.id.catrecyclerview);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacecat);
        recyclerView.addItemDecoration(new Dashboard.SpacesItemDecoration(spacingInPixels));





        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && bottomNavigationView.isShown()) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else if (dy < 0 ) {
                    bottomNavigationView.setVisibility(View.VISIBLE);

                }
            }
        });

        dataAdapter = new DataAdapter(this, list);
        recyclerView.setAdapter(dataAdapter);

        getData();

    }


    public void getCartCount() {


        mediator.getCartCount(UID, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    Log.e("cartco",msg);

          //          textCartItemCount.setText(msg);

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

    @Override
    protected void onResume() {
        super.onResume();
//        setUpBadge();
        getCartCount();
        bottomNavigationView.setSelectedItemId(R.id.navigation_category);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
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
                editor.putString("ToCart","Cat");
                editor.commit();


                Intent i = new Intent(Cat.this,Cart.class);
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

    private void getData() {
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
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    CatModel catModel = new CatModel();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    catModel.setCatID(jsonObject1.getString("CID"));
                                    catModel.setCatName(jsonObject1.getString("Category_Name"));
                                    String upper = jsonObject1.getString("CatImage");
                                    int upper_bound = upper.length();

                                    String ImageUrl = "";
                                    for(int j = 1; j<upper_bound;j++){
                                        ImageUrl+=Character.toString(upper.charAt(j));
                                    }

                                    catModel.setCatImageURLTrim(url1+ImageUrl);
                                    list.add(catModel);
                                }
                                dataAdapter.notifyDataSetChanged();
                            }

                            else
                                {
                                    Toast.makeText(Cat.this, response, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Server time out", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onCatClick(View v, String cid) {
        Intent i = new Intent(getApplicationContext(),SubCat.class);
        i.putExtra("cid",cid);
        startActivity(i);
    }


}
