package com.das.tirtha.sensordatacollector;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class active_projects extends Fragment {
    private Button stopProject1,stopProject2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Active Projects");
        View view= inflater.inflate(R.layout.fragment_active_projects,container,false);

        // bind views
        stopProject1=view.findViewById(R.id.stop_project_1);
        stopProject2=view.findViewById(R.id.stop_project_2);


        // onclick listeners
        stopProject1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceHelper serviceHelper2=new ServiceHelper("5d4b33931a55de05b5352ddd",(getActivity()));
                serviceHelper2.stopServices();

            }
        });

        stopProject2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceHelper serviceHelper2=new ServiceHelper("5d4c41221a55de05b5352dde",(getActivity()));
                serviceHelper2.stopServices();
            }
        });
        return  view;
    }
}
