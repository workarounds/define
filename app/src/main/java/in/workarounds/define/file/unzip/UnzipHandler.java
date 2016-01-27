package in.workarounds.define.file.unzip;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import timber.log.Timber;


/**
 * Created by madki on 30/09/15.
 */
public class UnzipHandler extends Handler {

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
                    Timber.d("Unzip progress: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        } else {
            Timber.e("No callback passed to handler");
            super.handleMessage(msg);
        }
    }

    public interface HandlerCallback {
        void onUnzipProgressUpdate(int progress);
    }
}