package in.workarounds.define.webviewDicts.livio.presenter;

import android.text.TextUtils;
import android.view.View;

import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.helper.ContextHelper;
import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;
import in.workarounds.define.webviewDicts.livio.LivioMeaningPage;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by madki on 07/11/15.
 */
public abstract class LivioBasePresenter implements MeaningPresenter, Observer<String> {
    private MeaningsController controller;
    private DictionaryException dictionaryException;
    private LivioDictionary dictionary;
    private LivioMeaningPage livioMeaningPage;
    private String word;
    private String webviewHtml;
    private Subscription subscription;
    private ContextHelper contextHelper;

    public LivioBasePresenter(LivioDictionary dictionary, MeaningsController controller, ContextHelper contextHelper) {
        this.dictionary = dictionary;
        this.controller = controller;
        this.contextHelper = contextHelper;
        controller.addMeaningPresenter(this);
    }

    @Override
    public void addView(View view) {
        this.livioMeaningPage = (LivioMeaningPage) view;
        initMeaningPage();
    }

    @Override
    public void dropView() {
        this.livioMeaningPage = null;
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
                    onMeaningsLoading();
                    updateWordOnPage(w);
                })
                .flatMap(w -> dictionary.resultsObservable(w, getPackageName()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @LivioDictionary.PACKAGE_NAME
    protected abstract String getPackageName();

    public DictionaryException getDictionaryException() {
        return dictionaryException;
    }

    public void setDictionaryException(DictionaryException dictionaryException) {
        this.dictionaryException = dictionaryException;
    }

    private void onResultsUpdated(final String html) {
        webviewHtml = html;
        if (livioMeaningPage != null) {
            if (html != null && !html.isEmpty()) {
                livioMeaningPage.updateMeanings(html);
            } else {
                livioMeaningPage.error("Sorry, no results found.");
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
            Timber.e(e, "Unknown exception");
        }
        setDictionaryException(exception);
        showException();
    }

    @Override
    public void onNext(String result) {
        onResultsUpdated(result);
    }

    public void onInstallClicked() {
        installLivio();
        controller.onInstallClicked();
    }

    public void installLivio() {
        contextHelper.openPlayStore(getPackageName());
    }

    private void initMeaningPage() {
        livioMeaningPage.title(word);
        if (TextUtils.isEmpty(word)) {
            livioMeaningPage.error("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
        } else if (getDictionaryException() != null) {
            showException();
        } else if (webviewHtml != null) {
            onResultsUpdated(webviewHtml);
        } else if (subscription != null && !subscription.isUnsubscribed()) {
            livioMeaningPage.meaningsLoading();
        } else {
            livioMeaningPage.error("Sorry, no results found");
        }
    }

    private void updateWordOnPage(String word) {
        this.word = word;
        if(livioMeaningPage != null) {
            livioMeaningPage.title(word);
        }
    }

    private void onMeaningsLoading() {
        if(livioMeaningPage != null) {
            livioMeaningPage.meaningsLoading();
        }
    }

    private void showException() {
        if (livioMeaningPage != null) {
            livioMeaningPage.error(getDictionaryException().getMessage());
            if (getDictionaryException().getType() == DictionaryException.DICTIONARY_NOT_FOUND) {
                livioMeaningPage.dictionaryNotAvailable();
            }
        }
    }
}
