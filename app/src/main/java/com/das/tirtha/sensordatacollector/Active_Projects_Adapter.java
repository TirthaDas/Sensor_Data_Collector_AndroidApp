package com.das.tirtha.sensordatacollector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Active_Projects_Adapter extends RecyclerView.Adapter<Active_Projects_Adapter.activeProjectsViewHolder> {

    private Context context;
    private ArrayList<Active_Project_Data> mprojectList;
    public SharedPreferences sp;

    public Active_Projects_Adapter(Context context, ArrayList<Active_Project_Data> mprojectList){
        this.context=context;
        this.mprojectList=mprojectList;
    }
    @NonNull
    @Override
    public activeProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.active_project_list_item,viewGroup,false);
        return  new Active_Projects_Adapter.activeProjectsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull activeProjectsViewHolder activeProjectsviewHolder, final int i) {
        final Active_Project_Data projectData=mprojectList.get(i);
        String Projecttitle= projectData.getProjectTitle();
        String ProjectDescription= projectData.getDescription();
        final String ProjectId=projectData.getProjectId();
        boolean hasQuestions=projectData.gethasQuestions();
        Log.d("adapter", "onBindViewHolder: P0000Id"+projectData.getId());
        activeProjectsviewHolder.projectTitle.setText(Projecttitle);
        activeProjectsviewHolder.projectDescriptoion.setText(ProjectDescription);
        activeProjectsviewHolder.aSwitch.setChecked(mprojectList.get(i).isCurrentlyActive);

//        activeProjectsviewHolder.itemView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//             Toast.makeText(context, "Item "  +i+ " is clicked.", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context,Project_Description.class);
//                intent.putExtra("projectId",mprojectList.get(i).getId());
//                intent.putExtra("Project_title",mprojectList.get(i).getProjectTitle());
//                intent.putExtra("Project_Description",mprojectList.get(i).getDescription());
//                intent.putExtra("sensorList",mprojectList.get(i).getSensorList());
//
//                context.startActivity(intent);
//
//            }
//        });
        if(hasQuestions){
            Log.d("*********", "onBindViewHolder: "+"it has questions");
            activeProjectsviewHolder.HasMoreQuestions.setVisibility(View.VISIBLE);
        }else {
            activeProjectsviewHolder.HasMoreQuestions.setVisibility(View.GONE);
            Log.d("*********", "onBindViewHolder: "+"it has NO questions");

        }
        activeProjectsviewHolder.HasMoreQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Item "  +i+ " is clicked.", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(context,Questions_Activity.class);
                intent.putExtra("ProjectId",ProjectId);
                context.startActivity(intent);

            }
        });

        activeProjectsviewHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, "Item "  +i+ " is ON.", Toast.LENGTH_SHORT).show();

                    sp = context.getSharedPreferences("login", MODE_PRIVATE);
                    int myIntValue_active_projects = sp.getInt("active_projects", -1);
                    ServiceHelper serviceHelper1 = new ServiceHelper(mprojectList.get(i).getProjectId(),mprojectList.get(i).getSensorList(),myIntValue_active_projects,context);
                    serviceHelper1.startService(true);

                } else {
//                    Toast.makeText(context, "Item "  +i+ " is OFF.", Toast.LENGTH_SHORT).show();
                    ServiceHelper serviceHelper2=new ServiceHelper(mprojectList.get(i).getProjectId(),context);
                    serviceHelper2.stopServices(mprojectList.get(i).getId());

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mprojectList.size();
    }
    public class activeProjectsViewHolder extends RecyclerView.ViewHolder{
        public TextView projectTitle,projectDescriptoion;
        public Button HasMoreQuestions;
        public Switch aSwitch;
        public activeProjectsViewHolder(@NonNull View itemView) {
            super(itemView);
            projectTitle=itemView.findViewById(R.id.activeProject_item_title);
            projectDescriptoion=itemView.findViewById(R.id.activeProject_item_description);
            HasMoreQuestions=itemView.findViewById(R.id.activeProjectAnswerQuestions);
            aSwitch=itemView.findViewById(R.id.activeProjectSwitch);
        }
    }
}
