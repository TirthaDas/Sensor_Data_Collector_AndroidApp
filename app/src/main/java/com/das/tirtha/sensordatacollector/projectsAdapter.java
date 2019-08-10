package com.das.tirtha.sensordatacollector;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class projectsAdapter extends RecyclerView.Adapter<projectsAdapter.projectsViewHolder>{
    private Context context;
    private ArrayList<projectData> mprojectList;

    public projectsAdapter(Context context,ArrayList<projectData> mprojectList){
        this.context=context;
        this.mprojectList=mprojectList;
    }

    @NonNull
    @Override
    public projectsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
         View view = LayoutInflater.from(context).inflate(R.layout.project_list_item,viewGroup,false);
         return  new projectsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull projectsViewHolder projectsViewHolder, final int i) {
        final projectData projectData=mprojectList.get(i);
        String Projecttitle= projectData.getProjectTitle();
        String ProjectDescription= projectData.getDescription();
        Log.d("adapter", "onBindViewHolder: P0000Id"+projectData.getId());
        projectsViewHolder.projectTitle.setText(Projecttitle);
        projectsViewHolder.projectDescriptoion.setText(ProjectDescription);

        projectsViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Toast.makeText(context, "Item "  +i+ " is clicked.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,Project_Description.class);
                intent.putExtra("projectId",mprojectList.get(i).getId());
                intent.putExtra("Project_title",mprojectList.get(i).getProjectTitle());
                intent.putExtra("Project_Description",mprojectList.get(i).getDescription());
                intent.putExtra("sensorList",mprojectList.get(i).getSensorList());

                context.startActivity(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return mprojectList.size();
    }

    public class projectsViewHolder extends RecyclerView.ViewHolder{
        public TextView projectTitle,projectDescriptoion;
        public projectsViewHolder(@NonNull View itemView) {
            super(itemView);
            projectTitle=itemView.findViewById(R.id.project_item_title);
            projectDescriptoion=itemView.findViewById(R.id.project_item_description);
        }
    }
}
