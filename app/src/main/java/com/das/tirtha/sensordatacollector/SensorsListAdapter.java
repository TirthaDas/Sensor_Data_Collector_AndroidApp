package com.das.tirtha.sensordatacollector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.security.PublicKey;
import java.util.ArrayList;

public class SensorsListAdapter extends RecyclerView.Adapter<SensorsListAdapter.SensorViewHolder> {
    private Context context;
    private ArrayList<Sensors> mSensorList;
    public void SensorsListAdapter(Context context,ArrayList<Sensors> mSensorList){
        this.context=context;
        this.mSensorList=mSensorList;
    }
    public void setmSensorList(ArrayList<Sensors> mSensorList) {
        this.mSensorList = new ArrayList<>();
        this.mSensorList = mSensorList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder sensorViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mSensorList.size();
    }


    class SensorViewHolder extends RecyclerView.ViewHolder{

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
