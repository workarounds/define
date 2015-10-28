package in.workarounds.define.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import in.workarounds.define.api.Constants;
import in.workarounds.define.constants.DictionaryId;
import in.workarounds.define.ui.activity.UserPrefActivity;

/**
 * Created by mouli on 15/3/15.
 */
public class PrefUtils {
    private static final String FILE_NAME = "default.prefs";
    public static final String KEY_NOTIFY_MODE = "key_notify_mode";
    public static final String KEY_TUTORIAL_DONE = "key_tutorial_done";
    public static final String KEY_DICTIONARIES_DONE = "key_dictionaries_done";
    public static final String KEY_DICTIONARY_ORDER = "key_dictionary_order";
    private static final String KEY_DICTIONARY_VISIBILITY = "key_dictionary_visibility";
    private static final String DELIMITER = ",";

    private static final String KEY_WORDNET_UNZIPPED = "key_wordnet_unzipped";

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

    public static void setUnzipped(String key, boolean unzipped, Context context){
        String prefKey = getPrefKeyUnzipped(key);
        if(prefKey != null){
            SharedPreferences prefs = getSharedPreferences(context);
            prefs.edit().putBoolean(prefKey, unzipped).apply();
        }
    }

    public static boolean getUnzipped(String key, Context context) {
        String prefKey = getPrefKeyUnzipped(key);
        return prefKey != null && getSharedPreferences(context).getBoolean(prefKey, false);
    }

    public static void setTutorialDone(boolean done, Context context){
        getSharedPreferences(context)
                .edit()
                .putBoolean(KEY_TUTORIAL_DONE, done)
                .apply();
    }

    public static boolean getTutorialDone(Context context){
        return getSharedPreferences(context).getBoolean(KEY_TUTORIAL_DONE, false);
    }

    public static void setDictionariesDone(boolean done, Context context){
        getSharedPreferences(context)
                .edit()
                .putBoolean(KEY_DICTIONARIES_DONE, done)
                .apply();
    }

    public static boolean getDictionariesDone(Context context){
        return getSharedPreferences(context).getBoolean(KEY_DICTIONARIES_DONE, false);
    }

    @Nullable
    private static String getPrefKeyUnzipped(String dictionaryKey){
        if(dictionaryKey == null){
            return null;
        } else if(dictionaryKey.equals(Constants.WORDNET)){
            return KEY_WORDNET_UNZIPPED;
        } else {
            return null;
        }
    }

    public static int[] getDictionaryOrder(Context context) {
        String order = getSharedPreferences(context).getString(KEY_DICTIONARY_ORDER, null);
        if(order != null) {
            return stringToIntArray(order.split(DELIMITER));

        }
        return DictionaryId.defaultOrder;
    }

    public static void setDictionaryOrder(Context context, int[] order) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_DICTIONARY_ORDER, arrayToString(intToStringArray(order)));
        editor.apply();
    }

    public static boolean[] getDictionaryVisibility(Context context) {
        String visibility = getSharedPreferences(context).getString(KEY_DICTIONARY_VISIBILITY, null);
        if(visibility != null) {
            return stringToBooleanArray(visibility.split(DELIMITER));

        }
        return DictionaryId.defaultVisibility;
    }

    public static void setDictionaryVisibility(Context context, boolean[] visibility) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_DICTIONARY_VISIBILITY, arrayToString(booleanToStringArray(visibility)));
        editor.apply();
    }

    private static int[] stringToIntArray(String[] stringArray) {
        int[] intArray = new int[stringArray.length];
        for(int i=0; i<stringArray.length; i++) {
            intArray[i] = Integer.valueOf(stringArray[i]);
        }
        return intArray;
    }

    private static String[] intToStringArray(int[] intArray) {
        String[] stringArray = new String[intArray.length];
        for(int i=0; i<intArray.length; i++) {
            stringArray[i] = Integer.toString(intArray[i]);
        }
        return stringArray;
    }

    private static boolean[] stringToBooleanArray(String[] stringArray) {
        boolean[] booleanArray = new boolean[stringArray.length];
        for(int i=0; i<stringArray.length; i++) {
            booleanArray[i] = Boolean.valueOf(stringArray[i]);
        }
        return booleanArray;
    }

    private static String[] booleanToStringArray(boolean[] booleanArray) {
         String[] stringArray = new String[booleanArray.length];
        for(int i=0; i<booleanArray.length; i++) {
            stringArray[i] = Boolean.toString(booleanArray[i]);
        }
        return stringArray;
    }

    private static String arrayToString(String[] stringArray) {
        String string = null;
        for (String s : stringArray) {
            if(string == null) {
                string = s;
            } else {
                string = string + DELIMITER + s;
            }
        }
        return string;
    }
}
