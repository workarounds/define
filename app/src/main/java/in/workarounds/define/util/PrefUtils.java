package in.workarounds.define.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mouli on 15/3/15.
 */
public class PrefUtils {
    private static final String FILE_NAME = "default.prefs";
    public static final String KEY_NOTIF_ONLY = "key_notif_only";

    private static SharedPreferences mSharedPreferences;

    public static SharedPreferences getSharedPreferences(Context context) {
        if(mSharedPreferences==null) {
            mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public static void setIsSilentMode(Context context, boolean isNotifOnly) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putBoolean(KEY_NOTIF_ONLY, isNotifOnly)
                .apply();
    }

    public static boolean getIsSilentMode(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_NOTIF_ONLY, false);
    }

}
