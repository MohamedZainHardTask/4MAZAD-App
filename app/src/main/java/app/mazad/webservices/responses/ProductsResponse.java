package app.mazad.webservices.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.mazad.webservices.models.AuctionModel;

public class ProductsResponse {
    @SerializedName("imageBanner")
    public String imageBanner;

    @SerializedName("auctions")
    public ArrayList<AuctionModel> auctions = new ArrayList<>();
}
