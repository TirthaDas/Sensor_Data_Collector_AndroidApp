package com.das.tirtha.sensordatacollector;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import static com.das.tirtha.sensordatacollector.NotificaltionChannel.ChannelId;

public class SensorService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("sensors");
        Intent notificationIntent= new Intent(this,projectDetails.class);
        PendingIntent pendingIntent=   PendingIntent.getActivity(this,
                 0,notificationIntent,0);
        Notification notification=new NotificationCompat.Builder(this,ChannelId)
                .setContentTitle("Sensor Data Collector is ON")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification );
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
