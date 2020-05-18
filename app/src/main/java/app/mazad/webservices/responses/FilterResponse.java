package app.mazad.webservices.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.mazad.webservices.models.FilterModel;

public class FilterResponse {

    @SerializedName("atterbute")
    public ArrayList<FilterModel> attributes;

    @SerializedName("minPrice")
    public int minPrice;

    @SerializedName("maxPrice")
    public int maxPrice;
}
