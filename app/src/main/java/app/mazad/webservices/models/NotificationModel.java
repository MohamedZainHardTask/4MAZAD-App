package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import app.mazad.activities.MainActivity;
import app.mazad.classes.GlobalFunctions;

public class NotificationModel {
    @SerializedName("productId")
    public int productId;

    @SerializedName("notificationDate")
    private String notificationDate;

    @SerializedName("arabicMessage")
    private String arabicMessage;

    @SerializedName("englishMessage")
    private String englishMessage;

    @SerializedName("isRead")
    public boolean isRead;

    @SerializedName("type")
    public String type;

    public String getMessage() {
        if (MainActivity.isEnglish)
            return englishMessage;
        else
            return arabicMessage;
    }

    public String getNotificationDate() {
        return GlobalFunctions.formatDate(notificationDate);
    }
}
