package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import app.mazad.activities.MainActivity;

public class FaqModel {
    public boolean isShow;

    @SerializedName("question_ar")
    private String questionAr;


    @SerializedName("question_en")
    private String questionEn;

    @SerializedName("answer_ar")
    private String answerAr;

    @SerializedName("answer_en")
    private String answerEn;

    public String getAnswer() {
        if (MainActivity.isEnglish)
            return answerEn;
        else
            return answerAr;
    }

    public String getQuestion() {
        if (MainActivity.isEnglish)
            return questionEn;
        else
            return questionAr;
    }
}
