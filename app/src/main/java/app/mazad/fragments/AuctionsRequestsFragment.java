package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.microsoft.signalr.HubConnectionState;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.adapters.AuctionsAdapter;
import app.mazad.adapters.BiddersAdapter;
import app.mazad.adapters.EndedAuctionsAdapter;
import app.mazad.classes.Constants;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.AuctionModel;
import app.mazad.webservices.models.BidderModel;
import app.mazad.webservices.models.SignalRModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static app.mazad.activities.MainActivity.hubConnection;

public class AuctionsRequestsFragment extends Fragment {
    public static FragmentActivity activity;
    public static AuctionsRequestsFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private ArrayList<AuctionModel> endedAuctionsList = new ArrayList<>();
    private LinearLayoutManager auctionsLayoutManager;
    private AuctionsAdapter currentAuctionsAdapter;
    private EndedAuctionsAdapter endedAuctionsAdapter;

    private int endedPageIndex = 1;
    private boolean endedIsLoading = false;
    private boolean endedIsLastPage = false;

    private ArrayList<AuctionModel> currentAuctionsList = new ArrayList<>();
    private int currentPageIndex = 1;
    private boolean currentIsLoading = false;
    private boolean currentIsLastPage = false;
    private boolean iscurrent = true;
    private ArrayList<BidderModel> biddersList = new ArrayList<>();
    private AlertDialog dialog;

    @BindView(R.id.fragment_auctions_requests_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_auctions_requests_tv_current)
    TextView current;
    @BindView(R.id.fragment_auctions_requests_tv_ended)
    TextView ended;
    @BindView(R.id.fragment_auctions_requests_rv_auctionsRequests)
    RecyclerView auctionsRequests;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static AuctionsRequestsFragment newInstance(FragmentActivity activity) {
        fragment = new AuctionsRequestsFragment();
        AuctionsRequestsFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_auctions_requests, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, true, false, true, activity.getString(R.string.myBidding));
        sessionManager = new SessionManager(activity);

