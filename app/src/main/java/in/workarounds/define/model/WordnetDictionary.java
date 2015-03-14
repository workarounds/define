package in.workarounds.define.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import in.workarounds.define.R;
import in.workarounds.define.util.FileUtils;
import in.workarounds.define.util.StringUtils;

public class WordnetDictionary {
    private Context mContext;

    /**
     * helper dictionary class, instantiates the dictionary and has method to
     * return meanings of words
     *
     * @param context
     */
    public WordnetDictionary(Context context) {
        mContext = context;
        System.setProperty(context.getResources().getString(R.string.wordnet_database_dir), FileUtils.getDictFilePath());
    }

    /**
     * given a word, returns an array of DictResult returns an array with no
     * elements if meaning isn't found
     *
     * @param wordForm
     * @return
     */
    public ArrayList<DictResult> getMeanings(String wordForm) {
        ArrayList<DictResult> results = new ArrayList<DictResult>();

        if (!wordForm.isEmpty()) {
            wordForm = StringUtils.preProcessWord(wordForm);
            // Get the synsets containing the wrod form
            WordNetDatabase database = WordNetDatabase.getFileInstance();
            Synset[] synsets = database.getSynsets(wordForm);

            // Display the word forms and definitions for synsets retrieved
            if (synsets.length > 0) {
                for (int i = 0; i < synsets.length; i++) {
                    String[] synonyms = synsets[i].getWordForms();
                    String meaning = synsets[i].getDefinition();
                    String type = typeToString(synsets[i].getType());
                    String[] usage = synsets[i].getUsageExamples();
                    DictResult result = new DictResult(wordForm, meaning, type,
                            usage, synonyms);
                    results.add(result);
                    Log.d("Dictionary", "" + result);
                }
            } else {
                Log.d("Dictionary", "no meaning found for : " + wordForm);
            }
        } else {
            Log.d("Dictionary", "no word given");
        }

        return results;
    }

    /**
     * given the SynsetType returns the type as a string.
     *
     * @param type
     * @return
     */
    private String typeToString(SynsetType type) {
        String typeStr = "";
        if (type == SynsetType.NOUN) {
            typeStr = "noun";
        } else if (type == SynsetType.VERB) {
            typeStr = "verb";
        } else if (type == SynsetType.ADJECTIVE) {
            typeStr = "adjective";
        } else if (type == SynsetType.ADVERB) {
            typeStr = "adverb";
        } else if (type == SynsetType.ADJECTIVE_SATELLITE) {
            typeStr = "adj. satellite";
        } else {
            typeStr = "unknown";
        }
        return typeStr;
    }

}

