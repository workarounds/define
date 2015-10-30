package in.workarounds.define.portal;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.zip.Inflater;

import in.workarounds.define.DefineApp;
import in.workarounds.define.R;
import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.network.DaggerNetworkComponent;
import in.workarounds.define.network.NetworkModule;
import in.workarounds.define.service.ClipboardService;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.portal.ParamUtils;
import in.workarounds.portal.Portal;
import in.workarounds.portal.PortalManager;
import in.workarounds.portal.Portlet;

/**
 * Created by Nithin on 30/10/15.
 */
public class UtilPortlet extends Portlet {
    public static final int UTIL_PORTLET_ID = 101;
    private Handler notificationHandler;
    private Runnable notificationHandlerRunnable;

    public UtilPortlet(Context base, int id) {
        super(base, id);
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

            NotificationUtils.INSTANCE.sendMeaningNotification(text, priority);

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
        Portal.with(this).data(bundle).send(MainPortal.class);
    }

    private void setNotificationClearer(){
        notificationHandler = new Handler();
        notificationHandlerRunnable =  new Runnable() {
            @Override
            public void run() {
                NotificationUtils.INSTANCE.getNotificationManager().cancel(NotificationUtils.SILENT_NOTIFICATION_ID);
            }
        };
    }
}
