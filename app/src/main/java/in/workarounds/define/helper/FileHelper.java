package in.workarounds.define.helper;

import java.io.File;

import in.workarounds.define.api.Constants;
import in.workarounds.define.util.FileUtils;
import in.workarounds.define.util.LogUtils;

public class FileHelper {
    private static final String TAG = LogUtils.makeLogTag(FileHelper.class);

    // zip file extension
    private static final String ZIP_EXT    = ".zip";

    /**
     * File paths
     */
    // currently can only be a direct folder in sdcard
    public static final String ROOT_PATH         = "sdcard/Define";
    public static final String WORDNET_FOLDER    = "Wordnet";
    public static final String WIKTIONARY_FOLDER = "Wiktionary";
    public static final String WORDNET_PATH      = ROOT_PATH + "/" + WORDNET_FOLDER;
    public static final String WIKTIONARY_PATH   = ROOT_PATH + "/" + WIKTIONARY_FOLDER;
    public static final String DOWNLOAD_PATH     = "/Define";

    /**
     * helper method to access the filePath of dictionary
     * @param dictionaryName name of dictionary WORDNET or WIKTIONARY
     * @return filePath if valid dictionaryName, else null
     */
    public static String getDictFilePath(String dictionaryName) {
        switch (dictionaryName) {
            case Constants.WORDNET:
                return FileUtils.getFilePathFromString(WORDNET_PATH);
            case Constants.WIKTIONARY:
                return FileUtils.getFilePathFromString(WIKTIONARY_PATH);
            default:
                return null;
        }
    }

    /**
     * helper method to access the dictionary file
     * @param dictionaryName name of dictionary WORDNET or WIKTIONARY
     * @return file if valid dictionaryName, else null
     */
    public static File getDictFile(String dictionaryName) {
        String filePath = getDictFilePath(dictionaryName);
        if(filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * helper method to access the zipFilePath of dictionary
     * @param dictionaryName name of dictionary WORDNET or WIKTIONARY
     * @return zipFilePath if valid dictionaryName, else null
     */
    public static String getZipFilePath(String dictionaryName) {
        String filePath = getDictFilePath(dictionaryName);

        if(filePath != null) {
            filePath = filePath + ZIP_EXT;
        }

        return filePath;
    }

    /**
     * helper method to access the dictionary zipFile
     * @param dictionaryName name of dictionary WORDNET or WIKTIONARY
     * @return zipFile if valid dictionaryName, else null
     */
    public static File getZipFile(String dictionaryName) {
        String filePath = getZipFilePath(dictionaryName);
        if(filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * helper method to access the rootFilePath
     * @return filePath
     */
    public static String getRootFilePath() {
        return FileUtils.getFilePathFromString(ROOT_PATH);
    }

    /**
     * helper method to access the rootFile
     * @return File
     */
    public static File getRootFile() {
        return new File(getRootFilePath());
    }

    /**
     * checks if the root file is present
     * if not creates the root file
     */
    public static void createRootFile() {
        FileUtils.assertDir(getRootFilePath());
    }

    /**
     * @return file path to be given to download manager
     */
    public static String getDownloadPath() {
        return FileUtils.getFilePathFromString(DOWNLOAD_PATH);
    }

    /**
     * @param dictionaryName constants WORDNET or WIKTIONARY
     * @return name of the download file to be given to DownloadManager
     */
    public static String getDownloadFileName(String dictionaryName) {
        switch (dictionaryName) {
            case Constants.WORDNET:
                return WORDNET_FOLDER + ZIP_EXT;
            case Constants.WIKTIONARY:
                return WIKTIONARY_FOLDER + ZIP_EXT;
            default:
                LogUtils.LOGE(TAG, "No such dictionary " + dictionaryName);
                return null;
        }
    }

}
