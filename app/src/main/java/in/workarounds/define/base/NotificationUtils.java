package in.workarounds.define.base;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import in.workarounds.define.R;
import in.workarounds.define.portal.MeaningPortal;
import in.workarounds.define.portal.PortalId;
import in.workarounds.define.service.DefinePortalService;
import in.workarounds.portal.Portals;

import static in.workarounds.define.constants.NotificationId.SILENT_BACKUP_NOTIFICATION;
import static in.workarounds.define.constants.NotificationId.SILENT_NOTIFICATION;

/**
 * Created by Nithin on 29/10/15.
 */
public class NotificationUtils {

    private NotificationManager notificationManager;
    private Context context;

    public NotificationUtils(Context context){
        this.context = context;
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
        getNotificationManager().cancel(SILENT_BACKUP_NOTIFICATION);
    }

    public void sendSilentBackupNotification(String text){
        sendNotification(text, SILENT_BACKUP_NOTIFICATION, NotificationCompat.PRIORITY_DEFAULT);
    }

    public void sendSilentMeaningNotification(String text){
        sendNotification(text, SILENT_NOTIFICATION, NotificationCompat.PRIORITY_DEFAULT);
    }

    public void sendPriorityMeaningNotification(String text){
        sendNotification(text, SILENT_NOTIFICATION, NotificationCompat.PRIORITY_HIGH);
    }

    public void sendMeaningNotification(String text,int priority){
        sendNotification(text, SILENT_NOTIFICATION, priority);
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
        bundle.putString(MeaningPortal.BUNDLE_KEY_CLIP_TEXT, text);
        Intent resultIntent = Portals.openIntent(PortalId.MEANING_PORTAL, bundle, context, DefinePortalService.class);
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
