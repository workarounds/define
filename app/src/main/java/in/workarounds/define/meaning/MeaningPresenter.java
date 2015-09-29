package in.workarounds.define.meaning;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.dictionary.DictionaryException;
import in.workarounds.define.dictionary.Result;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class MeaningPresenter {
    private static final String TAG = LogUtils.makeLogTag(MeaningPresenter.class);

    private Dictionary dictionary;
    private MeaningPage meaningPage;
    private String word;
    private MeaningsAdapter adapter = new MeaningsAdapter();

    @Inject
    public MeaningPresenter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void addView(MeaningPage view) {
        this.meaningPage = view;
    }

    public void dropView() {
        this.meaningPage = null;
    }

    public void word(String word) {
        if(word != null && !word.equals(this.word)) {
            adapter.update(new ArrayList<Result>());
        }
        this.word = word;
        if(meaningPage != null) {
            meaningPage.title(word);
        }
        new MeaningsTask().execute(word);
    }

    public String word() {
        return word;
    }

    public MeaningsAdapter adapter() {
        return adapter;
    }

    private void onResultsUpdated(List<Result> results) {
        adapter.update(results);
    }

    private class MeaningsTask extends AsyncTask<String, Integer, List<Result>> {
        @Override
        protected List<Result> doInBackground(String... params) {
            List<Result> results;
            try {
                results = dictionary.results(params[0]);
            } catch (DictionaryException exception) {
                exception.printStackTrace();
                results = new ArrayList<>();
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<Result> results) {
            onResultsUpdated(results);
        }
    }
}
