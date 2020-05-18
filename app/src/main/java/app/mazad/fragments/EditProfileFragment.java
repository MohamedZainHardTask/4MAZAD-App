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
import app.mazad.classes.FixControl;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.models.UserModel;
import app.mazad.webservices.requests.EditProfileRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    public static FragmentActivity activity;
    public static EditProfileFragment fragment;

    private View childView;
    private SessionManager sessionManager;
    private UserModel userData;
    private String emailTxt, fullNameTxt, civilIdTxt, userNameTxt, mobileTxt;

    @BindView(R.id.fragment_edit_profile_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_edit_profile_il_emailContainer)
    TextInputLayout emailContainer;
    @BindView(R.id.fragment_edit_profile_il_fullNameContainer)
    TextInputLayout fullNameContainer;
    @BindView(R.id.fragment_edit_profile_il_civilIdContainer)
    TextInputLayout civilIdContainer;
    @BindView(R.id.fragment_edit_profile_il_mobileContainer)
    TextInputLayout mobileContainer;
    @BindView(R.id.fragment_edit_profile_il_userNameContainer)
    TextInputLayout userNameContainer;
    @BindView(R.id.fragment_edit_profile_et_email)
    TextInputEditText email;
    @BindView(R.id.fragment_edit_profile_et_fullName)
    TextInputEditText fullName;
    @BindView(R.id.fragment_edit_profile_et_civilId)
    TextInputEditText civilId;
    @BindView(R.id.fragment_edit_profile_et_mobile)
    TextInputEditText mobile;
    @BindView(R.id.fragment_edit_profile_et_userName)
    TextInputEditText userName;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static EditProfileFragment newInstance(FragmentActivity activity) {
        fragment = new EditProfileFragment();
        EditProfileFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(true, true, false, false, activity.getString(R.string.editProfile));
        sessionManager = new SessionManager(activity);
        FixControl.setupUI(activity, container);

        if (userData == null) {
            userDataApi();
        } else {
            loading.setVisibility(View.GONE);
        }

    }

    private void userDataApi() {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().USER_DATA(sessionManager.getUserToken(), sessionManager.getUserId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            userData = handleUserDataResponse(response);
                            if (userData != null) {
                                fullName.setText(userData.fullName);
                                mobile.setText(userData.mobile);
                                email.setText(userData.email);
                                civilId.setText(userData.civilId);
                                userName.setText(userData.userName);
                            }
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

    private UserModel handleUserDataResponse(Response<ResponseBody> response) {
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
        UserModel user = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<UserModel>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            user = new Gson().fromJson(jsonResponse, type);
        }
        return user;
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

    @OnClick(R.id.fragment_edit_profile_tv_save)
    public void saveCLick() {
        checkName();
        checkEmail();
        checkCivilId();
        checkMobile();
        checkUserName();
        if (fullNameContainer.getError() == null && emailContainer.getError() == null
                && civilIdContainer.getError() == null && mobileContainer.getError() == null
                && userNameContainer.getError() == null) {
            EditProfileRequest request = new EditProfileRequest();
            request.id = sessionManager.getUserId();
            request.fullName = fullNameTxt;
            request.email = emailTxt;
            request.civilId = civilIdTxt;
            request.mobile = mobileTxt;
            request.userName = userNameTxt;
            updateUserDataApi(request);
        }
    }

    private void updateUserDataApi(EditProfileRequest request) {
        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().UPDATE_USER_DATA(
                sessionManager.getUserToken(),
                request)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            Snackbar.make(childView, activity.getString(R.string.profileUpdated), Snackbar.LENGTH_LONG).show();
                            clearFragments(1);
                        } else if (responseCode == 201) {
                            emailContainer.setError(activity.getString(R.string.emailAlreadyExist));
                        } else if (responseCode == 202) {
                            userNameContainer.setError(activity.getString(R.string.userNameAlreadyExist));
                        } else if (responseCode == 203) {
                            mobileContainer.setError(activity.getString(R.string.mobileAlreadyExist));
                        } else if (responseCode == 204) {
                            civilIdContainer.setError(activity.getString(R.string.civilIdAlreadyExist));
                        } else if (responseCode == 205) {
                            civilIdContainer.setError(activity.getString(R.string.civilIdNotValid));
                        } else if (responseCode == 206) {
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

    private void checkName() {
        fullNameTxt = fullName.getText().toString();
        if (fullNameTxt == null || fullNameTxt.isEmpty()) {
            fullNameContainer.setError(activity.getString(R.string.enterName));
        } else {
            fullNameContainer.setError(null);
        }
    }

    private void checkEmail() {
        emailTxt = email.getText().toString();
        if (emailTxt == null || emailTxt.isEmpty()) {
            emailContainer.setError(activity.getString(R.string.enterEmail));
        } else {
            if (!FixControl.isValidEmail(emailTxt)) {
                emailContainer.setError(activity.getString(R.string.invalidEmail));
            } else {
                emailContainer.setError(null);
            }
        }
    }

    private void checkCivilId() {
        civilIdTxt = civilId.getText().toString();
        if (civilIdTxt == null || civilIdTxt.isEmpty()) {
            civilIdContainer.setError(activity.getString(R.string.enterCivilId));
        } else {
            if (!FixControl.checkCivilID_12(civilIdTxt) || !GlobalFunctions.validateCivilID(civilIdTxt)) {
                civilIdContainer.setError(activity.getString(R.string.invalidCivilId));
            } else {
                civilIdContainer.setError(null);
            }
        }
    }

    private void checkMobile() {
        mobileTxt = mobile.getText().toString();
        if (mobileTxt == null || mobileTxt.isEmpty()) {
            mobileContainer.setError(activity.getString(R.string.enterMobile));
        } else {
            if (!FixControl.isValidPhone(mobileTxt)) {
                mobileContainer.setError(activity.getString(R.string.invalidMobile));
            } else {
                mobileContainer.setError(null);
            }
        }
    }

    private void checkUserName() {
        userNameTxt = userName.getText().toString();
        if (userNameTxt == null || userNameTxt.isEmpty()) {
            userNameContainer.setError(activity.getString(R.string.enterUserName));
        } else {
            userNameContainer.setError(null);
        }
    }
    private void clearFragments(int count) {
        for (int i = 1; i <= count; i++) {
            getFragmentManager().popBackStack();
        }
    }
}