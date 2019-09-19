package com.mkretail.retail;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mkretail.retail.Utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;


public class FirebaseInstanceId extends FirebaseMessagingService {


//    private String url = "http://fortunehealthplus.com/Tokree";
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("fcm", s);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Token",s);
        editor.commit();

        FirebaseMessaging.getInstance().subscribeToTopic("global")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "Subscribed successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        //   sendNotification(remoteMessage.getNotification().getBody());
        Log.e("message", String.valueOf(remoteMessage));
        String title = "", message = "", linktype="", linkid="", listCID = "", imageUrl = "", finalImg = "";
        if(remoteMessage.getData().size()>0)
        {
            Map<String, String> params = remoteMessage.getData();
            try {
               JSONObject object = new JSONObject(params.toString());
                Log.e("JSON_OBJECT", object.toString());
                JSONObject jsonArray = object.getJSONObject("data");
                 message = jsonArray.getString("message");
                title = jsonArray.getString("title");

               JSONObject jsonArray1 = jsonArray.getJSONObject("payload");
               Log.e("arrjson1", jsonArray1.toString());

                String upper = jsonArray.getString("image");
                int upper_bound = upper.length();

                //Since the image url starts with './', removing the dot part.
                for(int k = 1; k<upper_bound;k++){
                    imageUrl+=Character.toString(upper.charAt(k));
                }
                if(jsonArray1.has("cat")){
                    linktype = "cat";
                    linkid = jsonArray1.getString("cat");
                }
                else if(jsonArray1.has("prd")){
                    linktype = "prd";
                    linkid = jsonArray1.getString("prd");
                }
                else if(jsonArray1.has("CID")){
                    listCID = jsonArray1.getString("CID");
                    linktype = "list";
                    linkid = jsonArray1.getString("list");
                }

                if(!imageUrl.isEmpty()){
                    finalImg = AppConstant.WebURL +imageUrl;
                }

               sendNotification(title, message, linktype, linkid, listCID, finalImg);

            } catch (JSONException e) {
                e.printStackTrace();
            }

//          sendNotification(remoteMessage.getNotification().getBody());
            Log.d("remote","Message data payload"+remoteMessage.getData());
        }

        if(remoteMessage.getNotification()!=null)
        {
//            sendNotification(title, message, linktype, linkid, listCID, finalImg);
            Log.d("remoteNotification","Message data payload"+remoteMessage.getNotification().getBody());

        }

    }

    private void sendNotification(String title,String messageBody, String linktype, String id, String listCID, String image) {
        Intent intent = new Intent();
        if(linktype.equals("list")){
             intent = new Intent(this, SubCat.class);
             intent.putExtra("sid", id);
             intent.putExtra("cid", listCID);
        }
        else if(linktype.equals("cat")){
            intent = new Intent(this, Cat.class);
            intent.putExtra("cid", id);
        }
        else if(linktype.equals("prd")){
            intent = new Intent(this, SubCat.class);
            intent.putExtra("p_imid", id);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;

        if(image.equals("")){
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.to)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
        }
        else {
            Bitmap bmp = null;
            try {
                InputStream in = new URL(image).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(bmp).build();

            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.to)
                            .setContentTitle(title)
//                            .setStyle(bigPictureStyle)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp).setBigContentTitle(messageBody))
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent)
                            ;
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
