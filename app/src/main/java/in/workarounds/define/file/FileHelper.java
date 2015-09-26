package in.workarounds.define.file;

import java.io.File;

import in.workarounds.define.util.FileUtils;

/**
 * Created by madki on 26/09/15.
 */
public abstract class FileHelper {
    protected static final String ROOT_PATH         = "sdcard/Define";
    protected static final String ZIP_EXT         = ".zip";
    protected static final String DOWNLOAD_PATH     = "/Define";


    public abstract String dictFilePath();
    public abstract File dictFile();
    public abstract String zipFilePath();
    public abstract File zipFile();
    public abstract String downloadFileName();

    /**
     * helper method to access the rootFilePath
     * @return filePath
     */
    public static String rootFilePath() {
        return FileUtils.getFilePathFromString(ROOT_PATH);
    }

    /**
     * helper method to access the rootFile
     * @return File
     */
    public static File rootFile() {
        return new File(rootFilePath());
    }

    /**
     * checks if the root file is present
     * if not creates the root file
     */
    public static void createRootFile() {
        FileUtils.assertDir(rootFilePath());
    }

    /**
     * @return file path to be given to download manager
     */
    public static String getDownloadPath() {
        return FileUtils.getFilePathFromString(DOWNLOAD_PATH);
    }

}
