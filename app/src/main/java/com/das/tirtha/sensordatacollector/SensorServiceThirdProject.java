package com.das.tirtha.sensordatacollector;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.das.tirtha.sensordatacollector.NotificaltionChannel.Channel3Id;
import static com.das.tirtha.sensordatacollector.NotificaltionChannel.ChannelId;


public class SensorServiceThirdProject extends Service {
    private SensorManager sensorManager;
    private CountDownTimer countDownTimer;
    private Handler mainHandler = new Handler();
    Sensor sensor1, accelerometer, gyroscope, light, magnetic, gravity, temperature, proximity, gameRotationVector;
    public static final String TAG = "Background Service";
    public static final String Userid = "";
    private SharedPreferences sp;
    private boolean listenT0Aaccelerometer, listenToGyroscope, listenToLight, listenToMagnetic, listenToGravity, listenToTemperature, listenToProximity, listenToGameRotationVector;
    private RequestQueue requestQueue;
    private int mStatusCode;
    public volatile boolean stopRunnig1;
    int startId;


    @Override
    public void onCreate() {
        super.onCreate();

        // define the sensor manager and all the required sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        gameRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);


        //get the shared preference
        sp = getSharedPreferences("login", MODE_PRIVATE);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId=startId;
        String input = intent.getStringExtra("sensors");
        String projectId = intent.getStringExtra("projectId");
        String duration = intent.getStringExtra("duration");
        int durationInNumbers=30000;
        if(duration.equals("immediately")){
            durationInNumbers=30000;
        }
        if(duration.equals("twice a day")){
            durationInNumbers=43200000;
        }
        if(duration.equals("once a day")){
            durationInNumbers=86400000;

        }
        if(duration.equals("once a week")){
            durationInNumbers=604800000;
        }

        String[] Sensors = input.split("\\r?\\n");
        listenToGameRotationVector = false;
        listenToProximity = false;
        listenToGravity = false;
        listenToLight = false;
        listenToTemperature = false;
        listenToMagnetic = false;
        listenToGyroscope = false;
        listenT0Aaccelerometer = false;
        // set which sensors to listen to as true.
        Log.d(TAG, "PROJECTID====================================>>>>>>" + projectId);
        for (int x = 0; x < Sensors.length; x++) {
            Log.d(TAG, "SENSOR LIST====================================================>>>>>>>>>> " + Sensors[x]);
            switch (Sensors[x]) {
                case "accelerometer":
                    listenT0Aaccelerometer = true;
                    break;
                case "gyroscope":
                    listenToGyroscope = true;
                    break;
                case "magnetic_field":
                    listenToMagnetic = true;
                    break;
                case "ambient_temperature":
                    listenToTemperature = true;
                    break;
                case "light":
                    listenToLight = true;
                    break;
                case "gravity":
                    listenToGravity = true;
                    break;
                case "proximity":
                    listenToProximity = true;
                    break;
                case "game_rotation_vector":
                    listenToGameRotationVector = true;
                    break;
                default:
                    showToast("Sensor" + Sensors[x] + " not available in the app yet");
            }
        }

        Log.d(TAG, "ALL THAT ARE TRUE: " + listenT0Aaccelerometer + listenToGyroscope + listenToLight + listenToMagnetic + listenToGravity + listenToTemperature + listenToProximity + listenToGameRotationVector);

        // register broadcast receiver here
        IntentFilter intentFilter=new IntentFilter("com.das.tirtha.sensordatacollectorThirdservice");
        registerReceiver(broadcastReceiver3,intentFilter);
        // start notification here.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, Channel3Id)
                .setContentTitle("Sensor Data Collector is ON")
                .setContentText("This app is collecting 3rd project")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(3, notification);

        registerUserToProject(sp.getString("UserId", "noUsr"), projectId);

        // do work here on a separate thread
        startThread(sensorManager, sensor1, accelerometer, gyroscope, light, magnetic, gravity, temperature, proximity, gameRotationVector, projectId,durationInNumbers);


        return START_NOT_STICKY;
    }

