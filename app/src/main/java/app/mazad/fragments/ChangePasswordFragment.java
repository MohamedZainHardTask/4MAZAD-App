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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.Constants;
import app.mazad.classes.FixControl;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.requests.ChangePasswordRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {
    public static FragmentActivity activity;
    public static ChangePasswordFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private String oldPassTxt, newPassTxt, confirmPassTxt;

    @BindView(R.id.fragment_change_password_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_change_password_il_oldPassContainer)
    TextInputLayout oldPassContainer;
    @BindView(R.id.fragment_change_password_il_newPassContainer)
    TextInputLayout newPassContainer;
    @BindView(R.id.fragment_change_password_il_confirmPassContainer)
    TextInputLayout confirmPassContainer;
    @BindView(R.id.fragment_change_password_et_oldPass)
    TextInputEditText oldPass;
    @BindView(R.id.fragment_change_password_et_newPass)
    TextInputEditText newPass;
    @BindView(R.id.fragment_change_password_et_confirmPass)
    TextInputEditText confirmPass;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static ChangePasswordFragment newInstance(FragmentActivity activity) {
        fragment = new ChangePasswordFragment();
        ChangePasswordFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, true, false, false, activity.getString(R.string.changePassword));
        sessionManager = new SessionManager(activity);
        loading.setVisibility(View.GONE);
        FixControl.setupUI(activity, container);
    }

    @OnClick(R.id.fragment_change_password_tv_save)
    public void saveCLick() {
        checkPassword();
        if (oldPassContainer.getError() == null && confirmPassContainer.getError() == null
                && newPassContainer.getError() == null) {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.userId = sessionManager.getUserId();
            request.oldPassword = oldPassTxt;
            request.newPassword = newPassTxt;
            changePasswordApi(request);
        }
    }

    private void checkPassword() {
        oldPassTxt = oldPass.getText().toString();
        if (oldPassTxt == null || oldPassTxt.isEmpty()) {
            oldPassContainer.setError(activity.getString(R.string.enterPassword));
        } else {
            if (oldPassTxt.length() < 6) {
                oldPassContainer.setError(activity.getString(R.string.invalidPass));
            } else {
                oldPassContainer.setError(null);
            }
        }


        newPassTxt = newPass.getText().toString();
        if (newPassTxt == null || newPassTxt.isEmpty()) {
            newPassContainer.setError(activity.getString(R.string.enterPassword));
        } else {
            if (newPassTxt.length() < 6) {
                newPassContainer.setError(activity.getString(R.string.invalidPass));
            } else {
                newPassContainer.setError(null);
            }
        }

        confirmPassTxt = confirmPass.getText().toString();
        if (confirmPassTxt == null || confirmPassTxt.isEmpty()) {
            confirmPassContainer.setError(activity.getString(R.string.enterPassword));
        } else {
            if (confirmPassTxt.length() < 6) {
                confirmPassContainer.setError(activity.getString(R.string.invalidPass));
            } else {
                if (!newPassTxt.equals(confirmPassTxt)) {
                    confirmPassContainer.setError(activity.getString(R.string.mismatchPass));
                } else {
                    confirmPassContainer.setError(null);
                }
            }
        }
    }

    private void changePasswordApi(ChangePasswordRequest request) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().CHANGE_PASSWORD(sessionManager.getUserToken(), request)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            Snackbar.make(childView,activity.getString(R.string.passwordChanged),Snackbar.LENGTH_LONG).show();
                            clearFragments(1);
                        } else if (responseCode == 201) {
                            Log.d(Constants.MAZAD, "User Not found");
                        } else if (responseCode == 202) {
                            oldPassContainer.setError(activity.getString(R.string.wrongCurrentPassword));
                        } else if (responseCode == 400) {
                            GlobalFunctions.generalErrorMessage(activity,childView,loading);
                        } else if (responseCode == 401) {
                            showSessionTimeOutAlert();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
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


    private void clearFragments(int count) {
        for (int i = 1; i <= count; i++) {
            getFragmentManager().popBackStack();
        }
    }
}





