package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import app.mazad.activities.MainActivity;

public class PackageModel {
    @SerializedName("id")
    public int id;

    @SerializedName("arabicName")
    private String arabicName;

    @SerializedName("englishName")
    private String englishName;

    @SerializedName("price")
    public double price;

    @SerializedName("arabicAbout")
    private String arabicAbout;

    @SerializedName("englishAbout")
    private String englishAbout;

    @SerializedName("colorCode")
    public String colorCode;

    public String getAbout() {
        if(MainActivity.isEnglish)
        return englishAbout;
        else
            return arabicAbout;
    }

    public String getName() {
        if(MainActivity.isEnglish)
        return englishName;
        else
            return arabicName;
    }
}
