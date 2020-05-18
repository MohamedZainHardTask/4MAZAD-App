package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

public class SignalRModel {
    @SerializedName("id")
    public int id;

    @SerializedName("userBids")
    public int userBids;

    @SerializedName("titleEn")
    private String titleEn;

    @SerializedName("titleAr")
    private String titleAr;

    @SerializedName("lastBidPrice")
    public double lastBidPrice;

    @SerializedName("isHighValue")
    public boolean isHighValue;

    @SerializedName("remainTime")
    public Double remainTime;

    @SerializedName("fromTime")
    public Double startTime;

    @SerializedName("lastBidUserId")
    public int lastBidUserId;

    @SerializedName("startDate")
    public String startDate;

}
