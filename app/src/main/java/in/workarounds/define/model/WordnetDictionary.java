package in.workarounds.define.model;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import in.workarounds.define.R;
import in.workarounds.define.api.Constants;
import in.workarounds.define.helper.FileHelper;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.StringUtils;

public class WordnetDictionary implements Dictionary {
    private static final String TAG = LogUtils.makeLogTag(WordnetDictionary.class);
    private Context mContext;

    /**
     * helper dictionary class, instantiates the dictionary and has method to
     * return meanings of words
     *
     * @param context
     */
    public WordnetDictionary(Context context) {
        mContext = context;
        System.setProperty(context.getResources().getString(R.string.wordnet_database_dir), FileHelper.getDictFilePath(Constants.WORDNET));
    }

    /**
     * given a onWordUpdated, returns an array of DictResult returns an array with no
     * elements if definition isn't found
     *
     * @param wordForm
     * @return
     */
    @Override
    public ArrayList<DictResult> getMeanings(String wordForm) {
        ArrayList<DictResult> results = new ArrayList<>();

        if (!wordForm.isEmpty()) {
            wordForm = StringUtils.preProcessWord(wordForm);
            // Get the synsets containing the wrod form
            WordNetDatabase database;
            try {
                database = WordNetDatabase.getFileInstance();
            } catch (Exception e){
                Toast.makeText(mContext, "No dictionary found", Toast.LENGTH_SHORT).show();
                return results;
            }
            Synset[] synsets = database.getSynsets(wordForm);

            // Display the onWordUpdated forms and definitions for synset retrieved
            if (synsets.length > 0) {
                for (Synset synset : synsets) {
                    String[] synonyms = synset.getWordForms();
                    List<String> synonymsList = new ArrayList<>(Arrays.asList(synonyms));
                    synonymsList.remove(wordForm); // removing the onWordUpdated itself from the list of synonyms
                    synonymsList.remove(StringUtils.makeFirstLetterLowerCase(wordForm));
                    String meaning = synset.getDefinition();
                    String type = typeToString(synset.getType());
                    String[] usage = synset.getUsageExamples();
                    List<String> usagesList = new ArrayList<>(Arrays.asList(usage));

                    DictResult result = new DictResult(wordForm, meaning, type,
                            usagesList, synonymsList);
                    results.add(result);
                    LogUtils.LOGD(TAG, "result: " + result);
                }
            } else {
                LogUtils.LOGD(TAG, "No definition found for: " + wordForm);
            }
        } else {
            LogUtils.LOGD(TAG, "No onWordUpdated given");
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

