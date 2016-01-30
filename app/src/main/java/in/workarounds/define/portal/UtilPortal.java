package in.workarounds.define.portal;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v4.app.NotificationCompat;

import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.service.ClipboardService;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.portal.Portal;
import timber.log.Timber;

import static in.workarounds.define.constants.NotificationId.SILENT_NOTIFICATION;
import static in.workarounds.define.portal.PortalId.UTIL_PORTAL;

/**
 * Created by Nithin on 30/10/15.
 */
public class UtilPortal extends Portal<DefinePortalAdapter> {
    private Handler notificationHandler;
    private Runnable notificationHandlerRunnable;

    public UtilPortal(DefinePortalAdapter portalAdapter, int portalId) {
        super(portalAdapter, portalId);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setNotificationClearer();
        startActionResolver(getSelectedText(bundle));
    }

    @Override
    protected void setContentView(@LayoutRes int layoutId) {
        super.setContentView(layoutId);
    }

    @Override
    protected boolean onData(Bundle data) {
        super.onData(data);
        startActionResolver(getSelectedText(data));
        return true;
    }

    private String getSelectedText(Bundle data) {
        return data.getString(ClipboardService.BUNDLE_SELECTED_TEXT_KEY);
    }

    private void startActionResolver(String text) {

        @UserPrefActivity.NotifyMode int notifyMode = PrefUtils.getNotifyMode(this);
        if(notifyMode == UserPrefActivity.OPTION_SILENT || notifyMode == UserPrefActivity.OPTION_PRIORITY) {
//Set notification priority as high for priority mode, default for silent mode
            int priority =  (notifyMode == UserPrefActivity.OPTION_PRIORITY)
                    ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT;

            NotificationUtils notificationUtils = new NotificationUtils(this);
            notificationUtils.sendMeaningNotification(text, priority);

            if(PrefUtils.getNotificationAutoHideFlag(this)) {
                cancelCurrentNotificationClearer();
                notificationHandler.postDelayed(notificationHandlerRunnable, 10000);
            }
        } else {
            startPortal(text);
        }
    }

    public void cancelCurrentNotificationClearer(){
        notificationHandler.removeCallbacks(notificationHandlerRunnable);
    }

    private void startPortal(String text){
        Bundle bundle = new Bundle();
        bundle.putString(MeaningPortal.BUNDLE_KEY_CLIP_TEXT, text);
        Timber.d("Starting Portal");
        portalAdapter.open(PortalId.MEANING_PORTAL, bundle);
    }

    private void setNotificationClearer(){
        notificationHandler = new Handler();
        notificationHandlerRunnable = () -> {
            new NotificationUtils(getBaseContext()).getNotificationManager().cancel(SILENT_NOTIFICATION);
            portalAdapter.close(UTIL_PORTAL);
        };
    }
}
