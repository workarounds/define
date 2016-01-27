package in.workarounds.define.wordnet;


import android.os.AsyncTask;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.RetrievalException;
import in.workarounds.define.DefineApp;
import in.workarounds.define.R;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.IWordnetDictionary;
import in.workarounds.define.portal.PerPortal;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class WordnetDictionary implements IWordnetDictionary {
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
                    DefineApp.getContext().getString(R.string.exception_wordnet)
            );
        }
        return results;
    }

    public Observable<List<Synset>> resultsObservable(String word) {
        return Observable.fromCallable(() -> results(word))
                .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR));
    }
}
