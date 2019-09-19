package com.mkretail.retail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mkretail.retail.Adapters.DetailsAdapter;
import com.mkretail.retail.Adapters.MultiViewTypeAdapter;
import com.mkretail.retail.Adapters.MyAdapter;
import com.mkretail.retail.Models.Banner;
import com.mkretail.retail.Models.DashProductModel;
import com.mkretail.retail.Models.DetailModel;
import com.mkretail.retail.Models.Model;
import com.mkretail.retail.Utils.AppConstant;
import com.mkretail.retail.Utils.Operations;
import com.mkretail.retail.Utils.VolleyCallBack;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , Operations, MyAdapter.OnItemClickListener {

    private DividerItemDecoration dividerItemDecoration;
    private LinearLayoutManager linearLayoutManager;
    private String detail_url = AppConstant.TokreeProductItems;
    private String token_url = AppConstant.BaseURl+"Register/UpdateToken";
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    private String url1 = AppConstant.WebURL;
    private String sliderUrl = AppConstant.BaseURl+"Additionals/GetBanner";
    MyAdapter adapter;
    private TextView textCartItemCount;
    String UID, token;
    private String dash_url = AppConstant.BaseURl+"Nodes";
    private PopupWindow mPopupWindow;
    View view;
    BottomNavigationView bottomNavigationView;
    RecyclerView  recy1;
    String area_id;
    private String msg, mCartItemCount;
    private NavigationView navigationView;
    String msg1;
    Mediator mediator ;
    DetailsAdapter detailsAdapter;
    List<DetailModel> detailModels = new ArrayList<>();
    String reason;
    private RelativeLayout mLinearLayout;
    private String LogIN;
    private CarouselView carouselView;


    List<DashProductModel> list = new ArrayList<>();
//    List<Banner> banners = new ArrayList<>();
    private String [] banners;

    ArrayList<Model> models = new ArrayList<>();
    MultiViewTypeAdapter multiViewTypeAdapter  = new MultiViewTypeAdapter(models, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mLinearLayout = (RelativeLayout) findViewById(R.id.rl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //dialog = new Dialog(this);
        recy1 = findViewById(R.id.recyclerview1);

        //Setting up View Pager


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);

        recy1.setLayoutManager(linearLayoutManager);
        recy1.setHasFixedSize(true);
        recy1.setNestedScrollingEnabled(false);
//        recy1.setItemAnimator(new DefaultItemAnimator());
        recy1.setAdapter(multiViewTypeAdapter);


        mediator = new Mediator(this);
         //mySpinner = (Spinner) findViewById(R.id.spinner);
         bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                 switch (menuItem.getItemId())
                 {
                     case R.id.navigation_home:
                         break;

                     case R.id.navigation_category:
                         startActivity(new Intent(Dashboard.this,Cat.class));
                         break;

                     case R.id.navigation_search:
                         startActivity(new Intent(Dashboard.this,Search.class));
                         break;
                     case R.id.navigation_orders:
                         startActivity(new Intent(Dashboard.this,Orders.class));
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
        Log.e("UID",UID);
        LogIN = pref.getString("LogIN","");
        token = pref.getString("Token","");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyLoc",0);
        area_id = preferences.getString("AreaId", "");

        adapter = new MyAdapter(this, list);
//        createDummyData();
        getData();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code here
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();

            sendToDatabase(token, UID);

        }

        hideItem();

        showPopUpIfNotified();
    }
    //    public void createDummyData() {
//
//            ArrayList<DashProductModel> singleItem = new ArrayList<DashProductModel>();
//
//                singleItem.add(new DashProductModel("Mango","http://fortunehealthplus.com/Tokree/upload/Nodes/WDF_1203773.jpg"));
//                singleItem.add(new DashProductModel("Anaar khatta", "http://fortunehealthplus.com/Tokree/uploads/Banner/1230_1554813163.68_37777_5cac90eba6fed.png"));
//                //  dm.setAllItemsInSection(singleItem);
//        }

    @Override
    protected void onResume() {
        super.onResume();
        getCartCount();
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
       // hideItem();
       }


    private void init(final String [] list) {
//         final int[] mImage = new int[]{
//                R.drawable.ban1, R.drawable.ban2, R.drawable.ban3, R.drawable.ban4, R.drawable.ban5
//        };
        carouselView = findViewById(R.id.carousel);

        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
//                imageView.setImageResource(mImage[position]);
                Picasso.with(getApplicationContext()).load(list[position]).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        });
        carouselView.setPageCount(list.length);
        carouselView.setIndicatorVisibility(View.GONE);


        carouselView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        Log.w("touched","down");
                        carouselView.stopCarousel();
                        return true;
                    //break;

                    case MotionEvent.ACTION_UP:
                        Log.w("touched","up");
                        carouselView.playCarousel();
                        return true;
                    //break;
                }

                return false;
            }
        });
    }

    private void hideItem() {
        Menu nav_Menu = navigationView.getMenu();

        if(LogIN.equals("1")){
            nav_Menu.findItem(R.id.login).setVisible(false);
            getData();
            getSliderImages();
//            init();
        }
        else{
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.profile).setVisible(false);
        }
    }

    private void getSliderImages() {

        StringRequest  stringRequest = new StringRequest(Request.Method.POST, sliderUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                banners = new String[jsonArray.length()];
                                for(int i=0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String upper = jsonObject1.getString("BannerPath");
                                    int upper_bound = upper.length();

                                    String ImageUrl = "";
                                    for(int k = 1; k<upper_bound;k++){
                                        ImageUrl+=Character.toString(upper.charAt(k));
                                    }
                                    banners[i] = url1+ImageUrl;
//                                    banners.add(new Banner(url1+ImageUrl));
                                }
                                init(banners);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
                }
            }
        });

    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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
                editor.putString("ToCart","Dashboard");
                editor.commit();

                Intent i = new Intent(Dashboard.this,Cart.class);
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_cart) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.login) {

            if(LogIN.equals("1")){
                Toast.makeText(this, "Already Login", Toast.LENGTH_SHORT).show();
            }

            else{
                Intent i =new Intent(Dashboard.this,Login.class);
                startActivity(i);
            }

        }  else if (id == R.id.profile) {
           /* SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("FromCart","0");
            editor.commit();*/


            Intent i =new Intent(getApplicationContext(),Profile.class);
            startActivity(i);
        }
        else if(id == R.id.location){
            startActivity(new Intent(getApplicationContext(), LocationSelect.class));
        }

        else if(id == R.id.contact){
            startActivity(new Intent(getApplicationContext(), ContactUs.class));
        }
        else if(id == R.id.refer)
        {
            Intent i = new Intent(getApplicationContext(), Refer.class);
            startActivity(i);
        }

        else if(id == R.id.faq){
            startActivity(new Intent(getApplicationContext(), FAQ.class));
        }

        else if(id == R.id.notificationHistory){
            startActivity(new Intent(getApplicationContext(), NotificationHistory.class));
        }

        else if(id == R.id.feedcomp){
            startActivity(new Intent(getApplicationContext(), FeedComp.class));
        }
        else if(id == R.id.aboutus){
            startActivity(new Intent(getApplicationContext(), AboutUS.class));
        }
        else if(id == R.id.wishlist){


            startActivity(new Intent(getApplicationContext(), Wishlist.class));
        }

        else if(id == R.id.chat){
            String url = "https://api.whatsapp.com/send?phone=+919222277702";
            //919222277702
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        else if (id == R.id.logout) {
            if(LogIN.equals("1")){

                SharedPreferences pref=getApplicationContext().getSharedPreferences("MyPref",0);
             //   Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                SharedPreferences pref1=getApplicationContext().getSharedPreferences("MyLoc",0);
                SharedPreferences.Editor editor1 = pref1.edit();
                editor1.clear();
                editor1.commit();

                Intent i=new Intent(getApplicationContext(),Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                Toast.makeText(getApplicationContext(), "Logout Successfully", Toast.LENGTH_LONG).show();
            }

            else{
                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendToDatabase(final String token, final String UID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, token_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Do nothing
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
//                                Toast.makeText(getApplicationContext(), "Sent to database successfully", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Token",token);
                params.put("UID", UID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void getData()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, dash_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                               JSONArray jsonArray = jsonObject.getJSONArray("result");

                            //    saveJSONArray(jsonArray);
                           //     Log.e("jsonda", var);
                               // Log.e("ash", ls.toString());
                           //     Log.e("tikona", type.toString());
//                                Set<String> primesWithoutDuplicates = new LinkedHashSet<String>(type);
//                                type.clear();
//                                type.addAll(primesWithoutDuplicates);
//                                Log.e("tikona", String.valueOf(type.size()));

                                if(models!=null){
                                    models.clear();
                                }

                                for(int i = 0; i< jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    if(jsonObject1.getString("Type").equals("multi")){

                                                ArrayList<DashProductModel> prodsList =new ArrayList<DashProductModel>();

                                                String header = jsonObject1.getString("DATA");
                                                String ncid = jsonObject1.getString("NCID");
                                                //Fetching the products
                                                JSONArray jsonArray1 = jsonObject1.getJSONArray("Prds");

                                               Log.e("len", String.valueOf(jsonArray1.length())) ;
                                               //If there are more than 3 products

                                                   for(int m = 0; m<jsonArray1.length(); m++){

                                                       JSONObject jsonObject2 = jsonArray1.getJSONObject(m);
                                                       String title = jsonObject2.getString("Product_Name");
                                                       String desc = jsonObject2.getString("Description");
                                                       String imid = jsonObject2.getString("IMID");
                                                       String isExpress= "0";
                                                       if(jsonObject2.getString("isExpress").equals("1")){
                                                             isExpress = "1";
                                                       }
                                                       String inStock = jsonObject2.getString("InStock");

                                                       String upper = jsonObject2.getString("Banner");
                                                       int upper_bound = upper.length();

                                                       String ImageUrl = "";
                                                       for(int k = 1; k<upper_bound;k++){
                                                           ImageUrl+=Character.toString(upper.charAt(k));
                                                       }
                                                       String label = jsonObject2.getString("Title");
                                                       Float amount = Float.parseFloat(jsonObject2.getString("Amount"));
                                                       Float discount =  Float.parseFloat(jsonObject2.getString("SMDiscount"));
                                                       String type = jsonObject2.getString("SMDisType");

                                                       if(type.equals("percentage") || type.equals("percent"))
                                                       {
                                                           if(discount==0 || discount==0.0)
                                                           {
                                                               String price = label+" "+"@"+"\u20B9"+String.valueOf(amount);
                                                               prodsList.add(new DashProductModel(title, imid, url1+ImageUrl, desc, price, isExpress,
                                                                       inStock));
                                                           }
                                                           else
                                                           {//Discount
                                                               float final_amount = amount*(discount/100);
                                                               float price = amount-final_amount;
                                                               String _price = label+" "+"@"+"\u20B9"+String.valueOf(price);
                                                               prodsList.add(new DashProductModel(title, imid, url1+ImageUrl, desc,_price, isExpress,
                                                                       inStock));
                                                           }
                                                       }

                                                       else if(type.equals("price"))
                                                       {//Discount
                                                           float price = amount - discount;
                                                           String _price = label+" "+"@"+"\u20B9"+String.valueOf(price);
                                                           prodsList.add(new DashProductModel(title, imid, url1+ImageUrl, desc,_price, isExpress, inStock));
                                                       }
                                                       else
                                                       {
                                                           String price = label+" "+"@"+"\u20B9"+String.valueOf(amount);
                                                           prodsList.add(new DashProductModel(title, imid, url1+ImageUrl,desc, price,
                                                                   isExpress, inStock));
                                                       }
                                                   }

                                                models.add(new Model(header,prodsList,2, ncid));
                                    }

                                    else if(jsonObject1.getString("Type").equals("text")){

//                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                String id = jsonObject1.getString("MYID");
                                                String linktype = jsonObject1.getString("LinkType");
                                                String cid = jsonObject1.getString("CID");

                                                models.add(new Model(jsonObject1.getString("DATA"), 0, id, linktype, cid));
                                    }

                                    else if(jsonObject1.getString("Type").equals("img")){

                                                String id = jsonObject1.getString("MYID");
                                                String linktype = jsonObject1.getString("LinkType");

                                                String upper = jsonObject1.getString("DATA");
                                                int upper_bound = upper.length();

                                                String ImageUrl = "";
                                                for(int k = 1; k<upper_bound;k++){
                                                    ImageUrl+=Character.toString(upper.charAt(k));
                                                }

                                                String cid = jsonObject1.getString("CID");

                                                models.add(new Model(1, url1+ImageUrl, id, linktype, cid));
                                    }

                                }//Closed for loop

                                multiViewTypeAdapter.notifyDataSetChanged();

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

                AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
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
        });

RequestQueue  requestQueue = Volley.newRequestQueue(this);
requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

//                            if(entry.getString("Title").equals(""))
//                            {
//
//                            }
//                            else if(!entry.getString("Title").equals(""))
//                                {
                                    Model.setTitle(entry.getString("Title"));
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
//                                }


                            detailModels.add(Model);
                        }
                        detailsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                    else if(msg.equals("Error"))
                    {
                           /* recyclerView.setVisibility(View.GONE);
                            textView.setText("No products available in your area.");
                            textView.setVisibility(View.VISIBLE);*/
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
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
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


//    @Override
//    public void itemClick(View v, String title, String imid) {
//        //Send title
//            showPopUp(title, imid, area_id, detail_url);
//    }


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
                        reason = jsonObject.getString("reason");
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

                        else if(reason.equals("outofstock"))
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
                        Toast.makeText(Dashboard.this, ""+result, Toast.LENGTH_SHORT).show();
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


    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            // Add top margin only for the first item to avoid double space between items
           /* if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }*/
        }
    }

    public static class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }


    private void showPopUpIfNotified(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.PopUp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Do nothing
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String image = jsonObject.getString("ContentBanner");
                            String title = jsonObject.getString("ContentTitle");

                            if(image.equals("") && title.equals("Not Assign")){
                                //Do Nothing
//                                Toast.makeText(getApplicationContext(), "Nothing", Toast.LENGTH_SHORT).show();
                            }
                            else if(!image.equals("") && !title.equals("Not Assign")){

                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.popup, null);
                                final ImageView dialogImage = dialogView.findViewById(R.id.DialogImage);
                                TextView text = dialogView.findViewById(R.id.title);
                                text.setVisibility(View.VISIBLE);

                                Glide.with(dialogView).load(AppConstant.WebURL+image).into(dialogImage);
                                text.setText(title);
                                new AlertDialog.Builder(Dashboard.this)
                                        .setTitle("NewsFeed")
                                        .setView(dialogView)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        }).create().show();

                            }
                            else if(!image.equals("")){

                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.popup, null);
                                final ImageView dialogImage = dialogView.findViewById(R.id.DialogImage);

                                Glide.with(dialogView).load(AppConstant.WebURL+image).into(dialogImage);
                                new AlertDialog.Builder(Dashboard.this)
                                        .setTitle("NewsFeed")
                                        .setView(dialogView)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        }).create().show();

                            }
                            else if(!title.equals("Not Assign")){

                                new AlertDialog.Builder(Dashboard.this)
                                        .setTitle("NewsFeed")
                                        .setMessage(title)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        }).create().show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}


