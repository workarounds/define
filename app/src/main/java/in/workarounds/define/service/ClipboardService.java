package in.workarounds.define.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.webkit.URLUtil;

import in.workarounds.define.R;
import in.workarounds.define.portal.PortalId;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.portal.Portals;
import timber.log.Timber;

import static in.workarounds.define.constants.NotificationId.FOREGROUND_NOTIFICATION;
import static in.workarounds.define.constants.NotificationId.PENDING_FOREGROUND_NOTIFICATION;
import static in.workarounds.define.util.PrefUtils.KEY_NOTIFICATION_CLIPBOARD_SERVICE;

public class ClipboardService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static boolean isRunning = false;
    public static final String BUNDLE_SELECTED_TEXT_KEY = "BUNDLE_SELECTED_TEXT_KEY";

    @Override
    public void onCreate() {
        isRunning = true;
        if (PrefUtils.getNotifyClipboardService(this)) startForeground();
        PrefUtils.addListener(this, this);

        ClipboardManager clipboardManager = getClipboardManager();
        clipboardManager.addPrimaryClipChangedListener(this);
    }

    private void startForeground() {
        startForeground(FOREGROUND_NOTIFICATION, getClipServiceNotification(this));
    }

    private ClipboardManager getClipboardManager() {
        return (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onPrimaryClipChanged() {
        String text = getClipData();
        if(!TextUtils.isEmpty(text)) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_SELECTED_TEXT_KEY,text);
            Timber.d("Starting portlet");
            Portals.open(PortalId.UTIL_PORTAL, bundle, this, DefinePortalService.class);
        }
    }

    private String getClipData() {
        ClipData clipData = getClipboardManager().getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
        CharSequence text = item.getText();
        if (text != null && !URLUtil.isValidUrl(text.toString())) {
            return text.toString();
        } else {
            return null;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_NOTIFICATION_CLIPBOARD_SERVICE)) {
            if (PrefUtils.getNotifyClipboardService(this)) startForeground();
            else stopForeground(true);
        }
    }

    public static Notification getClipServiceNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.clip_service_notification))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setColor(ContextCompat.getColor(context, R.color.portal_foreground_notification));
        PendingIntent prefActivityIntent = PendingIntent.getActivity(
                context,
                PENDING_FOREGROUND_NOTIFICATION,
                new Intent(context, UserPrefActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(prefActivityIntent);
        return builder.build();
    }
}
