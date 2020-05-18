package app.mazad.classes;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager {
    Context context;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    public static final String USER_PREF = "user_preference";
    private static final String IS_LOGGED = "is_logged";
    private static final String USER_ID = "id";
    private static final String LANGUAGE_CODE = "language_lode";
    private static final String USER_TOKEN = "user_token";
    private static final String IS_GUEST = "is_guest";
    private static final String HAS_PACKAGE = "has_package";
    private static final String SHOW_TUTORIAL = "show_tutorial";
    private static final String PHONE_NUMBER = "phone_number";
    private static String IS_NOTIFICATION_ON = "is_Notification_on";
    private static final String REG_ID = "reg_id";

    public SessionManager(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void LoginSession() {
        editor.putBoolean(IS_LOGGED, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPref.getBoolean(IS_LOGGED, false);
    }

    public void guestSession() {
        editor.putBoolean(IS_GUEST, true);
        editor.apply();
    }

    public boolean isGuest() {
        return sharedPref.getBoolean(IS_GUEST, false);
    }

    public void guestLogout() {
        editor.putBoolean(IS_GUEST, false);
        editor.apply();
    }

    public void logout() {
        setUserId(0);
        setUserToken(null);
        setPackage(false);
        setPhoneNumber(null);
        editor.putBoolean(IS_LOGGED, false);
        editor.apply();
    }

    public void setUserToken(String token) {
        editor.putString(USER_TOKEN, "bearer" + " " + token);
        editor.apply();
    }

    public String getUserToken() {
        return sharedPref.getString(USER_TOKEN, "");
    }

    public void setUserId(int id) {
        editor.putInt(USER_ID, id);
        editor.apply();
    }

    public int getUserId() {
        return sharedPref.getInt(USER_ID, 0);
    }

    public void setUserLanguage(String languageCode) {
        editor.putString(LANGUAGE_CODE, languageCode);
        editor.apply();
    }

    public String getUserLanguage() {
        return sharedPref.getString(LANGUAGE_CODE, "");
    }

    public void setPackage(boolean hasPackage) {
        editor.putBoolean(HAS_PACKAGE, hasPackage);
        editor.apply();
    }

    public boolean hasPackage() {
        return sharedPref.getBoolean(HAS_PACKAGE, false);
    }

    public void setPhoneNumber(String phoneNumber) {
        editor.putString(PHONE_NUMBER, phoneNumber);
        editor.apply();
    }

    public String getPhoneNumber() {
        return sharedPref.getString(PHONE_NUMBER, "");
    }

    public void setNotification(boolean status) {
        editor.putBoolean(IS_NOTIFICATION_ON, status);
        editor.commit();
    }

    public boolean isNotificationOn() {
        return sharedPref.getBoolean(IS_NOTIFICATION_ON, true);
    }

    public String getRegId() {
        return sharedPref.getString(REG_ID, "");
    }

    public void setRegId(String id) {
        editor.putString(REG_ID, id);
        editor.commit();
    }

    public void setShowTutorial(boolean showTutorial) {
        editor.putBoolean(SHOW_TUTORIAL, showTutorial);
        editor.commit();
    }

    public boolean isShowTutorial()
    {
        return sharedPref.getBoolean(SHOW_TUTORIAL, false);
    }

}
