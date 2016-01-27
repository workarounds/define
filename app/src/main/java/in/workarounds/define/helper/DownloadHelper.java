package in.workarounds.define.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import in.workarounds.define.util.PrefUtils;
import timber.log.Timber;

/**
 * Created by madki on 17/05/15.
 */
public class DownloadHelper {

    /**
     * returns the relevant request for given suffix
     * @param suffix suffix (wordnet, wiktionary etc)
     * @param context context
     * @return download request if URL not null else null
     */
     public static DownloadManager.Request getRelevantRequest(String suffix, Context context) {
        DownloadManager.Request request = null;
        String URL = getURL(suffix, context);
        if(URL != null) {
            Uri uri = Uri.parse(URL);
            request = new DownloadManager.Request(uri);
            request.setTitle(getTitle(suffix, context));
            request.setDescription(getDescription(suffix, context));
            request.setDestinationInExternalPublicDir(FileHelper.getDownloadPath(),
                    FileHelper.getDownloadFileName(suffix));
            request.setVisibleInDownloadsUi(false);
        } else {
            Timber.e("No URL provided for download : " + suffix);
        }
        return request;
    }

    /**
     * gives fileName as whatever follows the last '/' in URL
     * @param URL url
     * @return file name
     */
    private static String getFileNameFromURL(String URL) {
        String[] parts = URL.split("/");
        return parts[parts.length-1];
    }

    /**
     * helper method to access relevant title form string resources
     * @param suffix suffix
     * @param context context
     * @return string
     */
    private static String getTitle(String suffix, Context context) {
        return getRelevantString("dm_title", suffix, context);
    }

    /**
     * helper method to access relevant description form string resources
     * @param suffix suffix
     * @param context context
     * @return string
     */
    private static String getDescription(String suffix, Context context) {
        return getRelevantString("dm_description", suffix, context);
    }

    /**
     * helper method to access relevant URL form string resources
     * @param suffix suffix
     * @param context context
     * @return string
     */
    private static String getURL(String suffix, Context context) {
        return getRelevantString("dm_url", suffix, context);
    }

    /**
     * helper method to get String resources by name
     * gets stringName_suffix resource if present else
     * gets stringName string as default
     * @param stringName name of the string
     * @param suffix suffix
     * @param context context
     * @return string resource if exists else null
     */
    private static String getRelevantString(String stringName, String suffix, Context context) {
        String relevantString = null;
        int resourceId = context.getResources().getIdentifier(stringName + "_" + suffix, "string", context.getPackageName());
        int defaultId  = context.getResources().getIdentifier(stringName, "string", context.getPackageName());
        if(resourceId != 0) {
            relevantString = context.getString(resourceId);
        } else {
            relevantString = context.getString(defaultId);
        }
        return relevantString;
    }

    /**
     * gets String id of a download
     * @param downloadId downloadId returned by DownloadManager
     * @param context context
     * @return StringId if exists else null
     */
    public static String getStringId(long downloadId, Context context) {
        long tempDownloadId;
        for(String id: DownloadResolver.ALL_DOWNLOADS) {
            tempDownloadId = PrefUtils.getDownloadId(id, context);
            if(downloadId == tempDownloadId) {
                return id;
            }
        }
        return null;
    }

}
