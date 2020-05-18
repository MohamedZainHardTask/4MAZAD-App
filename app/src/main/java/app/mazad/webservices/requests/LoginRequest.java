package app.mazad.webservices.requests;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("mobile")
    public String mobile;

    @SerializedName("password")
    public String password;
}
