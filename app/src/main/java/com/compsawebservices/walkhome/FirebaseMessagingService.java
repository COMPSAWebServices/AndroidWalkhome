package com.compsawebservices.walkhome;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import com.google.firebase.messaging.RemoteMessage;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Ly on 2016-10-05.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    private int status = 1;
    static StatusTracker st = new StatusTracker();
    static UserProfile userProfile = new UserProfile();
    private String walkID;
    private String walkStatus;

//    StatusActivity statusActivity = new StatusActivity();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



//        statusActivity.updateStatus();




        System.out.println("STATUSssssssssssssssssssss: " + st.getCount());
        Intent intent = new Intent(this, StatusActivity.class);
        intent.putExtra("status", st.getCount());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        sendNotification(remoteMessage.getNotification().getBody());

//        Intent intent2 = ((Activity) getApplicationContext()).getIntent();
//        if(intent2.getExtras()!= null){
//            //do your stuff
//            startActivity(intent);
//            sendNotification(remoteMessage.getNotification().getBody());
//        }else{
//            //do that you normally do
//            startActivity(intent);
//
//        }

    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, StatusActivity.class);
        intent.putExtra("status", st.getCount());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.walkhomelogo2)
                .setContentTitle("Walkhome")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
