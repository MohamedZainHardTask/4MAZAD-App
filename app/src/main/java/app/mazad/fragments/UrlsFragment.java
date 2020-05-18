package app.mazad.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.Constants;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * UrlsFragment is used for opening the links and Urls inside the urlLink
 * without leave the app*/
public class UrlsFragment extends Fragment {
    public static FragmentActivity activity;
    public static UrlsFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private String Url;
    private String title;
    private String comingFrom;
    private boolean isPayment;
    private Integer auctionId;
    @BindView(R.id.loading)
    ProgressBar loading;
    //urlLink for loading the content of Url
    @BindView(R.id.fragment_urls_wv_urlLink)
    WebView urlLink;

    //pass the Url as parameter when fragment is loaded for loading Url content
    public static UrlsFragment newInstance(FragmentActivity activity, String Url, String title, boolean isPayment, String comingFrom, Integer auctionId) {
        fragment = new UrlsFragment();
        UrlsFragment.activity = activity;
        if (Url.contains("http"))
            fragment.Url = Url;
        else
            fragment.Url = "https://" + Url;
        fragment.title = title;
        fragment.comingFrom = comingFrom;
        fragment.isPayment = isPayment;
        fragment.auctionId = auctionId;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_urls, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        if (title.length() > 25)
            title = title.substring(0, 25) + "...";
        MainActivity.setupAppbar(true, true, false, true, title);
        sessionManager = new SessionManager(activity);
        //WebView should execute JavaScript
        urlLink.getSettings().setJavaScriptEnabled(true);
        /*when enabling this Property is that it would then allow ANY website
        that takes advantage of DOM storage to use said storage options on the device*/
        urlLink.getSettings().setDomStorageEnabled(true);
        urlLink.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        //load the Url content inside the webView
        urlLink.loadUrl(Url);
        if (isPayment) {
            setWebClientForPayment();
        } else {
            setWebClient();
        }

    }

    private void setWebClient() {
        urlLink.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("vnd.youtube")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }

            public void onPageFinished(WebView view, String url) {
                // hide progress of Loading after finishing
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void setWebClientForPayment() {
        urlLink.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(Constants.SUCCESS_PAGE)) {
                    if (comingFrom.equals(Constants.AUCTION_DETAILS)) {
                        clearFragments(1);
                        Navigator.loadFragment(activity, AuctionDetailsFragment.newInstance(activity, auctionId)
                                , R.id.activity_main_fl_appContainer, true);
                        Snackbar.make(childView, activity.getString(R.string.bidSuccessfully), Snackbar.LENGTH_SHORT).show();
                    } else if (comingFrom.equals(Constants.WINNING_AUCTION)) {
                        clearFragments(1);
                        Navigator.loadFragment(activity, HomeFragment.newInstance(activity)
                                , R.id.activity_main_fl_appContainer, true);
                        Snackbar.make(childView, activity.getString(R.string.auctionPaidSuccessfully), Snackbar.LENGTH_SHORT).show();
                    } else if (comingFrom.equals(Constants.PACKAGES)) {
                        clearFragments(2);
                        sessionManager.setPackage(true);
                        Snackbar.make(childView, activity.getString(R.string.subscribedSuccessfully), Snackbar.LENGTH_SHORT).show();
                    }
                } else if (url.contains(Constants.ERROR_PAGE)) {
                    getFragmentManager().popBackStack();
                    Snackbar.make(childView, activity.getString(R.string.OperationFailed), Snackbar.LENGTH_LONG).show();
                    return false;

                }
                urlLink.loadUrl(url);

                return true;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // hide progress of Loading after finishing
                loading.setVisibility(View.GONE);
            }


        });
    }

    private void clearFragments(int count) {
        for (int i = 1; i <= count; i++) {
            getFragmentManager().popBackStack();
        }
    }

}
