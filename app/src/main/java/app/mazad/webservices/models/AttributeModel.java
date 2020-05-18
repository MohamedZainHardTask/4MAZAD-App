package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import app.mazad.activities.MainActivity;

public class AttributeModel {
    @SerializedName("arabicName")
    private String arabicName;

    @SerializedName("englishName")
    private String englishName;

    public String getName() {
        if (MainActivity.isEnglish)
            return englishName;
        else
            return arabicName;
    }

    @SerializedName("arabicValue")
    private String arabicValue;

    @SerializedName("englishValue")
    private String englishValue;

    public String getValue() {
        if (MainActivity.isEnglish)
            return englishValue;
        else
            return arabicValue;
    }

    public String carBrand;
    public String carModel;
}
