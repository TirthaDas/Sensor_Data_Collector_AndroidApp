package com.das.tirtha.sensordatacollector;

public class Questions_Data {
    String QuestionId;
    String Question;
    String ProjectId;
    String UserId;
    String Answer;


    public Questions_Data(String QuestionId, String Question, String ProjectId, String UserId, String Answer) {
        this.QuestionId=QuestionId;
        this.Question=Question;
        this.ProjectId=ProjectId;
        this.UserId=UserId;
        this.Answer=Answer;
    }
    // getters
    public String getQuesitonId() {
        return QuestionId;
    }

    public String getQuesiton() {
        return Question;
    }

    public String getProjectId() {
        return ProjectId;
    }

    public String getUserId() {
        return UserId;
    }

    public String getAnswer() {
        return Answer;
    }
}
