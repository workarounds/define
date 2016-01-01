package in.workarounds.define.portal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import in.workarounds.define.R;
import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.service.ClipboardService;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.portal.Portal;
import in.workarounds.portal.PortalManager;
import in.workarounds.portal.Portlet;
import timber.log.Timber;

/**
 * Created by Nithin on 30/10/15.
 */
public class UtilPortlet extends Portlet {
    public static final int UTIL_PORTLET_ID = 101;
    private Handler notificationHandler;
    private Runnable notificationHandlerRunnable;

    public UtilPortlet(Context base, int id) {
        super(base, id);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.empty_view);
        setNotificationClearer();
    }

    @Override
    protected void setContentView(@LayoutRes int layoutId) {
        super.setContentView(layoutId);
        mView.setVisibility(View.GONE);
    }

    @Override
    protected void onData(Bundle data) {
        super.onData(data);
        startActionResolver(data.getString(ClipboardService.BUNDLE_SELECTED_TEXT_KEY));
    }

    private void startActionResolver(String text) {
        int state = PortalManager.getPortalState(this, MainPortal.class);

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
        }
        else{
            startPortal(text);
        }
    }

    public void cancelCurrentNotificationClearer(){
        notificationHandler.removeCallbacks(notificationHandlerRunnable);
    }

    private void startPortal(String text){
        Bundle bundle = new Bundle();
        bundle.putString(MainPortal.BUNDLE_KEY_CLIP_TEXT, text);
        Timber.d("Starting Portal");
        Portal.with(this).data(bundle).send(MainPortal.class);
    }

    private void setNotificationClearer(){
        notificationHandler = new Handler();
        notificationHandlerRunnable =  new Runnable() {
            @Override
            public void run() {
                new NotificationUtils(getBaseContext()).getNotificationManager().cancel(NotificationUtils.SILENT_NOTIFICATION_ID);
            }
        };
    }
}
