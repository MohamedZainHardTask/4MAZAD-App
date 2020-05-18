package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

public class ContactModel {
    @SerializedName("name")
    public String name;

    @SerializedName("value")
    public String value;
}
