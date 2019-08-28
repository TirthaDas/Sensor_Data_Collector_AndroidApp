package com.das.tirtha.sensordatacollector;

import java.util.ArrayList;

public class projectData {

    String projectTitle;
    String Description;
    String id;
    ArrayList<String > sensorList;
    String duration;
    public projectData(String id,String projectTitle, String Description, ArrayList<String> sensorList,String duration)
    {
        this.projectTitle = projectTitle;
        this.Description = Description;
        this.sensorList=sensorList;
        this.id = id;
        this.duration=duration;

    }
    public String getProjectTitle()
    {
        return projectTitle;
    }
    public String getDescription()
    {
        return Description;
    }
    public ArrayList<String> getSensorList(){
        return sensorList;
    }
    public String getId(){
        return id;
    }
    public String getDuration(){
        return duration;
    }
}
