package in.workarounds.define.urban;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.IUrbanDictionary;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.base.Result;
import in.workarounds.define.portal.MainPortal;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;
import in.workarounds.typography.TextView;

/**
 * Created by madki on 13/10/15.
 */
@PerPortal
public class UrbanPresenter implements MeaningPresenter {
    private static final String TAG = LogUtils.makeLogTag(UrbanPresenter.class);
    private static final int LOAD_STATUS = 1;
    private static final int LOAD_PROGRESS = 2;
    private static final int MEANING_LIST = 3;

    private UrbanDictionary dictionary;
    private UrbanMeaningPage urbanMeaningPage;
    private String word;
    private UrbanMeaningAdapter adapter;
    private TextView loadStatus;
    private ProgressBar loadProgress;
    private RecyclerView meaningList;
    private MeaningsTask task;

    @Inject
    public UrbanPresenter(UrbanDictionary dictionary, UrbanMeaningAdapter adapter, MainPortal portal) {
        this.dictionary = dictionary;
        this.adapter = adapter;
        portal.addPresenter(this);
    }

    @Override
    public void addView(View view) {
        LogUtils.LOGD(TAG, "View added");
        this.urbanMeaningPage = (UrbanMeaningPage) view;
        initViews();
        setInitialViews();
    }

    @Override
    public void dropView() {
        LogUtils.LOGD(TAG, "View dropped");
        this.urbanMeaningPage = null;
        if(task != null) task.cancel(true);
        dropViews();
    }

    @Override
    public void onWordUpdated(String word) {
        LogUtils.LOGD(TAG, "Word updated: " + word);
        if (word != null && !word.equals(this.word)) {
            showProgress();
        }
        this.word = word;
        if (urbanMeaningPage != null) {
            LogUtils.LOGD(TAG, "urbanMeaningPage not null");
            urbanMeaningPage.title(word);
        }
        if(task != null) {
            task.cancel(true);
        }

        task = new MeaningsTask();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, word);
        } else {
            task.execute(word);
        }
    }

    public String word() {
        return word;
    }

    public UrbanMeaningAdapter adapter() {
        return adapter;
    }

    private void onResultsUpdated(UrbanResult results) {
        adapter.update(results);
        if (results != null && results.getMeanings().size() != 0) {
            showList();
        } else {
            showStatus("Sorry, no results found.");
        }
    }

    private class MeaningsTask extends AsyncTask<String, Integer, UrbanResult> {
        private DictionaryException dictionaryException;
        @Override
        protected UrbanResult doInBackground(String... params) {
            UrbanResult results = null;
            try {
                results = dictionary.results(params[0]);
            } catch (DictionaryException exception) {
                exception.printStackTrace();
                dictionaryException = exception;
            }
            return results;
        }

        @Override
        protected void onPostExecute(UrbanResult results) {
            if (dictionaryException != null) {
                showStatus(dictionaryException.getMessage());
            }else {
                onResultsUpdated(results);
            }
        }
    }

    private void initViews() {
        loadStatus = (TextView) urbanMeaningPage.findViewById(R.id.tv_load_status);
        loadProgress = (ProgressBar) urbanMeaningPage.findViewById(R.id.pb_load_progress);
        meaningList = (RecyclerView) urbanMeaningPage.findViewById(R.id.rv_meaning_list);
    }

    private void setInitialViews() {
        if (TextUtils.isEmpty(word)) {
            showStatus("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
        } else if (adapter != null && adapter.getItemCount() != 0) {
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
        if (loadStatus != null) {
            loadStatus.setVisibility(status ? View.VISIBLE : View.GONE);
        }
        if (loadProgress != null) {
            loadProgress.setVisibility(progress ? View.VISIBLE : View.GONE);
        }
        if (meaningList != null) {
            meaningList.setVisibility(list ? View.VISIBLE : View.GONE);
        }
    }

    @IntDef({LOAD_PROGRESS, LOAD_STATUS, MEANING_LIST})
    private @interface ViewEnum {
    }
}
