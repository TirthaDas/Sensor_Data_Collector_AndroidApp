package com.das.tirtha.sensordatacollector;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.das.tirtha.sensordatacollector.NotificaltionChannel.ChannelId;


public class SensorService extends Service {
    private SensorManager sensorManager;
    private Handler mainHandler=new Handler();
    Sensor sensor1;
    public static final String TAG="Background Service";
    public static final String Userid="";
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor1=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sp = getSharedPreferences("login",MODE_PRIVATE);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("sensors");
        String[] Sensors=input.split("\\r?\\n");
        Log.d(TAG, "first sensor: "+Sensors[0]);


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

        // do work here on a separate thread
        startThread(sensorManager,sensor1);



        return START_NOT_STICKY;
    }

    public void startThread(SensorManager sensorManager,Sensor sensor1){
        SensorRunnable sensorRunnable=new SensorRunnable(sensorManager,sensor1);
        new Thread(sensorRunnable).start();
    }

    public void stopThread(){

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


    //Thread for background work
    class SensorRunnable implements Runnable, SensorEventListener {

        private SensorManager sensorManager;
        private boolean dataUploaded=false;
        private  boolean mIsSensorUpdateEnabled = false;
        Sensor sensor1;
        public final static String APP_PATH_SD_CARD = "/SensorDataCollector";
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
        FileOutputStream fOut;
        private FileWriter writer;
        File sensorFile;

        SensorRunnable(SensorManager sensorManager,Sensor sensor){
            Log.d(TAG, "SensorRunnable: "+" INITIALIZING SENSOR SERVICES");
            this.sensor1=sensor;
            this.sensorManager=sensorManager;

        }

        @Override
        public void run() {
            String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
            Log.d(TAG, "run: dateeee"+date);

            try {
                File dir = new File(fullPath);
                 sensorFile = new File(dir, "Accer"+date+".txt");

                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            catch(Exception e){
                Log.w("creating file error", e.toString());
            }
//            sensorManager.registerListener(this,sensor1,SensorManager.SENSOR_DELAY_FASTEST);
            startListeningToSensor();

            Log.d(TAG, "SensorRunnable: "+" Registered SENSOR listener");









        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (!mIsSensorUpdateEnabled) {
                stopListeningToSensor();
                return;
            }
                Log.d(TAG, "onSensorChanged: " + "x value" + sensorEvent.values[0] + "\n" + "y value" + sensorEvent.values[1] + "\n" + "z value" + sensorEvent.values[2]);
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                long timeInMillis = (new Date()).getTime()
                        + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                try {
                    writer = new FileWriter(sensorFile, true);
                } catch (IOException e) {
                    Log.e(TAG, "onSensorChanged: ERRRRRR", e);
                    e.printStackTrace();
                }

                try {
                    writer.write("x_value: " + x + "," + "y_value: " + y + "," + "z_value: " + z + "," + "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + timeInMillis + "\n");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        public void startListeningToSensor(){
            Log.d(TAG, "startListeningToSensor: starting sensor");
            sensorManager.registerListener(this,sensor1,SensorManager.SENSOR_DELAY_FASTEST);
            mIsSensorUpdateEnabled =true;
            startTimer();


        }
        public void stopListeningToSensor(){
            sensorManager.unregisterListener(this);
            mIsSensorUpdateEnabled =false;
        }

        public void startTimer(){
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    new CountDownTimer(30000,1000){

                        @Override
                        public void onTick(long l) {
                            Log.d(TAG, "onTick: CLOCK TICK"+l);

                        }

                        @Override
                        public void onFinish() {
                            String file=sensorFile.getAbsolutePath();
                            Log.d(TAG, "onFinish: TIMER OVER");
                            stopListeningToSensor();
                            uploadDataToServer(sensorFile);

                        }
                    }.start();

                }
            });

        }

        public void  uploadDataToServer(final File sensorFile){
            Thread DataUpload= new Thread(new Runnable() {
                @Override
                public void run() {
                        String filePath=sensorFile.getPath();
                        String content_type=getContentType(filePath);
                    OkHttpClient client=new OkHttpClient();
                    RequestBody file_body=RequestBody.create(MediaType.parse(content_type),sensorFile);
                    RequestBody requestBody=new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type",content_type)
                            .addFormDataPart("userId",sp.getString("UserId","noUsr"))
                            .addFormDataPart("SensorData",filePath.substring(filePath.lastIndexOf("/")+1),file_body)
                            .build();
                    String url=getResources().getString(R.string.IP)+"api/uploadSensorData";
                    Request request = new Request.Builder().url(url)
                            .post(requestBody)
                            .build();

                    try {
                        Response response= client.newCall(request).execute();
                        if(!response.isSuccessful()){

                            throw new IOException("error"+response);
                        }
                        else {
                            // do something here
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            DataUpload.start();
        }

        private String getContentType(String filePath){
            String extension= MimeTypeMap.getFileExtensionFromUrl(filePath);
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
    }
}
