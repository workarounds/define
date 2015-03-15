package in.workarounds.define.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import in.workarounds.define.R;

/**
 * Created by mouli on 15/3/15.
 */
public class NotificationUtils {
    private static final String TAG = LogUtils.makeLogTag(NotificationUtils.class);
    public static final int NOTIFICATION_ID = 1;

    @SuppressLint("NewApi")
    public static void setNotification(Context context, Intent intent, String title, String contentText, boolean isBig) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);
        if(isBig) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notificationBuilder.setStyle(new Notification.BigTextStyle()
                        .bigText(contentText));
            }
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationBuilder!=null) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public static void setNotification(Context context, Intent intent, String title, String contextText) {
        setNotification(context, intent, title, contextText, false);
    }

    public static void setNotification(Context context, Intent intent, String title) {
        setNotification(context, intent, title, context.getString(R.string.hint_no_meaning));
    }

    public static void setNotification(Context context, Intent intent) {
        setNotification(context, intent, context.getString(R.string.app_name), context.getString(R.string.hint_multiple_words_selected), true);
    }
}
