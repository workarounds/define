package in.workarounds.define.wordnet;

import java.io.File;

import in.workarounds.define.file.FileHelper;
import in.workarounds.define.util.FileUtils;

/**
 * Created by madki on 26/09/15.
 */
public class WordnetFileHelper extends FileHelper {
    public static final String WORDNET_FOLDER    = "Wordnet";
    public static final String WORDNET_PATH      = ROOT_PATH + "/" + WORDNET_FOLDER;

    @Override
    public String dictFilePath() {
        return FileUtils.getFilePathFromString(WORDNET_PATH);
    }

    @Override
    public File dictFile() {
        return new File(dictFilePath());
    }

    @Override
    public String zipFilePath() {
        return dictFilePath() + ZIP_EXT;
    }

    @Override
    public File zipFile() {
        return new File(zipFilePath());
    }

    @Override
    public String downloadFileName() {
        return WORDNET_FOLDER + ZIP_EXT;
    }
}
