package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.mazad.activities.MainActivity;

public class FilterModel {

    @SerializedName("id")
    public int id;

    @SerializedName("type")
    public String type;

    @SerializedName("arabicName")
    private String arabicName;

    @SerializedName("englishName")
    private String englishName;

    @SerializedName("values")
    public ArrayList<FilterValueModel> filterValues;

    //this variable not coming from backEnd but used it for sending selectedValues
    public String value = "";

    public String getName() {
        if (MainActivity.isEnglish)
            return englishName;
        else
            return arabicName;
    }

    public void setName(String name) {
        arabicName = name;
        englishName = name;
    }
}
