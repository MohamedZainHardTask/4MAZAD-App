package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

public class CountryCodeModel {
    @SerializedName("id")
    public int id;

    @SerializedName("countryCode")
    public String countryCode;
}
