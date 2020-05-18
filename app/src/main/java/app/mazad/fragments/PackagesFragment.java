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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.adapters.PackagesAdapter;
import app.mazad.classes.Constants;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.PackageModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackagesFragment extends Fragment {
    public static FragmentActivity activity;
    public static PackagesFragment fragment;
    private View childView;
    private SessionManager sessionManager;

    private ArrayList<PackageModel> packagesList = new ArrayList<>();
    private LinearLayoutManager packagesLayoutManager;
    private PackagesAdapter packagesAdapter;

    @BindView(R.id.fragment_packages_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_packages_rv_packages)
    RecyclerView packages;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static PackagesFragment newInstance(FragmentActivity activity) {
        fragment = new PackagesFragment();
        PackagesFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_packages, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, true, false, false, activity.getString(R.string.selectPackage));
        sessionManager = new SessionManager(activity);
        intiPackages();
        if (packagesList.size() == 0)
            packagesApi();
        else
            loading.setVisibility(View.GONE);
    }

    private void intiPackages() {
        packagesLayoutManager = new LinearLayoutManager(activity);
        packagesAdapter = new PackagesAdapter(activity, packagesList, new PackagesAdapter.OnItemClickListener() {
            @Override
            public void subscribeClick(int position) {
                subscribeApi(packagesList.get(position).id);
            }
        });
        packages.setLayoutManager(packagesLayoutManager);
        packages.setAdapter(packagesAdapter);
    }


    private void packagesApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().PACKAGES(sessionManager.getUserToken())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            packagesList.addAll(handlePackagesResponse(response));
                            packagesAdapter.notifyDataSetChanged();
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

    private void subscribeApi(int subscriptionId) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().MAKE_SUBSCRIPTION(sessionManager.getUserToken(),
                sessionManager.getUserId(), subscriptionId)
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
                            Navigator.loadFragment(activity, UrlsFragment.newInstance(activity, paymentLink, null,true,Constants.PACKAGES,null)
                                    , R.id.activity_main_fl_appContainer, true);
                        } else if (responseCode == 203) {
                            Log.d(Constants.MAZAD, "user Id not found");
                        } else if (responseCode == 204) {
                            Log.d(Constants.MAZAD, "subscription not found");
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

    private ArrayList<PackageModel> handlePackagesResponse(Response<ResponseBody> response) {
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
        ArrayList<PackageModel> packages = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<ArrayList<PackageModel>>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            packages = new Gson().fromJson(jsonResponse, type);
        }
        return packages;
    }

    // private String handleMakeSubscriptionResponse(Response<ResponseBody> response) {
//        ResponseBody body = response.body();
//        String outResponse = "";
//        String jsonResponse = "";
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream()));
//            StringBuilder out = new StringBuilder();
//            String newLine = System.getProperty("line.separator");
//            String line;
//            while ((line = reader.readLine()) != null) {
//                out.append(line);
//                out.append(newLine);
//            }
//            outResponse = out.toString();
//            jsonResponse = out.toString();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        String paymentLink = null;
//        if (outResponse != null) {
//            outResponse = outResponse.replace("\"", "");
//            outResponse = outResponse.replace("\n", "");
//            Type type = new TypeToken<String>() {
//            }.getType();
//            JsonReader reader = new JsonReader(new StringReader(outResponse));
//            reader.setLenient(true);
//            paymentLink = new Gson().fromJson(jsonResponse, type).toString();
//        }
//        return paymentLink;
//    }


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


