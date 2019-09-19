package com.mkretail.retail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkretail.retail.Models.Notification;
import com.mkretail.retail.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationHistory extends AppCompatActivity {

    private ListView list;
    private List<Notification> notifications ;
    private NotificationAdapter adapter;
    private String UID;
    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_history);
        list = findViewById(R.id.notificationList);
        textView = findViewById(R.id.noNoti);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UID","");
        notifications = new ArrayList<>();

        populateListView(UID);

    }


    public void populateListView(final String UID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GetNotifications,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
//                                Notification notification = new Notification();

                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                                    notification.setTitle(jsonObject1.getString("Title"));
//                                    notification.setContent(jsonObject1.getString("Message"));
                                    notifications.add(new Notification(jsonObject1.getString("Title"), jsonObject1.getString("Message")));
                                }
                                adapter = new NotificationAdapter(notifications, NotificationHistory.this);

                                list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            else if(msg.equals("error")){
                                list.setVisibility(View.GONE);
                                textView.setVisibility(View.VISIBLE);
                            }
                            //9222277702
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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

    public class NotificationAdapter extends BaseAdapter{

        private List<Notification> notificationList;
        private Context mContext;

        public NotificationAdapter(List<Notification> notificationList, Context mContext) {
            this.notificationList = notificationList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return notificationList.size();
        }

        @Override
        public Object getItem(int position) {
            return notificationList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mContext, R.layout.notification_item, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView content = (TextView) view.findViewById(R.id.content);

            title.setText(notificationList.get(position).getTitle());
            content.setText(notificationList.get(position).getContent());
            return view;
        }
    }
}
