package in.workarounds.define.file.unzip;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 30/09/15.
 */
public class UnzipHandler extends Handler {
    private static final String TAG = LogUtils.makeLogTag(UnzipHandler.class);

    private WeakReference<HandlerCallback> callback;
    public static final int MSG_UNZIP_PROGRESS = 1;

    public UnzipHandler(HandlerCallback callback) {
        this.callback = new WeakReference<HandlerCallback>(callback);
    }

    @Override
    public void handleMessage(Message msg) {
        HandlerCallback handlerCallback = callback.get();
        if(handlerCallback != null) {
            switch (msg.what) {
                case MSG_UNZIP_PROGRESS:
                    handlerCallback.onUnzipProgressUpdate(msg.arg1);
                    LogUtils.LOGD(TAG, "Unzip progress: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        } else {
            LogUtils.LOGE(TAG, "No callback passed to handler");
            super.handleMessage(msg);
        }
    }

    public interface HandlerCallback {
        void onUnzipProgressUpdate(int progress);
    }
}