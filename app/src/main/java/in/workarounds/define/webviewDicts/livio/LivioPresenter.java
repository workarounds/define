package in.workarounds.define.webviewDicts.livio;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.portal.MainPortal;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.webviewDicts.DaggerWebViewComponent;
import in.workarounds.define.webviewDicts.JavaScriptInterface;
import in.workarounds.define.webviewDicts.WebViewComponent;
import in.workarounds.define.webviewDicts.WebViewModule;
import in.workarounds.typography.TextView;

/**
 * Created by madki on 13/10/15.
 */
@PerPortal
public class LivioPresenter implements MeaningPresenter {
    private static final String TAG = LogUtils.makeLogTag(LivioPresenter.class);
    private static final int LOAD_STATUS = 1;
    private static final int LOAD_PROGRESS = 2;
    private static final int MEANING_LIST = 3;

    private static final String mime = "text/html";
    private static final String encoding = "utf-8";

    private JavaScriptInterface javaScriptInterface;
    private LivioDictionary dictionary;
    private LivioMeaningPage livioMeaningPage;
    private String word;
    private TextView loadStatus;
    private ProgressBar loadProgress;
    private WebView meaningList;
    private MeaningsTask task;

    @Inject
    public LivioPresenter(LivioDictionary dictionary, MainPortal portal) {
        this.dictionary = dictionary;
        portal.addPresenter(this);
        initComponents();
    }

    @Override
    public void addView(View view) {
        LogUtils.LOGD(TAG, "View added");
        this.livioMeaningPage = (LivioMeaningPage) view;
        initViews();
        setInitialViews();
    }

    @Override
    public void dropView() {
        LogUtils.LOGD(TAG, "View dropped");
        this.livioMeaningPage = null;
        dropViews();
    }

    private void initComponents() {
        Handler handler = new Handler();
        WebViewComponent webViewComponent = DaggerWebViewComponent.builder()
                .webViewModule(new WebViewModule(this, handler))
                .build();
        javaScriptInterface = webViewComponent.provideJavaScriptInterface();
    }

    @Override
    public void onWordUpdated(String word) {
        LogUtils.LOGD(TAG, "Word updated: " + word);
        if (word != null && !word.equals(this.word)) {
            showProgress();
        }
        this.word = word;
        if (livioMeaningPage != null) {
            LogUtils.LOGD(TAG, "livioMeaningPage not null");
            livioMeaningPage.title(word);
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

    private void onResultsUpdated(final String html) {
        if (html != null && !html.isEmpty()) {
            meaningList.post(new Runnable() {
                public void run() {
                    meaningList.loadDataWithBaseURL("file:///android_asset/", html, mime, encoding, null);
                }
            });
        } else {
            showStatus("Sorry, no results found.");
        }
    }

    private class MeaningsTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String html = "";
            try {
                html = dictionary.results(params[0]);
            } catch (DictionaryException exception) {
                exception.printStackTrace();
            }
            return html;
        }

        @Override
        protected void onPostExecute(String html) {
            onResultsUpdated(html);
        }
    }

    /* Ignore security vulnerability warnings for sdk below below jelly bean as we are in control of the html */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initViews() {
        loadStatus = (TextView) livioMeaningPage.findViewById(R.id.tv_load_status);
        loadProgress = (ProgressBar) livioMeaningPage.findViewById(R.id.pb_load_progress);
        meaningList = (WebView) livioMeaningPage.findViewById(R.id.rv_meaning_list);
        meaningList.getSettings().setJavaScriptEnabled(true);
        meaningList.addJavascriptInterface(javaScriptInterface, "JSInterface");
        meaningList.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                showList();
            }
        });
    }

    private void setInitialViews() {
        if (TextUtils.isEmpty(word)) {
            showStatus("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
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
