package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.mazad.activities.MainActivity;
import app.mazad.classes.GlobalFunctions;

public class WinningAuctionDetailsModel {

    @SerializedName("titleEn")
    private String titleEn;

    @SerializedName("titleAr")
    private String titleAr;

    public String getTitle() {
        if (MainActivity.isEnglish)
            return titleEn;
        else
            return titleAr;
    }

    @SerializedName("isHighValue")
    public boolean isHighValue;

    @SerializedName("lastBidPrice")
    public double lastBidPrice;

    @SerializedName("descriptionAr")
    private String descriptionAr;

    @SerializedName("descriptionEn")
    private String descriptionEn;

    @SerializedName("isPayed")
    public boolean isPayed;

    @SerializedName("isPayOnline")
    public boolean isPayOnline;


    public String getDescription() {
        if (MainActivity.isEnglish)
            return descriptionEn;
        else
            return descriptionAr;
    }

    @SerializedName("endDate")
    private String endDate;


    public String getDate() {
        return GlobalFunctions.formatDateAndTime(endDate);
    }

    @SerializedName("productAttachment")
    public ArrayList<AuctionDetailsModel.AttachmentModel> productAttachment;

    public class AttachmentModel {
        @SerializedName("fileUrl")
        public String fileUrl;
    }


}
