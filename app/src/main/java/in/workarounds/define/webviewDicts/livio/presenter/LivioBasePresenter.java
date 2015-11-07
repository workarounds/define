package in.workarounds.define.webviewDicts.livio.presenter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.workarounds.define.DefineApp;
import in.workarounds.define.R;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.portal.MainPortal;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;
import in.workarounds.define.webviewDicts.livio.LivioMeaningPage;

/**
 * Created by madki on 07/11/15.
 */
public abstract class LivioBasePresenter implements MeaningPresenter {
    private static final String TAG = LogUtils.makeLogTag(LivioBasePresenter.class);
    private static final int LOAD_STATUS = 1;
    private static final int LOAD_PROGRESS = 2;
    private static final int MEANING_LIST = 3;

    private static final String mime = "text/html";
    private static final String encoding = "utf-8";

    private MainPortal portal;
    private DictionaryException dictionaryException;
    private LivioDictionary dictionary;
    private LivioMeaningPage livioMeaningPage;
    private String word;
    private TextView loadStatus;
    private Button installLivioBtn;
    private ProgressBar loadProgress;
    private WebView meaningList;
    private String webviewHtml;
    private MeaningsTask task;
    private Handler handler;

    public LivioBasePresenter(LivioDictionary dictionary, MainPortal portal) {
        this.dictionary = dictionary;
        this.portal = portal;
        this.handler = new Handler();
        portal.addPresenter(this);
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
        if (task != null) task.cancel(true);
        dropViews();
    }

    @android.webkit.JavascriptInterface
    public void loadMeaning(final String word) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // code here will run on UI thread
                LivioBasePresenter.this.onWordUpdated(word.split(":")[1]);
            }
        });
    }

    @Override
    public void onWordUpdated(String word) {
        LogUtils.LOGD(TAG, "Word updated: " + word);
        if (word != null && !word.equals(this.word)) {
            showProgress();
            this.word = word;
            if (livioMeaningPage != null) {
                LogUtils.LOGD(TAG, "livioMeaningPage not null");
                livioMeaningPage.title(word);
            }
            if (task != null) {
                task.cancel(true);
            }

            task = new MeaningsTask();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, word);
            } else {
                task.execute(word);
            }
        }
    }

    @LivioDictionary.PACKAGE_NAME
    protected abstract String getPackageName();

    public DictionaryException getDictionaryException() {
        return dictionaryException;
    }

    public void setDictionaryException(DictionaryException dictionaryException) {
        this.dictionaryException = dictionaryException;
    }

    public String word() {
        return word;
    }

    private void onResultsUpdated(final String html) {
        webviewHtml = html;
        if (livioMeaningPage != null) {
            installLivioBtn.setVisibility(View.GONE);
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
    }

    private class MeaningsTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String html = "";
            try {
                html = dictionary.results(params[0], getPackageName());
                setDictionaryException(null);
            } catch (DictionaryException exception) {
                exception.printStackTrace();
                setDictionaryException(exception);
            } catch (Exception exception) {
                exception.printStackTrace();
                setDictionaryException(new
                        DictionaryException(DictionaryException.UNKNOWN, "Sorry, something went wrong."));
            }
            return html;
        }

        @Override
        protected void onPostExecute(String html) {
            if (getDictionaryException() != null) {
                showException();
            } else {
                onResultsUpdated(html);
            }
        }
    }

    /* Ignore security vulnerability warnings for sdk below below jelly bean as we are in control of the html */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initViews() {
        loadStatus = (TextView) livioMeaningPage.findViewById(R.id.tv_load_status);
        installLivioBtn = (Button) livioMeaningPage.findViewById(R.id.btn_install_livio);
        loadProgress = (ProgressBar) livioMeaningPage.findViewById(R.id.pb_load_progress);
        meaningList = (WebView) livioMeaningPage.findViewById(R.id.rv_meaning_list);
        meaningList.getSettings().setJavaScriptEnabled(true);
        meaningList.addJavascriptInterface(this, "JSInterface");
        meaningList.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                showList();
            }
        });
        installLivioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installLivio();
            }
        });
    }

    public void installLivio() {
        String packageName = getPackageName();
        try {
            DefineApp.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            DefineApp.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        portal.finish();
    }

    private void setInitialViews() {
        if (TextUtils.isEmpty(word)) {
            showStatus("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
        } else if (getDictionaryException() != null) {
            showException();
        } else if (webviewHtml != null) {
            onResultsUpdated(webviewHtml);
        } else if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            showProgress();
        } else {
            showStatus("Sorry, no results found");
        }
    }

    private void dropViews() {
        loadStatus = null;
        installLivioBtn = null;
        loadProgress = null;
        meaningList = null;
    }

    private void showStatus(String status) {
        loadStatus.setText(status);
        showView(LOAD_STATUS);
    }

    private void showException() {
        if (livioMeaningPage != null) {
            showStatus(getDictionaryException().getMessage());
            if (getDictionaryException().getType() == DictionaryException.DICTIONARY_NOT_FOUND) {
                installLivioBtn.setVisibility(View.VISIBLE);
            }
        }
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