    public void startThread(SensorManager sensorManager, Sensor sensor1, Sensor accelerometer, Sensor gyroscope, Sensor light, Sensor magnetic, Sensor gravity, Sensor temperature, Sensor proximity, Sensor gameRotationVector, String projectId,int durationInNumbers) {

        if (listenToProximity) {
            SensorRunnable ProximityRunnable = new SensorRunnable(sensorManager, proximity, projectId,durationInNumbers);
            Thread a = new Thread(ProximityRunnable);
            a.start();

        }
        if (listenToMagnetic) {

            SensorRunnable MagneticRunnable = new SensorRunnable(sensorManager, magnetic, projectId,durationInNumbers);
            new Thread(MagneticRunnable).start();
        }
        if (listenToTemperature) {
            SensorRunnable TemperatureRunnable = new SensorRunnable(sensorManager, temperature, projectId,durationInNumbers);
            new Thread(TemperatureRunnable).start();

        }
        if (listenToLight) {

            SensorRunnable LightRunnable = new SensorRunnable(sensorManager, light, projectId,durationInNumbers);
            new Thread(LightRunnable).start();

        }
        if (listenToGameRotationVector) {

            SensorRunnable GameRotationVectorRunnable = new SensorRunnable(sensorManager, gameRotationVector, projectId,durationInNumbers);
            new Thread(GameRotationVectorRunnable).start();

        }
        if (listenT0Aaccelerometer) {
            SensorRunnable AccelerometerRunnable = new SensorRunnable(sensorManager, accelerometer, projectId,durationInNumbers);
            Thread thread = new Thread(AccelerometerRunnable);
            thread.start();
            Log.d(TAG, "^^^^^^^^^^^^^^^THREAD COUNT99999999999999^^^^^^^^^^^^^^^: " + Thread.activeCount());
        }
        if (listenToGyroscope) {
            SensorRunnable GyroscopeRunnable = new SensorRunnable(sensorManager, gyroscope, projectId,durationInNumbers);
            new Thread(GyroscopeRunnable).start();

        }
        if (listenToGravity) {
            SensorRunnable GravityRunnable = new SensorRunnable(sensorManager, gravity, projectId,durationInNumbers);
            new Thread(GravityRunnable).start();

        }
        Log.d(TAG, "^^^^^^^^^^^^^^^THREAD COUNT^^^^^^^^^^^^^^^: " + Thread.activeCount());
    }

    public void stopThread() {

    }

