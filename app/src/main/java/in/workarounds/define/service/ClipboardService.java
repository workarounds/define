package in.workarounds.define.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.webkit.URLUtil;

import in.workarounds.define.BuildConfig;

public class ClipboardService extends Service implements
		ClipboardManager.OnPrimaryClipChangedListener {
	public static final String TAG = "ClipboardService";
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
		if(!PopupManager.isPopupShown()) showPopupIfClipIsText();
	}

	private void showPopupIfClipIsText() {		
		ClipData clipData = getClipboardManager().getPrimaryClip();
		ClipData.Item item = clipData.getItemAt(0);
		CharSequence text = item.getText();
		boolean isUrl = false;
		if(text != null) URLUtil.isValidUrl(text.toString());
		if (text != null && !isUrl) {
			Intent intent = new Intent(this, PopupManager.class);
			intent.putExtra(PopupManager.INTENT_EXTRA_CLIPTEXT, text.toString());
			getBaseContext().startService(intent);
		}
	}

	public static void printLog(String text) {
		boolean debug = BuildConfig.DEBUG;
		if (debug)
			Log.d(TAG, text);
	}

}
