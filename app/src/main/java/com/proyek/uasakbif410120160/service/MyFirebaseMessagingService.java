package com.proyek.uasakbif410120160.service;

/**
 * NIM : 10120160
 * Nama : Rendi Julianto
 * Kelas : IF-4
 */
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.proyek.uasakbif410120160.MainActivity;
import com.proyek.uasakbif410120160.R;


import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Pesan Notif";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            generateNotification( remoteMessage.getData().get("title"),remoteMessage.getData().get("body"));
        }
        if (remoteMessage.getNotification() != null) {
            generateNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
        Log.d(TAG, String.valueOf(remoteMessage.getNotification()));
        Log.d(TAG, String.valueOf(remoteMessage.getData()));
        super.onMessageReceived(remoteMessage);
    }

    private void generateNotification(String title, String msg) {

        try {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "channel-fbase";
            String channelName = "demoFbase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                int color = 0x008000;
                mBuilder.setColor(color);
            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            }
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));

            mBuilder.setContentTitle(title);
            mBuilder.setContentText(msg);
            mBuilder.setContentIntent(pendingIntent);
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            notificationManager.notify(m,mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(String title, String body) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Gagal mengambil token Firebase Messaging", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        FcmSenderTask senderTask = new FcmSenderTask(token,title, body);
                        senderTask.execute();
                    }
                });
    }



}
