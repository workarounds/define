package in.workarounds.define.util;

import android.content.Context;
import android.content.SharedPreferences;

import in.workarounds.define.ui.activity.UserPrefActivity;

/**
 * Created by mouli on 15/3/15.
 */
public class PrefUtils {
    private static final String FILE_NAME = "default.prefs";
    public static final String KEY_NOTIFY_MODE = "key_notify_mode";

    private static final String DM = "dm_";

    private static SharedPreferences mSharedPreferences;

    public static SharedPreferences getSharedPreferences(Context context) {
        if(mSharedPreferences==null) {
            mSharedPreferences = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public static void setNotifyMode(@UserPrefActivity.NotifyMode int mode, Context context) {
        getSharedPreferences(context).edit().putInt(KEY_NOTIFY_MODE, mode).apply();
    }

    public static int getNotifyMode( @UserPrefActivity.NotifyMode int defaultValue, Context context) {
        return getSharedPreferences(context).getInt(KEY_NOTIFY_MODE, defaultValue);
    }

    public static long getDownloadId(String key, Context context) {
        SharedPreferences preferences = PrefUtils.getSharedPreferences(context);
        return preferences.getLong(DM + key, 0);
    }

    public static void setDownloadId(String key, long downloadId, Context context) {
        SharedPreferences preferences = PrefUtils.getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(DM + key, downloadId);
        editor.apply();
    }

    public static void removeDownloadId(String key, Context context) {
        SharedPreferences preferences = PrefUtils.getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(DM + key);
        editor.apply();
    }
}
