package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.adapters.ProductsAdapter;
import app.mazad.classes.Constants;
import app.mazad.classes.FixControl;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.AuctionModel;
import app.mazad.webservices.models.CategoryModel;
import app.mazad.webservices.models.SignalRModel;
import app.mazad.webservices.responses.ProductsResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static app.mazad.activities.MainActivity.hubConnection;

//this fragment contains the items(auctions) of each category
public class ProductsFragment extends Fragment {
    public static FragmentActivity activity;
    public static ProductsFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private boolean isFilterResult;
    private CategoryModel category;
    private String bannerPath;
    private ArrayList<AuctionModel> auctionsList = new ArrayList<>();
    private LinearLayoutManager auctionsLayoutManager;
    private ProductsAdapter auctionsAdapter;
    private int pageIndex = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private BottomSheetDialog sortDialog;

    @BindView(R.id.fragment_products_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_products_iv_productsCover)
    ImageView productsCover;
    @BindView(R.id.fragment_products_rv_products)
    RecyclerView products;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static ProductsFragment newInstance(FragmentActivity activity, boolean isFilterResult, CategoryModel category) {
        fragment = new ProductsFragment();
        ProductsFragment.activity = activity;
        fragment.isFilterResult = isFilterResult;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_products, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        category = (CategoryModel) getArguments().getSerializable(Constants.CATEGORY);
        MainActivity.setupAppbar(true, true, true, true, category.getName());
        sessionManager = new SessionManager(activity);
        intiAuctions();

        if (!isFilterResult) {
            if (auctionsList.size() == 0) {
                productsApi();
            } else {
                setImage(bannerPath, productsCover);
                loading.setVisibility(View.GONE);
            }
        } else {
            bannerPath = category.imageBannerUrl;
            setImage(bannerPath, productsCover);
            filterAndSortProductsApi(null);

        }
        searchClick();
        sortClick();
        filterClick();

    }

    @Override
    public void onResume() {
        super.onResume();
        setupSignalR();
    }


    //khalas tmam I think , right ?

