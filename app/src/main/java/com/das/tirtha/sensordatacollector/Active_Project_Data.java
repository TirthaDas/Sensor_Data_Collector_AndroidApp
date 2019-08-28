package com.das.tirtha.sensordatacollector;

import java.util.ArrayList;

public class Active_Project_Data {
    String projectTitle;
    String Description;
    String id;
    String  sensorList;
    String Userid;
    String ProjectId;
    boolean isCurrentlyActive;
    boolean hasQuestions;
    String duration;
    public Active_Project_Data(String id,String projectTitle, String Description, String sensorList, String Userid,String ProjectId,boolean isCurrentlyActive,boolean hasQuestions,String duration)
    {
        this.projectTitle = projectTitle;
        this.Description = Description;
        this.sensorList=sensorList;
        this.id = id;
        this.Userid=Userid;
        this.ProjectId=ProjectId;
        this.isCurrentlyActive=isCurrentlyActive;
        this.hasQuestions=hasQuestions;
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
    public String getDuration()
    {
        return duration;
    }

    public String  getSensorList(){
        return sensorList;
    }
    public String getId(){
        return id;
    }
    public  String getUserId(){
        return Userid;
    }
    public String getProjectId(){
        return ProjectId;
    }
    public boolean getProjectIsCurrentlyActive(){
        return isCurrentlyActive;
    }
    public boolean gethasQuestions(){
        return hasQuestions;
    }
}
