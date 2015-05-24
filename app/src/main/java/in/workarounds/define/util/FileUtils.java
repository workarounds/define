package in.workarounds.define.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by madki on 14/05/15.
 */
public class FileUtils {
    private static final String TAG = LogUtils.makeLogTag(FileUtils.class);

    /**
     * @param fileString path as: "sdcard/some/path"
     * @return path as:
     * Environment.getExternalStorageDirectory() + File.separator + some + File.separator + path
     */
    public static String getFilePathFromString(String fileString) {
        String filePath = fileString;

        if(filePath != null) {
            filePath = fileString.replaceFirst("sdcard",
                    Environment.getExternalStorageDirectory().getAbsolutePath());
            filePath = filePath.replaceAll("/", File.separator);
        }

        return filePath;
    }

    /**
     * checks if a give directory exists, and creates it if it doesn't
     * @param filePath path of the directory
     */
    public static void assertDir(String filePath) {
        File file = new File(filePath);

        if (!file.isDirectory()) {
            LogUtils.LOGD(TAG, "Creating file: " + file.getAbsolutePath());
            if(!file.mkdirs()) {
                LogUtils.LOGE(TAG, "Unable to create: " + file.getAbsolutePath());
            }
        }
    }

}
