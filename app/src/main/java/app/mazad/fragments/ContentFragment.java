package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.Constants;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.responses.AboutUsResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentFragment extends Fragment {
    public static FragmentActivity activity;
    public static ContentFragment fragment;
    private SessionManager sessionManager;
    private View childView;
    private AboutUsResponse aboutUs;

    @BindView(R.id.fragment_content_iv_logo)
    ImageView logo;
    @BindView(R.id.fragment_content_wv_content)
    WebView content;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static ContentFragment newInstance(FragmentActivity activity, String comingFrom) {
        fragment = new ContentFragment();
        ContentFragment.activity = activity;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.COMING_FROM, comingFrom);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        String title = "";
        if (getArguments().getString(Constants.COMING_FROM).equals(Constants.TERMS))
            title = activity.getString(R.string.termsAndConditions);
        else if (getArguments().getString(Constants.COMING_FROM).equals(Constants.ABOUT_US))
            title = activity.getString(R.string.aboutUs);
        else if (getArguments().getString(Constants.COMING_FROM).equals(Constants.HELP)) {
            title = activity.getString(R.string.help);
            logo.setVisibility(View.GONE);
        }
        MainActivity.setupAppbar(true, true, false, false, title);
        sessionManager = new SessionManager(activity);

        if (aboutUs == null) {
            aboutUsApi();
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    private void aboutUsApi() {
        loading.setVisibility(View.VISIBLE);
        RetrofitConfig.getServices().ABOUT_US()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            aboutUs = handleAboutUsResponse(response);
                            if (getArguments().getString(Constants.COMING_FROM).equals(Constants.TERMS))
                                setupWebView(aboutUs.getTermsCondition());
                            else if (getArguments().getString(Constants.COMING_FROM).equals(Constants.ABOUT_US))
                                setupWebView(aboutUs.getAboutUs());
                            else if (getArguments().getString(Constants.COMING_FROM).equals(Constants.HELP))
                                setupWebView(aboutUs.getHelp());
                        } else if (responseCode == 400) {
                            GlobalFunctions.generalErrorMessage(activity, childView, loading);
                        } else if (responseCode == 401) {
                            showSessionTimeOutAlert();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        GlobalFunctions.generalErrorMessage(activity, childView, loading);
                    }
                });
    }


    private AboutUsResponse handleAboutUsResponse(Response<ResponseBody> response) {
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
        AboutUsResponse aboutUs = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<AboutUsResponse>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            aboutUs = new Gson().fromJson(jsonResponse, type);
        }
        return aboutUs;
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
        content.loadDataWithBaseURL("", htmlData, "text/html; charset=utf-8", "utf-8", "");

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


