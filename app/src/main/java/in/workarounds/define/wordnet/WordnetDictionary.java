package in.workarounds.define.wordnet;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.dictionary.Result;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 26/09/15.
 */
public class WordnetDictionary implements Dictionary {
    private static final String TAG = LogUtils.makeLogTag(WordnetDictionary.class);
    private static final String WORDNET_DATABASE_DIR = "wordnet.database.dir";
    private WordNetDatabase database;

    @Inject
    public WordnetDictionary (WordnetFileHelper wordnetFileHelper) {
        System.setProperty(WORDNET_DATABASE_DIR, wordnetFileHelper.dictFilePath());
        try {
            database = WordNetDatabase.getFileInstance();
        } catch (Exception e) {
            LogUtils.LOGE(TAG, "No dictionary found");
        }
    }

    @Override
    public List<Result> results(String word) {
        List<Result> results = new ArrayList<>();
        if(database != null) {
            Synset[] synsets = database.getSynsets(word);
            for (Synset synset: synsets) {
                results.add(toResult(synset));
            }
        } else {
            LogUtils.LOGE(TAG, "No dictionary found");
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
