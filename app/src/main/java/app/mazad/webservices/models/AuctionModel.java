package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import app.mazad.activities.MainActivity;
import app.mazad.classes.GlobalFunctions;

public class AuctionModel implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("userBids")
    public int userBids;

    @SerializedName("titleEn")
    private String titleEn;

    @SerializedName("titleAr")
    private String titleAr;

    @SerializedName("photoUrl")
    public String photoUrl;

    @SerializedName("lastBidPrice")
    public double lastBidPrice;

    @SerializedName("isHighValue")
    public boolean isHighValue;

    @SerializedName("remainTime")
    public Double remainTime;

    @SerializedName("fromTime")
    public Double startTime;

    @SerializedName("startDate")
    public String startDate;

    @SerializedName("lastBidUserId")
    public int lastBidUserId;

    @SerializedName("user")
    public String user;

    public String getTitle() {
        if (MainActivity.isEnglish)
            return titleEn;
        else
            return titleAr;
    }


    //for ended Auctions
    @SerializedName("closedPrice")
    public double closedPrice;

    @SerializedName("closeDate")
    private String closeDate;

    public String getCloseDate() {
        return GlobalFunctions.formatDateAndTime(closeDate);
    }

    @SerializedName("userId")
    public int userId;

}
