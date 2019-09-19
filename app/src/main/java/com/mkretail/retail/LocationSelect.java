package com.mkretail.retail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkretail.retail.Models.LocationSelectModel;
import com.mkretail.retail.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationSelect extends Activity {

    RecyclerView recyclerView;
    private String url = AppConstant.Area;
    List<LocationSelectModel> locationSelectModels = new ArrayList<>();
    LocationAdapter locationAdapter;
    String area;
    SharedPreferences pref1;
 //   TextView t1, t2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.location_select);
        recyclerView = findViewById(R.id.location_select_recycler);
      //  mLinearLayout = (LinearLayout) findViewById(R.id.lini);

        this.setFinishOnTouchOutside(true);

        //locationSelectModels.add(new LocationSelectModel("Miraroad",1));
       // t1 = findViewById(R.id.location_title);
      //  t2 = findViewById(R.id.location_id);

        locationAdapter = new LocationAdapter(locationSelectModels,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(locationAdapter);

        pref1=getApplicationContext().getSharedPreferences("MyLoc",0);
       // area = pref1.getString("AreaId","");

        getArea();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Do Nothing
        //8928914477
    }

    private void getArea()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.optString("msg").equals("SUCCESS"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                            //    Log.e("Success","Succ");
                                for(int i = 0; i<jsonArray.length(); i++)
                                {
                                    LocationSelectModel locationSelectModel = new LocationSelectModel();
                                    final JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    locationSelectModel.setLocation_name(jsonObject1.getString("AreaName"));
                                    locationSelectModel.setLocation_id(jsonObject1.getString("AreaID"));

                                    locationSelectModels.add(locationSelectModel);
                                }
                                locationAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), " No products available in this area", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }




    public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

        private List<LocationSelectModel> locationSelects;
        private Context context;

        public LocationAdapter(List<LocationSelectModel> locationSelects, Context context) {
            this.locationSelects = locationSelects;
            this.context = context;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.location_item,
                    viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            final LocationSelectModel locationSelect = locationSelects.get(i);

            viewHolder.t1.setText(locationSelect.getLocation_name());
            viewHolder.t2.setText(locationSelect.getLocation_id());

           viewHolder.t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(pref1.getString("AreaId","")!=null){

                        // Log.e("area",pref1.getString("AreaId",""));
                        SharedPreferences.Editor editor1 = pref1.edit();
                        editor1.clear();
                        editor1.commit();
                    }
                        Intent i  = new Intent(context, Dashboard.class);
                        SharedPreferences pref = context.getSharedPreferences("MyLoc", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("AreaId", locationSelect.getLocation_id());
                        editor.commit();
                        context.startActivity(i);
                  //  viewHolder.submit.setBackgroundColor(Color.parseColor("#4caf50"));

                }
            });
        }

        @Override
        public int getItemCount() {
            return locationSelects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView  t1,t2, submit;
           // RadioButton textView;
            //Button submit;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                t1 = itemView.findViewById(R.id.location_title);
                t2 = itemView.findViewById(R.id.location_id);

            }
        }
    }

}
