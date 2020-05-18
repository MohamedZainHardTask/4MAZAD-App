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
import androidx.recyclerview.widget.GridLayoutManager;
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
import app.mazad.adapters.CategoriesAdapter;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.CategoryModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment {
    public static FragmentActivity activity;
    public static CategoriesFragment fragment;
    private View childView;
    private SessionManager sessionManager;

    private ArrayList<CategoryModel> categoriesList = new ArrayList<>();
    private GridLayoutManager categoriesLayoutManager;
    private CategoriesAdapter categoriesAdapter;

    @BindView(R.id.fragment_categories_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_categories_rv_categories)
    RecyclerView categories;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static CategoriesFragment newInstance(FragmentActivity activity) {
        fragment = new CategoriesFragment();
        CategoriesFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, false, false, true, activity.getString(R.string.categories));
        MainActivity.bottomAppbar.getMenu().findItem(R.id.bottom_navigation_menu_categories).setChecked(true);
        loading.setVisibility(View.GONE);
        sessionManager = new SessionManager(activity);
        intiCategories();
        if (categoriesList.size() == 0) {
            categoriesApi();
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    private void intiCategories() {
        categoriesLayoutManager = new GridLayoutManager(activity,2);
        categoriesAdapter = new CategoriesAdapter(activity, categoriesList, new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void categoryClick(int position) {
                Navigator.loadFragment(activity,ProductsFragment.newInstance(activity,false,categoriesList.get(position)),R.id.activity_main_fl_appContainer,true);
            }
        });
        categories.setLayoutManager(categoriesLayoutManager);
        categories.setAdapter(categoriesAdapter);
    }

    private void categoriesApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().CATEGORIES()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            container.setVisibility(View.VISIBLE);
                            categoriesList.addAll(handleCategoriesResponse(response));
                            categoriesAdapter.notifyDataSetChanged();
                        } else if (responseCode == 400) {
                            GlobalFunctions.generalErrorMessage(activity,childView,loading);
                        } else if (responseCode == 401) {
                            showSessionTimeOutAlert();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        GlobalFunctions.EnableLayout(container);
                        GlobalFunctions.generalErrorMessage(activity,childView,loading);
                    }
                });
    }

    private ArrayList<CategoryModel> handleCategoriesResponse(Response<ResponseBody> response) {
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
        ArrayList<CategoryModel> categories = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<ArrayList<CategoryModel>>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            categories = new Gson().fromJson(jsonResponse, type);
        }
        return categories;
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


