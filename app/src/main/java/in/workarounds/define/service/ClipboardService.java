package in.workarounds.define.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.webkit.URLUtil;

import in.workarounds.define.ui.activity.TestActivity;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.PrefUtils;

public class ClipboardService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener {
    private static final String TAG = LogUtils.makeLogTag(ClipboardService.class);
    private static boolean isRunning = false;

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
        if (!PopupManager.isPopupShown()) {
            String text = getClipData();
            startActionResolver(text);
        }
    }

    private String getClipData() {
        ClipData clipData = getClipboardManager().getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
        CharSequence text = item.getText();
        if(text != null && !URLUtil.isValidUrl(text.toString())) {
            return text.toString();
        } else {
            return null;
        }
    }

    private void startActionResolver(String text) {
        if (text != null) {
            Intent intent;
            boolean useUIService = PrefUtils.getSharedPreferences(this)
                    .getBoolean(TestActivity.KEY_USE_UI_SERVICE, false);
            if(useUIService) {
                intent = new Intent(this, DefineUIService.class);
                intent.putExtra(DefineUIService.INTENT_EXTRA_CLIPTEXT, text);
            } else {
                intent = new Intent(this, PopupManager.class);
                intent.putExtra(PopupManager.INTENT_EXTRA_CLIPTEXT, text);
            }
            getBaseContext().startService(intent);
        }
    }
}
