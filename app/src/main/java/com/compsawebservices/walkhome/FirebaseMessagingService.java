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
    static StatusTracker st = new StatusTracker();
//    StatusActivity statusActivity = new StatusActivity();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



//        statusActivity.updateStatus();


        st.updateCount();
        sendNotification(remoteMessage.getNotification().getBody());
        System.out.println("STATUSssssssssssssssssssss: " + st.getCount());
        Intent intent = new Intent(this, StatusActivity.class);
        intent.putExtra("status", st.getCount());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
