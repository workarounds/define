package in.workarounds.define.wordnet;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.RetrievalException;
import in.workarounds.define.base.Dictionary;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.Result;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class WordnetDictionary implements Dictionary {
    private static final String TAG = LogUtils.makeLogTag(WordnetDictionary.class);
    private static final String WORDNET_DATABASE_DIR = "wordnet.database.dir";
    private WordNetDatabase database;

    @Inject
    public WordnetDictionary(WordnetFileHelper wordnetFileHelper) {
        System.setProperty(WORDNET_DATABASE_DIR, wordnetFileHelper.dictFilePath());
        database = WordNetDatabase.getFileInstance();
    }

    @Override
    public List<Result> results(String word) throws DictionaryException {
        List<Result> results = new ArrayList<>();

        try {
            Synset[] synsets = database.getSynsets(word);
            for (Synset synset : synsets) {
                results.add(toResult(synset));
            }
        } catch (RetrievalException e) {
            throw new DictionaryException(
                    DictionaryException.DICTIONARY_NOT_FOUND,
                    "Unable to load wordnet dictionary. Make sure you have downloaded wordnet dictionary and it's present in sdcard/Define/Wordnet"
            );
        }

        return results;
    }

    private Result toResult(Synset synset) {
        Result result = new Result();
        List<String> synonyms = new ArrayList<>(Arrays.asList(synset.getWordForms()));
        result.synonyms(synonyms);
        result.definition(synset.getDefinition());
        List<String> usages = new ArrayList<>(Arrays.asList(synset.getUsageExamples()));
        result.usages(usages);
        result.type(convertType(synset.getType()));
        return result;
    }

    private String convertType(SynsetType type) {
        String typeStr = "";
        if (type == SynsetType.NOUN) {
            typeStr = Result.TYPE_NOUN;
        } else if (type == SynsetType.VERB) {
            typeStr = Result.TYPE_VERB;
        } else if (type == SynsetType.ADJECTIVE) {
            typeStr = Result.TYPE_ADJ;
        } else if (type == SynsetType.ADVERB) {
            typeStr = Result.TYPE_ADV;
        } else if (type == SynsetType.ADJECTIVE_SATELLITE) {
            typeStr = Result.TYPE_ADJS;
        } else {
            typeStr = Result.TYPE_NONE;
        }
        return typeStr;
    }
}
