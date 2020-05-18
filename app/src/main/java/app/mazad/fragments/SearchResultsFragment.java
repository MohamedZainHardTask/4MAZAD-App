package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.adapters.ProductsAdapter;
import app.mazad.classes.Constants;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.AuctionModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsFragment extends Fragment {
    public static FragmentActivity activity;
    public static SearchResultsFragment fragment;
    private View childView;
    private SessionManager sessionManager;

    private ArrayList<AuctionModel> searchResultList = new ArrayList<>();
    private LinearLayoutManager searchResultLayoutManager;
    private ProductsAdapter searchResultAdapter;
    private int pageIndex = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @BindView(R.id.fragment_search_results_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_search_results_rv_searchResults)
    RecyclerView searchResults;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static SearchResultsFragment newInstance(FragmentActivity activity, String searchQuery,int categoryId) {
        fragment = new SearchResultsFragment();
        SearchResultsFragment.activity = activity;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SEARCH_QUERY, searchQuery);
        bundle.putInt(Constants.CATEGORY_ID,categoryId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, true, false, false, activity.getString(R.string.searchResult));
        sessionManager = new SessionManager(activity);
        intiSearchResult();
        if (searchResultList.size() == 0) {
            searchApi();
        }else{
            loading.setVisibility(View.GONE);
        }
    }

    private void intiSearchResult() {
        searchResultLayoutManager = new LinearLayoutManager(activity);
        searchResultAdapter = new ProductsAdapter(activity, searchResultList, new ProductsAdapter.OnItemCLickListener(){
            @Override
            public void detailsClick(int position) {
                Navigator.loadFragment(activity,AuctionDetailsFragment.newInstance(activity,searchResultList.get(position).id),R.id.activity_main_fl_appContainer,true);
            }
        });
        searchResults.setLayoutManager(searchResultLayoutManager);
        searchResults.setAdapter(searchResultAdapter);
    }

    private void searchApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().SEARCH(getArguments().getString(Constants.SEARCH_QUERY),
                getArguments().getInt(Constants.CATEGORY_ID), pageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            container.setVisibility(View.VISIBLE);
                            searchResultList.addAll(handleSearchResultResponse(response));
                            searchResultAdapter.notifyDataSetChanged();
                            if (searchResultList.size() > 0) {
                                searchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        if (!isLastPage) {
                                            int visibleItemCount = searchResultLayoutManager.getChildCount();

                                            int totalItemCount = searchResultLayoutManager.getItemCount();

                                            int pastVisibleItems = searchResultLayoutManager.findFirstVisibleItemPosition();

                                /*isLoading variable used for check if the user send many requests
                                for pagination(make many scrolls in the same time)
                                1- if isLoading true >> there is request already sent so,
                                no more requests till the response of last request coming
                                2- else >> send new request for load more data (News)*/
                                            if (!isLoading) {

                                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                                    isLoading = true;

                                                    pageIndex = pageIndex + 1;

                                                    getMoreSearchResult();

                                                }
                                            }
                                        }
                                    }
                                });
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

    private void getMoreSearchResult() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().LATEST_AUCTIONS(pageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            if (handleSearchResultResponse(response).size() > 0) {
                                searchResultList.addAll(handleSearchResultResponse(response));
                                searchResultAdapter.notifyDataSetChanged();
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

    private ArrayList<AuctionModel> handleSearchResultResponse(Response<ResponseBody> response) {
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