    private void setupSignalR() {
        Log.d("setupSignalRChat", "setupSignalRChat");
        Log.d(Constants.MAZAD + "socket:", "https://4mazad.com/" + "hubs/product");
        if (hubConnection != null) {
            if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                hubConnection.invoke(Void.class, "MakeListnerProductCategory", category.id);
            hubConnection.on("UpdateProduct", (SignalRModel) -> {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (SignalRModel != null) {
                            Log.d(Constants.MAZAD, "Name : -> " + "products");
                            for (int i = 0; i < auctionsList.size(); i++) {
                                if (auctionsList.get(i).id == SignalRModel.id) {
                                    auctionsList.get(i).lastBidPrice = SignalRModel.lastBidPrice;
                                    auctionsList.get(i).userBids = SignalRModel.userBids;
                                    intiAuctions();
                                }
                            }
                        }
                    }
                });
            }, SignalRModel.class);
        }}
    }

    @OnClick(R.id.fragment_products_iv_productsCover)
    public void productsCoverClick() {
    }

    private void searchClick() {
        //change the color of editText in searchView
        EditText searchEditText = (EditText) MainActivity.search.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        searchEditText.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));

        MainActivity.search.setMaxWidth(Integer.MAX_VALUE);
        MainActivity.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainActivity.search.onActionViewCollapsed();
                Navigator.loadFragment(activity, SearchResultsFragment.newInstance(activity, query, category.id), R.id.activity_main_fl_appContainer, true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void sortClick() {
        MainActivity.sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortBottomDialog();
            }
        });
    }

    private void filterClick() {
        MainActivity.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.loadFragment(activity, FiltersFragment.newInstance(activity, category), R.id.activity_main_fl_appContainer, true);
            }
        });
    }

    private void sortBottomDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_sort, null);
        RadioButton nameSorting = view.findViewById(R.id.bottom_sheet_dialog_sort_rb_nameSorting);
        RadioButton descendingNameSorting = view.findViewById(R.id.bottom_sheet_dialog_sort_rb_descendingNameSorting);
        RadioButton priceSorting = view.findViewById(R.id.bottom_sheet_dialog_sort_rb_priceSorting);
        RadioButton descendingPriceSorting = view.findViewById(R.id.bottom_sheet_dialog_sort_rb_descendingPriceSorting);
        TextView done = view.findViewById(R.id.bottom_sheet_dialog_sort_tv_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sortType = 0;
                if (nameSorting.isChecked())
                    sortType = 1;
                else if (descendingNameSorting.isChecked())
                    sortType = 2;
                else if (priceSorting.isChecked())
                    sortType = 3;
                else if (descendingPriceSorting.isChecked())
                    sortType = 4;

                if (sortType > 0) {
                    initPagination();
                    filterAndSortProductsApi(sortType);
                }
                sortDialog.cancel();
            }
        });
        sortDialog = new BottomSheetDialog(activity);
        sortDialog.setContentView(view);
        sortDialog.show();
    }

    private void intiAuctions() {
        auctionsLayoutManager = new LinearLayoutManager(activity);
        auctionsAdapter = new ProductsAdapter(activity, auctionsList, new ProductsAdapter.OnItemCLickListener() {
            @Override
            public void detailsClick(int position) {
                Navigator.loadFragment(activity, AuctionDetailsFragment.newInstance(activity, auctionsList.get(position).id), R.id.activity_main_fl_appContainer, true);
            }
        });
        products.setLayoutManager(auctionsLayoutManager);
        products.setAdapter(auctionsAdapter);
    }

    private void productsApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().PRODUCTS(category.id, pageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            container.setVisibility(View.VISIBLE);
                            ProductsResponse productsResponse = handleAuctionsResponse(response);
                            bannerPath = productsResponse.imageBanner;
                            setImage(bannerPath, productsCover);
                            auctionsList.clear();
                            auctionsList.addAll(productsResponse.auctions);
                            auctionsAdapter.notifyDataSetChanged();
                            if (auctionsList.size() > 0) {
                                products.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        if (!isLastPage) {
                                            int visibleItemCount = auctionsLayoutManager.getChildCount();

                                            int totalItemCount = auctionsLayoutManager.getItemCount();

                                            int pastVisibleItems = auctionsLayoutManager.findFirstVisibleItemPosition();

                                /*isLoading variable used for check if the user send many requests
                                for pagination(make many scrolls in the same time)
                                1- if isLoading true >> there is request already sent so,
                                no more requests till the response of last request coming
                                2- else >> send new request for load more data (News)*/
                                            if (!isLoading) {

                                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                                    isLoading = true;

                                                    pageIndex = pageIndex + 1;

                                                    getMoreProducts();

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

    private void getMoreProducts() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().PRODUCTS(category.id, pageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            if (handleAuctionsResponse(response).auctions.size() > 0) {
                                auctionsList.addAll(handleAuctionsResponse(response).auctions);
                                auctionsAdapter.notifyDataSetChanged();
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

    private void filterAndSortProductsApi(Integer sortType) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().FILTER_AND_SORT_PRODUCTS(
                sessionManager.getUserLanguage(),
                sortType,
                category.id,
                String.valueOf(FiltersFragment.startPriceValue), String.valueOf(FiltersFragment.endPriceValue),
                FiltersFragment.requestFilterList,
                pageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            ProductsResponse productsResponse = handleAuctionsResponse(response);
                            bannerPath = productsResponse.imageBanner;
                            setImage(bannerPath, productsCover);
                            auctionsList.clear();
                            auctionsList.addAll(productsResponse.auctions);
                            auctionsAdapter.notifyDataSetChanged();
                            if (auctionsList.size() > 0) {
                                products.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        if (!isLastPage) {
                                            int visibleItemCount = auctionsLayoutManager.getChildCount();

                                            int totalItemCount = auctionsLayoutManager.getItemCount();

                                            int pastVisibleItems = auctionsLayoutManager.findFirstVisibleItemPosition();

                                /*isLoading variable used for check if the user send many requests
                                for pagination(make many scrolls in the same time)
                                1- if isLoading true >> there is request already sent so,
                                no more requests till the response of last request coming
                                2- else >> send new request for load more data (News)*/
                                            if (!isLoading) {

                                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                                    isLoading = true;

                                                    pageIndex = pageIndex + 1;

                                                    getMoreFilterAndSortProducts(sortType);

                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                Snackbar.make(childView, activity.getString(R.string.noAuction), Snackbar.LENGTH_LONG).show();
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

    private void getMoreFilterAndSortProducts(Integer sortType) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().FILTER_AND_SORT_PRODUCTS(
                sessionManager.getUserLanguage(),
                sortType,
                category.id,
                String.valueOf(FiltersFragment.startPriceValue), String.valueOf(FiltersFragment.endPriceValue),
                FiltersFragment.requestFilterList,
                pageIndex)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            if (handleAuctionsResponse(response).auctions.size() > 0) {
                                auctionsList.addAll(handleAuctionsResponse(response).auctions);
                                auctionsAdapter.notifyDataSetChanged();
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

    private void setImage(String imagePath, ImageView image) {
        if (imagePath != null) {
            try {
                int Width = FixControl.getImageWidth(activity, R.drawable.products_top_noimg);
                int Height = FixControl.getImageHeight(activity, R.drawable.products_top_noimg);
                image.getLayoutParams().height = Height;
                image.getLayoutParams().width = Width;
                Glide.with(activity.getApplicationContext()).load(imagePath)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.drawable.products_top_noimg))
                        .into(image);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private ProductsResponse handleAuctionsResponse(Response<ResponseBody> response) {
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
        ProductsResponse productsResponse = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<ProductsResponse>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            productsResponse = new Gson().fromJson(jsonResponse, type);
        }
        return productsResponse;
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

    private void initPagination() {
        auctionsList.clear();
        auctionsAdapter.notifyDataSetChanged();
        pageIndex = 1;
        isLoading = false;
        isLastPage = false;
    }
}


