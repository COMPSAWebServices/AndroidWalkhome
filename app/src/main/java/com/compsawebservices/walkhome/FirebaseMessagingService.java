package com.compsawebservices.walkhome;


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

/**
 * Created by Ly on 2016-10-05.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    private int status = 1;

//    StatusActivity statusActivity = new StatusActivity();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        sendNotification(remoteMessage.getNotification().getBody());
//        statusActivity.updateStatus();

        System.out.println("STATUSssssssssssssssssssss: " + status);
        Intent intent = new Intent(this, StatusActivity.class);
        intent.putExtra("status", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.walkhomelogo2)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
