package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import app.mazad.activities.MainActivity;

public class CarModelModel {
    @SerializedName("id")
    public int id;

    @SerializedName("arabicName")
    private String arabicValue;

    @SerializedName("englishName")
    private String englishValue;

    public String getValue() {
        if (MainActivity.isEnglish)
            return englishValue;
        else
            return arabicValue;
    }

    @SerializedName("brandId")
    public int brandId;
}
