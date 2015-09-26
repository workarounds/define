package in.workarounds.define.meaning;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.workarounds.define.dictionary.Dictionary;
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

    private boolean old;

    @Inject
    public MeaningPresenter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void addView(MeaningPage view) {
        this.meaningPage = view;
        if(old) {
            restoreView();
        }
    }

    public void dropView() {
        this.meaningPage = null;
        old = true;
    }

    public void word(String word) {
        this.word = word;
        new MeaningsTask().execute(word);
    }

    public String word() {
        return word;
    }

    public boolean old() {
        return old;
    }

    private void restoreView() {
        meaningPage.title(word);
    }

    public MeaningsAdapter adapter() {
        return adapter;
    }

    private void onResultsUpdated(List<Result> results) {
        adapter.update(results);
    }

    private class MeaningsTask extends AsyncTask<String, Integer, ArrayList<Result>> {
        @Override
        protected ArrayList<Result> doInBackground(String... params) {
            return dictionary.results(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Result> results) {
            onResultsUpdated(results);
        }
    }
}
