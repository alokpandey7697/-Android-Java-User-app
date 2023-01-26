package com.alok.alok.Notification.SendNotificationPack;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alok.alok.Buyer.LoginActivity;
import com.alok.alok.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.alok.alok.Notification.MainActivity;


import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message,sender;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            title=remoteMessage.getData().get("Title");
            message=remoteMessage.getData().get("Message");
        sender = remoteMessage.getData().get("Sender");



        Log.d("titlee", message);
            Log.d("title", title);

//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(getApplicationContext())
//                        .setSmallIcon(R.drawable.ic_android_black_24dp)
//                        .setContentTitle(title)
//                        .setContentText(message);

            String CHANNEL_ID = "MESSAGE";
            String CHANNEL_NAME = "MESSAGE";

        NotificationManagerCompat manager = NotificationManagerCompat.from(MyFireBaseMessagingService.this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }


        Intent intent = new Intent(this, LoginActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(getRandomNumber(), notification);
    }

    private static  int getRandomNumber() {
        Date dd = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("mmssSS");
        String s = ft.format(dd);
        return Integer.parseInt(s);
    }
}


