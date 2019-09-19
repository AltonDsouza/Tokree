package com.mkretail.retail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkretail.retail.Models.Complaint;
import com.mkretail.retail.Models.Replacment;
import com.mkretail.retail.Utils.AppConstant;
import com.kosalgeek.android.photoutil.GalleryPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedComp extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    EditText subject, message, ord_id;
    ListView listView;
    private ReplacementAdapter replacementAdapter;
    private ArrayList<String> imagesEncodedList;
    private ArrayList<String> images_to_pass;
    private ArrayList<String> products;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    TextView mItemSelected;
    private static String [] imid;




    private List<Replacment> lists = new ArrayList<>();
    private String UID, path;
    String imageEncoded;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Complaint> complaints = new ArrayList<>();

    private static final String TAG = "ReplacementActivity";


    Button button, choose, checkOrderID;
    Spinner spinner;
    List<String> spinnerArray =  new ArrayList<String>();
    GalleryPhoto galleryPhoto;
    private ProgressDialog progressDialog;
    private static final int GALLERY_REQ = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_comp);
        fvid();
        spinner.setOnItemSelectedListener(this);
        spinnerArray.add(0, "Choose Categories");
        spinnerArray.add("FeedBack");
        spinnerArray.add("Complaint");
        spinnerArray.add("Replacement");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        //Multiple list selection
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);



        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        UID = pref.getString("UID","");

       button.setOnClickListener(this);
       choose.setOnClickListener(this);
       checkOrderID.setOnClickListener(this);
    }


    public void fvid(){
        subject = (EditText)findViewById(R.id.sub);
        message = (EditText)findViewById(R.id.message);
        button = (Button) findViewById(R.id.subreplace);
//        listView = (ListView) findViewById(R.id.orderitemListView);
        mItemSelected = (TextView)findViewById(R.id.prodsName);
        spinner = (Spinner)findViewById(R.id.selectone);
        ord_id = (EditText)findViewById(R.id.orderid);
        choose = (Button)findViewById(R.id.choose);
        galleryPhoto = new GalleryPhoto(getApplicationContext());
//        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        checkOrderID = (Button)findViewById(R.id.checkOrder);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = spinner.getSelectedItem().toString();
            Log.e("name",name);

            if(name.equals("Replacement")){
                subject.setVisibility(View.GONE);
                message.setVisibility(View.GONE);

//                listView.setVisibility(View.VISIBLE);
                ord_id.setVisibility(View.VISIBLE);
                checkOrderID.setVisibility(View.VISIBLE);
                choose.setVisibility(View.VISIBLE);
                //Enter Order Id
                //Populate the listview
//                linearLayout.setVisibility(View.VISIBLE);
            }
            else if(name.equals("Complaint")){
                ord_id.setVisibility(View.GONE);
                checkOrderID.setVisibility(View.GONE);
                choose.setVisibility(View.GONE);

                subject.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
                subject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getComplaintTitle();
                    }
                });
            }
            else {
//                listView.setVisibility(View.GONE);
                ord_id.setVisibility(View.GONE);
                checkOrderID.setVisibility(View.GONE);
                choose.setVisibility(View.GONE);

                subject.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            //Do nothing
    }

    @Override
    public void onClick(View v) {
        if(v == button){
            //Check if replacement/feedback/complaint
            if(spinner.getSelectedItem().equals("Replacement")){
                if(ord_id.getText().length()==0){
                    Toast.makeText(getApplicationContext(), "First select product by checking order ID", Toast.LENGTH_LONG).show();
                }
                else {
                    submitReplacement(UID, ord_id.getText().toString(), imid, images_to_pass);
                }
            }
            else if(spinner.getSelectedItem().equals("Complaint")){

            }
            //Feedback
            else {
                    //Submit FeedBack
                if(subject.getText().length()==0){
                        subject.setError("Cannot be empty!");
                }
                else if(message.getText().length()<8){
                    message.setError("Message too short!");
                }
                else {
                    submitFeedback(UID, subject.getText().toString(), message.getText().toString());
                }
            }

        }
        else if(v == choose){
//            Intent intent = galleryPhoto.openGalleryIntent();
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(i, GALLERY_REQ);
        }

        else if(v == checkOrderID){
            //Check Order and populate Listview
            populateListView(UID, ord_id.getText().toString());

            //Show alert dialog
        }
    }

    private void submitFeedback(final String UID, final String Subject, final String Message){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.SubmitFeedback,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Do nothing
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                Toast toast = Toast.makeText(getApplicationContext(), "Feedback sent!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                            else if(msg.equals("error")){
//                                Toast.makeText(getApplicationContext(), "Any order with this id does not exist", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
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
                params.put("UID",UID);
                params.put("Subject", Subject);
                params.put("Message", Message);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void populateListView(final String UID, final String orderID) {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.ReplaceOrder,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Do nothing
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                String [] names = new String[jsonArray.length()];
                               imid = new String[jsonArray.length()];
                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String name = jsonObject1.getString("Product_Name");
                                   // lists.add(new Replacment(name));
                                    names[i] = name;
                                    imid[i] = jsonObject1.getString("IMID");
                                }
//                                replacementAdapter = new ReplacementAdapter(FeedComp.this, lists);
//                                listView.setAdapter(replacementAdapter);
                                setUpMultiAlertDialog(names);

                            }

                            else if(msg.equals("error")){
                                Toast.makeText(getApplicationContext(), "Any order with this id does not exist", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
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
                params.put("UID",UID);
                params.put("OrderID", orderID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getComplaintTitle(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.ComplaintTitle,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Do nothing
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                String complaintID = "", title = "";
                                String cid[] = new String[jsonArray.length()];
                                String titles [] = new String[jsonArray.length()];
                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    complaintID = jsonObject1.getString("COTID");
                                    title = jsonObject1.getString("Complaint_Title");
//                                    complaints.add(new Complaint(complaintID, title));
                                    cid[i] = complaintID;
                                    titles[i] = title;
                                }
                                setUpAlertDialog(titles, cid);
                            }

                            else if(msg.equals("error")){
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void submitReplacement(final String UID, final String orderID, final String [] products , final ArrayList<String> images){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.SubmitReplacement,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Do nothing
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if(msg.equals("SUCCESS")){
                                Toast.makeText(getApplicationContext(), "Replacement request submitted!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else if(msg.equals("error")){
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Connection timeout error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "server couldn't find the authenticated request", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server is not responding", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "Your device is not connected to internet", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Parse Error (because of invalid json or xml)", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserID",UID);
                params.put("OrderID", orderID);
                for(int i=0; i<products.length; i++){
                    params.put("Products["+i+"]",products[i]);
                }

                for(int i=0; i<images.size(); i++){
                    params.put("Images["+i+"]",images.get(i));
                }
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

    private void setUpAlertDialog(final String [] titles, final String [] cids){
        //Select the complaint title
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a subject");
        final String[] animals = new String[titles.length];
        for(int i=0; i<titles.length; i++){
            animals[i] = cids[i]+" "+ titles[i];
        }
        int checkedItem = 0;
        if(mUserItems.size()>0){
            mUserItems.clear();
        }

        builder.setSingleChoiceItems(animals, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                // user checked an item
                mUserItems.add(position);
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                    //User clicked ok
                String item = "", cid ="";
//                Toast.makeText(getApplicationContext(), ""+titles[mUserItems.get(position)], Toast.LENGTH_SHORT).show();
//                    item = item + titles[mUserItems.get(position)];
//                    cid = cid + cids[mUserItems.get(position)];
//                        products.add(item);
                subject.setText(item);
            }
        });
        builder.setNegativeButton("cancel", null);
// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setUpMultiAlertDialog(final String [] listItems){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Choose products");
        if(mUserItems.size()>0){
            mUserItems.clear();
        }
        final boolean checkedItems[] = new boolean[listItems.length];
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mUserItems.size(); i++) {
                    item = item + listItems[mUserItems.get(i)];
                    if (i != mUserItems.size() - 1) {
                        products.add(item);
                        item = item + ", ";
                    }
                }
                mItemSelected.setText(item);
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    products.add("");
                    mUserItems.clear();
                    mItemSelected.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      try{
          if (requestCode == GALLERY_REQ && resultCode == RESULT_OK
                  && null != data) {
              // Get the Image from data

              String[] filePathColumn = {MediaStore.Images.Media.DATA};
              imagesEncodedList = new ArrayList<String>();

              if (data.getClipData() != null) {
                  ClipData mClipData = data.getClipData();
                  ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                  for (int i = 0; i < mClipData.getItemCount(); i++) {

                      ClipData.Item item = mClipData.getItemAt(i);
                      Uri uri = item.getUri();
                      mArrayUri.add(uri);

                      // Get the cursor
//                      Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                      // Move to first row
//                      cursor.moveToFirst();
//
//                      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                      imageEncoded = cursor.getString(columnIndex);
//                      imagesEncodedList.add(imageEncoded);
//                      cursor.close();

                  }
                  images_to_pass = getImages(mArrayUri);
//                  Log.e(TAG, images_to_pass.toString());
                  //Send images to database

                  Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                  Toast.makeText(getApplicationContext(), "Photos Selected!", Toast.LENGTH_SHORT).show();
              }
             else {
                  if (data.getData() != null) {

                      Uri mImageUri = data.getData();

                      // Get the cursor
                      Cursor cursor = getContentResolver().query(mImageUri,
                              filePathColumn, null, null, null);
                      // Move to first row
                      cursor.moveToFirst();

                      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                      imageEncoded = cursor.getString(columnIndex);
                      cursor.close();
                  }
              }
          }
          else {
              Toast.makeText(getApplicationContext(), "You haven't picked Image",
                      Toast.LENGTH_LONG).show();
          }

      }catch (Exception ex){
          Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG)
                  .show();
      }
    }

    //Converting list of images to base64 format
    private ArrayList<String> getImages(ArrayList<Uri> imageslist){

        for(int i=0; i<imageslist.size(); i++){
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageslist.get(i));
                path = getStringImage(bitmap);
                list.add(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //Converting image to Base64
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public class ReplacementAdapter extends BaseAdapter{
        private Context context;
        private List<Replacment> replacmentList;

        public ReplacementAdapter(Context context, List<Replacment> replacmentList) {
            this.context = context;
            this.replacmentList = replacmentList;
        }

        @Override
        public int getCount() {
            return replacmentList.size();
        }

        @Override
        public Object getItem(int position) {
            return replacmentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = View.inflate(context, R.layout.order_list, null);
            TextView name = view.findViewById(R.id.product_name);

            name.setText(replacmentList.get(position).getProduct_name());
            return view;
        }
    }
}
