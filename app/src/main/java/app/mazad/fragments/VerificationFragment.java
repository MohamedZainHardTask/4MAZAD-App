package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.Constants;
import app.mazad.classes.FixControl;
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

public class VerificationFragment extends Fragment {
    public static FragmentActivity activity;
    public static VerificationFragment fragment;
    private View childView;
    private SessionManager sessionManager;

    @BindView(R.id.fragment_verification_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_verification_tv_mobileNumber)
    TextView mobileNumber;
    @BindView(R.id.fragment_verification_et_first)
    EditText first;
    @BindView(R.id.fragment_verification_et_second)
    EditText second;
    @BindView(R.id.fragment_verification_et_third)
    EditText third;
    @BindView(R.id.fragment_verification_et_forth)
    EditText forth;
    @BindView(R.id.fragment_verification_tv_error)
    TextView error;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static VerificationFragment newInstance(FragmentActivity activity) {
        fragment = new VerificationFragment();
        VerificationFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_verification, container, false);
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
        error.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.GONE);
        sessionManager = new SessionManager(activity);
        mobileNumber.setText(activity.getString(R.string.kuwaitCode) + " " + sessionManager.getPhoneNumber());
        writePins();
    }

    private void writePins() {
        first.requestFocus();

        first.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (first.getText().toString().length() == 1)
                    second.requestFocus();


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        second.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (first.getText().toString().length() == 1 && third.getText().toString().length() == 1)
                    ;
                else {
                    if (second.getText().toString().length() == 1)
                        third.requestFocus();
                    else
                        first.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        third.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (second.getText().toString().length() == 1 && forth.getText().toString().length() == 1)
                    ;
                else {
                    if (third.getText().toString().length() == 1)
                        forth.requestFocus();
                    else
                        second.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        forth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick(R.id.fragment_verification_iv_verificationBack)
    public void backClick() {
        activity.onBackPressed();
    }

    @OnClick(R.id.fragment_verification_ll_resendCode)
    public void resendCodeClick() {
        resendCodeApi();
    }

    @OnClick(R.id.fragment_verification_tv_verify)
    public void verifyClick() {
        String firstTxt = first.getText().toString();
        String secondTxt = second.getText().toString();
        String thirdTxt = third.getText().toString();
        String forthTxt = forth.getText().toString();
        if (firstTxt == null || firstTxt.isEmpty()
                || secondTxt == null || secondTxt.isEmpty()
                || thirdTxt == null || thirdTxt.isEmpty()
                || forthTxt == null || forthTxt.isEmpty()) {
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.INVISIBLE);
            String codeTxt = firstTxt + secondTxt + thirdTxt + forthTxt;
            verifyApi(codeTxt);
        }
    }

    private void verifyApi(String codeTxt) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().VERIFY(sessionManager.getUserToken(), sessionManager.getUserId(), codeTxt)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            Snackbar.make(childView, activity.getString(R.string.accountCreated), Snackbar.LENGTH_LONG).show();
                            Navigator.loadFragment(activity, HomeFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
                        } else if (responseCode == 201) {
                            Log.d(Constants.MAZAD, "user Not Found");
                        } else if (responseCode == 202) {
                            error.setVisibility(View.VISIBLE);
                            error.setText(activity.getString(R.string.wrongCode));
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

    private void resendCodeApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().RESEND_CODE(sessionManager.getUserToken(), sessionManager.getUserId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                        } else if (responseCode == 201) {
                            Log.d(Constants.MAZAD, "user Not Found");
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




