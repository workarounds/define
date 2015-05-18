package in.workarounds.define.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.workarounds.define.helper.DownloadResolver;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 14/05/15.
 */
public class DownloadReceiver extends BroadcastReceiver {
    private static final String TAG = LogUtils.makeLogTag(DownloadReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.LOGD(TAG, "Received download broadcast");
        String action = intent.getAction();
        if(action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            LogUtils.LOGD(TAG, "Notification clicked");
            long[] downloadIds = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            if(downloadIds != null && downloadIds.length > 0) {
                DownloadResolver.onNotificationClicked(downloadIds[0], context);
            } else {
                LogUtils.LOGE(TAG, "No ids received on notification click !");
            }
        } else if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            LogUtils.LOGD(TAG, "Download complete");
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if(downloadId != 0) {
                DownloadResolver.onDownloadFinished(downloadId, context);
            } else {
                LogUtils.LOGE(TAG, "No downloadId received on download complete !");
            }
        }
    }
}
