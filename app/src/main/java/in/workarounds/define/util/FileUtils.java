package in.workarounds.define.util;

import android.os.Environment;

import java.io.File;

public class FileUtils {
    private static final String TAG = LogUtils.makeLogTag(FileUtils.class);

    public static String getDictFilePath() {
        return getRootFilePath()
                + File.separator
                + "dict";
    }

    public static File getDictFile() {
        return new File(getDictFilePath());
    }

    public static File getZipFile() {
        return new File(getRootFilePath() + File.separator + "dict.zip");
    }

    public static String getRootFilePath() {
        return Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "Define";
    }

    public static File getRootFile() {
        return new File(getRootFilePath());
    }

    public static void createRootFile() {
        File rootFile = getRootFile();
        if(!rootFile.exists()) {
            if(!rootFile.mkdir()) {
                LogUtils.LOGE(TAG, "Unable to create root file!");
            } else {
                LogUtils.LOGD(TAG, "Created root file");
            }
        }
    }
}
