package com.das.tirtha.sensordatacollector;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class NotificaltionChannel extends Application {
    public static final String ChannelId="SensorServiceChannel";
    public static final String Channel2Id="SensorServiceChannel2";
    public static final String Channel3Id="SensorServiceChannel3";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("notifify*******", "onCreate: ********************************");
        createNotificationChannels();
    }
    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    ChannelId,
                    "Example Service Channel 1",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setDescription("this is channel 1");

// second channel
            NotificationChannel serviceChannel2 = new NotificationChannel(
                    Channel2Id,
                    "Example Service Channel 2",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel2.setDescription("this is channel 2");


// third channel
            NotificationChannel serviceChannel3 = new NotificationChannel(
                    Channel3Id,
                    "Example Service Channel 3",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel3.setDescription("this is channel 3");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            manager.createNotificationChannel(serviceChannel2);
            manager.createNotificationChannel(serviceChannel3);

        }
    }

}
