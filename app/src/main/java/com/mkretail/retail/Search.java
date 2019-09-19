package com.mkretail.retail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkretail.retail.Models.SearchModel;
import com.mkretail.retail.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    SearchAdapter searchAdapter;
    EditText editText;
    BottomNavigationView bottomNavigationView;

    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    private String url = AppConstant.TokreeSearch;
    List<SearchModel> searchModels = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        editText = findViewById(R.id.inputSearch);
        recyclerView = findViewById(R.id.list_view);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_home:
                        startActivity(new Intent(Search.this,Dashboard.class));
                        break;

                    case R.id.navigation_category:
                         startActivity(new Intent(Search.this,Cat.class));
                        break;

                    case R.id.navigation_search:
                        break;

                    case R.id.navigation_orders:
                        startActivity(new Intent(Search.this,Orders.class));
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

        progressDialog = new ProgressDialog(this);
        searchAdapter = new SearchAdapter(this, searchModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);

        recyclerView.setVisibility(View.GONE);

        getAllProductName();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
              //  filter(searchModels, String.valueOf(s));
                if(s.equals("")){
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    filter(s.toString());
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
    }

    public void filter(String text){
        List<SearchModel> temp = new ArrayList();
        for(SearchModel d: searchModels){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getText().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        searchAdapter.updateList(temp);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Dashboard.class));
    }

    private void getAllProductName() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for(int i = 0;i<jsonArray.length();i++)
                                {
                                    SearchModel model = new SearchModel();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    model.setText(jsonObject1.getString("Product_Name"));

                                    searchModels.add(model);
                                }


                            }
                            else
                                {

                                }
                            searchAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Search.this, ""+error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>
    {
        private Context context;
        private List<SearchModel> searchModels;

        public SearchAdapter(Context context, List<SearchModel> searchModels) {
            this.context = context;
            this.searchModels = searchModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
                final SearchModel searchModel = searchModels.get(i);
                viewHolder.textView.setText(searchModel.getText());

                viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),SearchResults.class);
                        intent.putExtra("text",searchModel.getText());
                        startActivity(intent);
                    }
                });
        }

        @Override
        public int getItemCount() {
            return searchModels.size();
        }

        public void updateList(List<SearchModel> list){
            this.searchModels = list;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView textView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.product_name);
            }
        }
    }


}
