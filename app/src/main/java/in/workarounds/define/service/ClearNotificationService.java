package in.workarounds.define.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import in.workarounds.define.constants.NotificationId;

/**
 * Created by madki on 28/01/16.
 */
public class ClearNotificationService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NotificationId.CLIP_SERVICE_NOTIFICATION,
                ClipboardService.getClipServiceNotification(this));
        stopSelf();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
