package in.workarounds.define.helper;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;

import in.workarounds.define.ui.activity.MainActivity;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by madki on 17/05/15.
 */
public class DownloadResolver {
    private static final String TAG = LogUtils.makeLogTag(DownloadResolver.class);

    public static final String WORDNET_DOWNLOAD = "wordnet";
    public static final String[] ALL_DOWNLOADS = new String[] {WORDNET_DOWNLOAD};

    public static DownloadManager.Request getDownloadRequest(String id, Context context) {
        DownloadManager.Request request;
        switch (id) {
            case WORDNET_DOWNLOAD:
                request = DownloadHelper.getRelevantRequest(WORDNET_DOWNLOAD, context);
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
            case WORDNET_DOWNLOAD:
                Intent activityIntent = new Intent(context, MainActivity.class);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
                break;
            default:
                LogUtils.LOGW(TAG, "No relevant download found for " + id + " doing nothing on notification click");
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
            case WORDNET_DOWNLOAD:
                // TODO start unzipping service
                break;
            default:
                LogUtils.LOGW(TAG, "No relevant download found for " + id + " doing nothing on download complete");
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
            LogUtils.LOGE(TAG, "download " + id + "isn't running. Doing nothing");
        }
    }

    public static void onNotificationClicked(long downloadId, Context context) {
        String id = DownloadHelper.getStringId(downloadId, context);
        if(id != null) {
            onNotificationClicked(id, context);
        } else {
            LogUtils.LOGE(TAG, "No download present with downloadId " + downloadId + "doing nothing on Notification clicked");
        }
    }

    public static void onDownloadFinished(long downloadId, Context context) {
        String id = DownloadHelper.getStringId(downloadId, context);
        if(id != null) {
            onDownloadFinished(id, context);
        } else {
            LogUtils.LOGE(TAG, "No download present with downloadId " + downloadId + "doing nothing on Download finish");
        }
    }


}
