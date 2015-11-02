package in.workarounds.define.base;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import in.workarounds.define.DefineApp;
import in.workarounds.define.R;
import in.workarounds.define.portal.MainPortal;
import in.workarounds.portal.Portal;

/**
 * Created by Nithin on 29/10/15.
 */
public enum NotificationUtils {

    INSTANCE;
    public static final int SILENT_NOTIFICATION_ID = 201;
    public static final int SILENT_BACKUP_NOTIFICATION_ID = 202;
    private NotificationManager notificationManager;
    private Context context;

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

    public void cancelBackupNotification(){
        getNotificationManager().cancel(SILENT_BACKUP_NOTIFICATION_ID);
    }

    public void sendSilentBackupNotification(String text){
        sendNotification(text, SILENT_BACKUP_NOTIFICATION_ID, NotificationCompat.PRIORITY_DEFAULT);
    }

    public void sendSilentMeaningNotification(String text){
        sendNotification(text,SILENT_NOTIFICATION_ID, NotificationCompat.PRIORITY_DEFAULT);
    }

    public void sendPriorityMeaningNotification(String text){
        sendNotification(text,SILENT_NOTIFICATION_ID, NotificationCompat.PRIORITY_HIGH);
    }

    public void sendMeaningNotification(String text,int priority){
        sendNotification(text,SILENT_NOTIFICATION_ID, priority);
    }

    public void sendNotification(String text,int notificationid,int priority){
        int color = ContextCompat.getColor(getContext(), R.color.theme_primary);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setPriority(priority)
                        .setColor(color)
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

        getNotificationManager().notify(notificationid, mBuilder.build());
    }
}
