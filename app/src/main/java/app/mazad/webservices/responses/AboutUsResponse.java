package app.mazad.webservices.responses;

import com.google.gson.annotations.SerializedName;

import app.mazad.activities.MainActivity;

public class AboutUsResponse {
    @SerializedName("aboutUsAr")
    private String aboutUsAr;
    @SerializedName("aboutUsEn")
    private String aboutUsEn;
    @SerializedName("termsConditionAr")
    private String termsConditionAr;
    @SerializedName("termsConditionEn")
    private String termsConditionEn;
    @SerializedName("helpAr")
    private String helpAr;
    @SerializedName("helpEn")
    private String helpEn;

    public String getAboutUs() {
        if (MainActivity.isEnglish)
            return aboutUsEn;
        else
            return aboutUsAr;
    }

    public String getTermsCondition() {
        if (MainActivity.isEnglish)
            return termsConditionEn;
        else
            return termsConditionAr;
    }

    public String getHelp() {
        if (MainActivity.isEnglish)
            return helpEn;
        else
            return helpAr;
    }
}
