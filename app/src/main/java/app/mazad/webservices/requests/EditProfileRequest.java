package app.mazad.webservices.requests;

import com.google.gson.annotations.SerializedName;

public class EditProfileRequest {

    @SerializedName("id")
    public int id;

    @SerializedName("fullName")
    public String fullName;

    @SerializedName("mobile")
    public String mobile;

    @SerializedName("email")
    public String email;

    @SerializedName("civilId")
    public String civilId;

    @SerializedName("userName")
    public String userName;

}
