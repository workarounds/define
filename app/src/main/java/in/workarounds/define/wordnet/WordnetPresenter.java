package in.workarounds.define.wordnet;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import edu.smu.tspell.wordnet.Synset;
import in.workarounds.define.api.Constants;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.helper.ContextHelper;
import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.portal.PerPortal;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class WordnetPresenter implements MeaningPresenter{
    private MeaningsController controller;
    private WordnetDictionary dictionary;
    private WordnetMeaningPage wordnetMeaningPage;
    private Subscription subscription;
    private ContextHelper contextHelper;

    //State
    private String word;
    private List<Synset> meanings = Collections.emptyList();
    private boolean loading = false;
    private DictionaryException exception;

    @Inject
    public WordnetPresenter(WordnetDictionary dictionary, MeaningsController controller, ContextHelper contextHelper) {
        this.dictionary = dictionary;
        this.controller = controller;
        this.contextHelper = contextHelper;
        controller.addMeaningPresenter(this);
    }

    @Override
    public void addView(View view) {
        this.wordnetMeaningPage = (WordnetMeaningPage) view;

        updateView();
    }

    @Override
    public void dropView() {
        this.wordnetMeaningPage = null;
    }

    @Override
    public void onWordUpdated(String word) {
        Timber.d("Word updated : %s", word);
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = Observable.just(word)
                .filter(w -> w != null && !w.equals(WordnetPresenter.this.word))
                .doOnNext(this::onLoadingMeanings)
                .flatMap(dictionary::resultsObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onResultsUpdated,
                        this::onError
                );
    }

    public void onDownloadClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contextHelper.openDictionariesActivity();
            controller.onDownloadClicked();
        }else {
            contextHelper.startDownload(Constants.WORDNET);
        }
    }

    private void onLoadingMeanings(String word) {
        this.word = word;
        loading = true;
        exception = null;
        meanings = Collections.emptyList();

        updateView();
    }

    private void onResultsUpdated(List<Synset> results) {
        loading = false;
        meanings = results;
        exception = null;

        updateView();
    }

    private void onError(Throwable e) {
        loading = false;
        meanings = Collections.emptyList();
        if(e instanceof DictionaryException) {
            exception = (DictionaryException) e;
        } else {
            //noinspection ThrowableInstanceNeverThrown
            exception = new DictionaryException(
                    DictionaryException.UNKNOWN,
                    UNKNOWN_ERROR
            );
            Timber.e(e, "Unknown exception");
        }

        updateView();
    }

    private void updateView() {
        if(wordnetMeaningPage == null) return;

        wordnetMeaningPage.title(word);
        if(TextUtils.isEmpty(word)) {
            wordnetMeaningPage.error(NO_WORD_SELECTED_ERROR);
        } else if(loading) {
            wordnetMeaningPage.meaningsLoading();
        } else if(exception != null) {
            wordnetMeaningPage.error(exception.getMessage());
            if(exception.getType() == DictionaryException.DICTIONARY_NOT_FOUND) {
                wordnetMeaningPage.dictionaryNotAvailable();
            }
        } else {
            if(meanings.size() == 0) {
                wordnetMeaningPage.error(NO_RESULTS_FOUND_ERROR);
            } else {
                wordnetMeaningPage.meanings(meanings);
            }
        }
    }
}
