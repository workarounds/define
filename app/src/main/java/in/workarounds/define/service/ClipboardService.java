package in.workarounds.define.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.webkit.URLUtil;

import in.workarounds.define.portal.PortalId;
import in.workarounds.define.util.LogUtils;
import in.workarounds.portal.Portals;
import timber.log.Timber;

public class ClipboardService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener {
    private static final String TAG = LogUtils.makeLogTag(ClipboardService.class);
    private static boolean isRunning = false;
    public static final String BUNDLE_SELECTED_TEXT_KEY = "BUNDLE_SELECTED_TEXT_KEY";

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
}
