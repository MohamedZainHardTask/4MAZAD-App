package app.mazad.webservices.requests;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("userid")
    public int userId;

    @SerializedName("oldPassword")
    public String oldPassword;

    @SerializedName("password")
    public String newPassword;
}
