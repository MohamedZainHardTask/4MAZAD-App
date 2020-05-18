package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.adapters.SliderAdapter;
import app.mazad.classes.Constants;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.AuctionDetailsModel;
import app.mazad.webservices.models.WinningAuctionDetailsModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WinningAuctionDetailsFragment extends Fragment {
    public static FragmentActivity activity;
    public static WinningAuctionDetailsFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private WinningAuctionDetailsModel winningAuctionDetails;
    private ArrayList<AuctionDetailsModel.AttachmentModel> attachmentList = new ArrayList<>();
    private SliderAdapter sliderAdapter;
    private int currentPage = 0;
    private int NUM_PAGES = 0;

    @BindView(R.id.fragment_winning_auction_details_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_winning_auction_details_tv_date)
    TextView date;
    @BindView(R.id.fragment_winning_auction_details_tv_price)
    TextView price;
    @BindView(R.id.fragment_winning_auction_details_tv_highValue)
    TextView highValue;
    @BindView(R.id.fragment_winning_auction_details_tv_productName)
    TextView productName;
    @BindView(R.id.fragment_winning_auction_details_tv_description)
    TextView description;
    @BindView(R.id.fragment_winning_auction_details_vp_imagesSlider)
    RtlViewPager slider;
    @BindView(R.id.fragment_winning_auction_details_ci_sliderCircles)
    CircleIndicator sliderCircles;
    @BindView(R.id.fragment_winning_auction_details_tv_pay)
    TextView pay;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static WinningAuctionDetailsFragment newInstance(FragmentActivity activity, int id) {
        fragment = new WinningAuctionDetailsFragment();
        WinningAuctionDetailsFragment.activity = activity;
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_winning_auction_details, container, false);
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
        if (winningAuctionDetails == null) {
            winnerAuctionDetailsApi();
        } else {
            setData();
            loading.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.fragment_winning_auction_details_iv_detailsBack)
    public void backClick() {
        activity.onBackPressed();
    }

    @OnClick(R.id.fragment_winning_auction_details_iv_share)
    public void shareClick() {
        share();
    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = winningAuctionDetails.getTitle() + "\n\n" + activity.getString(R.string.checkAdOnApp)
                + "\n\n" + activity.getString(R.string.iPhone) + "\n" + "iphoneLink"
                + "\n\n" + activity.getString(R.string.android) + "\n" + "androidLink";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    @OnClick(R.id.fragment_winning_auction_details_tv_pay)
    public void payClick() {
        payAuctionApi(getArguments().getInt(Constants.ID));
    }

    private void payAuctionApi(int productId) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().PAY_AUCTION(sessionManager.getUserToken(),productId, sessionManager.getUserId())
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
                            Navigator.loadFragment(activity, UrlsFragment.newInstance(activity, paymentLink,null, true, Constants.WINNING_AUCTION,null)
                                    , R.id.activity_main_fl_appContainer, true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        GlobalFunctions.EnableLayout(container);
                        GlobalFunctions.generalErrorMessage(activity, childView, loading);
                    }
                });
    }

    private void winnerAuctionDetailsApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().WINNING_AUCTION_DETAILS(sessionManager.getUserToken(),sessionManager.getUserId(), getArguments().getInt(Constants.ID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            container.setVisibility(View.VISIBLE);
                            winningAuctionDetails = handleWinningAuctionDetailsResponse(response).get(0);
                            setData();
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

    private ArrayList<WinningAuctionDetailsModel> handleWinningAuctionDetailsResponse(Response<ResponseBody> response) {
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
        ArrayList<WinningAuctionDetailsModel> winningAuctionDetails = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<ArrayList<WinningAuctionDetailsModel>>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            winningAuctionDetails = new Gson().fromJson(jsonResponse, type);
        }
        return winningAuctionDetails;
    }

    private void setData() {
        if (!winningAuctionDetails.isPayed && winningAuctionDetails.isPayOnline) {
            pay.setVisibility(View.VISIBLE);
        }else{
            pay.setVisibility(View.GONE);
        }
        setupSlider(winningAuctionDetails.productAttachment);
        date.setText(winningAuctionDetails.getDate());


        int intValue = (int) winningAuctionDetails.lastBidPrice;
        double doubleValue = winningAuctionDetails.lastBidPrice;
        if (doubleValue - intValue > 0) {
            price.setText(doubleValue + " " + activity.getString(R.string.kuwaitCurrency));
        } else {
            price.setText(intValue + " " + activity.getString(R.string.kuwaitCurrency));
        }

        if (winningAuctionDetails.isHighValue) {
            highValue.setVisibility(View.VISIBLE);
        } else {
            highValue.setVisibility(View.GONE);
        }

        productName.setText(winningAuctionDetails.getTitle());
        description.setText(winningAuctionDetails.getDescription());

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
                        Navigator.loadFragment(activity, LoginFragment.newInstance(activity,""), R.id.activity_main_fl_appContainer, true);
                    }
                })
                .setIcon(R.mipmap.ic_launcher_icon)
                .show();
    }
}



