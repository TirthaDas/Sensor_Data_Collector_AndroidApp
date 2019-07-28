package com.das.tirtha.sensordatacollector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SensorsListAdapter extends RecyclerView.Adapter<SensorsListAdapter.SensorViewHolder> {
    private Context context;
    private ArrayList<Sensors> mSensorList;

    public  SensorsListAdapter(Context context,ArrayList<Sensors> mSensorList){
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

        View view = LayoutInflater.from(context).inflate(R.layout.sensors_list_item,viewGroup,false);
        return  new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder sensorViewHolder, int i) {

        sensorViewHolder.bind(mSensorList.get(i));
//        Sensors mSensor=;
//        String SensorName=mSensor.getName();

    }

    @Override
    public int getItemCount() {
        return mSensorList.size();
    }


    class SensorViewHolder extends RecyclerView.ViewHolder{
        public TextView sensorName;
        public ImageView sensorSelected;
        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            sensorName=itemView.findViewById(R.id.sensor_item_name);
            sensorSelected=itemView.findViewById(R.id.sensor_item_selected);
            sensorSelected.setVisibility(View.VISIBLE);
        }

        void bind(final Sensors mSensor) {
            sensorSelected.setVisibility(mSensor.isChecked() ? View.VISIBLE : View.GONE);
            sensorName.setText(mSensor.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSensor.setChecked(!mSensor.isChecked());
                    Log.d("checking on click ", "onClick: "+mSensor.getName()+mSensor.isChecked());
                    sensorSelected.setVisibility(mSensor.isChecked() ? View.VISIBLE : View.GONE);
                }
            });
        }
    }
    public ArrayList<Sensors> getAll() {
        return mSensorList;
    }

    public ArrayList<Sensors> getSelected() {
        ArrayList<Sensors> selected = new ArrayList<>();
        for (int i = 0; i < mSensorList.size(); i++) {
            if (mSensorList.get(i).isChecked()) {
                selected.add(mSensorList.get(i));
            }
        }
        return selected;
    }


}
