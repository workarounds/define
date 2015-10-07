package in.workarounds.define.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.workarounds.define.service.ClipboardService;
import in.workarounds.define.util.LogUtils;

/**
 * Broadcast receiver that restarts service.
 * Triggered after phone reboot and application upgrade
 */
public class BootUpReceiver extends BroadcastReceiver {
    private static final String TAG = LogUtils.makeLogTag(BootUpReceiver.class);

    @Override
    public void onReceive(Context context, Intent arg1) {
        LogUtils.LOGD(TAG, "Received broadcast");
        boolean serviceUp = ClipboardService.isRunning();
        if (!serviceUp) {
            Intent intent = new Intent(context, ClipboardService.class);
            context.startService(intent);
        }
    }

}
