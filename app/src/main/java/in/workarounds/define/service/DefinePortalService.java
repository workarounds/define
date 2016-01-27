package in.workarounds.define.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import in.workarounds.define.R;
import in.workarounds.define.portal.DefinePermissionHelper;
import in.workarounds.define.portal.DefinePortalAdapter;
import in.workarounds.portal.PortalService;
import in.workarounds.portal.Portals;

import static in.workarounds.define.constants.NotificationId.FOREGROUND_NOTIFICATION;
import static in.workarounds.define.constants.NotificationId.PENDING_FOREGROUND_NOTIFICATION;

/**
 * Created by madki on 05/01/16.
 */
public class DefinePortalService extends PortalService<DefinePortalAdapter, DefinePermissionHelper> {

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(FOREGROUND_NOTIFICATION, getForegroundNotification());
    }

    @NonNull
    @Override
    protected DefinePortalAdapter createPortalAdapter() {
        return new DefinePortalAdapter(this, R.style.AppTheme);
    }

    @Override
    protected DefinePermissionHelper createPermissionHelper() {
        return new DefinePermissionHelper(this);
    }

    private Notification getForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.foreground_notification))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setColor(ContextCompat.getColor(this, R.color.portal_foreground_notification));
        PendingIntent deleteIntent = PendingIntent.getService(
                this,
                PENDING_FOREGROUND_NOTIFICATION,
                Portals.closeManagerIntent(getApplicationContext(), DefinePortalService.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(deleteIntent);
        return builder.build();
    }
}
