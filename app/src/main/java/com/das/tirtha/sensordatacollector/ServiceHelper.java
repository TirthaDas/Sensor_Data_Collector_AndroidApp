package com.das.tirtha.sensordatacollector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class ServiceHelper {
    // member variables
    private String ProjectId;
    private String SensorList;
    int myIntValue_active_projects;
    private Context context;
    SharedPreferences sp;

    // constructors
    public ServiceHelper(String ProjectId,Context context) {
        this.ProjectId = ProjectId;
        this.context=context;
    }

    public ServiceHelper(String ProjectId, String SensorList, int myIntValue_active_projects,Context context) {
        this.ProjectId = ProjectId;
        this.SensorList = SensorList;
        this.myIntValue_active_projects=myIntValue_active_projects;
        this.context=context;
    }


    //Start service method
    public void startService() {
        sp = context.getSharedPreferences("login", MODE_PRIVATE);
        Log.d("FROM SERVICE HELPER", "ACTIVE PROJECTSSS: " + myIntValue_active_projects);

        //call the relevant service

        switch (myIntValue_active_projects) {
            case 0:
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("active_projects", 1);
                editor.putString("case0",ProjectId);
                editor.apply();
                Intent serviceIntent = new Intent(context, SensorService.class);
                serviceIntent.putExtra("sensors", SensorList);
                serviceIntent.putExtra("projectId", ProjectId);
                context.startService(serviceIntent);
                break;

            case 1:
                SharedPreferences.Editor editor1 = sp.edit();
                editor1.putInt("active_projects", 2);
                editor1.putString("case1",ProjectId);
                editor1.apply();
                Intent serviceIntent2 = new Intent(context, SensorServiceSecondProject.class);
                serviceIntent2.putExtra("sensors", SensorList);
                serviceIntent2.putExtra("projectId", ProjectId);
                context.startService(serviceIntent2);
                break;

            case 2:
                SharedPreferences.Editor editor2 = sp.edit();
                editor2.putInt("active_projects", 3);
                editor2.putString("case2",ProjectId);
                editor2.apply();
                Intent serviceIntent3 = new Intent(context, SensorServiceThirdProject.class);
                serviceIntent3.putExtra("sensors", SensorList);
                serviceIntent3.putExtra("projectId",ProjectId);
                context.startService(serviceIntent3);

                break;
            default:
                showToast("maximum number of projects already running");
        }

        Intent AvailableProjectsIntent = new Intent(context, MainActivity.class);
        context.startActivity(AvailableProjectsIntent);
    }


    public void stopServices(){
        sp = context.getSharedPreferences("login", MODE_PRIVATE);
        String ProjectInService0=sp.getString("case0","");
        String ProjectInService1=sp.getString("case1","");
        String ProjectInService2=sp.getString("case2","");
        if (ProjectInService0.equals(ProjectId)){
            Intent serviceIntent = new Intent(context, SensorService.class);
            context.stopService(serviceIntent);

        }
        if (ProjectInService1.equals(ProjectId)){
            Intent serviceIntent2 = new Intent(context, SensorServiceSecondProject.class);
            context.stopService(serviceIntent2);

        }
        if (ProjectInService2.equals(ProjectId)){
            Intent serviceIntent3 = new Intent(context, SensorServiceThirdProject.class);
            context.stopService(serviceIntent3);

        }



    }

    //  toast maker
    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
