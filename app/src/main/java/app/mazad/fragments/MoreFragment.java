package app.mazad.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.Constants;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.responses.AboutUsResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreFragment extends Fragment {
    public static FragmentActivity activity;
    public static MoreFragment fragment;
    private SessionManager sessionManager;

    @BindView(R.id.fragment_more_tv_myAccountTxt)
    TextView myAccountTxt;
    @BindView(R.id.fragment_more_iv_myAccountArrow)
    ImageView myAccountArrow;
    @BindView(R.id.fragment_more_iv_aboutUsArrow)
    ImageView aboutUsArrow;
    @BindView(R.id.fragment_more_iv_contactUsArrow)
    ImageView contactUsArrow;
    @BindView(R.id.fragment_more_iv_faqArrow)
    ImageView faqArrow;
    @BindView(R.id.fragment_more_iv_helpArrow)
    ImageView helpArrow;
    @BindView(R.id.fragment_more_iv_changeLangArrow)
    ImageView changeLangArrow;

    public static MoreFragment newInstance(FragmentActivity activity) {
        fragment = new MoreFragment();
        MoreFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, false, false, true, activity.getString(R.string.more));
        MainActivity.bottomAppbar.getMenu().findItem(R.id.bottom_navigation_menu_more).setChecked(true);
        sessionManager = new SessionManager(activity);
        if (sessionManager.isLoggedIn()) {
            myAccountTxt.setText(activity.getString(R.string.myAccount));
        } else {
            myAccountTxt.setText(activity.getString(R.string.login));
        }
        if (!MainActivity.isEnglish) {
            myAccountArrow.setRotation(180);
            aboutUsArrow.setRotation(180);
            contactUsArrow.setRotation(180);
            faqArrow.setRotation(180);
            helpArrow.setRotation(180);
            changeLangArrow.setRotation(180);
        }
    }


    @OnClick(R.id.fragment_more_v_myAccount)
    public void myAccountClick() {
        if (myAccountTxt.getText().toString().equals(activity.getString(R.string.myAccount))) {
            Navigator.loadFragment(activity, AccountFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
        } else {
            Navigator.loadFragment(activity, LoginFragment.newInstance(activity, ""), R.id.activity_main_fl_appContainer, true);
        }
    }

    @OnClick(R.id.fragment_more_v_aboutUs)
    public void aboutUsClick() {
        Navigator.loadFragment(activity, ContentFragment.newInstance(activity, Constants.ABOUT_US), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_more_v_contactUs)
    public void contactUsClick() {
        Navigator.loadFragment(activity, ContactUsFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_more_v_faq)
    public void faqClick() {
        Navigator.loadFragment(activity, FaqFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_more_v_help)
    public void helpClick() {
        Navigator.loadFragment(activity, ContentFragment.newInstance(activity, Constants.HELP), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_more_v_changeLang)
    public void changeLangClick() {
        GlobalFunctions.changeLanguage(activity);
    }
}
