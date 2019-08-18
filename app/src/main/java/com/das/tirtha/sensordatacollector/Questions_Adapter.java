package com.das.tirtha.sensordatacollector;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Questions_Adapter extends RecyclerView.Adapter<Questions_Adapter.QuestionsViewHolder> {
    private Context context;
    private ArrayList<Questions_Data> mQuestionList;
    public SharedPreferences sp;
    public Questions_Adapter(Context context, ArrayList<Questions_Data> mQuestionList) {
        this.context=context;
        this.mQuestionList=mQuestionList;
    }

    @NonNull
    @Override
    public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.questions_list_item,viewGroup,false);
        return  new Questions_Adapter.QuestionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsViewHolder questionsViewHolder, int i) {
        final Questions_Data questions_data=mQuestionList.get(i);
        questionsViewHolder.questionText.setText(questions_data.getQuesiton());


    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }

    public class  QuestionsViewHolder extends  RecyclerView.ViewHolder{
        public TextView questionText;
        public EditText answer;
        public QuestionsViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText=itemView.findViewById(R.id.question_text);
            answer=itemView.findViewById(R.id.answer);

        }
    }
}
