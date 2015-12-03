package in.workarounds.define.webviewDicts.livio.presenter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;
import in.workarounds.define.webviewDicts.livio.LivioMeaningPage;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by madki on 07/11/15.
 */
public abstract class LivioBasePresenter implements MeaningPresenter, Observer<String> {
    private static final int LOAD_STATUS = 1;
    private static final int LOAD_PROGRESS = 2;
    private static final int MEANING_LIST = 3;

    private static final String mime = "text/html";
    private static final String encoding = "utf-8";

    private MeaningsController controller;
    private DictionaryException dictionaryException;
    private LivioDictionary dictionary;
    private LivioMeaningPage livioMeaningPage;
    private String word;
    private TextView loadStatus;
    private Button installLivioBtn;
    private ProgressBar loadProgress;
    private WebView meaningList;
    private String webviewHtml;
    private Subscription subscription;
    private Handler handler;

    public LivioBasePresenter(LivioDictionary dictionary, MeaningsController controller) {
        this.dictionary = dictionary;
        this.controller = controller;
        this.handler = new Handler();
        controller.addMeaningPresenter(this);
    }

    @Override
    public void addView(View view) {
        this.livioMeaningPage = (LivioMeaningPage) view;
        initViews();
        setInitialViews();
    }

    @Override
    public void dropView() {
        this.livioMeaningPage = null;
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
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
        Timber.d("Word updates : %s", word);
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = Observable.just(word)
                .filter(w -> w != null && !w.equals(LivioBasePresenter.this.word))
                .doOnNext(w -> {
                    showProgress();
                    updateWordOnPage(w);
                })
                .observeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                .flatMap(this::getResults)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    private Observable<String> getResults(final String word) {
        return Observable.fromCallable(() -> dictionary.results(word, getPackageName()));
    }

    private void updateWordOnPage(String word) {
        this.word = word;
        if(livioMeaningPage != null) {
            livioMeaningPage.title(word);
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
                        if(meaningList != null) {
                            meaningList.loadDataWithBaseURL("file:///android_asset/", html, mime, encoding, null);
                        }
                    }
                });
            } else {
                showStatus("Sorry, no results found.");
            }
        }
    }

    @Override
    public void onCompleted() {
        setDictionaryException(null);
    }

    @Override
    public void onError(Throwable e) {
        DictionaryException exception;
        if(e instanceof DictionaryException) {
            exception = (DictionaryException) e;
        } else {
            //noinspection ThrowableInstanceNeverThrown
            exception = new DictionaryException(
                    DictionaryException.UNKNOWN,
                    "Sorry, something went wrong"
            );
        }
        setDictionaryException(exception);
        showException();
    }

    @Override
    public void onNext(String result) {
        onResultsUpdated(result);
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
                controller.onInstallClicked();
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
    }

    private void setInitialViews() {
        if (TextUtils.isEmpty(word)) {
            showStatus("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
        } else if (getDictionaryException() != null) {
            showException();
        } else if (webviewHtml != null) {
            onResultsUpdated(webviewHtml);
        } else if (subscription != null && !subscription.isUnsubscribed()) {
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
