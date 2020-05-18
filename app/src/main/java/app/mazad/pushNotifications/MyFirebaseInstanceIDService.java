package app.mazad.pushNotifications;

import android.os.AsyncTask;

import com.google.firebase.messaging.FirebaseMessagingService;

import app.mazad.classes.AppController;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.RetrofitConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    SessionManager sessionManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sessionManager = new SessionManager(this);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(final String regId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                msg = "Device registered, registration ID=" + regId;
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
                insertTokenApi(regId);
            }
        }.execute(null, null, null);
    }

    private void insertTokenApi(final String regId) {
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

}
