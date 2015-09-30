package in.workarounds.define.view.meaning;

import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.dictionary.DictionaryException;
import in.workarounds.define.dictionary.Result;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;
import in.workarounds.typography.TextView;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class MeaningPresenter {
    private static final String TAG = LogUtils.makeLogTag(MeaningPresenter.class);
    private static final int LOAD_STATUS   = 1;
    private static final int LOAD_PROGRESS = 2;
    private static final int MEANING_LIST  = 3;

    private Dictionary dictionary;
    private MeaningPage meaningPage;
    private String word;
    private MeaningsAdapter adapter = new MeaningsAdapter();
    private TextView loadStatus;
    private ProgressBar loadProgress;
    private RecyclerView meaningList;

    @Inject
    public MeaningPresenter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void addView(MeaningPage view) {
        this.meaningPage = view;
        initViews();
        setInitialViews();
    }

    public void dropView() {
        this.meaningPage = null;
        dropViews();
    }

    public void word(String word) {
        if(word != null && !word.equals(this.word)) {
            showProgress();
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
        if(results != null && results.size() != 0) {
            showList();
        } else {
            showStatus("Sorry, no results found.");
        }
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

    private void initViews() {
        loadStatus = (TextView) meaningPage.findViewById(R.id.tv_load_status);
        loadProgress = (ProgressBar) meaningPage.findViewById(R.id.pb_load_progress);
        meaningList = (RecyclerView) meaningPage.findViewById(R.id.rv_meaning_list);
    }

    private void setInitialViews() {
        if(TextUtils.isEmpty(word)) {
            showStatus("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
        } else if(adapter != null && adapter.getItemCount() != 0) {
            showList();
        } else {
            showStatus("Sorry, no results found");
        }
    }

    private void dropViews() {
        loadStatus = null;
        loadProgress = null;
        meaningList = null;
    }

    private void showStatus(String status) {
        loadStatus.setText(status);
        showView(LOAD_STATUS);
    }

    private void showProgress() {
        showView(LOAD_PROGRESS);
    }

    private void showList() {
        showView(MEANING_LIST);
    }

    private void showView(@ViewEnum int view) {
        switch (view) {
            case LOAD_STATUS:
                changeViewVisibilities(true, false, false);
                break;
            case LOAD_PROGRESS:
                changeViewVisibilities(false, true, false);
                break;
            case MEANING_LIST:
                changeViewVisibilities(false, false, true);
                break;
        }
    }

    private void changeViewVisibilities(boolean status, boolean progress, boolean list) {
        loadStatus.setVisibility(status?View.VISIBLE:View.GONE);
        loadProgress.setVisibility(progress?View.VISIBLE:View.GONE);
        meaningList.setVisibility(list?View.VISIBLE:View.GONE);
    }

    @IntDef({LOAD_PROGRESS, LOAD_STATUS, MEANING_LIST})
    private @interface ViewEnum {
    }
}
