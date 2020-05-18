package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("id")
    public int id;

    @SerializedName("fullName")
    public String fullName;

    @SerializedName("mobile")
    public String mobile;

    @SerializedName("email")
    public String email;

    @SerializedName("userName")
    public String userName;

    @SerializedName("civilIDFrontUrl")
    public String civilIDFrontUrl;

    @SerializedName("civilIDBackUrl")
    public String civilIDBackUrl;

    @SerializedName("civilId")
    public String civilId;

    @SerializedName("isActive")
    public boolean isActive;
}
