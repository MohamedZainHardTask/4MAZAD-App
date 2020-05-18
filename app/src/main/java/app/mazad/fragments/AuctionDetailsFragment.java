package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.adapters.AttributesAdapter;
import app.mazad.adapters.AuctionsAdapter;
import app.mazad.adapters.BiddersAdapter;
import app.mazad.adapters.SliderAdapter;
import app.mazad.classes.Constants;
import app.mazad.classes.FileDownloader;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.RecyclerItemClickListener;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.AttributeModel;
import app.mazad.webservices.models.AuctionDetailsModel;
import app.mazad.webservices.models.BidderModel;
import app.mazad.webservices.models.SignalRModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static app.mazad.activities.MainActivity.hubConnection;

public class AuctionDetailsFragment extends Fragment {
    public static FragmentActivity activity;
    public static AuctionDetailsFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private BottomSheetDialog feesDialog;
    private AuctionDetailsModel auctionDetails;
    private ArrayList<AuctionDetailsModel.AttachmentModel> attachmentList = new ArrayList<>();
    private SliderAdapter sliderAdapter;
    private int currentPage = 0;
    private int NUM_PAGES = 0;
    private ArrayList<AttributeModel> attributesList = new ArrayList<>();
    private AttributesAdapter attributesAdapter;
    private LinearLayoutManager attributesLayoutManager;
    private Double userBiddingPriceValue;
    private CountDownTimer countDownTimer;
    private ArrayList<BidderModel> biddersList = new ArrayList<>();
    private AlertDialog dialog;

