package app.mazad.fragments;

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
import app.mazad.webservices.RetrofitConfig;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordFragment extends Fragment {
    public static FragmentActivity activity;
    public static ForgetPasswordFragment fragment;
    private View childView;

    @BindView(R.id.fragment_forget_password_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_forget_password_il_emailContainer)
    TextInputLayout mobileContainer;
    @BindView(R.id.fragment_forget_password_et_email)
    TextInputEditText email;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static ForgetPasswordFragment newInstance(FragmentActivity activity) {
        fragment = new ForgetPasswordFragment();
        ForgetPasswordFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_forget_password, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(false, false, false, false, null);
        FixControl.setupUI(activity, container);
        loading.setVisibility(View.GONE);

    }

    @OnClick(R.id.fragment_forget_password_tv_send)
    public void sendCLick() {
        String emailTxt = email.getText().toString();
        if (emailTxt == null || emailTxt.isEmpty()) {
            mobileContainer.setError(activity.getString(R.string.enterUserName));
        } else {
            forgetPasswordApi(emailTxt);
        }
    }

    private void forgetPasswordApi(String email) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().FORGET_PASSWORD(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading.setVisibility(View.GONE);
                GlobalFunctions.EnableLayout(container);
                int responseCode = response.code();
                if (responseCode == 200) {
                    clearFragments(1);
                } else if (responseCode == 203) {
                    Snackbar.make(loading, activity.getString(R.string.emailNotFound), Snackbar.LENGTH_SHORT).show();
                } else if (responseCode == 400) {
                    Snackbar.make(loading, activity.getString(R.string.generalError), Snackbar.LENGTH_SHORT).show();
                } else if (responseCode == 401) {
                    //unauthorized
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                GlobalFunctions.EnableLayout(container);
                GlobalFunctions.generalErrorMessage(activity, childView, loading);
            }
        });
    }

    private void clearFragments(int count) {
        for (int i = 1; i <= count; i++) {
            getFragmentManager().popBackStack();
        }
    }
}

