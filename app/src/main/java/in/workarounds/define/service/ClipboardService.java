package in.workarounds.define.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.webkit.URLUtil;

import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.portal.MainPortal;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.portal.Portal;
import in.workarounds.portal.PortalManager;

public class ClipboardService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener {
    private static final String TAG = LogUtils.makeLogTag(ClipboardService.class);
    private static boolean isRunning = false;
    private Handler notificationHandler;
    private Runnable notificationHandlerRunnable;

    @Override
    public void onCreate() {
        isRunning = true;
        notificationHandler = new Handler();
        ClipboardManager clipboardManager = getClipboardManager();
        clipboardManager.addPrimaryClipChangedListener(this);
        setNotificationClearer();
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
            startActionResolver(text);
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

    private void startActionResolver(String text) {
        int state = PortalManager.getPortalState(this, MainPortal.class);

        @UserPrefActivity.NotifyMode int notifyMode = PrefUtils.getNotifyMode(this);
        if(notifyMode == UserPrefActivity.OPTION_SILENT || notifyMode == UserPrefActivity.OPTION_PRIORITY) {
//Set notification priority as high for priority mode, default for silent mode
            int priority =  (notifyMode == UserPrefActivity.OPTION_PRIORITY)
                    ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT;

            NotificationUtils.INSTANCE.sendMeaningNotification(text, priority);

            if(PrefUtils.getNotificationAutoHideFlag(this)) {
                NotificationUtils.INSTANCE.cancelBackupNotification();
                notificationHandler.removeCallbacks(notificationHandlerRunnable);
                notificationHandler.postDelayed(notificationHandlerRunnable, 10000);
            }
        }
        else{
            startPortal(text);
        }
    }

    private void startPortal(String text){
        Bundle bundle = new Bundle();
        bundle.putString(MainPortal.BUNDLE_KEY_CLIP_TEXT, text);
        Portal.with(this).data(bundle).send(MainPortal.class);
    }

    private void setNotificationClearer(){
        notificationHandlerRunnable =  new Runnable() {
            @Override
            public void run() {
                NotificationUtils.INSTANCE.getNotificationManager().cancel(NotificationUtils.SILENT_NOTIFICATION_ID);
            }
        };
    }
}