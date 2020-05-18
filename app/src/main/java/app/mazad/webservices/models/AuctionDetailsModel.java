package app.mazad.webservices.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.mazad.activities.MainActivity;
import app.mazad.classes.GlobalFunctions;

public class AuctionDetailsModel {

    @SerializedName("id")
    public int id;

    @SerializedName("userBids")
    public int userBids;

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

    @SerializedName("lastBidUserId")
    public int lastBidUserId;

    @SerializedName("bidPrice")
    public double bidPrice;

    @SerializedName("descriptionAr")
    private String descriptionAr;

    @SerializedName("descriptionEn")
    private String descriptionEn;

    public String getDescription() {
        if (MainActivity.isEnglish)
            return descriptionEn;
        else
            return descriptionAr;
    }

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("isCar")
    public boolean isCar;

    @SerializedName("minimumIncrement")
    public double minimumIncrement;

    @SerializedName("startBidPrice")
    public double startBidPrice;

    @SerializedName("startDate")
    private String startDate;

    public String getStartDate() {
        return GlobalFunctions.formatDateAndTime(startDate);
    }

    @SerializedName("user")
    public String user;

    @SerializedName("brand")
    public BrandModel brand;


    @SerializedName("modules")
    public CarModelModel carModel;

    @SerializedName("remainTime")
    public double remainTime;

    @SerializedName("fromTime")
    private double fromTime;

    @SerializedName("isBidBefor")
    public boolean isBidBefore;

    @SerializedName("productAttachment")
    public ArrayList<AttachmentModel> productAttachment;

    @SerializedName("productAttribute")
    public ArrayList<AttributeModel> productAttribute;

    @SerializedName("technicalInspection")
    public String pdf;

    @SerializedName("technicalInspectionAr")
    private String pdfNameAr;

    @SerializedName("technicalInspectionEn")
    private String pdfNameEn;

    public String getPdfName(){
        if (MainActivity.isEnglish)
            return pdfNameEn;
        else
            return pdfNameAr;
    }

    @SerializedName("termsAr")
    private String termsAr;

    @SerializedName("termsEn")
    private String termsEn;

    public String getTerms() {
        if (MainActivity.isEnglish)
            return termsEn;
        else
            return termsAr;
    }

    public class AttachmentModel {
        @SerializedName("fileUrl")
        public String fileUrl;
    }


}
