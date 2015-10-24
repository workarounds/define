package in.workarounds.define.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.webkit.URLUtil;

import in.workarounds.define.R;
import in.workarounds.define.portal.MainPortal;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.portal.Portal;

public class ClipboardService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener {
    private static final String TAG = LogUtils.makeLogTag(ClipboardService.class);
    private static boolean isRunning = false;
    private static final String INTENT_SILENT_NOTIFICATION_CLICK_SELECTED_TEXT_KEY
            = "INTENT_SILENT_NOTIFICATION_CLICK_SELECTED_TEXT_KEY";
    private static final int SILENT_NOTIFICATION_ID = 201;

    @Override
    public void onCreate() {
        isRunning = true;
        ClipboardManager clipboardManager = getClipboardManager();
        clipboardManager.addPrimaryClipChangedListener(this);
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
        if(intent != null && intent.hasExtra(INTENT_SILENT_NOTIFICATION_CLICK_SELECTED_TEXT_KEY)){
            startPortal(intent.getStringExtra(INTENT_SILENT_NOTIFICATION_CLICK_SELECTED_TEXT_KEY));
            //remove notification
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(SILENT_NOTIFICATION_ID);
        }
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
        startActionResolver(text);
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

    private void startActionResolver(String text) {
        @UserPrefActivity.NotifyMode int notifyMode = PrefUtils.getNotifyMode(UserPrefActivity.OPTION_DIRECT,this);
        if(notifyMode == UserPrefActivity.OPTION_SILENT || notifyMode == UserPrefActivity.OPTION_PRIORITY) {
//Set notification priority as high for priority mode, default for silent mode
            int priority =  (notifyMode == UserPrefActivity.OPTION_PRIORITY)
                    ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT;

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_notification_icon)
                            .setPriority(priority)
                            .setVibrate(new long[0]) //mandatory for high priority,setting no vibration
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.notification_content));
// Creates an explicit intent for clipboard Service
            Intent resultIntent = new Intent(this, ClipboardService.class);
            resultIntent.putExtra(INTENT_SILENT_NOTIFICATION_CLICK_SELECTED_TEXT_KEY, text);
            PendingIntent resultPendingIntent =
                    PendingIntent.getService(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// SILENT_NOTIFICATION_ID allows you to update/remove the notification later on.
            mNotificationManager.notify(SILENT_NOTIFICATION_ID, mBuilder.build());
        }
        else{
            startPortal(text);
        }
    }

    private void startPortal(String text){
        // TODO check if portal already open. If yes send data and don't re-open
        Bundle bundle = new Bundle();
        bundle.putString(MainPortal.BUNDLE_KEY_CLIP_TEXT, text);
        Portal.with(this).type(MainPortal.class).data(bundle).open();
    }
}
