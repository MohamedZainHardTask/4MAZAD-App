package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import app.mazad.activities.MainActivity;

public class CategoryModel implements Serializable {
    @SerializedName("id")
    public int id;

    @SerializedName("arabicName")
    private String arabicName;

    @SerializedName("englishName")
    private String englishName;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("imageBannerUrl")
    public String imageBannerUrl;

    public String getName() {
        if (MainActivity.isEnglish)
            return englishName;
        else
            return arabicName;
    }
}
