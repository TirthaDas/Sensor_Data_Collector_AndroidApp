package com.das.tirtha.sensordatacollector;

import java.util.ArrayList;

public class Active_Project_Data {
    String projectTitle;
    String Description;
    String id;
    String  sensorList;
    String Userid;
    public Active_Project_Data(String id,String projectTitle, String Description, String sensorList, String Userid)
    {
        this.projectTitle = projectTitle;
        this.Description = Description;
        this.sensorList=sensorList;
        this.id = id;
        this.Userid=Userid;

    }
    public String getProjectTitle()
    {
        return projectTitle;
    }
    public String getDescription()
    {
        return Description;
    }
    public String  getSensorList(){
        return sensorList;
    }
    public String getId(){
        return id;
    }
    public  String getUserid(){
        return Userid;
    }
}
