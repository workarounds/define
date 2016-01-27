package in.workarounds.define.helper;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.workarounds.define.api.Constants;
import in.workarounds.define.file.unzip.UnzipService;
import in.workarounds.define.ui.activity.DictionariesActivity;
import in.workarounds.define.util.PrefUtils;
import timber.log.Timber;

/**
 * Created by madki on 17/05/15.
 */
public class DownloadResolver {

    public static final String[] ALL_DOWNLOADS = new String[] {Constants.WORDNET};

    public static DownloadManager.Request getDownloadRequest(String id, Context context) {
        DownloadManager.Request request;
        switch (id) {
            case Constants.WORDNET:
                request = DownloadHelper.getRelevantRequest(Constants.WORDNET, context);
                break;
            default:
                request = null;
                break;
        }

        return request;
    }

    /**
     * decides action when download notification is clicked
     * @param id local string identifier for download
     * @param context context
     */
    private static void onNotificationClicked(String id, Context context) {
        switch (id) {
            case Constants.WORDNET:
                Intent activityIntent = new Intent(context, DictionariesActivity.class);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
                break;
            default:
                Timber.w("No relevant download found for " + id + " doing nothing on notification click");
                break;
        }
    }

    /**
     * decides action when download is finished
     * @param id local string identifier for download
     * @param context context
     */
    private static void onDownloadFinished(String id, Context context) {
        switch (id) {
            case Constants.WORDNET:
                Intent intent = new Intent(context, UnzipService.class);
                intent.putExtra(UnzipService.INTENT_KEY_DICT_NAME, id);
                context.startService(intent);
                break;
            default:
                Timber.w("No relevant download found for " + id + " doing nothing on download complete");
                break;
        }
    }

    /**
     * helper method to start a download
     * @param id local string id of the download
     * @param context context
     */
    public static void startDownload(String id, Context context) {
        DownloadManager.Request request = getDownloadRequest(id, context);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);
        PrefUtils.setDownloadId(id, downloadId, context);
    }

    /**
     * helper method to cancel a running download
     * @param id local string id of the download
     * @param context context
     */
    public static void cancelDownload(String id, Context context) {
        long downloadId = PrefUtils.getDownloadId(id, context);
        if(downloadId != 0) {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.remove(downloadId);
            PrefUtils.removeDownloadId(id, context);
        } else {
            Timber.e("download " + id + "isn't running. Doing nothing");
        }
    }

    /**
     * sets up a thread to track download status and post to given progress bar and TextView
     * @param id local string id reference to download
     * @param progressBar progress bar with max set to 100
     * @param statusView textView that will show status
     * @param context context
     * @return a handle to the thread created (close it in onDestroy of activity)
     */
    public static DownloadProgressThread setUpProgress(String id, ProgressBar progressBar, TextView statusView, Context context) {
        DownloadProgressThread progressThread = new DownloadProgressThread(id, progressBar, statusView, context);
        progressThread.start();
        return progressThread;
    }

    /**
     * method called by DownloadReceiver when Notification is clicked
     * @param downloadId the first downloadId passed by list of longs
     * @param context context
     */
    public static void onNotificationClicked(long downloadId, Context context) {
        String id = DownloadHelper.getStringId(downloadId, context);
        if(id != null) {
            onNotificationClicked(id, context);
        } else {
            Timber.e("No download present with downloadId " + downloadId + "doing nothing on Notification clicked");
        }
    }

    /**
     * method called by DownloadReceiver when download is finished
     * @param downloadId id of the finished download
     * @param context context
     */
    public static void onDownloadFinished(long downloadId, Context context) {
        String id = DownloadHelper.getStringId(downloadId, context);
        if(id != null) {
            onDownloadFinished(id, context);
        } else {
            Timber.e("No download present with downloadId " + downloadId + "doing nothing on Download finish");
        }
    }

}
