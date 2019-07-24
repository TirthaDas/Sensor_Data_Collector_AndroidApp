package com.das.tirtha.sensordatacollector;

public class projectData {

    String projectTitle;
    String Description;

    public projectData(String projectTitle, String Description)
    {
        this.projectTitle = projectTitle;
        this.Description = Description;

    }
    public String getProjectTitle()
    {
        return projectTitle;
    }
    public String getDescription()
    {
        return Description;
    }
}
