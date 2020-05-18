package app.mazad.pushNotifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.SessionManager;


public class MyFCMListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d("onMessageReceived1", "Notification Message Body: " +
                remoteMessage.getData() + "");
        Log.d("onMessageReceived2", "From: " + remoteMessage.getFrom());
        Log.d("onMessageReceived3", "id: " + remoteMessage.getData().get("I"));
        Log.d("onMessageReceived3", "type: " + remoteMessage.getData().get("T"));
        Log.d("onMessageReceived3", "message: " + remoteMessage.getData().get("message"));
        Id = remoteMessage.getData().get("I");
        type = remoteMessage.getData().get("T");
        // isSpecial = remoteMessage.getData().values().toArray()[0].toString();
        Log.d("onMessageReceived3", "message: " + remoteMessage.getData().get("message"));
        Map data = remoteMessage.getData();

        Log.d("onMessageReceived3", "From: " + remoteMessage.getData());
        Log.d("onMessageReceived", "Notification Message Body: " +
                remoteMessage.getData());
        Log.d("onMessageReceived", "Notification Message Body: " +
                remoteMessage.toString());

        //sendNotification(remoteMessage.getData().get("message"));

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Log.e("onMessageReceived", "Data Payload: " + remoteMessage.getData().toString());

            if (new SessionManager(this).isNotificationOn()) {

                handleDataMessage(remoteMessage);

            }

        }

    }

    String Id = "", type = "";

    NotificationUtils notificationUtils;

    private void handleDataMessage(RemoteMessage data) {

        Log.e("handleDataMessage", "push json: " + data.toString());

        String type = data.getData().get("T") + "";

        String Id = data.getData().get("I") + "";

        String message = data.getData().get("message");

        //boolean isBackground = data.getBoolean("is_background");


        // app is in background, show the notification in notification tray
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        resultIntent.putExtra("message", message);

        resultIntent.putExtra("type", type);

        if (Id != null && Id.length() > 0)
            resultIntent.putExtra("Id", Id);

        // check for image attachment


        showNotificationMessage(getApplicationContext(), getString(R.string.app_name), message, resultIntent);

    }


    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent);

    }
}