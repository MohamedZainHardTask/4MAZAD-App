package app.mazad.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.AppController;
import app.mazad.classes.Constants;
import app.mazad.classes.FixControl;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import app.mazad.webservices.responses.AuthResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends Fragment {
    public static FragmentActivity activity;
    public static SignUpFragment fragment;
    private View childView;
    private SessionManager sessionManager;
    private String emailTxt, fullNameTxt, civilIdTxt, userNameTxt, mobileTxt, passTxt, confirmPassTxt;
    private String civilIdFrontPath, civilIdBackPath;
    private boolean isFront;
    private String regId;

    @BindView(R.id.fragment_sign_up_cl_container)
    ConstraintLayout container;
    @BindView(R.id.fragment_sign_up_il_emailContainer)
    TextInputLayout emailContainer;
    @BindView(R.id.fragment_sign_up_il_fullNameContainer)
    TextInputLayout fullNameContainer;
    @BindView(R.id.fragment_sign_up_il_civilIdContainer)
    TextInputLayout civilIdContainer;
    @BindView(R.id.fragment_sign_up_il_mobileContainer)
    TextInputLayout mobileContainer;
    @BindView(R.id.fragment_sign_up_il_userNameContainer)
    TextInputLayout userNameContainer;
    @BindView(R.id.fragment_sign_up_il_passContainer)
    TextInputLayout passContainer;
    @BindView(R.id.fragment_sign_up_il_confirmPassContainer)
    TextInputLayout confirmPassContainer;
    @BindView(R.id.fragment_sign_up_et_email)
    TextInputEditText email;
    @BindView(R.id.fragment_sign_up_et_fullName)
    TextInputEditText fullName;
    @BindView(R.id.fragment_sign_up_et_civilId)
    TextInputEditText civilId;
    @BindView(R.id.fragment_sign_up_et_mobile)
    TextInputEditText mobile;
    @BindView(R.id.fragment_sign_up_et_userName)
    TextInputEditText userName;
    @BindView(R.id.fragment_sign_up_et_pass)
    TextInputEditText pass;
    @BindView(R.id.fragment_sign_up_et_confirmPass)
    TextInputEditText confirmPass;
    @BindView(R.id.fragment_sign_up_iv_frontCivilId)
    ImageView frontCivilId;
    @BindView(R.id.fragment_sign_up_iv_backCivilId)
    ImageView backCivilId;
    @BindView(R.id.fragment_sign_up_tv_error)
    TextView error;
    @BindView(R.id.fragment_sign_up_cb_agree)
    CheckBox agree;
    @BindView(R.id.fragment_sign_up_tv_changeLang)
    TextView changeLang;
    @BindView(R.id.loading)
    ProgressBar loading;

    public static SignUpFragment newInstance(FragmentActivity activity) {
        fragment = new SignUpFragment();
        SignUpFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_sign_up, container, false);
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
        error.setVisibility(View.GONE);
        sessionManager = new SessionManager(activity);
        //make the input type passwordType programmatically because the passwordType in xml make the font not work
        pass.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        pass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //make the input type passwordType programmatically because the passwordType in xml make the font not work
        confirmPass.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        if (!sessionManager.getUserLanguage().equals(Constants.AR)) {
            Typeface arBold = Typeface.createFromAsset(activity.getAssets(), "droid_arabic_kufi.ttf");
            changeLang.setTypeface(arBold);
        }
    }

    @OnClick(R.id.fragment_sign_up_iv_frontCivilId)
    public void frontCivilIdClick() {
        isFront = true;
        pickImage();
    }

    @OnClick(R.id.fragment_sign_up_iv_backCivilId)
    public void backCivilIdClick() {
        isFront = false;
        pickImage();
    }

    private void pickImage() {
        ImagePicker.with(this)              //  Initialize ImagePicker with activity or fragment context
                .setCameraOnly(false)               //  Camera mode
                .setMultipleMode(false)              //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setShowCamera(true)                //  Show camera button
                .setMaxSize(1)                     //  Max images can be selected
                .setSavePath("ImagePicker")         //  Image capture folder name
                .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loading.setVisibility(View.GONE);
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null) {
                for (Image uri : images) {
                    if (isFront) {
                        civilIdFrontPath = uri.getPath();
                        setImage(civilIdFrontPath, frontCivilId);
                    } else {
                        civilIdBackPath = uri.getPath();
                        setImage(civilIdBackPath, backCivilId);
                    }
                }
            }
        }
    }

    private void setImage(String imagePath, ImageView image) {
        if (imagePath != null) {
            try {
                int Width = FixControl.getImageWidth(activity, R.drawable.upload_btn);
                int Height = FixControl.getImageHeight(activity, R.drawable.upload_btn);
                image.getLayoutParams().height = Height;
                image.getLayoutParams().width = Width;
                Glide.with(activity.getApplicationContext()).load(imagePath)
                        .apply(new RequestOptions().placeholder(R.drawable.upload_btn).centerCrop())
                        .into(image);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @OnClick(R.id.fragment_sign_up_iv_signUpBack)
    public void backClick() {
        activity.onBackPressed();
    }

    @OnClick(R.id.fragment_sign_up_tv_termsAndConditions)
    public void termsAndConditionsClick() {
        Navigator.loadFragment(activity, ContentFragment.newInstance(activity, Constants.TERMS), R.id.activity_main_fl_appContainer, true);
    }

    @OnClick(R.id.fragment_sign_up_tv_changeLang)
    public void changeLangClick() {
        GlobalFunctions.changeLanguage(activity);
    }

    @OnClick(R.id.fragment_sign_up_tv_signUp)
    public void signUpClick() {
        checkName();
        checkEmail();
        checkCivilId();
        checkMobile();
        checkUserName();
        checkPassword();
        checkCivilIdImages();

        if (!agree.isChecked()) {
            Snackbar.make(childView, activity.getString(R.string.agreeForTerms), Snackbar.LENGTH_SHORT).show();
        }
        if (fullNameContainer.getError() == null && emailContainer.getError() == null
                && civilIdContainer.getError() == null && mobileContainer.getError() == null
                && userNameContainer.getError() == null && passContainer.getError() == null
                && confirmPassContainer.getError() == null && civilIdFrontPath != null
                && civilIdBackPath != null && agree.isChecked()) {
            signUpApi();
        }
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

    private void checkPassword() {
        passTxt = pass.getText().toString();
        if (passTxt == null || passTxt.isEmpty()) {
            passContainer.setError(activity.getString(R.string.enterPassword));
        } else {
            if (passTxt.length() < 6) {
                passContainer.setError(activity.getString(R.string.invalidPass));
            } else {
                passContainer.setError(null);
            }
        }
        confirmPassTxt = confirmPass.getText().toString();
        if (confirmPassTxt == null || confirmPassTxt.isEmpty()) {
            confirmPassContainer.setError(activity.getString(R.string.enterPassword));
        } else {
            if (confirmPassTxt.length() < 6) {
                confirmPassContainer.setError(activity.getString(R.string.invalidPass));
            } else {
                if (!passTxt.equals(confirmPassTxt)) {
                    confirmPassContainer.setError(activity.getString(R.string.mismatchPass));
                } else {
                    confirmPassContainer.setError(null);
                }
            }
        }
    }

    private void checkCivilIdImages() {
        if (civilIdFrontPath == null || civilIdFrontPath.isEmpty()
                || civilIdBackPath == null || civilIdBackPath.isEmpty()) {
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }

    private void signUpApi() {
        RequestBody fullNameReq = RequestBody.create(MediaType.parse("text/plain"), fullNameTxt);
        RequestBody mobileReq = RequestBody.create(MediaType.parse("text/plain"), mobileTxt);
        RequestBody emailReq = RequestBody.create(MediaType.parse("text/plain"), emailTxt);
        RequestBody civilIdReq = RequestBody.create(MediaType.parse("text/plain"), civilIdTxt);
        RequestBody passReq = RequestBody.create(MediaType.parse("text/plain"), passTxt);
        RequestBody userNameReq = RequestBody.create(MediaType.parse("text/plain"), userNameTxt);

        File civilIdFrontFile = new File(civilIdFrontPath);
        RequestBody civilIdFrontRequestBody = RequestBody.create(MediaType.parse("image/*"), civilIdFrontFile);
        MultipartBody.Part civilIdFrontPart = MultipartBody.Part.createFormData("CivilIdFront", civilIdFrontFile.getName(), civilIdFrontRequestBody);

        File civilIdBackFile = new File(civilIdBackPath);
        RequestBody civilIdBackRequestBody = RequestBody.create(MediaType.parse("image/*"), civilIdBackFile);
        MultipartBody.Part civilIdBackPart = MultipartBody.Part.createFormData("CivilIdBack", civilIdBackFile.getName(), civilIdBackRequestBody);

        loading.setVisibility(View.VISIBLE);
        GlobalFunctions.DisableLayout(container);
        RetrofitConfig.getServices().SIGN_UP(fullNameReq, mobileReq, emailReq, civilIdReq,
                passReq, userNameReq, civilIdFrontPart, civilIdBackPart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setVisibility(View.GONE);
                        GlobalFunctions.EnableLayout(container);
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            AuthResponse authentication = handleSignUpResponse(response);
                            sessionManager.setUserToken(authentication.token);
                            sessionManager.setUserId(authentication.user.id);
                            sessionManager.setPhoneNumber(authentication.user.mobile);
                            if (sessionManager.isGuest()) {
                                sessionManager.guestLogout();
                            }
                            sessionManager.LoginSession();
                            registrationFireBase();
                            if (authentication.user.isActive) {
                                Navigator.loadFragment(activity, HomeFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
                            } else {
                                Navigator.loadFragment(activity, VerificationFragment.newInstance(activity), R.id.activity_main_fl_appContainer, true);
                            }
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

    private AuthResponse handleSignUpResponse(Response<ResponseBody> response) {
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
        AuthResponse authentication = null;
        if (outResponse != null) {
            outResponse = outResponse.replace("\"", "");
            outResponse = outResponse.replace("\n", "");
            Type type = new TypeToken<AuthResponse>() {
            }.getType();
            JsonReader reader = new JsonReader(new StringReader(outResponse));
            reader.setLenient(true);
            authentication = new Gson().fromJson(jsonResponse, type);
        }
        return authentication;
    }

    private void addDeviceToken(final String regId) {
        RetrofitConfig.getServices().ADD_DEVICE_TOKEN(sessionManager.getUserId(), regId
                , 2, AppController.getInstance().getDeviceID()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int responseCode = response.code();
                if (responseCode == 200) {
                    sessionManager.setRegId(regId);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void registrationFireBase() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("login", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        regId = task.getResult().getToken();

                        Log.e("registrationId", "regId -> " + regId);

                        addDeviceToken(regId);
                    }
                });
    }

}
