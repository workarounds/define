package in.workarounds.define.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.workarounds.define.service.ClipboardService;
import timber.log.Timber;

/**
 * Broadcast receiver that restarts service.
 * Triggered after phone reboot and application upgrade
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        Timber.d("Received broadcast");
        boolean serviceUp = ClipboardService.isRunning();
        if (!serviceUp) {
            Intent intent = new Intent(context, ClipboardService.class);
            context.startService(intent);
        }
    }

}
