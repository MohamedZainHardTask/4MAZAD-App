package app.mazad.webservices.responses;

import com.google.gson.annotations.SerializedName;

import app.mazad.webservices.models.UserModel;

public class AuthResponse {
    @SerializedName("user")
    public UserModel user;

    @SerializedName("token")
    public String token;

    @SerializedName("issubscribe")
    public boolean isSubscribe;
}
