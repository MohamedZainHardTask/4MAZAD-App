package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import app.mazad.activities.MainActivity;
import app.mazad.classes.GlobalFunctions;

public class PaymentHistoryModel {
    @SerializedName("creationDate")
    private String creationDate;

    @SerializedName("flag")
    public int flag;

    @SerializedName("amount")
    public double amount;

    @SerializedName("serviceAr")
    private String serviceAr;

    @SerializedName("serviceEn")
    private String serviceEn;

    public String getCreationDate() {
        return GlobalFunctions.formatDate(creationDate);
    }


    public String getService() {
        if (MainActivity.isEnglish)
            return serviceEn;
        else
            return serviceAr;
    }


}