    @BindView(R.id.fragment_auction_details_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_auction_details_tv_highValue)
    TextView highValue;
    @BindView(R.id.fragment_auction_details_tv_date)
    TextView date;
    @BindView(R.id.fragment_auction_details_tv_productName)
    TextView productName;
    @BindView(R.id.fragment_auction_details_tv_currentPrice)
    TextView currentPrice;
    @BindView(R.id.fragment_auction_details_tv_startPrice)
    TextView startPrice;
    @BindView(R.id.fragment_auction_details_tv_minimumIncrement)
    TextView minimumIncrement;
    @BindView(R.id.fragment_auction_details_tv_userBiddingPrice)
    TextView userBiddingPrice;
    @BindView(R.id.fragment_auction_details_tv_remainingDays)
    TextView remainingDays;
    @BindView(R.id.fragment_auction_details_tv_remainingHours)
    TextView remainingHours;
    @BindView(R.id.fragment_auction_details_tv_remainingMinutes)
    TextView remainingMinutes;
    @BindView(R.id.fragment_auction_details_tv_remainingSeconds)
    TextView remainingSeconds;
    @BindView(R.id.fragment_auction_details_tv_description)
    TextView description;
    @BindView(R.id.fragment_auction_details_tv_biddingCounter)
    TextView biddingCounter;
    @BindView(R.id.fragment_auction_details_rv_attributes)
    RecyclerView attributes;
    @BindView(R.id.fragment_auction_details_vp_imagesSlider)
    RtlViewPager slider;
    @BindView(R.id.fragment_auction_details_ci_sliderCircles)
    CircleIndicator sliderCircles;
    @BindView(R.id.fragment_auction_details_iv_checkUpFileArrow)
    ImageView checkUpArrow;
    @BindView(R.id.fragment_auction_details_v_pdfLine)
    View pdfLine;
    @BindView(R.id.fragment_auction_details_tv_termsWord)
    TextView termsWord;
    @BindView(R.id.fragment_auction_details_v_termsLine)
    View termsLine;
    @BindView(R.id.fragment_auction_details_wv_terms)
    WebView terms;
    @BindView(R.id.fragment_auction_details_tv_checkUpFile)
    TextView checkUpFile;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static AuctionDetailsFragment newInstance(FragmentActivity activity, int id) {
        fragment = new AuctionDetailsFragment();
        AuctionDetailsFragment.activity = activity;
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_auction_details, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(false, false, false, true, null);
        container.setVisibility(View.GONE);
        sessionManager = new SessionManager(activity);
        if (sessionManager.getUserLanguage().equalsIgnoreCase(Constants.AR)) {
            checkUpArrow.setRotation(180);
        }
        if (auctionDetails == null) {
            auctionDetailsApi();
        } else {
            setData();
            loading.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
    }

    private void setupSignalR() {
        Log.d("setupSignalRChat", "setupSignalRChat");
        Log.d("fapa=details", hubConnection.getConnectionState() + "");
        Log.d(Constants.MAZAD + "socket:", "https://4mazad.com/" + "hubs/product");
        if (hubConnection != null) {
            if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                MainActivity.hubConnection.invoke(Void.class, "MakeListnerProductDetails", auctionDetails.id);
                MainActivity.hubConnection.on("UpdateProduct", (SignalRModel) -> {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (SignalRModel != null) {
                                Log.d(Constants.MAZAD, "Name : -> " + "details");
                                auctionDetails.lastBidPrice = SignalRModel.lastBidPrice;
                                auctionDetails.userBids = SignalRModel.userBids;

                                int intValue = (int) auctionDetails.lastBidPrice;
                                double doubleValue = auctionDetails.lastBidPrice;
                                if (doubleValue - intValue > 0) {
                                    currentPrice.setText(doubleValue + " " + activity.getString(R.string.kuwaitCurrency));
                                } else {
                                    currentPrice.setText(intValue + " " + activity.getString(R.string.kuwaitCurrency));
                                }

                                intValue = (int) (auctionDetails.lastBidPrice + auctionDetails.minimumIncrement);
                                doubleValue = auctionDetails.lastBidPrice + auctionDetails.minimumIncrement;
                                if (doubleValue - intValue > 0) {
                                    userBiddingPrice.setText(doubleValue + " " + activity.getString(R.string.kuwaitCurrency));
                                } else {
                                    userBiddingPrice.setText(intValue + " " + activity.getString(R.string.kuwaitCurrency));
                                }
                                biddingCounter.setText(String.valueOf(auctionDetails.userBids));
                            }
                        }
                    });
                }, SignalRModel.class);
            }
        }
    }

    @OnClick(R.id.fragment_auction_details_tv_checkUpFile)
    public void checkUpFileClick() {
        if (auctionDetails.pdf != null && !auctionDetails.pdf.isEmpty()) {
            Navigator.loadFragment(activity, UrlsFragment.newInstance(activity,
                    "https://docs.google.com/gview?embedded=true&url=" + auctionDetails.pdf
                    , auctionDetails.getPdfName(), false, null, null),
                    R.id.activity_main_fl_appContainer, true);
        } else {
            Snackbar.make(childView, activity.getString(R.string.fileNotFound), Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.fragment_auction_details_iv_detailsBack)
    public void backClick() {
        activity.onBackPressed();
    }

    @OnClick(R.id.fragment_auction_details_iv_share)
    public void shareClick() {
        share();
    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = auctionDetails.getTitle() + "\n\n" + activity.getString(R.string.checkAdOnApp)
                + "\n\n" + activity.getString(R.string.iPhone) + "\n" + "iphoneLink"
                + "\n\n" + activity.getString(R.string.android) + "\n" + "androidLink";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @OnClick(R.id.fragment_auction_details_iv_increment)
    public void incrementClick() {
        userBiddingPriceValue = Double.parseDouble(userBiddingPrice.getText().toString().replace(activity.getString(R.string.kuwaitCurrency), ""));
        userBiddingPrice.setText(userBiddingPriceValue + auctionDetails.minimumIncrement + " " + activity.getString(R.string.kuwaitCurrency));
    }

    @OnClick(R.id.fragment_auction_details_iv_decrement)
    public void decrementClick() {
        userBiddingPriceValue = Double.parseDouble(userBiddingPrice.getText().toString().replace(activity.getString(R.string.kuwaitCurrency), ""));
        if (userBiddingPriceValue == Double.parseDouble(currentPrice.getText().toString().replace(activity.getString(R.string.kuwaitCurrency), ""))
                + auctionDetails.minimumIncrement) {
            Snackbar.make(childView, activity.getString(R.string.canNotDecrement), Snackbar.LENGTH_SHORT).show();
        } else {
            userBiddingPrice.setText(userBiddingPriceValue - auctionDetails.minimumIncrement + " " + activity.getString(R.string.kuwaitCurrency));
        }
    }

    @OnClick(R.id.fragment_auction_details_tv_bidNow)
    public void bidNowtClick() {
        if (!sessionManager.isLoggedIn())
            Navigator.loadFragment(activity, LoginFragment.newInstance(activity, Constants.PRODUCT_DETAILS), R.id.activity_main_fl_appContainer, true);
        else {
            if (!sessionManager.hasPackage()) {
                Navigator.loadFragment(activity, PackagesFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
            } else {
                if (auctionDetails.isHighValue && !auctionDetails.isBidBefore) {
                    showFeesDialog();
                } else {
                    userBiddingPriceValue = Double.parseDouble(userBiddingPrice.getText().toString().replace(activity.getString(R.string.kuwaitCurrency), ""));
                    bidNowApi(userBiddingPriceValue);
                }
            }
        }

    }

    @OnClick(R.id.fragment_auction_details_tv_biddingCounter)
    public void biddingCounterCLick() {
        biddersApi();
    }

    private void biddersApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().BIDDERS(sessionManager.getUserToken(), getArguments().getInt(Constants.ID), 1)
                .enqueue(new Callback<ArrayList<BidderModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<BidderModel>> call, Response<ArrayList<BidderModel>> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            biddersList.clear();
                            biddersList.addAll(response.body());
                            if (biddersList.size() == 0) {
                                Snackbar.make(childView, activity.getString(R.string.noBidders), Snackbar.LENGTH_LONG).show();
                            } else {
                                showBiddersDialog();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<BidderModel>> call, Throwable t) {
                        GlobalFunctions.EnableLayout(container);
                        GlobalFunctions.generalErrorMessage(activity, childView, loading);

                    }
                });
    }

    private void showBiddersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View popUpView = activity.getLayoutInflater().inflate(R.layout.dialog_bidder, null);
        RecyclerView bidders = (RecyclerView) popUpView.findViewById(R.id.dialog_bidder_rv_bidders);
        bidders.setLayoutManager(new LinearLayoutManager(activity));
        bidders.setAdapter(new BiddersAdapter(activity, biddersList));
        builder.setCancelable(true);
        builder.setView(popUpView);
        dialog = builder.create();
        dialog.show();
        bidders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void showFeesDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_fees, null);
        TextView fees = view.findViewById(R.id.bottom_sheet_dialog_fees_tv_cost);

        int intValue = (int) auctionDetails.bidPrice;
        double doubleValue = auctionDetails.bidPrice;
        if (doubleValue - intValue > 0) {
            fees.setText(doubleValue + " ");
        } else {
            fees.setText(intValue + " ");
        }

        TextView done = view.findViewById(R.id.bottom_sheet_dialog_fees_tv_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userBiddingPriceValue = Double.parseDouble(userBiddingPrice.getText().toString().replace(activity.getString(R.string.kuwaitCurrency), ""));
                bidNowApi(userBiddingPriceValue);
                feesDialog.cancel();
            }
        });
        feesDialog = new BottomSheetDialog(activity);
        feesDialog.setContentView(view);
        feesDialog.show();
    }

    private void bidNowApi(double userBiddingPriceValue) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().BID_NOW(sessionManager.getUserToken(), auctionDetails.id, sessionManager.getUserId(), userBiddingPriceValue)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            String paymentLink = null;
                            try {
                                paymentLink = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (paymentLink.equals("1")) {
                                Snackbar.make(childView, activity.getString(R.string.bidSuccessfully), Snackbar.LENGTH_SHORT).show();
                            } else if (paymentLink.contains("http")) {
                                Navigator.loadFragment(activity, UrlsFragment.newInstance(activity, paymentLink, null, true, Constants.AUCTION_DETAILS, getArguments().getInt(Constants.ID))
                                        , R.id.activity_main_fl_appContainer, true);
                            }
                        } else if (responseCode == 201) {
                            Log.d(Constants.MAZAD, "auction not available");
                        } else if (responseCode == 202) {
                            Log.d(Constants.MAZAD, "user not found");
                        } else if (responseCode == 203) {
                            Snackbar.make(childView, activity.getString(R.string.canNotBid), Snackbar.LENGTH_SHORT).show();
                        } else if (responseCode == 204) {
                            Snackbar.make(childView, activity.getString(R.string.paymentError), Snackbar.LENGTH_SHORT).show();
                        } else if (responseCode == 201) {
                        } else if (responseCode == 400) {
                            GlobalFunctions.generalErrorMessage(activity, childView, loading);
                        } else if (responseCode == 401) {
                            showSessionTimeOutAlert();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        GlobalFunctions.EnableLayout(container);
                        GlobalFunctions.generalErrorMessage(activity, childView, loading);
                    }
                });
    }

    private void auctionDetailsApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().AUCTION_DETAILS(sessionManager.getUserId(), getArguments().getInt(Constants.ID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            container.setVisibility(View.VISIBLE);
                            auctionDetails = handleAuctionDetailsResponse(response).get(0);
                            setData();
                            remainTimeCounter(auctionDetails.remainTime);
                        } else if (responseCode == 201) {
                            Log.d(Constants.MAZAD, "auction not available");
                        } else if (responseCode == 400) {
                            GlobalFunctions.generalErrorMessage(activity, childView, loading);
                        } else if (responseCode == 401) {
                            showSessionTimeOutAlert();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        GlobalFunctions.EnableLayout(container);
                        GlobalFunctions.generalErrorMessage(activity, childView, loading);
                    }
                });
    }

    private ArrayList<AuctionDetailsModel> handleAuctionDetailsResponse(Response<ResponseBody> response) {
        ResponseBody body = response.body();
        String outResponse = "";
        String jsonResponse = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream()));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
            outResponse = out.toString();
            jsonResponse = out.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ArrayList<AuctionDetailsModel> categories = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<ArrayList<AuctionDetailsModel>>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            categories = new Gson().fromJson(jsonResponse, type);
        }
        return categories;
    }

    private void setData() {
        setupSignalR();
        setupSlider(auctionDetails.productAttachment);
        if (auctionDetails.isHighValue) {
            highValue.setVisibility(View.VISIBLE);
        } else {
            highValue.setVisibility(View.GONE);
        }

        date.setText(auctionDetails.getStartDate());
        productName.setText(auctionDetails.getTitle());

        int intValue = (int) auctionDetails.startBidPrice;
        double doubleValue = auctionDetails.startBidPrice;
        if (doubleValue - intValue > 0) {
            startPrice.setText(doubleValue + " " + activity.getString(R.string.kuwaitCurrency));
        } else {
            startPrice.setText(intValue + " " + activity.getString(R.string.kuwaitCurrency));
        }

        description.setText(auctionDetails.getDescription());
        intValue = (int) auctionDetails.minimumIncrement;
        doubleValue = auctionDetails.minimumIncrement;
        if (doubleValue - intValue > 0) {
            minimumIncrement.setText(doubleValue + " " + activity.getString(R.string.kuwaitCurrency));
        } else {
            minimumIncrement.setText(intValue + " " + activity.getString(R.string.kuwaitCurrency));
        }


        intValue = (int) auctionDetails.lastBidPrice;
        doubleValue = auctionDetails.lastBidPrice;
        if (doubleValue - intValue > 0) {
            currentPrice.setText(doubleValue + " " + activity.getString(R.string.kuwaitCurrency));
        } else {
            currentPrice.setText(intValue + " " + activity.getString(R.string.kuwaitCurrency));
        }

        biddingCounter.setText(String.valueOf(auctionDetails.userBids));

        intValue = (int) (auctionDetails.lastBidPrice + auctionDetails.minimumIncrement);
        doubleValue = auctionDetails.lastBidPrice + auctionDetails.minimumIncrement;
        if (doubleValue - intValue > 0) {
            userBiddingPrice.setText(doubleValue + " " + activity.getString(R.string.kuwaitCurrency));
        } else {
            userBiddingPrice.setText(intValue + " " + activity.getString(R.string.kuwaitCurrency));
        }

        attributesList.clear();
        if (auctionDetails.isCar) {
            AttributeModel carBrand = new AttributeModel();
            carBrand.carBrand = auctionDetails.brand.getValue();

            AttributeModel carModel = new AttributeModel();
            carModel.carModel = auctionDetails.carModel.getValue();

            attributesList.add(carBrand);
            attributesList.add(carModel);
        }
        attributesList.addAll(auctionDetails.productAttribute);
        attributesLayoutManager = new LinearLayoutManager(activity);
        attributesAdapter = new AttributesAdapter(activity, attributesList);
        attributes.setLayoutManager(attributesLayoutManager);
        attributes.setAdapter(attributesAdapter);

        if (auctionDetails.getTerms() != null && !auctionDetails.getTerms().isEmpty()) {
            terms.setVisibility(View.VISIBLE);
            termsLine.setVisibility(View.VISIBLE);
            termsWord.setVisibility(View.VISIBLE);
            setupWebView(auctionDetails.getTerms());
            //  terms.setText(auctionDetails.getTerms());

        } else {
            terms.setVisibility(View.GONE);
            termsLine.setVisibility(View.GONE);
            termsWord.setVisibility(View.INVISIBLE);
        }

        if (auctionDetails.getPdfName() != null && !auctionDetails.getPdfName().isEmpty()) {
            checkUpFile.setVisibility(View.VISIBLE);
            checkUpArrow.setVisibility(View.VISIBLE);
            pdfLine.setVisibility(View.VISIBLE);
            checkUpFile.setText(auctionDetails.getPdfName());
        } else {
            checkUpFile.setVisibility(View.GONE);
            checkUpArrow.setVisibility(View.GONE);
            pdfLine.setVisibility(View.GONE);
        }
    }

    private void setupWebView(String contentStr) {
        contentStr = contentStr.replace("font", "f");
        contentStr = contentStr.replace("color", "c");
        contentStr = contentStr.replace("size", "s");
        String fontName;
        if (MainActivity.isEnglish)
            fontName = Constants.RALEWAY_REGULAR;
        else
            fontName = Constants.DROID_ARABIC_REGULAR;

        String head = "<head><style>@font-face {font-family: 'verdana';src: url('file:///android_asset/" + fontName + "');}body {font-family: 'verdana';}</style></head>";
        String htmlData = "<html>" + head + (MainActivity.isEnglish ? "<body dir=\"ltr\"" : "<body dir=\"rtl\"") + " style=\"font-family: verdana\">" +
                contentStr + "</body></html>";
        terms.loadDataWithBaseURL("", htmlData, "text/html; charset=utf-8", "utf-8", "");

    }

    private void setupSlider(ArrayList<AuctionDetailsModel.AttachmentModel> attachmentList) {
        /*make slider autoPlay , every 5 seconds the image will replace with the next one
         * when lastImage come , the cycle will start from 0 again*/
        slider.setCurrentItem(0, true);
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                slider.setCurrentItem(currentPage++, true);
            }
        };


        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 5000, 5000);

        NUM_PAGES = attachmentList.size();
        sliderAdapter = new SliderAdapter(activity, attachmentList);
        slider.setAdapter(sliderAdapter);
        sliderCircles.setViewPager(slider);
        sliderCircles.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                currentPage = position;
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void remainTimeCounter(Double remainTime) {
        long remainTimeLong = Math.round(remainTime);

        setTime(remainTimeLong);
        countDownTimer = new CountDownTimer(remainTimeLong, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();

                if (isAdded()) {
                    Snackbar.make(childView, activity.getString(R.string.auctionEnded), Snackbar.LENGTH_LONG).show();
                    getFragmentManager().popBackStack();
                    Navigator.loadFragment(activity, HomeFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
                }
            }
        }.start();
    }

    private void setTime(long remainTime) {
        final long remainDays = TimeUnit.MILLISECONDS.toDays(remainTime);
        final long remainHours = TimeUnit.MILLISECONDS.toHours(remainTime)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(remainTime));
        final long remainMinutes = TimeUnit.MILLISECONDS.toMinutes(remainTime)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime));
        final long remainSeconds = TimeUnit.MILLISECONDS.toSeconds(remainTime)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime));

        remainingDays.setText(String.valueOf(remainDays));
        remainingHours.setText(String.valueOf(remainHours));
        remainingMinutes.setText(String.valueOf(remainMinutes));
        remainingSeconds.setText(String.valueOf(remainSeconds));
//        if (remainDays == 0 && remainHours == 0 && remainMinutes == 0 && remainSeconds == 0) {
////            if (childView == null) {
////                childView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_auction_details, container, false);
////            }
//            if(isAdded()) {
//                Snackbar.make(childView, activity.getString(R.string.auctionEnded), Snackbar.LENGTH_LONG).show();
//                Navigator.loadFragment(activity, HomeFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
//            }
//        }

    }

    private void showSessionTimeOutAlert() {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.app_name))
                .setMessage(activity.getString(R.string.sessionTimeOut))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sessionManager.logout();
                        activity.finish();
                        activity.overridePendingTransition(0, 0);
                        startActivity(new Intent(activity, MainActivity.class));
                        Navigator.loadFragment(activity, LoginFragment.newInstance(activity, ""), R.id.activity_main_fl_appContainer, true);
                    }
                })
                .setIcon(R.mipmap.ic_launcher_icon)
                .show();
    }

}


