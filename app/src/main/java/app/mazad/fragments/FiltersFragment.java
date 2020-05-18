package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.appyvet.materialrangebar.RangeBar;
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
import app.mazad.adapters.FilterAdapter;
import app.mazad.classes.Constants;
import app.mazad.classes.FixControl;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.CategoryModel;
import app.mazad.webservices.models.FilterModel;
import app.mazad.webservices.responses.FilterResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FiltersFragment extends Fragment {
    public static FragmentActivity activity;
    public static FiltersFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private CategoryModel category;
    private FilterResponse filterResponse;
    private ArrayList<FilterModel> responseFiltersList = new ArrayList<>();
    public static double startPriceValue;
    public static double endPriceValue;
    public static ArrayList<FilterModel> requestFilterList = new ArrayList<>();
    private FilterAdapter filterAdapter;
    private LinearLayoutManager filtersLayoutManager;

    @BindView(R.id.fragment_filters_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_filters_rv_filters)
    RecyclerView filters;
    @BindView(R.id.fragment_filters_rsb_priceRange)
    RangeSeekBar priceRange;
    @BindView(R.id.fragment_filters_tv_initStartPrice)
    TextView initStartPrice;
    @BindView(R.id.fragment_filters_tv_initEndPrice)
    TextView initEndPrice;
    @BindView(R.id.fragment_filters_tv_startPrice)
    TextView startPrice;
    @BindView(R.id.fragment_filters_tv_endPrice)
    TextView endPrice;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static FiltersFragment newInstance(FragmentActivity activity, CategoryModel category) {
        fragment = new FiltersFragment();
        FiltersFragment.activity = activity;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_filters, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, true, false, false, activity.getString(R.string.filter));
        container.setVisibility(View.GONE);
        FixControl.setupUI(activity, container);
        sessionManager = new SessionManager(activity);
        setupPricesRangeBar();
        category = (CategoryModel) getArguments().getSerializable(Constants.CATEGORY);
            getFiltersApi(category.id);
    }

    private void setupPricesRangeBar() {
        priceRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar rangeSeekBar, int i, int i1, boolean b) {
                startPrice.setText(priceRange.getProgressStart() + " " + activity.getString(R.string.kuwaitCurrency));
                endPrice.setText(priceRange.getProgressEnd() + " " + activity.getString(R.string.kuwaitCurrency));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar rangeSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar rangeSeekBar) {

            }
        });
    }

    private void setupFilters() {
        filtersLayoutManager = new LinearLayoutManager(activity);
        filterAdapter = new FilterAdapter(activity, responseFiltersList);
        filters.setLayoutManager(filtersLayoutManager);
        filters.setAdapter(filterAdapter);
    }

    @OnClick(R.id.fragment_filters_tv_clear)
    public void clearClick() {
        startPriceValue = filterResponse.minPrice;
        endPriceValue = filterResponse.maxPrice;
        requestFilterList.clear();
        clearFragments(1);
        Navigator.loadFragment(activity, FiltersFragment.newInstance(activity, category), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_filters_tv_done)
    public void doneClick() {
        startPriceValue = priceRange.getProgressStart();
        endPriceValue = priceRange.getProgressEnd();
        for (FilterModel item : requestFilterList) {
            item.filterValues = null;
        }
        Navigator.loadFragment(activity, ProductsFragment.newInstance(activity, true, category), R.id.activity_main_fl_appContainer, true);
    }

    private void getFiltersApi(int categoryId) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().GET_FILTERS(categoryId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        container.setVisibility(View.VISIBLE);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            filterResponse = handleFiltersResponse(response);
                            responseFiltersList.clear();
                            requestFilterList.clear();
                            responseFiltersList.addAll(filterResponse.attributes);
                            requestFilterList.addAll(responseFiltersList);
                            setupFilters();
                            startPrice.setText(filterResponse.minPrice + " " + activity.getString(R.string.kuwaitCurrency));
                            endPrice.setText(filterResponse.maxPrice + " " + activity.getString(R.string.kuwaitCurrency));
                            initStartPrice.setText(filterResponse.minPrice + " " + activity.getString(R.string.kuwaitCurrency));
                            initEndPrice.setText(filterResponse.maxPrice + " " + activity.getString(R.string.kuwaitCurrency));
                            priceRange.setMax(filterResponse.maxPrice);
                        } else if (responseCode == 201) {
                            Log.d(Constants.MAZAD, "category Not Found");
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

    private FilterResponse handleFiltersResponse(Response<ResponseBody> response) {
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
        FilterResponse filters = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<FilterResponse>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            filters = new Gson().fromJson(jsonResponse, type);
        }
        return filters;
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

    private void clearFragments(int count) {
        for (int i = 1; i <= count; i++) {
            getFragmentManager().popBackStack();
        }
    }
}








