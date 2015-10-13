package in.workarounds.define.wordnet;


import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.RetrievalException;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.IWordnetDictionary;
import in.workarounds.define.base.Result;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class WordnetDictionary implements IWordnetDictionary {
    private static final String TAG = LogUtils.makeLogTag(WordnetDictionary.class);
    private static final String WORDNET_DATABASE_DIR = "wordnet.database.dir";
    private WordNetDatabase database;

    @Inject
    public WordnetDictionary(WordnetFileHelper wordnetFileHelper) {
        System.setProperty(WORDNET_DATABASE_DIR, wordnetFileHelper.dictFilePath());
        database = WordNetDatabase.getFileInstance();
    }

    @Override
    public List<Synset> results(String word) throws DictionaryException {
        List<Synset> results;
        try {
            Synset[] synsets = database.getSynsets(word);
            results =  Arrays.asList(synsets);
        } catch (RetrievalException e) {
            throw new DictionaryException(
                    DictionaryException.DICTIONARY_NOT_FOUND,
                    "Unable to load wordnet dictionary. Make sure you have downloaded wordnet dictionary and it's present in sdcard/Define/Wordnet"
            );
        }
        return results;
    }
}
