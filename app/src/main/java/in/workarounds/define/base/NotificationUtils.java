package in.workarounds.define.base;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import in.workarounds.define.DefineApp;
import in.workarounds.define.R;
import in.workarounds.define.portal.MainPortal;
import in.workarounds.portal.Portal;

/**
 * Created by Nithin on 29/10/15.
 */
public enum NotificationUtils {

    INSTANCE;
    private NotificationManager notificationManager;
    private Context context;
    public static final int SILENT_NOTIFICATION_ID = 201;

    NotificationUtils(){
        context = DefineApp.getContext();
        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Context getContext() {
        return context;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void sendSilentMeaningNotification(String text){
        sendMeaningNotification(text, NotificationCompat.PRIORITY_DEFAULT);
    }

    public void sendPriorityMeaningNotification(String text){
        sendMeaningNotification(text, NotificationCompat.PRIORITY_HIGH);
    }

    public void sendMeaningNotification(String text,int priority){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setPriority(priority)
                        .setVibrate(new long[0]) //mandatory for high priority,setting no vibration
                        .setContentTitle(getContext().getString(R.string.app_name))
                        .setContentText(getContext().getString(R.string.notification_content));
// Creates an explicit intent for clipboard Service

        Bundle bundle = new Bundle();
        bundle.putString(MainPortal.BUNDLE_KEY_CLIP_TEXT, text);
        Intent resultIntent =
                Portal.with(getContext())
                        .data(bundle)
                        .sendIntent(MainPortal.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getService(
                        getContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        getNotificationManager().notify(SILENT_NOTIFICATION_ID, mBuilder.build());
    }
}
