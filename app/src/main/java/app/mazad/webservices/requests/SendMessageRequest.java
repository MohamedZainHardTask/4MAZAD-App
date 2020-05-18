package app.mazad.webservices.requests;

import com.google.gson.annotations.SerializedName;

public class SendMessageRequest {
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("mobile")
    public String mobile;

    @SerializedName("message")
    public String message;
}
