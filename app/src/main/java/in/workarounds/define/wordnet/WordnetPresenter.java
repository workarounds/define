package in.workarounds.define.wordnet;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;

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
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class WordnetPresenter implements MeaningPresenter, Observer<List<Synset>>{
    private MeaningsController controller;
    private DictionaryException dictionaryException;
    private WordnetDictionary dictionary;
    private WordnetMeaningPage wordnetMeaningPage;
    private String word;
    private WordnetMeaningAdapter adapter;
    private Subscription subscription;
    private ContextHelper contextHelper;

    @Inject
    public WordnetPresenter(WordnetDictionary dictionary, WordnetMeaningAdapter adapter, MeaningsController controller, ContextHelper contextHelper) {
        this.dictionary = dictionary;
        this.adapter = adapter;
        this.controller = controller;
        this.contextHelper = contextHelper;
        controller.addMeaningPresenter(this);
    }

    @Override
    public void addView(View view) {
        this.wordnetMeaningPage = (WordnetMeaningPage) view;
        initMeaningPage();
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
                .doOnNext(w -> {
                    onLoadingMeanings();
                    updateWordOnPage(w);
                })
                .flatMap(dictionary::resultsObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    private void updateWordOnPage(String word) {
        this.word = word;
        if (wordnetMeaningPage != null) {
            wordnetMeaningPage.title(word);
        }
    }

    public DictionaryException getDictionaryException() {
        return dictionaryException;
    }

    public void setDictionaryException(DictionaryException dictionaryException) {
        this.dictionaryException = dictionaryException;
    }

    private void onResultsUpdated(List<Synset> results) {
        adapter.update(results);
        if(wordnetMeaningPage != null) {
            adapter.notifyDataSetChanged();
            if (results != null && results.size() != 0) {
                wordnetMeaningPage.meaningsLoaded();
            } else {
                wordnetMeaningPage.error("Sorry, no results found.");
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
    public void onNext(List<Synset> synsets) {
        onResultsUpdated(synsets);
    }

    public void onDownloadClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contextHelper.openDictionariesActivity();
            controller.onDownloadClicked();
        }else {
            contextHelper.startDownload(Constants.WORDNET);
        }
    }

    private void initMeaningPage() {
        wordnetMeaningPage.title(word);
        wordnetMeaningPage.setAdapter(adapter);
        if (TextUtils.isEmpty(word)) {
            wordnetMeaningPage.error("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
        } else if (adapter != null && adapter.getItemCount() != 0) {
            wordnetMeaningPage.meaningsLoaded();
        } else if (getDictionaryException() != null) {
            showException();
        } else if(subscription != null && !subscription.isUnsubscribed()){
            wordnetMeaningPage.meaningsLoading();
        } else {
            wordnetMeaningPage.error("Sorry, no results found");
        }
    }

    private void onLoadingMeanings() {
        if(wordnetMeaningPage != null) {
            wordnetMeaningPage.meaningsLoading();
        }
    }

    private void showException() {
        if(wordnetMeaningPage != null) {
            DictionaryException exception = getDictionaryException();
            wordnetMeaningPage.error(exception.getMessage());
            if(exception.getType() == DictionaryException.DICTIONARY_NOT_FOUND) {
                wordnetMeaningPage.dictionaryNotAvailable();
            }
        }
    }
}
