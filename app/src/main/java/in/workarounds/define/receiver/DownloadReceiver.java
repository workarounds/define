package in.workarounds.define.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.workarounds.define.helper.DownloadResolver;
import timber.log.Timber;

/**
 * Created by madki on 14/05/15.
 */
public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("Received download broadcast");
        String action = intent.getAction();
        if(action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            Timber.d("Notification clicked");
            long[] downloadIds = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            if(downloadIds != null && downloadIds.length > 0) {
                DownloadResolver.onNotificationClicked(downloadIds[0], context);
            } else {
                Timber.e("No ids received on notification click !");
            }
        } else if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            Timber.d("Download complete");
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if(downloadId != 0) {
                DownloadResolver.onDownloadFinished(downloadId, context);
            } else {
                Timber.e("No downloadId received on download complete !");
            }
        }
    }
}