    @Override
    public void onDestroy() {
        stopRunnig1 = true;
        stopThread();
        try{
            unregisterReceiver(broadcastReceiver3);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
        stopSelf(startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //Thread for background work
    class SensorRunnable implements Runnable, SensorEventListener {

        private Handler sensorThreadHandler;
        private Looper sensorThreadLooper;
        private SensorManager sensorManager;
        private boolean dataUploaded = false;
        private boolean mIsSensorUpdateEnabled = false;
        Sensor Sensor123;
        String projectId;
        public final static String APP_PATH_SD_CARD = "/SensorDataCollector";
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
        FileOutputStream fOut;
        private FileWriter writer, accelerometerWriter, gyroscopeWriter, temperatureWriter, magneticWriter, lightWriter, gravityWriter, proximityWriter, gameRotationVectorWriter;
        File sensorFile;
        private volatile boolean exit;
        int durationInNumbers;


        SensorRunnable(SensorManager sensorManager, Sensor sensor, String projectId,int durationInNumbers) {

            Log.d(TAG, "SensorRunnable: " + " INITIALIZING SENSOR SERVICES");
            this.sensorManager = sensorManager;
            this.projectId = projectId;
            this.Sensor123 = sensor;
            this.exit = false;
            this.durationInNumbers=durationInNumbers;
        }

        @Override
        public void run() {
//            Looper.prepare();
//            sensorThreadLooper = Looper.myLooper();
//            sensorThreadHandler = new Handler();
            if(!stopRunnig1) {
                String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
                Log.d(TAG, "run: dateeee" + date);

                try {
                    File dir = new File(fullPath);
                    sensorFile = new File(dir, Sensor123.getStringType().substring(Sensor123.getStringType().lastIndexOf('.') + 1) + date + ".csv");

                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } catch (Exception e) {
                    Log.w("creating file error", e.toString());
                }
                startListeningToSensor(Sensor123, projectId,durationInNumbers);

//            Log.d(TAG, "SensorRunnable: " + " Registered SENSOR listener");
//
//            Log.d(TAG, "^^^^^^^^^^^^^^^THREAD COUNT1111111111^^^^^^^^^^^^^^^: " + Thread.activeCount());
//            Looper.loop();
            }

        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (!mIsSensorUpdateEnabled) {
                stopListeningToSensor();
                return;
            }
            String SensorType = sensorEvent.sensor.getStringType().substring(sensorEvent.sensor.getStringType().lastIndexOf('.') + 1);
            switch (SensorType) {
                case "accelerometer":
                    long accelerometerTimeInMillis = (new Date()).getTime() + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                    try {
                        accelerometerWriter = new FileWriter(sensorFile, true);
                        accelerometerWriter.write("Acceleration force along the x axis : " + sensorEvent.values[0] + "," + "Acceleration force along the y axis : " + sensorEvent.values[1] + "," + "Acceleration force along the z axis : " + sensorEvent.values[2] + "," + "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + accelerometerTimeInMillis + "\n");
                        accelerometerWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, "onSensorChanged: IO error", e);
                        e.printStackTrace();
                    }

                    break;
                case "gyroscope":
                    long gyroscopeTimeInMillis = (new Date()).getTime() + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                    try {
                        gyroscopeWriter = new FileWriter(sensorFile, true);
                        gyroscopeWriter.write("  Rate of rotation around the x axis :   " + sensorEvent.values[0] + "," + "  Rate of rotation around the y axis: " + sensorEvent.values[1] + "," + "  Rate of rotation around the z axis :   " + sensorEvent.values[2] + "," + "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + gyroscopeTimeInMillis + "\n");
                        gyroscopeWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, "onSensorChanged: IO error", e);
                        e.printStackTrace();
                    }

                    break;
                case "magnetic_field":
                    long magnetic_fieldTimeInMillis = (new Date()).getTime() + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                    try {
                        magneticWriter = new FileWriter(sensorFile, true);
                        magneticWriter.write("Geomagnetic field strength along the  x axis :  " + sensorEvent.values[0] + "," + "  Geomagnetic field strength along the  y axis: " + sensorEvent.values[1] + "," + "  Geomagnetic field strength along the  z axis :   " + sensorEvent.values[2] + "," + "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + magnetic_fieldTimeInMillis + "\n");
                        magneticWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, "onSensorChanged: IO error", e);
                        e.printStackTrace();
                    }

                    break;
                case "ambient_temperature":
                    long ambient_temperatureTimeInMillis = (new Date()).getTime() + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                    try {
                        temperatureWriter = new FileWriter(sensorFile, true);
                        temperatureWriter.write("Ambient air temperature. :  " + sensorEvent.values[0] + "," + "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + ambient_temperatureTimeInMillis + "\n");
                        temperatureWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, "onSensorChanged: IO error", e);
                        e.printStackTrace();
                    }

                    break;
                case "light":
                    long lightTimeInMillis = (new Date()).getTime() + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                    try {
                        lightWriter = new FileWriter(sensorFile, true);
                        lightWriter.write("luminance :  " + sensorEvent.values[0] + "," + "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + lightTimeInMillis + "\n");
                        lightWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, "onSensorChanged: IO error", e);
                        e.printStackTrace();
                    }

                    break;
                case "gravity":
                    long gravityTimeInMillis = (new Date()).getTime() + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                    try {
                        gravityWriter = new FileWriter(sensorFile, true);
                        gravityWriter.write("Force of gravity along the x axis :  " + sensorEvent.values[0] + "," + "  Force of gravity along the y axis " + sensorEvent.values[1] + "," + "  Force of gravity along the z axis :   " + sensorEvent.values[2] + "," + "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + gravityTimeInMillis + "\n");
                        gravityWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, "onSensorChanged: IO error", e);
                        e.printStackTrace();
                    }


                    break;
                case "proximity":
                    long proximityTimeInMillis = (new Date()).getTime() + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                    try {
                        proximityWriter = new FileWriter(sensorFile, true);
                        proximityWriter.write("Distance from object :  " + sensorEvent.values[0] + "," + "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + proximityTimeInMillis + "\n");
                        proximityWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, "onSensorChanged: IO error", e);
                        e.printStackTrace();
                    }
                    break;
                case "game_rotation_vector":
                    long game_rotation_vectorTimeInMillis = (new Date()).getTime() + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                    try {
                        gameRotationVectorWriter = new FileWriter(sensorFile, true);
                        gameRotationVectorWriter.write("Rotation vector component along the x axis: " + sensorEvent.values[0] + "," +
                                "Rotation vector component along the y axis: " + sensorEvent.values[1] + "," + "Rotation vector component along the z axis: " + sensorEvent.values[2] + "," +
                                "TimeStamp:" + sensorEvent.timestamp + "," + "TimeStamp In Milliseconds" + game_rotation_vectorTimeInMillis + "\n");
                        gameRotationVectorWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, "onSensorChanged: IO error", e);
                        e.printStackTrace();
                    }
                    break;
                default:
                    showToast("Sensor" + SensorType + " not available in the app yet");

            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        public void startListeningToSensor(Sensor Sensor123, String projectId,int durationInNumbers) {
            sensorManager.registerListener(this, Sensor123, SensorManager.SENSOR_DELAY_FASTEST);
            mIsSensorUpdateEnabled = true;
            startTimer(projectId,durationInNumbers);
        }

        public void stopListeningToSensor() {
            sensorManager.unregisterListener(this);
            mIsSensorUpdateEnabled = false;
        }

        public void startTimer(final String projectId, final int durationInNumbers) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.w("WARN DURATION  3", "run: DURATIONNNNN IN SERVICE THREEE"+durationInNumbers);
                    countDownTimer=new CountDownTimer(durationInNumbers, 1000) {
                        @Override
                        public void onTick(long l) {
//                            Log.d(TAG, "onTick: CLOCK TICK" + l);
                        }

                        @Override
                        public void onFinish() {
                            boolean state=checkNetworkStatus();
                            if(state) {
                            String file = sensorFile.getAbsolutePath();
                            Log.d(TAG, "onFinish: TIMER OVER");
                            stopListeningToSensor();
                            uploadDataToServer(sensorFile, Sensor123, projectId);
                            Log.d(TAG, "onFinish:after upload  ^^^^^^^^^>>>>>>>>>>>>>>><<<<<<<<<<" + Thread.activeCount());
                            }
                            else {
                                countDownTimer.start();
                            }
                        }
                    }.start();

                }
            });

        }

        public void uploadDataToServer(final File sensorFile, final Sensor Sensor123, final String projectId) {
            Log.d(TAG, "uploadDataToServer: PROJECTID" + projectId);

            Thread DataUpload = new Thread(new Runnable() {
                @Override
                public void run() {

                    String filePath = sensorFile.getPath();
                    String content_type = getContentType(filePath);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), sensorFile);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("projectId", projectId)
                            .addFormDataPart("type", content_type)
                            .addFormDataPart("userId", sp.getString("UserId", "noUsr"))
                            .addFormDataPart("SensorData", filePath.substring(filePath.lastIndexOf("/") + 1), file_body)
                            .build();
                    String url = getResources().getString(R.string.IP) + "api/uploadSensorData";
                    Request request = new Request.Builder().url(url)
                            .post(requestBody)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful()) {

                            throw new IOException("error" + response);


                        } else {
                            // do something here
                            Log.d(TAG, "data uploaded succesfully: ");

                            // delete the file form the device
                            boolean deleted = sensorFile.delete();
                            Log.d(TAG, "is the file deleted or not " + deleted);

                            // check here if the project still exist

                            checkIfProjectStillExist(Sensor123.getStringType().substring(Sensor123.getStringType().lastIndexOf(".")+1),projectId,durationInNumbers);

//                            start the thread again
//                            SensorRunnable sensorRunnable = new SensorRunnable(sensorManager, Sensor123, projectId);
//                            Thread b = new Thread(sensorRunnable);
//                            b.start();
//                            Log.d(TAG, "^^^^^^^^^^^^^^^THREAD COUNT22222222^^^^^^^^^^^^^^^: " + Thread.activeCount());
//                            Intent intent = new Intent("com.das.tirtha.sensordatacollectorThirdservice");
//                            intent.putExtra("com.das.tirtha.sensor3", Sensor123.getStringType().substring(Sensor123.getStringType().lastIndexOf(".") + 1));
//                            intent.putExtra("com.das.tirtha.projectId", projectId);
//                            sendBroadcast(intent);

                        }
                        response.body().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "run: FILE NOT fOUND SERVICE three"+e);

                        checkIfProjectStillExist(Sensor123.getStringType().substring(Sensor123.getStringType().lastIndexOf(".")+1),projectId,durationInNumbers);

                    }
                }
            });
            DataUpload.start();
        }

        private String getContentType(String filePath) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void registerUserToProject(final String userid, final String projectId) {

        String ip = getResources().getString(R.string.IP);
        String url = ip + "api/addUsersToPosts";
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String message = responseObject.getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SensorServiceThirdProject.this, "project update error" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SensorServiceThirdProject.this, "project update error" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userid);
                params.put("projectId", projectId);
                return params;
            }

            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
    public void checkIfProjectStillExist(final String  sensorRcvd, final String prjctid, final int durationInNum){
        String ip = getResources().getString(R.string.IP);
        String url=ip+"api/checkIfProjectExist";
        final String uid=sp.getString("UserId", "noUsr");
        Log.d(TAG, "checkIfProjectStillExist: UIDDDDD########"+uid);
        StringRequest request= new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject=new JSONObject(response);
                            String message=responseObject.getString("message");
                            if(responseObject.has("activeProject")){
                                String activeProjectId=responseObject.getString("activeProject");
                                Log.d(TAG, "onResponse: ACTV PID"+activeProjectId);
                            }

                            Log.d(TAG, "onResponse: IS TH EPROJECT STILL ACTIVE *********"+prjctid+message);
                            if(message.equals("project found")){
                                Intent intent=new Intent("com.das.tirtha.sensordatacollectorThirdservice");
                                intent.putExtra("com.das.tirtha.sensor3",sensorRcvd);
                                intent.putExtra("com.das.tirtha.projectId",prjctid);
                                intent.putExtra("com.das.tirtha.durationInNum",durationInNum);
                                sendBroadcast(intent);
                            }
                            else{
                                Log.d(TAG, "onResponse: the project is deleted");

//                            ServiceHelper helper=new ServiceHelper(prjctid, getApplicationContext());
//                            helper.stopServices(activeProjectId);
                                String ProjectInService0=sp.getString("case0","");
                                String ProjectInService1=sp.getString("case1","");
                                String ProjectInService2=sp.getString("case2","");
                                if (ProjectInService0.equals(prjctid)){
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean("Thread0",false);
                                    editor.apply();
                                }
                                if (ProjectInService1.equals(prjctid)){
                                    SharedPreferences.Editor editor1 = sp.edit();
                                    editor1.putBoolean("Thread1",false);
                                    editor1.apply();
                                }
                                if (ProjectInService2.equals(prjctid)){
                                    SharedPreferences.Editor editor2 = sp.edit();
                                    editor2.putBoolean("Thread2",false);
                                    editor2.apply();
                                }
                                onDestroy();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("projectId",prjctid);
                params.put("userId",uid);

                return params;
            }
            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);



    }

    public BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String recieved = intent.getStringExtra("com.das.tirtha.sensor3");
            String projectId = intent.getStringExtra("com.das.tirtha.projectId");
            int durationNum=intent.getIntExtra("com.das.tirtha.durationInNum",30000);

            if (recieved.equals("accelerometer")) {
                SensorServiceThirdProject.SensorRunnable sensorRunnable = new SensorServiceThirdProject.SensorRunnable(sensorManager, accelerometer, projectId,durationNum);
                Thread b = new Thread(sensorRunnable);
                b.start();
            }
            if (recieved.equals("gyroscope")) {
                SensorServiceThirdProject.SensorRunnable sensorRunnable = new SensorServiceThirdProject.SensorRunnable(sensorManager, gyroscope, projectId,durationNum);
                new Thread(sensorRunnable).start();
            }
            if (recieved.equals("magnetic_field")) {
                SensorServiceThirdProject.SensorRunnable sensorRunnable = new SensorServiceThirdProject.SensorRunnable(sensorManager, magnetic, projectId,durationNum);
                new Thread(sensorRunnable).start();
            }
            if (recieved.equals("ambient_temperature")) {
                SensorServiceThirdProject.SensorRunnable sensorRunnable = new SensorServiceThirdProject.SensorRunnable(sensorManager, temperature, projectId,durationNum);
                new Thread(sensorRunnable).start();
            }
            if (recieved.equals("light")) {
                SensorServiceThirdProject.SensorRunnable sensorRunnable = new SensorServiceThirdProject.SensorRunnable(sensorManager, light, projectId,durationNum);
                new Thread(sensorRunnable).start();
            }
            if (recieved.equals("gravity")) {
                SensorServiceThirdProject.SensorRunnable sensorRunnable = new SensorServiceThirdProject.SensorRunnable(sensorManager, gravity, projectId,durationNum);
                new Thread(sensorRunnable).start();
            }
            if (recieved.equals("proximity")) {
                SensorServiceThirdProject.SensorRunnable sensorRunnable = new SensorServiceThirdProject.SensorRunnable(sensorManager, proximity, projectId,durationNum);
                new Thread(sensorRunnable).start();
            }
            if (recieved.equals("game_rotation_vector")) {
                SensorServiceThirdProject.SensorRunnable sensorRunnable = new SensorServiceThirdProject.SensorRunnable(sensorManager, gameRotationVector, projectId,durationNum);
                new Thread(sensorRunnable).start();
            }
        }
    };
    public boolean checkNetworkStatus(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }

        return connected;
    }
}
