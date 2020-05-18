package app.mazad.webservices;


import java.util.ArrayList;

import app.mazad.classes.Constants;
import app.mazad.webservices.models.BidderModel;
import app.mazad.webservices.models.FilterModel;
import app.mazad.webservices.requests.ChangePasswordRequest;
import app.mazad.webservices.requests.EditProfileRequest;
import app.mazad.webservices.requests.LoginRequest;
import app.mazad.webservices.requests.SendMessageRequest;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface WebServices {
    @POST("LogIn")
    Call<ResponseBody> LOGIN(@Body LoginRequest request);

    @POST("ForgetPassword")
    Call<ResponseBody> FORGET_PASSWORD(@Query("Email") String Email);

    @POST("ChangePassword")
    Call<ResponseBody> CHANGE_PASSWORD(@Header(Constants.AUTHORIZATION) String userToken,
                                       @Body ChangePasswordRequest request);

    @POST("logout")
    Call<ResponseBody> LOGOUT(@Header(Constants.AUTHORIZATION) String userToken,
                              @Query("userid") int userId);

    @GET("FAQs")
    Call<ResponseBody> FAQ();

    @GET("ContactUs")
    Call<ResponseBody> CONTACTS();

    @GET("AboutUs")
    Call<ResponseBody> ABOUT_US();

    @POST("ContactUs")
    Call<ResponseBody> SEND_MESSAGE(@Body SendMessageRequest request);

    @Multipart
    @POST("SignUp")
    Call<ResponseBody> SIGN_UP(@Part("FullName") RequestBody fullName,
                               @Part("Mobile") RequestBody mobile,
                               @Part("Email") RequestBody email,
                               @Part("CivilId") RequestBody CivilId,
                               @Part("Password") RequestBody password,
                               @Part("UserName") RequestBody userName,
                               @Part MultipartBody.Part CivilIdFront,
                               @Part MultipartBody.Part CivilIdBack);

    @GET("Categories")
    Call<ResponseBody> CATEGORIES();

    @GET("AuctionCategory")
    Call<ResponseBody> PRODUCTS(@Query("CategoryId") int CategoryId,
                                @Query("pageNum") int pageNum);

    @GET("LatestAuction")
    Call<ResponseBody> LATEST_AUCTIONS(@Query("pageNum") int pageNum);

    @GET("SearchAuctions")
    Call<ResponseBody> SEARCH(@Query("strSearch") String strSearch,
                              @Query("CategoryId") int CategoryId,
                              @Query("pageNum") int pageNum);


    @GET("CommAds")
    Call<ResponseBody> AD_CALL();


    @GET("GetUserData")
    Call<ResponseBody> USER_DATA(@Header(Constants.AUTHORIZATION) String userToken,
                                 @Query("userId") int userId);

    @POST("UpdateUserData")
    Call<ResponseBody> UPDATE_USER_DATA(@Header(Constants.AUTHORIZATION) String userToken,
                                        @Body EditProfileRequest request);


    @POST("VerifyUser")
    Call<ResponseBody> VERIFY(@Header(Constants.AUTHORIZATION) String userToken,
                              @Query("userId") int userId,
                              @Query("VerifyCode") String VerifyCode);

    @POST("ResendCode")
    Call<ResponseBody> RESEND_CODE(@Header(Constants.AUTHORIZATION) String userToken,
                                   @Query("userId") int userId);

    //********************************************Notifications********************************************
    @GET("GetUserNotifications")
    Call<ResponseBody> GET_NOTIFICATIONS(@Header(Constants.AUTHORIZATION) String userToken,
                                         @Query("userid") int userId,
                                         @Query("pageNum") int pageNum);

    @POST("UpdateUserNotifications")
    Call<ResponseBody> UPDATE_NOTIFICATIONS(@Header(Constants.AUTHORIZATION) String userToken,
                                            @Query("userid") int userId);


    //********************************************Subscriptions(Packages)********************************************
    @GET("GetSubscriptions")
    Call<ResponseBody> PACKAGES(@Header(Constants.AUTHORIZATION) String userToken);

    @GET("PaymentHistory")
    Call<ResponseBody> PAYMENT_HISTORY(@Header(Constants.AUTHORIZATION) String userToken,
                                       @Query("userid") int userId);

    @POST("MakeSubscription")
    Call<ResponseBody> MAKE_SUBSCRIPTION(@Header(Constants.AUTHORIZATION) String userToken,
                                         @Query("userid") int userId,
                                         @Query("subscriptionid") int subscriptionId);


    @GET("UserCurrentAuctions")
    Call<ResponseBody> CURRENT_AUCTIONS(@Header(Constants.AUTHORIZATION) String userToken,
                                        @Query("userId") int userId,
                                        @Query("pageNum") int pageNum);

    @GET("UserEndedAuctions")
    Call<ResponseBody> ENDED_AUCTIONS(@Header(Constants.AUTHORIZATION) String userToken,
                                      @Query("userId") int userId,
                                      @Query("pageNum") int pageNum);

    @GET("AuctionDetails")
    Call<ResponseBody> AUCTION_DETAILS(@Query("userId") int userId,
                                       @Query("productId") int productId);

    @POST("AuctionBids")
    Call<ResponseBody> BID_NOW(@Header(Constants.AUTHORIZATION) String userToken,
                               @Query("productId") int productId,
                               @Query("userId") int userId,
                               @Query("bidPrice") double bidPrice);

    @GET("CategoryFilter")
    Call<ResponseBody> GET_FILTERS(@Query("CategoryId") int CategoryId);

    @POST("Filter")
    Call<ResponseBody> FILTER_AND_SORT_PRODUCTS(@Header(Constants.ACCEPT_LANGUAGE) String AcceptLanguage,
                                                @Query("SortType") Integer SortType,
                                                @Query("categoryId") int categoryId,
                                                @Query("priceFrom") String priceFrom,
                                                @Query("priceTo") String priceTo,
                                                @Body ArrayList<FilterModel> filters,
                                                @Query("pageNum") int pageNum);

    @GET("UserWiningAuction")
    Call<ResponseBody> WINNING_AUCTIONS(@Header(Constants.AUTHORIZATION) String userToken,
                                        @Query("userId") int userId,
                                        @Query("pageNum") int pageNum);

    @GET("WinningAuctionDetails")
    Call<ResponseBody> WINNING_AUCTION_DETAILS(@Header(Constants.AUTHORIZATION) String userToken,
                                               @Query("userId") int userId,
                                               @Query("productId") int productId);

    @POST("PayAuction")
    Call<ResponseBody> PAY_AUCTION(@Header(Constants.AUTHORIZATION) String userToken,
                                   @Query("productId") int productId,
                                   @Query("userId") int userId);

    @GET("AuctionBidders")
    Call<ArrayList<BidderModel>> BIDDERS(@Header(Constants.AUTHORIZATION) String userToken,
                                         @Query("productId") int productId,
                                         @Query("pageNum") int pageNum);


    @POST("AddDevicetoken")
    Call<ResponseBody> ADD_DEVICE_TOKEN(@Query("userid") int userId,
                                        @Query("token") String token,
                                        @Query("DeviceType") int DeviceType,
                                        @Query("DeviceId") String DeviceId);
}
