package com.das.tirtha.sensordatacollector;

import android.content.Intent;

import java.io.Serializable;

public class Sensors implements Serializable {
    private String Name;
    private String Vendor;
    private Integer type;
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return Name;
    }

    public String getVendor() {
        return Vendor;
    }

    public Integer getType() {
        return type;
    }

    public void setVendor(String vendor) {
        this.Vendor = vendor;
    }

    public void setName(String name) {
        this.Name = name;
    }
    public void setType(Integer type) {
        this.type = type;
    }



}
