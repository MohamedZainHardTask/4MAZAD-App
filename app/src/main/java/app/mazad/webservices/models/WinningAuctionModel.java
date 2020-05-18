package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import app.mazad.activities.MainActivity;
import app.mazad.classes.GlobalFunctions;

public class WinningAuctionModel {

    @SerializedName("productId")
    public int productId;

    @SerializedName("titleEn")
    private String titleEn;

    @SerializedName("titleAr")
    private String titleAr;

    @SerializedName("photoUrl")
    public String photoUrl;

    @SerializedName("lastBidPrice")
    public int lastBidPrice;

    @SerializedName("isHighValue")
    public boolean isHighValue;

    @SerializedName("lastBidUserId")
    public int lastBidUserId;

    @SerializedName("user")
    public String user;

    @SerializedName("isPayed")
    public boolean isPayed;

    @SerializedName("isPayOnline")
    public boolean isPayOnline;

    public String getTitle() {
        if (MainActivity.isEnglish)
            return titleEn;
        else
            return titleAr;
    }

    @SerializedName("closeDate")
    private String closeDate;

    public String getCloseDate() {
        return GlobalFunctions.formatDate(closeDate);
    }

    @SerializedName("userId")
    public int userId;



}
