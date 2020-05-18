package app.mazad.fragments;

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

import com.google.android.material.snackbar.Snackbar;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.Constants;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    public static FragmentActivity activity;
    public static AccountFragment fragment;
    private  View childView;
    private SessionManager sessionManager;

    @BindView(R.id.fragment_account_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_account_iv_editProfileArrow)
    ImageView editProfileArrow;
    @BindView(R.id.fragment_account_iv_changePassArrow)
    ImageView changePassArrow;
    @BindView(R.id.fragment_account_iv_auctionsRequestsArrow)
    ImageView auctionsRequestsArrow;
    @BindView(R.id.fragment_account_iv_paymentHistoryArrow)
    ImageView paymentHistoryArrow;
    @BindView(R.id.fragment_account_iv_winningAuctionsArrow)
    ImageView winningAuctionsArrow;
    @BindView(R.id.fragment_account_iv_logoutArrow)
    ImageView logoutArrow;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static AccountFragment newInstance(FragmentActivity activity) {
        fragment = new AccountFragment();
        AccountFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       childView = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, false, false, true, activity.getString(R.string.myAccount));
        loading.setVisibility(View.GONE);
        sessionManager = new SessionManager(activity);
        if (!MainActivity.isEnglish) {
            editProfileArrow.setRotation(180);
            changePassArrow.setRotation(180);
            auctionsRequestsArrow.setRotation(180);
            paymentHistoryArrow.setRotation(180);
            winningAuctionsArrow.setRotation(180);
            logoutArrow.setRotation(180);
        }
    }

    @OnClick(R.id.fragment_account_v_editProfile)
    public void editProfileClick() {
        Navigator.loadFragment(activity, EditProfileFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_account_v_changePass)
    public void changePassClick() {
        Navigator.loadFragment(activity, ChangePasswordFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_account_v_auctionsRequests)
    public void auctionsRequestsClick() {
        Navigator.loadFragment(activity, AuctionsRequestsFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_account_v_paymentHistory)
    public void paymentHistoryClick() {
        Navigator.loadFragment(activity, PaymentHistoryFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_account_v_winningAuctions)
    public void winningAuctionsClick() {
        Navigator.loadFragment(activity, WinningAuctionsFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_account_v_logout)
    public void logoutClick() {
        logoutApi();
    }

    private void logoutApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().LOGOUT(sessionManager.getUserToken(), sessionManager.getUserId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            sessionManager.logout();
                            activity.finish();
                            activity.overridePendingTransition(0, 0);
                            startActivity(new Intent(activity, MainActivity.class));
                        } else if (responseCode == 201) {
                            Log.d(Constants.MAZAD, "user Id not found");
                        } else if (responseCode == 400) {
                            Snackbar.make(loading, activity.getString(R.string.generalError), Snackbar.LENGTH_SHORT).show();
                        } else if (responseCode == 401) {
                            //unauthorized
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        GlobalFunctions.EnableLayout(container);
                        GlobalFunctions.generalErrorMessage(activity,childView,loading);
                    }
                });
    }
}



