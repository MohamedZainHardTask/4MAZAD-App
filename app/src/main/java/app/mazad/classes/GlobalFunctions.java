package app.mazad.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;


public class GlobalFunctions {
    public static SessionManager sessionManager;

    public static void setDefaultLanguage(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        String language = sessionManager.getUserLanguage();
        if (language.equals(Constants.AR)) {
            MainActivity.isEnglish = false;
            LocaleHelper.setLocale(context, Constants.AR);
        } else {
            MainActivity.isEnglish = true;
            LocaleHelper.setLocale(context, Constants.EN);
            if (language == null || language.isEmpty()) {
                sessionManager.setUserLanguage(Constants.EN);
            }
        }
    }

    public static void changeLanguage(Context context) {
         /*for changing the language of App
        1- check the value of currentLanguage  and Reflects it
         2- set the new value of Language in local and change the value of languageSharedPreference to new value
         3- restart the mainActivity with noAnimation
        * */

        if (sessionManager.getUserLanguage().equals(Constants.AR)) {
            sessionManager.setUserLanguage(Constants.EN);
            MainActivity.isEnglish = true;
        } else {
            sessionManager.setUserLanguage(Constants.AR);
            MainActivity.isEnglish = false;
        }

        LocaleHelper.setLocale(context, sessionManager.getUserLanguage());
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(0, 0);
        ((Activity) context).startActivity(new Intent(context, MainActivity.class));
        GlobalFunctions.setUpFont(context);
    }

    public static void setUpFont(Context context) {
        sessionManager = new SessionManager(context);
        if (sessionManager.getUserLanguage().equals(Constants.AR)) {
            ViewPump.init(ViewPump.builder()
                    .addInterceptor(new CalligraphyInterceptor(
                            new CalligraphyConfig.Builder()
                                    .setDefaultFontPath("droid_arabic_kufi.ttf")
                                    .setFontAttrId(R.attr.fontPath)
                                    .build()))
                    .build());
        }
    }

    public static void DisableLayout(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                DisableLayout((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    public static void EnableLayout(ViewGroup layout) {
        layout.setEnabled(true);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                EnableLayout((ViewGroup) child);
            } else {
                child.setEnabled(true);
            }
        }
    }

    public static boolean validateCivilID(String civilID) {
        boolean valid = false;

        /*Log.d("a1", "1 -> "+civilID.substring(0, 1));

        Log.d("a1", "2 -> "+civilID.substring(1, 2));

        int a1 = (Integer.parseInt(civilID.substring(0, 1)) * 2);

        Log.d("a1", ""+a1);

        int a1 = (Integer.parseInt(civilID.substring(1, 1)) * 1);

        int a1 = (Integer.parseInt(civilID.substring(0, 1)) * 2);*/

        double test = 11 - (((Integer.parseInt(civilID.substring(0, 1)) * 2) +
                (Integer.parseInt(civilID.substring(1, 2)) * 1) +
                (Integer.parseInt(civilID.substring(2, 3)) * 6) +
                (Integer.parseInt(civilID.substring(3, 4)) * 3) +
                (Integer.parseInt(civilID.substring(4, 5)) * 7) +
                (Integer.parseInt(civilID.substring(5, 6)) * 9) +
                (Integer.parseInt(civilID.substring(6, 7)) * 10) +
                (Integer.parseInt(civilID.substring(7, 8)) * 5) +
                (Integer.parseInt(civilID.substring(8, 9)) * 8) +
                (Integer.parseInt(civilID.substring(9, 10)) * 4) +
                (Integer.parseInt(civilID.substring(10, 11)) * 2)) % 11);

        double checksumValue = Double.parseDouble(civilID.substring(11));

        if (test == checksumValue) {
            valid = true;
        }

        return valid;
    }

    public static String formatDate(String date) {
        String dateResult = "";
        Locale locale = new Locale("en");


        SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale);
        SimpleDateFormat dateFormatter2 = new SimpleDateFormat("dd/MM/yyyy", locale);

        //SimpleDateFormat dateFormatter2 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa", locale);

        int index = date.lastIndexOf('/');

        try {

            dateResult = dateFormatter2.format(dateFormatter1.parse(date.substring(index + 1)));

        } catch (ParseException e) {

            e.printStackTrace();

        }

        return dateResult;
    }

    public static String formatDateAndTime(String dateAndTime) {
        String dateResult = "";
        Locale locale = new Locale("en");


        SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale);
        SimpleDateFormat dateFormatter2 = new SimpleDateFormat("dd/MM/yyyy hh:mm aaa", locale);
        int index = dateAndTime.lastIndexOf('/');

        try {

            dateResult = dateFormatter2.format(dateFormatter1.parse(dateAndTime.substring(index + 1)));

        } catch (ParseException e) {

            e.printStackTrace();

        }

        dateFormatter2.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormatter2.parse(dateResult);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateFormatter2.setTimeZone(TimeZone.getDefault());
        String formattedDate = dateFormatter2.format(date);
        return formattedDate;
    }


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected() && netInfo.isAvailable()) {
            return true;
        } else {
            return false;
        }

    }

    public static void generalErrorMessage(Context context,View childView,ProgressBar loading) {
        loading.setVisibility(View.GONE);
        Snackbar.make(childView, context.getString(R.string.generalError), Snackbar.LENGTH_SHORT).show();
    }

}