        if (iscurrent) {
            intiCurrentAuctions();
            currentAuctionsApi();
        } else {
            intiEndedAuctions();
            endedAuctionApi();

        }
    }

    private void setupSignalR() {
        Log.d("setupSignalRChat", "setupSignalRChat");
        Log.d(Constants.MAZAD + "socket:", "https://4mazad.com/" + "hubs/product");
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            MainActivity.hubConnection.invoke(Void.class, "MakeListnerHome");
            MainActivity.hubConnection.on("UpdateProduct", (SignalRModel) -> {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (SignalRModel != null) {
                            Log.d(Constants.MAZAD, "Name : -> " + "live");
                            for (int i = 0; i < currentAuctionsList.size(); i++) {
                                if (currentAuctionsList.get(i).id == SignalRModel.id) {
                                    currentAuctionsList.get(i).lastBidPrice = SignalRModel.lastBidPrice;
                                    currentAuctionsList.get(i).userBids = SignalRModel.userBids;
                                    currentAuctionsList.get(i).remainTime = SignalRModel.remainTime;
                                    intiCurrentAuctions();
                                }
                            }
                        }
                    }
                });
            }, SignalRModel.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupSignalR();
    }

    private void intiCurrentAuctions() {
        auctionsLayoutManager = new LinearLayoutManager(activity);
        currentAuctionsAdapter = new AuctionsAdapter(activity, currentAuctionsList, Constants.LIVE, new AuctionsAdapter.OnItemCLickListener() {
            @Override
            public void detailsClick(int position) {
                Navigator.loadFragment(activity, AuctionDetailsFragment.newInstance(activity, currentAuctionsList.get(position).id), R.id.activity_main_fl_appContainer, true);
            }

            @Override
            public void biddersClick(int position) {
                biddersApi(position);
            }
        });
        auctionsRequests.setLayoutManager(auctionsLayoutManager);
        auctionsRequests.setAdapter(currentAuctionsAdapter);
    }

    private void biddersApi(int position) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().BIDDERS(sessionManager.getUserToken(), currentAuctionsList.get(position).id, 1)
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
                                Snackbar.make(childView,activity.getString(R.string.noBidders),Snackbar.LENGTH_LONG).show();
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


    private void intiEndedAuctions() {
        auctionsLayoutManager = new LinearLayoutManager(activity);
        endedAuctionsAdapter = new EndedAuctionsAdapter(activity, endedAuctionsList);
        auctionsRequests.setLayoutManager(auctionsLayoutManager);
        auctionsRequests.setAdapter(endedAuctionsAdapter);
    }

    @OnClick(R.id.fragment_auctions_requests_tv_current)
    public void currentClick() {
        iscurrent = true;
        current.setBackgroundResource(R.drawable.tap_sel_right);
        current.setTextColor(Color.parseColor("#ffffff"));
        intiCurrentAuctions();
        if (currentAuctionsList.size() == 0) {
            currentAuctionsApi();
        } else {
            loading.setVisibility(View.GONE);
        }

        ended.setBackground(null);
        ended.setTextColor(Color.parseColor("#52AECD"));
    }

    @OnClick(R.id.fragment_auctions_requests_tv_ended)
    public void endedClick() {
        iscurrent = false;
        ended.setBackgroundResource(R.drawable.tap_sel_right);
        ended.setTextColor(Color.parseColor("#ffffff"));
        intiEndedAuctions();
        if (endedAuctionsList.size() == 0) {
            endedAuctionApi();
        } else {
            loading.setVisibility(View.GONE);
        }
        current.setBackground(null);
        current.setTextColor(Color.parseColor("#52AECD"));
    }

    private void currentAuctionsApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().CURRENT_AUCTIONS(sessionManager.getUserToken(), sessionManager.getUserId(), currentPageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            currentAuctionsList.addAll(handleAuctionsRequestsResponse(response));
                            currentAuctionsAdapter.notifyDataSetChanged();
                            if (currentAuctionsList.size() > 0) {
                                auctionsRequests.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        if (!currentIsLastPage) {
                                            int visibleItemCount = auctionsLayoutManager.getChildCount();

                                            int totalItemCount = auctionsLayoutManager.getItemCount();

                                            int pastVisibleItems = auctionsLayoutManager.findFirstVisibleItemPosition();

                                /*isLoading variable used for check if the user send many requests
                                for pagination(make many scrolls in the same time)
                                1- if isLoading true >> there is request already sent so,
                                no more requests till the response of last request coming
                                2- else >> send new request for load more data (News)*/
                                            if (!currentIsLoading) {

                                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                                    currentIsLoading = true;

                                                    currentPageIndex = currentPageIndex + 1;

                                                    getMoreCurrentAuction();

                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                Snackbar.make(childView, activity.getString(R.string.noCurrentRequestsFound), Snackbar.LENGTH_SHORT).show();
                            }
                        } else if (responseCode == 400) {
                            Snackbar.make(childView, activity.getString(R.string.generalError), Snackbar.LENGTH_SHORT).show();
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

    private void getMoreCurrentAuction() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().CURRENT_AUCTIONS(sessionManager.getUserToken(), sessionManager.getUserId(), currentPageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            if (handleAuctionsRequestsResponse(response).size() > 0) {
                                currentAuctionsList.addAll(handleAuctionsRequestsResponse(response));
                                currentAuctionsAdapter.notifyDataSetChanged();
                            } else {
                                currentIsLastPage = true;
                                currentPageIndex = currentPageIndex - 1;
                            }
                            currentIsLoading = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    private void endedAuctionApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().ENDED_AUCTIONS(sessionManager.getUserToken(), sessionManager.getUserId(), endedPageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            endedAuctionsList.addAll(handleAuctionsRequestsResponse(response));
                            endedAuctionsAdapter.notifyDataSetChanged();
                            if (endedAuctionsList.size() > 0) {
                                auctionsRequests.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        if (!endedIsLastPage) {
                                            int visibleItemCount = auctionsLayoutManager.getChildCount();

                                            int totalItemCount = auctionsLayoutManager.getItemCount();

                                            int pastVisibleItems = auctionsLayoutManager.findFirstVisibleItemPosition();

                                /*isLoading variable used for check if the user send many requests
                                for pagination(make many scrolls in the same time)
                                1- if isLoading true >> there is request already sent so,
                                no more requests till the response of last request coming
                                2- else >> send new request for load more data (News)*/
                                            if (!endedIsLoading) {

                                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                                    endedIsLoading = true;

                                                    endedPageIndex = endedPageIndex + 1;

                                                    getMoreEndedAuction();

                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                Snackbar.make(childView, activity.getString(R.string.noEndedRequestsFound), Snackbar.LENGTH_SHORT).show();
                            }
                        } else if (responseCode == 400) {
                            Snackbar.make(childView, activity.getString(R.string.incorrectPhoneOrPass), Snackbar.LENGTH_SHORT).show();
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

    private void getMoreEndedAuction() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().ENDED_AUCTIONS(sessionManager.getUserToken(), sessionManager.getUserId(), endedPageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            if (handleAuctionsRequestsResponse(response).size() > 0) {
                                endedAuctionsList.addAll(handleAuctionsRequestsResponse(response));
                                endedAuctionsAdapter.notifyDataSetChanged();
                            } else {
                                endedIsLastPage = true;
                                endedPageIndex = endedPageIndex - 1;
                            }
                            endedIsLoading = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    private ArrayList<AuctionModel> handleAuctionsRequestsResponse(Response<ResponseBody> response) {
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
        ArrayList<AuctionModel> auctions = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<ArrayList<AuctionModel>>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            auctions = new Gson().fromJson(jsonResponse, type);
        }
        return auctions;
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




