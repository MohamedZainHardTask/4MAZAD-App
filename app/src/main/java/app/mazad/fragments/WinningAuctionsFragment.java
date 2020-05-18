package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.adapters.AuctionsAdapter;
import app.mazad.adapters.WinningAuctionsAdapter;
import app.mazad.classes.Constants;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.AuctionModel;
import app.mazad.webservices.models.WinningAuctionModel;
import app.mazad.webservices.responses.AdResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WinningAuctionsFragment extends Fragment {
    public static FragmentActivity activity;
    public static WinningAuctionsFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private ArrayList<WinningAuctionModel> winningAuctionsList = new ArrayList<>();
    private LinearLayoutManager winningAuctionsLayoutManager;
    private WinningAuctionsAdapter winningAuctionsAdapter;
    private int pageIndex = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @BindView(R.id.fragment_winning_auctions_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_winning_auctions_rv_winningAuctions)
    RecyclerView winningAuctions;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static WinningAuctionsFragment newInstance(FragmentActivity activity) {
        fragment = new WinningAuctionsFragment();
        WinningAuctionsFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_winning_auctions, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, true, false, false, activity.getString(R.string.myWinning));
        loading.setVisibility(View.GONE);
        sessionManager = new SessionManager(activity);

        intiWinningAuctions();
        if (winningAuctionsList.size() == 0) {
            winningAuctionsApi();
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    private void intiWinningAuctions() {
        winningAuctionsLayoutManager = new LinearLayoutManager(activity);
        winningAuctionsAdapter = new WinningAuctionsAdapter(activity, winningAuctionsList, new WinningAuctionsAdapter.OnItemCLickListener() {
            @Override
            public void detailsClick(int position) {
                Navigator.loadFragment(activity, WinningAuctionDetailsFragment.newInstance(activity, winningAuctionsList.get(position).productId), R.id.activity_main_fl_appContainer, true);
            }

            @Override
            public void payClick(int position) {
                payAuctionApi(winningAuctionsList.get(position).productId);
            }
        });
        winningAuctions.setLayoutManager(winningAuctionsLayoutManager);
        winningAuctions.setAdapter(winningAuctionsAdapter);
    }


    private void winningAuctionsApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().WINNING_AUCTIONS(sessionManager.getUserToken(), sessionManager.getUserId(), pageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            container.setVisibility(View.VISIBLE);
                            winningAuctionsList.addAll(handleWinningAuctionsResponse(response));
                            winningAuctionsAdapter.notifyDataSetChanged();
                            if (winningAuctionsList.size() > 0) {
                                winningAuctions.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        if (!isLastPage) {
                                            int visibleItemCount = winningAuctionsLayoutManager.getChildCount();

                                            int totalItemCount = winningAuctionsLayoutManager.getItemCount();

                                            int pastVisibleItems = winningAuctionsLayoutManager.findFirstVisibleItemPosition();

                                /*isLoading variable used for check if the user send many requests
                                for pagination(make many scrolls in the same time)
                                1- if isLoading true >> there is request already sent so,
                                no more requests till the response of last request coming
                                2- else >> send new request for load more data (News)*/
                                            if (!isLoading) {

                                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                                    isLoading = true;

                                                    pageIndex = pageIndex + 1;

                                                    getMoreWinningAuctions();

                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                Snackbar.make(loading, activity.getString(R.string.noAuction), Snackbar.LENGTH_LONG).show();
                            }
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

    private void getMoreWinningAuctions() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().WINNING_AUCTIONS(sessionManager.getUserToken(), sessionManager.getUserId(), pageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            if (handleWinningAuctionsResponse(response).size() > 0) {
                                winningAuctionsList.addAll(handleWinningAuctionsResponse(response));
                                winningAuctionsAdapter.notifyDataSetChanged();
                            } else {
                                isLastPage = true;
                                pageIndex = pageIndex - 1;
                            }
                            isLoading = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });

    }

    private void payAuctionApi(int productId) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().PAY_AUCTION(sessionManager.getUserToken(), productId, sessionManager.getUserId())
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
                            Navigator.loadFragment(activity, UrlsFragment.newInstance(activity, paymentLink, null,true, Constants.WINNING_AUCTION, null)
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

    private ArrayList<WinningAuctionModel> handleWinningAuctionsResponse(Response<ResponseBody> response) {
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
        ArrayList<WinningAuctionModel> winningAuctions = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<ArrayList<WinningAuctionModel>>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            winningAuctions = new Gson().fromJson(jsonResponse, type);
        }
        return winningAuctions;
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




