package in.workarounds.define.helper;

import java.io.File;

import in.workarounds.define.util.FileUtils;
import in.workarounds.define.util.LogUtils;

public class FileHelper {
    private static final String TAG = LogUtils.makeLogTag(FileHelper.class);

    /**
     * String constants for easy access
     */
    // dictionaryName
    public static final String WORDNET    = "wordnet";
    public static final String WIKTIONARY = "wiktionary";
    // zip file extension
    public static final String ZIP_EXT    = ".zip";

    /**
     * File paths
     */
    // currently can only be a direct folder in sdcard
    public static final String ROOT_PATH        = "sdcard/Define";
    public static final String WORDNET_PATH     = ROOT_PATH + "/Wordnet";
    public static final String WIKTIONARY_PATH  = ROOT_PATH + "/Wiktionary";
    public static final String DOWNLOAD_PATH    = "/Define";

    /**
     * helper method to access the filePath of dictionary
     * @param dictionaryName name of dictionary WORDNET or WIKTIONARY
     * @return filePath if valid dictionaryName, else null
     */
    public static String getDictFilePath(String dictionaryName) {
        switch (dictionaryName) {
            case WORDNET:
                return FileUtils.getFilePathFromString(WORDNET_PATH);
            case WIKTIONARY:
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

    public static String getDownloadPath() {
        return FileUtils.getFilePathFromString(DOWNLOAD_PATH);
    }
}
